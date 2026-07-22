package com.paketrool.casinocraft.block.entity;

import com.paketrool.casinocraft.item.CasinocraftItems;
import com.paketrool.casinocraft.menu.SlotMachineMenu;
import com.paketrool.casinocraft.slot.SlotMachineLogic;
import com.paketrool.casinocraft.slot.SlotSpinResult;
import com.paketrool.casinocraft.slot.SlotSymbol;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SlotMachineBlockEntity extends BlockEntity implements Container, MenuProvider {

	public static final int SLOT_COUNT = 1;
	public static final int DATA_COUNT = 12;

	public static final int DATA_BET = 0;
	public static final int DATA_LAST_PAYOUT = 1;
	public static final int DATA_LAST_JACKPOT = 2;
	// DATA_LAST_GRID indices 3..11 hold the last-result grid (ordinals of 9 symbols)

	private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
	private int bet = 1;
	private int lastPayout = 0;
	private int lastJackpot = 0;
	private final int[] lastGrid = new int[9];

	private final ContainerData dataAccess = new ContainerData() {
		@Override
		public int get(int index) {
			return switch (index) {
				case DATA_BET -> bet;
				case DATA_LAST_PAYOUT -> lastPayout;
				case DATA_LAST_JACKPOT -> lastJackpot;
				default -> {
					int gridIdx = index - 3;
					yield (gridIdx >= 0 && gridIdx < 9) ? lastGrid[gridIdx] : 0;
				}
			};
		}

		@Override
		public void set(int index, int value) {
			switch (index) {
				case DATA_BET -> bet = value;
				case DATA_LAST_PAYOUT -> lastPayout = value;
				case DATA_LAST_JACKPOT -> lastJackpot = value;
				default -> {
					int gridIdx = index - 3;
					if (gridIdx >= 0 && gridIdx < 9) lastGrid[gridIdx] = value;
				}
			}
		}

		@Override
		public int getCount() {
			return DATA_COUNT;
		}
	};

	public SlotMachineBlockEntity(BlockPos pos, BlockState state) {
		super(CasinocraftBlockEntities.SLOT_MACHINE, pos, state);
	}

	// ---------- gameplay ----------

	public int getBet() {
		return bet;
	}

	public void setBet(int bet) {
		if (bet != 1 && bet != 5 && bet != 10) return;
		this.bet = bet;
		setChanged();
	}

	public boolean canSpin() {
		ItemStack pool = items.get(0);
		return pool.is(CasinocraftItems.CASINO_CHIP) && pool.getCount() >= bet;
	}

	public void spin(Player player) {
		if (level == null || level.isClientSide) return;
		if (!canSpin()) return;

		ItemStack pool = items.get(0);
		pool.shrink(bet);
		if (pool.isEmpty()) items.set(0, ItemStack.EMPTY);

		SlotSpinResult result = SlotMachineLogic.spin(level.random, bet);

		lastPayout = result.chipsPayout();
		lastJackpot = result.talismansPayout();
		for (int c = 0; c < 3; c++) {
			for (int r = 0; r < 3; r++) {
				lastGrid[c * 3 + r] = result.grid()[c][r].ordinal();
			}
		}

		// payout chips: refill pool slot, overflow given to player
		if (result.chipsPayout() > 0) {
			int payout = result.chipsPayout();
			ItemStack current = items.get(0);
			if (current.isEmpty()) {
				int give = Math.min(payout, CasinocraftItems.CASINO_CHIP.getDefaultMaxStackSize());
				items.set(0, new ItemStack(CasinocraftItems.CASINO_CHIP, give));
				payout -= give;
			} else {
				int space = current.getMaxStackSize() - current.getCount();
				int give = Math.min(payout, space);
				current.grow(give);
				payout -= give;
			}
			while (payout > 0) {
				int give = Math.min(payout, CasinocraftItems.CASINO_CHIP.getDefaultMaxStackSize());
				ItemStack overflow = new ItemStack(CasinocraftItems.CASINO_CHIP, give);
				if (!player.getInventory().add(overflow)) {
					player.drop(overflow, false);
				}
				payout -= give;
			}
		}

		if (result.talismansPayout() > 0) {
			ItemStack talismans = new ItemStack(CasinocraftItems.GOLDEN_TALISMAN, result.talismansPayout());
			if (!player.getInventory().add(talismans)) {
				player.drop(talismans, false);
			}
		}

		setChanged();
		if (level != null) {
			level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
		}
	}

	public ContainerData getDataAccess() {
		return dataAccess;
	}

	// ---------- Container ----------

	@Override
	public int getContainerSize() {
		return SLOT_COUNT;
	}

	@Override
	public boolean isEmpty() {
		return items.get(0).isEmpty();
	}

	@Override
	public ItemStack getItem(int slot) {
		return items.get(slot);
	}

	@Override
	public ItemStack removeItem(int slot, int amount) {
		ItemStack stack = items.get(slot);
		if (stack.isEmpty()) return ItemStack.EMPTY;
		ItemStack out = stack.split(amount);
		if (stack.isEmpty()) items.set(slot, ItemStack.EMPTY);
		setChanged();
		return out;
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		ItemStack stack = items.get(slot);
		items.set(slot, ItemStack.EMPTY);
		return stack;
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		items.set(slot, stack);
		if (stack.getCount() > getMaxStackSize()) stack.setCount(getMaxStackSize());
		setChanged();
	}

	@Override
	public boolean stillValid(Player player) {
		if (level == null || level.getBlockEntity(worldPosition) != this) return false;
		return player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) <= 64.0;
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		return stack.is(CasinocraftItems.CASINO_CHIP);
	}

	@Override
	public void clearContent() {
		items.clear();
	}

	// ---------- MenuProvider ----------

	@Override
	public Component getDisplayName() {
		return Component.translatable("block.casinocraft.slot_machine");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
		return new SlotMachineMenu(containerId, playerInventory, this, dataAccess);
	}

	// ---------- Save / load ----------

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		items.clear();
		tag.getCompound("Pool").ifPresent(pool ->
			ItemStack.parse(registries, pool).ifPresent(stack -> items.set(0, stack))
		);
		bet = tag.getIntOr("Bet", 1);
		lastPayout = tag.getIntOr("LastPayout", 0);
		lastJackpot = tag.getIntOr("LastJackpot", 0);
		int[] grid = tag.getIntArray("LastGrid").orElse(new int[0]);
		for (int i = 0; i < lastGrid.length; i++) {
			lastGrid[i] = i < grid.length ? grid[i] : SlotSymbol.COAL.ordinal();
		}
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		ItemStack pool = items.get(0);
		if (!pool.isEmpty()) {
			tag.put("Pool", pool.save(registries, new CompoundTag()));
		}
		tag.putInt("Bet", bet);
		tag.putInt("LastPayout", lastPayout);
		tag.putInt("LastJackpot", lastJackpot);
		tag.putIntArray("LastGrid", lastGrid.clone());
	}

	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		return saveWithoutMetadata(registries);
	}
}
