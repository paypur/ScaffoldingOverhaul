package me.paypur.scaffoldingoverhaul.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ScaffoldingBlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ScaffoldingBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(ScaffoldingBlockItem.class)
public abstract class ScaffoldingBlockItemMixin {

//    @Inject(method = "updatePlacementContext", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;move(Lnet/minecraft/core/Direction;)Lnet/minecraft/core/BlockPos$MutableBlockPos;", shift =  At.Shift.BEFORE))
//    private void inject(BlockPlaceContext pContext, CallbackInfoReturnable<BlockPlaceContext> cir){
//        if (pContext.isInside()) {
//            Direction direction = pContext.getHorizontalDirection();
//        }
//        else {
//            double x = Math.abs(pContext.getClickLocation().x % 1);
//            double z = Math.abs(pContext.getClickLocation().z % 1);
//            Direction direction = (x > 0.25 && x < 0.75 && z > 0.25 && z < 0.75) ? Direction.UP : pContext.getHorizontalDirection();
//        }
//    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    @Nullable
    public BlockPlaceContext updatePlacementContext(BlockPlaceContext pContext) {
        BlockPos blockpos = pContext.getClickedPos();
        Level level = pContext.getLevel();
        BlockState blockstate = level.getBlockState(blockpos);
        Block block = ((ScaffoldingBlockItem) (Object) this).getBlock();
        if (!blockstate.is(block)) {
            return ScaffoldingBlock.getDistance(level, blockpos) == 7 ? null : pContext;
        } else {
            Direction direction;

            // incorrect behavior inside scaffolding

            double x = Math.abs(pContext.getClickLocation().x % 1);
            double y = Math.abs(pContext.getClickLocation().y % 1);
            double z = Math.abs(pContext.getClickLocation().z % 1);
            direction = (x > 0.25 && x < 0.75 && y == 0 && z > 0.25 && z < 0.75) ? Direction.UP : pContext.getHorizontalDirection();

            int i = 0;
            BlockPos.MutableBlockPos blockpos$mutableblockpos = blockpos.mutable().move(direction);

            while(i < 7) {
                if (!level.isClientSide && !level.isInWorldBounds(blockpos$mutableblockpos)) {
                    Player player = pContext.getPlayer();
                    int j = level.getMaxBuildHeight();
                    if (player instanceof ServerPlayer && blockpos$mutableblockpos.getY() >= j) {
                        ((ServerPlayer)player).sendMessage((new TranslatableComponent("build.tooHigh", j - 1)).withStyle(ChatFormatting.RED), ChatType.GAME_INFO, Util.NIL_UUID);
                    }
                    break;
                }

                blockstate = level.getBlockState(blockpos$mutableblockpos);
                if (!blockstate.is(((ScaffoldingBlockItem) (Object) this).getBlock())) {
                    if (blockstate.canBeReplaced(pContext)) {
                        return BlockPlaceContext.at(pContext, blockpos$mutableblockpos, direction);
                    }
                    break;
                }

                blockpos$mutableblockpos.move(direction);
                if (direction.getAxis().isHorizontal()) {
                    ++i;
                }
            }

            return null;
        }
    }

}
