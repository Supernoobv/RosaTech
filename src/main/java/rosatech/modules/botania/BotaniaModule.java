package rosatech.modules.botania;

import gregtech.api.modules.GregTechModule;
import gregtech.integration.IntegrationSubmodule;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import rosatech.RosaTech;
import rosatech.api.util.RosaMods;
import rosatech.modules.RosaModules;

@GregTechModule(
        moduleID = RosaModules.MODULE_BOTANIA,
        containerID = RosaTech.MODID,
        modDependencies = RosaMods.Names.BOTANIA,
        name = "RosaTech Botania Integration",
        description = "RosaTech Botania Integration")
public class BotaniaModule extends IntegrationSubmodule {


}
