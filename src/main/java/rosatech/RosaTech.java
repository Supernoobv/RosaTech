package rosatech;

import gregtech.GTInternalTags;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import rosatech.client.renderer.textures.RosaTextures;
import rosatech.common.blocks.RosaBlocks;
import rosatech.common.metatileentities.RosaMetaTileEntities;

@Mod(modid = RosaTech.MODID, version = Tags.VERSION, name = RosaTech.NAME, dependencies = GTInternalTags.DEP_VERSION_STRING, acceptedMinecraftVersions = "[1.12.2]")
public class RosaTech {

    public static final String NAME = "RosaTech";
    public static final String MODID = "rosatech";


    @SidedProxy(modId = MODID, clientSide = "rosatech.ClientProxy", serverSide = "rosatech.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance(RosaTech.MODID)
    public static RosaTech instance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preLoad();

        RosaTextures.preInit();

        RosaMetaTileEntities.init();
        RosaBlocks.init();
    }
    @EventHandler
    // load "Do your mod setup. Build whatever data structures you care about." (Remove if not needed)
    public void init(FMLInitializationEvent event) {
        proxy.load();
    }


}
