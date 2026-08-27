package com.paketrool.casinocraft.block.entity;

import com.paketrool.casinocraft.compat.NbtCompat;
import com.paketrool.casinocraft.enchantment.CasinocraftEnchantments;
import com.paketrool.casinocraft.item.CasinocraftItems;
import com.paketrool.casinocraft.menu.SlotMachineMenu;
import com.paketrool.casinocraft.slot.SlotMachineLogic;
import com.paketrool.casinocraft.slot.SlotSpinResult;
import com.paketrool.casinocraft.slot.SlotSymbol;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SlotMachineBlockEntity extends BlockEntity implements Container, MenuProvider {

	public static final int SLOT_POOL = 0;
	public static final int SLOT_BONUS = 1;
	public static final int SLOT_COUNT = 2;
	public static final int DATA_COUNT = 13;

	public static final int DATA_BET = 0;
	public static final int DATA_LAST_PAYOUT = 1;
	public static final int DATA_LAST_JACKPOT = 2;
	// DATA_LAST_GRID indices 3..11 hold the last-result grid (ordinals of 9 symbols)
	public static final int DATA_SPIN_TICKS_LEFT = 12;

	public static final int SPIN_DURATION_TICKS = 40;

	private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
	private int bet = 1;
	private int lastPayout = 0;
	private int lastJackpot = 0;
	private final int[] lastGrid = new int[9];
	private long spinStartTime = -1L;

	private final ContainerData dataAccess = new ContainerData() {
		@Override
		public int get(int index) {
			return switch (index) {
				case DATA_BET -> bet;
				case DATA_LAST_PAYOUT -> lastPayout;
				case DATA_LAST_JACKPOT -> lastJackpot;
				case DATA_SPIN_TICKS_LEFT -> {
					if (spinStartTime < 0 || level == null) yield 0;
					long elapsed = level.getGameTime() - spinStartTime;
					yield (int) Math.max(0L, SPIN_DURATION_TICKS - elapsed);
				}
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
		ItemStack pool = items.get(SLOT_POOL);
		return pool.is(CasinocraftItems.CASINO_CHIP) && pool.getCount() >= bet;
	}

	public long getSpinStartTime() {
		return spinStartTime;
	}

	public int getLastGridSymbol(int col, int row) {
		int idx = col * 3 + row;
		if (idx < 0 || idx >= lastGrid.length) return 0;
		return lastGrid[idx];
	}

	public boolean isSpinning() {
		if (level == null || spinStartTime < 0) return false;
		return (level.getGameTime() - spinStartTime) < SPIN_DURATION_TICKS;
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, SlotMachineBlockEntity be) {
		if (be.spinStartTime < 0) return;
		long elapsed = level.getGameTime() - be.spinStartTime;

		if (elapsed < SPIN_DURATION_TICKS && elapsed % 4L == 0L) {
			float pitch = 0.8f + (elapsed / 4f) * 0.05f;
			level.playSound(null, pos, SoundEvents.NOTE_BLOCK_HAT.value(), SoundSource.BLOCKS, 0.4f, pitch);
		}

		if (elapsed >= SPIN_DURATION_TICKS) {
			if (be.lastJackpot > 0 && level instanceof ServerLevel serverLevel) {
				level.playSound(null, pos, SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 1.0f, 1.0f);
				level.playSound(null, pos, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.BLOCKS, 1.0f, 1.0f);
				serverLevel.sendParticles(ParticleTypes.END_ROD,
					pos.getX() + 0.5, pos.getY() + 2.5, pos.getZ() + 0.5,
					30, 0.8, 0.8, 0.8, 0.3);
				serverLevel.sendParticles(ParticleTypes.FIREWORK,
					pos.getX() + 0.5, pos.getY() + 2.5, pos.getZ() + 0.5,
					40, 0.8, 0.8, 0.8, 0.5);
			} else if (be.lastPayout > 0 && level instanceof ServerLevel serverLevel) {
				level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 1.0f, 1.2f);
				serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
					pos.getX() + 0.5, pos.getY() + 2.0, pos.getZ() + 0.5,
					20, 0.5, 0.5, 0.5, 0.1);
			} else {
				level.playSound(null, pos, SoundEvents.NOTE_BLOCK_BASS.value(), SoundSource.BLOCKS, 0.4f, 0.8f);
			}
			be.spinStartTime = -1L;
			be.setChanged();
			level.sendBlockUpdated(pos, state, state, 3);
		}
	}

	public void spin(Player player) {
		if (level == null || level.isClientSide()) return;
		if (!canSpin()) return;

		ItemStack pool = items.get(SLOT_POOL);
		pool.shrink(bet);
		if (pool.isEmpty()) items.set(SLOT_POOL, ItemStack.EMPTY);

		spinStartTime = level.getGameTime();

		level.playSound(null, worldPosition, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.BLOCKS, 0.5f, 1.4f);

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
			ItemStack current = items.get(SLOT_POOL);
			if (current.isEmpty()) {
				int give = Math.min(payout, CasinocraftItems.CASINO_CHIP.getDefaultMaxStackSize());
				items.set(SLOT_POOL, new ItemStack(CasinocraftItems.CASINO_CHIP, give));
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
			enchantBonusOnJackpot();
		}

		setChanged();
		if (level != null) {
			level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
		}
	}

	private void enchantBonusOnJackpot() {
		if (level == null) return;
		ItemStack bonus = items.get(SLOT_BONUS);
		if (bonus.isEmpty()) return;
		ResourceKey<Enchantment> key = CasinocraftEnchantments.pickForItem(bonus);
		if (key == null) return;
		Holder<Enchantment> holder = level.registryAccess()
			.lookupOrThrow(Registries.ENCHANTMENT)
			.getOrThrow(key);
		bonus.enchant(holder, 1);
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
		for (ItemStack stack : items) {
			if (!stack.isEmpty()) return false;
		}
		return true;
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
		if (slot == SLOT_BONUS) {
			if (stack.getCount() > 1) stack.setCount(1);
		} else if (stack.getCount() > getMaxStackSize()) {
			stack.setCount(getMaxStackSize());
		}
		setChanged();
	}

	@Override
	public boolean stillValid(Player player) {
		if (level == null || level.getBlockEntity(worldPosition) != this) return false;
		return player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) <= 64.0;
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		if (slot == SLOT_POOL) {
			return stack.is(CasinocraftItems.CASINO_CHIP);
		}
		if (slot == SLOT_BONUS) {
			return isBonusAllowed(stack);
		}
		return false;
	}

	public static boolean isBonusAllowed(ItemStack stack) {
		if (stack.isEmpty()) return false;
		if (stack.is(CasinocraftItems.CASINO_CHIP)) return false;
		if (isSpecialAllowed(stack)) return true;
		return !(stack.getItem() instanceof BlockItem);
	}

	private static boolean isSpecialAllowed(ItemStack stack) {
		return stack.is(Items.TORCH)
			|| stack.is(Items.ENDER_PEARL)
			|| stack.is(Items.CHORUS_FRUIT)
			|| stack.is(Items.SNOWBALL);
	}

	@Override
	public int getMaxStackSize() {
		return 64;
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

	//? if >=1.21.6 {
	/*@Override
	protected void loadAdditional(net.minecraft.world.level.storage.ValueInput input) {
		super.loadAdditional(input);
		items.clear();
		input.read("Pool", ItemStack.OPTIONAL_CODEC).ifPresent(stack -> items.set(SLOT_POOL, stack));
		input.read("Bonus", ItemStack.OPTIONAL_CODEC).ifPresent(stack -> items.set(SLOT_BONUS, stack));
		bet = input.getIntOr("Bet", 1);
		lastPayout = input.getIntOr("LastPayout", 0);
		lastJackpot = input.getIntOr("LastJackpot", 0);
		spinStartTime = input.getLongOr("SpinStartTime", -1L);
		int[] grid = input.getIntArray("LastGrid").orElse(new int[0]);
		for (int i = 0; i < lastGrid.length; i++) {
			lastGrid[i] = i < grid.length ? grid[i] : SlotSymbol.COAL.ordinal();
		}
	}

	@Override
	protected void saveAdditional(net.minecraft.world.level.storage.ValueOutput output) {
		super.saveAdditional(output);
		ItemStack pool = items.get(SLOT_POOL);
		if (!pool.isEmpty()) {
			output.store("Pool", ItemStack.OPTIONAL_CODEC, pool);
		}
		ItemStack bonus = items.get(SLOT_BONUS);
		if (!bonus.isEmpty()) {
			output.store("Bonus", ItemStack.OPTIONAL_CODEC, bonus);
		}
		output.putInt("Bet", bet);
		output.putInt("LastPayout", lastPayout);
		output.putInt("LastJackpot", lastJackpot);
		output.putIntArray("LastGrid", lastGrid.clone());
		output.putLong("SpinStartTime", spinStartTime);
	}
	*///?} else {
	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		items.clear();
		NbtCompat.ifCompoundPresent(tag, "Pool", pool ->
			ItemStack.parse(registries, pool).ifPresent(stack -> items.set(SLOT_POOL, stack))
		);
		NbtCompat.ifCompoundPresent(tag, "Bonus", bonus ->
			ItemStack.parse(registries, bonus).ifPresent(stack -> items.set(SLOT_BONUS, stack))
		);
		bet = NbtCompat.getIntOr(tag, "Bet", 1);
		lastPayout = NbtCompat.getIntOr(tag, "LastPayout", 0);
		lastJackpot = NbtCompat.getIntOr(tag, "LastJackpot", 0);
		spinStartTime = NbtCompat.getLongOr(tag, "SpinStartTime", -1L);
		int[] grid = NbtCompat.getIntArrayOr(tag, "LastGrid", new int[0]);
		for (int i = 0; i < lastGrid.length; i++) {
			lastGrid[i] = i < grid.length ? grid[i] : SlotSymbol.COAL.ordinal();
		}
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		ItemStack pool = items.get(SLOT_POOL);
		if (!pool.isEmpty()) {
			tag.put("Pool", pool.save(registries, new CompoundTag()));
		}
		ItemStack bonus = items.get(SLOT_BONUS);
		if (!bonus.isEmpty()) {
			tag.put("Bonus", bonus.save(registries, new CompoundTag()));
		}
		tag.putInt("Bet", bet);
		tag.putInt("LastPayout", lastPayout);
		tag.putInt("LastJackpot", lastJackpot);
		tag.putIntArray("LastGrid", lastGrid.clone());
		tag.putLong("SpinStartTime", spinStartTime);
	}
	//?}

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
