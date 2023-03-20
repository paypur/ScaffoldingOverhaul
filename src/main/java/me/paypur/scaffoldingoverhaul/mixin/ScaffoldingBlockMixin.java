package me.paypur.scaffoldingoverhaul.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.ScaffoldingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ScaffoldingBlock.class)
public abstract class ScaffoldingBlockMixin {
    @Shadow
    @Final
    private static VoxelShape STABLE_SHAPE;

    @Shadow
    @Final
    private static VoxelShape UNSTABLE_SHAPE_BOTTOM;

    @Inject(method = "getCollisionShape", at = @At("RETURN"), cancellable = true)
    public void getStableShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext, CallbackInfoReturnable<VoxelShape> cir) {
        cir.setReturnValue(cir.getReturnValue() == UNSTABLE_SHAPE_BOTTOM ? STABLE_SHAPE : cir.getReturnValue());
    }
}
