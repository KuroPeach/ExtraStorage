package edivad.extrastorage.compat;

import com.refinedmods.refinedstorage.apiimpl.network.node.CrafterNetworkNode;
import com.refinedmods.refinedstorage.blockentity.CrafterBlockEntity;
import edivad.extrastorage.Main;
import edivad.extrastorage.blockentity.AdvancedCrafterBlockEntity;
import edivad.extrastorage.nodes.AdvancedCrafterNetworkNode;
import edivad.extrastorage.tools.Translations;
import java.util.function.Function;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TOPIntegration implements IProbeInfoProvider, Function<ITheOneProbe, Void> {

    @Override
    public Void apply(ITheOneProbe probe)
    {
        probe.registerProvider(this);
        return null;
    }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo probeInfo, Player player, Level level, BlockState blockState, IProbeHitData data)
    {
        BlockEntity te = level.getBlockEntity(data.getPos());
        int patterns = 0, speed = 0, slots = 0;

        if(te instanceof AdvancedCrafterBlockEntity tile) {
            AdvancedCrafterNetworkNode node = tile.getNode();
            patterns = node.getPatterns().size();
            speed = node.getMaximumSuccessfulCraftingUpdates();
            slots = tile.getTier().getSlots();
            probeInfo.horizontal().text(Component.translatable(Translations.OCCUPIED_SPACE, String.valueOf(patterns), String.valueOf(slots)));

            String translation = node.getTierSpeed() != node.getMaximumSuccessfulCraftingUpdates() ? Translations.LIMITED_SPEED : Translations.CURRENT_SPEED;
            probeInfo.horizontal().text(Component.translatable(translation, String.valueOf(speed)));
        } else if (te instanceof CrafterBlockEntity tile) {
            CrafterNetworkNode node = tile.getNode();
            patterns = node.getPatterns().size();
            speed = node.getMaximumSuccessfulCraftingUpdates();
            slots = 9;
            probeInfo.horizontal().text(Component.translatable(Translations.OCCUPIED_SPACE, String.valueOf(patterns), String.valueOf(slots)));
            probeInfo.horizontal().text(Component.translatable(Translations.CURRENT_SPEED, String.valueOf(speed)));
        }
    }

    @Override
    public ResourceLocation getID()
    {
        return new ResourceLocation(Main.MODID, "default");
    }
}
