package wraith.fwaystones.mixin;

import net.minecraft.resource.ResourceManager;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagGroupLoader;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wraith.fwaystones.FabricWaystones;
import wraith.fwaystones.registry.BlockRegistry;
import wraith.fwaystones.util.Config;
import wraith.fwaystones.util.Utils;

import java.util.Map;

@Mixin(TagGroupLoader.class)
public class TagGroupLoaderMixin {

    @Shadow
    @Final
    private String dataType;

    @Inject(method = "loadTags", at = @At("TAIL"))
    private void loadWaystoneTags(ResourceManager manager, CallbackInfoReturnable<Map<Identifier, Tag.Builder>> cir) {
        if (!dataType.equals("tags/blocks")) {
            return;
        }
        var miningLevel = Config.getInstance().getMiningLevel();
        var miningLevelTag = new Identifier(switch (miningLevel) {
            case 1 -> "minecraft:needs_stone_tool";
            case 2 -> "minecraft:needs_iron_tool";
            case 3 -> "minecraft:needs_diamond_tool";
            default -> "fabric:needs_tool_level_" + miningLevel;
        });
        var map = cir.getReturnValue();
        var miningLevelBuilder = map.computeIfAbsent(miningLevelTag, k -> new Tag.Builder());
        var minableBuilder = map.computeIfAbsent(new Identifier("mineable/pickaxe"), k -> new Tag.Builder());
        BlockRegistry.WAYSTONE_BLOCKS.forEach((id, block) -> {
            var waystoneId = Utils.ID(id);
            miningLevelBuilder.add(waystoneId, FabricWaystones.MOD_ID);
            minableBuilder.add(waystoneId, FabricWaystones.MOD_ID);
        });
    }

}
