package com.paketrool.casinocraft.block;

import com.mojang.serialization.MapCodec;
import com.paketrool.casinocraft.block.entity.SlotMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class SlotMachineBlock extends HorizontalDirectionalBlock implements EntityBlock {
	public static final MapCodec<SlotMachineBlock> CODEC = simpleCodec(SlotMachineBlock::new);
	public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

	public SlotMachineBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState()
			.setValue(FACING, Direction.NORTH)
			.setValue(HALF, DoubleBlockHalf.LOWER));
	}

	@Override
	protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
		return CODEC;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, HALF);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockPos pos = context.getClickedPos();
		if (pos.getY() >= context.getLevel().getMaxY() - 1) return null;
		if (!context.getLevel().getBlockState(pos.above()).canBeReplaced(context)) return null;
		return defaultBlockState()
			.setValue(FACING, context.getHorizontalDirection().getOpposite())
			.setValue(HALF, DoubleBlockHalf.LOWER);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		BlockPos above = pos.above();
		level.setBlock(above, state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		if (state.getValue(HALF) == DoubleBlockHalf.LOWER) return true;
		BlockState below = level.getBlockState(pos.below());
		return below.is(this) && below.getValue(HALF) == DoubleBlockHalf.LOWER;
	}

	@Override
	protected BlockState updateShape(BlockState state, LevelReader level, net.minecraft.world.level.ScheduledTickAccess tick, BlockPos pos, Direction dir, BlockPos neighborPos, BlockState neighborState, net.minecraft.util.RandomSource random) {
		DoubleBlockHalf half = state.getValue(HALF);
		if (dir.getAxis() == Direction.Axis.Y && (half == DoubleBlockHalf.LOWER) == (dir == Direction.UP)) {
			if (!neighborState.is(this) || neighborState.getValue(HALF) == half) {
				return Blocks.AIR.defaultBlockState();
			}
			return state;
		}
		return super.updateShape(state, level, tick, pos, dir, neighborPos, neighborState, random);
	}

	@Override
	public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		if (!level.isClientSide) {
			preventCreativeDropFromOtherHalf(level, pos, state, player);
		}
		return super.playerWillDestroy(level, pos, state, player);
	}

	private static void preventCreativeDropFromOtherHalf(Level level, BlockPos pos, BlockState state, Player player) {
		DoubleBlockHalf half = state.getValue(HALF);
		if (half == DoubleBlockHalf.UPPER) {
			BlockPos below = pos.below();
			BlockState belowState = level.getBlockState(below);
			if (belowState.is(state.getBlock()) && belowState.getValue(HALF) == DoubleBlockHalf.LOWER) {
				BlockState replacement = belowState.getFluidState().is(Fluids.WATER)
					? Blocks.WATER.defaultBlockState()
					: Blocks.AIR.defaultBlockState();
				level.setBlock(below, replacement, 35);
				level.levelEvent(player, 2001, below, Block.getId(belowState));
			}
		} else {
			BlockPos above = pos.above();
			BlockState aboveState = level.getBlockState(above);
			if (aboveState.is(state.getBlock()) && aboveState.getValue(HALF) == DoubleBlockHalf.UPPER) {
				BlockState replacement = aboveState.getFluidState().is(Fluids.WATER)
					? Blocks.WATER.defaultBlockState()
					: Blocks.AIR.defaultBlockState();
				level.setBlock(above, replacement, 35);
			}
		}
	}

	@Override
	protected BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	protected BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		if (state.getValue(HALF) == DoubleBlockHalf.UPPER) return null;
		return new SlotMachineBlockEntity(pos, state);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (!level.isClientSide) {
			BlockPos entityPos = state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos;
			BlockEntity be = level.getBlockEntity(entityPos);
			if (be instanceof SlotMachineBlockEntity slot) {
				player.openMenu(slot);
			}
		}
		return InteractionResult.SUCCESS;
	}
}
