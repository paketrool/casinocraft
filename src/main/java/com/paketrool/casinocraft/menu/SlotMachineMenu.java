package com.paketrool.casinocraft.menu;

import com.paketrool.casinocraft.block.CasinocraftBlocks;
import com.paketrool.casinocraft.block.entity.SlotMachineBlockEntity;
import com.paketrool.casinocraft.item.CasinocraftItems;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SlotMachineMenu extends AbstractContainerMenu {

	public static final int BUTTON_BET_1 = 0;
	public static final int BUTTON_BET_5 = 1;
	public static final int BUTTON_BET_10 = 2;
	public static final int BUTTON_SPIN = 3;

	public static final int POOL_SLOT_X = 48;
	public static final int POOL_SLOT_Y = 82;
	public static final int BONUS_SLOT_X = 150;
	public static final int BONUS_SLOT_Y = 62;

	private final Container container;
	private final ContainerData data;
	private final ContainerLevelAccess access;

	public SlotMachineMenu(int id, Inventory playerInventory) {
		this(id, playerInventory, new SimpleContainer(SlotMachineBlockEntity.SLOT_COUNT),
			new SimpleContainerData(SlotMachineBlockEntity.DATA_COUNT));
	}

	public SlotMachineMenu(int id, Inventory playerInventory, Container container, ContainerData data) {
		super(CasinocraftMenus.SLOT_MACHINE, id);
		this.container = container;
		this.data = data;
		this.access = container instanceof SlotMachineBlockEntity be && be.getLevel() != null
			? ContainerLevelAccess.create(be.getLevel(), be.getBlockPos())
			: ContainerLevelAccess.NULL;

		this.addSlot(new Slot(container, SlotMachineBlockEntity.SLOT_POOL, POOL_SLOT_X, POOL_SLOT_Y) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return stack.is(CasinocraftItems.CASINO_CHIP);
			}
		});

		this.addSlot(new Slot(container, SlotMachineBlockEntity.SLOT_BONUS, BONUS_SLOT_X, BONUS_SLOT_Y) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return SlotMachineBlockEntity.isBonusAllowed(stack);
			}

			@Override
			public int getMaxStackSize() {
				return 1;
			}

			@Override
			public int getMaxStackSize(ItemStack stack) {
				return 1;
			}
		});

		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 9; col++) {
				this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 106 + row * 18));
			}
		}
		for (int col = 0; col < 9; col++) {
			this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 164));
		}

		this.addDataSlots(data);
	}

	public int getBet() { return data.get(SlotMachineBlockEntity.DATA_BET); }
	public int getLastPayout() { return data.get(SlotMachineBlockEntity.DATA_LAST_PAYOUT); }
	public int getLastJackpot() { return data.get(SlotMachineBlockEntity.DATA_LAST_JACKPOT); }
	public int getSpinTicksLeft() { return data.get(SlotMachineBlockEntity.DATA_SPIN_TICKS_LEFT); }
	public int getLastGridSymbol(int col, int row) {
		return data.get(3 + col * 3 + row);
	}
	public int getChipsPool() {
		ItemStack pool = container.getItem(SlotMachineBlockEntity.SLOT_POOL);
		return pool.is(CasinocraftItems.CASINO_CHIP) ? pool.getCount() : 0;
	}

	@Override
	public boolean clickMenuButton(Player player, int id) {
		if (!(container instanceof SlotMachineBlockEntity be)) return false;
		return switch (id) {
			case BUTTON_BET_1 -> { be.setBet(1); yield true; }
			case BUTTON_BET_5 -> { be.setBet(5); yield true; }
			case BUTTON_BET_10 -> { be.setBet(10); yield true; }
			case BUTTON_SPIN -> {
				if (be.canSpin()) {
					be.spin(player);
					yield true;
				}
				yield false;
			}
			default -> false;
		};
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack copy = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack stack = slot.getItem();
			copy = stack.copy();
			if (index < SlotMachineBlockEntity.SLOT_COUNT) {
				if (!this.moveItemStackTo(stack, SlotMachineBlockEntity.SLOT_COUNT, this.slots.size(), true))
					return ItemStack.EMPTY;
			} else {
				if (stack.is(CasinocraftItems.CASINO_CHIP)) {
					if (!this.moveItemStackTo(stack, SlotMachineBlockEntity.SLOT_POOL, SlotMachineBlockEntity.SLOT_POOL + 1, false))
						return ItemStack.EMPTY;
				} else if (SlotMachineBlockEntity.isBonusAllowed(stack)) {
					if (!this.moveItemStackTo(stack, SlotMachineBlockEntity.SLOT_BONUS, SlotMachineBlockEntity.SLOT_BONUS + 1, false))
						return ItemStack.EMPTY;
				} else {
					return ItemStack.EMPTY;
				}
			}
			if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
			else slot.setChanged();
		}
		return copy;
	}

	@Override
	public void removed(Player player) {
		super.removed(player);
		this.access.execute((level, pos) -> {
			if (!(container instanceof SlotMachineBlockEntity be)) return;
			ItemStack bonus = be.removeItemNoUpdate(SlotMachineBlockEntity.SLOT_BONUS);
			if (!bonus.isEmpty()) {
				if (!player.getInventory().add(bonus)) {
					player.drop(bonus, false);
				}
				be.setChanged();
			}
		});
	}

	@Override
	public boolean stillValid(Player player) {
		return stillValid(access, player, CasinocraftBlocks.SLOT_MACHINE);
	}
}
