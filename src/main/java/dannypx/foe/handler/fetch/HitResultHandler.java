package dannypx.foe.handler.fetch;

import dannypx.foe.handler.Handler;
import dannypx.foe.type.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class HitResultHandler extends Handler {
    private static HitResultHandler INSTANCE = new HitResultHandler();

    public static HitResultHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new HitResultHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private HitResult hitResult = null;
    private @Nullable BlockHitResult blockHitResult = null;
    private @Nullable EntityHitResult entityHitResult = null;

    private @Nullable ItemStack itemFrameItem = ItemStack.EMPTY;

    public @Nullable BlockHitResult getBlockHitResult() {
        return blockHitResult;
    }

    public @Nullable EntityHitResult getEntityHitResult() {
        return entityHitResult;
    }

    public @Nullable ItemStack getItemFrameItem() {
        return itemFrameItem;
    }
    //endregion

    //region Methods
    public void tick() {
        hitResult = minecraft.hitResult;

        if(hitResult instanceof BlockHitResult blockHit) this.blockHitResult = blockHit;
        else this.blockHitResult = null;

        if(hitResult instanceof EntityHitResult entityHit) this.entityHitResult = entityHit;
        else this.entityHitResult = null;
        
        this.checkEntity();
    }

    private void checkEntity() {
        if(this.entityHitResult != null && entityHitResult.getEntity() instanceof ItemFrame itemFrameEntity) {
            itemFrameItem = itemFrameEntity.getItem();
        } else {
            itemFrameItem = ItemStack.EMPTY;
        }
    }

    public MutableComponent getBlockFromHitResult() {
        if(getBlockHitResult() != null && minecraft.level != null) {
            BlockPos blockPos = getBlockHitResult().getBlockPos();
            Block block = minecraft.level.getBlockState(blockPos).getBlock();
            return block.getName();
        }
        return null;
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    protected Map<String, Pair<MutableComponent, MutableComponent>> _getFields() {
        return Map.of(
                "entityHitResult", Pair.of(getEntityHitResult() != null ? getEntityHitResult().getEntity().getName().copy() : Component.empty(), Component.empty()),
                "blockHitResult" , Pair.of(getBlockHitResult() != null ? getBlockFromHitResult() : Component.empty(), Component.empty())
        );
    }
    //endregion
}
