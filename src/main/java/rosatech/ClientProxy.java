package rosatech;

import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import rosatech.RosaTech;
import rosatech.CommonProxy;
import rosatech.common.blocks.RosaBlocks;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = RosaTech.MODID, value = Side.CLIENT)
public class ClientProxy extends CommonProxy {

    public void preLoad() {
        super.preLoad();
    }

    public void load() {
        super.load();
    }

    @SubscribeEvent
    public static void registerModels(@NotNull ModelRegistryEvent event) {
        RosaBlocks.registerItemModels();
    }
}
