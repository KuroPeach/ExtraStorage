package edivad.extrastorage.setup;

import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.render.BakedModelOverrideRegistry;
import edivad.edivadlib.setup.UpdateChecker;
import edivad.extrastorage.Main;
import edivad.extrastorage.blocks.CrafterTier;
import edivad.extrastorage.client.screen.*;
import edivad.extrastorage.container.AdvancedCrafterContainerMenu;
import edivad.extrastorage.items.storage.fluid.FluidStorageType;
import edivad.extrastorage.items.storage.item.ItemStorageType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientSetup {
    private static final BakedModelOverrideRegistry BAKED_MODEL_OVERRIDE_REGISTRY = new BakedModelOverrideRegistry();

    private ClientSetup() {
    }

    @SubscribeEvent
    public static void init(FMLClientSetupEvent event) {
        //Version checker
        MinecraftForge.EVENT_BUS.register(new UpdateChecker(Main.MODID));

        //Special render & GUI
        for (var tier : CrafterTier.values()) {
            MenuScreens.register(Registration.CRAFTER_CONTAINER.get(tier).get(), AdvancedCrafterScreen::new);
        }

        MenuScreens.register(Registration.ADVANCED_EXPORTER_CONTAINER.get(), AdvancedExporterScreen::new);
        MenuScreens.register(Registration.ADVANCED_IMPORTER_CONTAINER.get(), AdvancedImporterScreen::new);

        for (var type : ItemStorageType.values())
            MenuScreens.register(Registration.ITEM_STORAGE_CONTAINER.get(type).get(), AdvancedStorageBlockScreen::new);
        for (var type : FluidStorageType.values())
            MenuScreens.register(Registration.FLUID_STORAGE_CONTAINER.get(type).get(), AdvancedFluidStorageBlockScreen::new);

        API.instance().addPatternRenderHandler(pattern -> {
            var container = Minecraft.getInstance().player.containerMenu;
            if (container instanceof AdvancedCrafterContainerMenu actualContainer) {
                int slots = actualContainer.getBlockEntity().getTier().getSlots();
                for (int i = 0; i < slots; i++)
                    if (container.getSlot(i).getItem() == pattern)
                        return true;
            }
            return false;
        });
    }

    @SubscribeEvent
    public static void onModelBake(ModelEvent.BakingCompleted e) {
        for (var id : e.getModels().keySet()) {
            var factory = BAKED_MODEL_OVERRIDE_REGISTRY.get(new ResourceLocation(id.getNamespace(), id.getPath()));
            if (factory != null) {
                e.getModels().put(id, factory.create(e.getModels().get(id), e.getModels()));
            }
        }
    }
}