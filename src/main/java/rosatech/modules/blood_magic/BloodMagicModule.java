package rosatech.modules.blood_magic;

import gregtech.api.modules.GregTechModule;
import gregtech.integration.IntegrationSubmodule;
import rosatech.RosaTech;
import rosatech.api.util.RosaMods;
import rosatech.modules.RosaModules;

@GregTechModule(
        moduleID = RosaModules.MODULE_BLOOD_MAGIC,
        containerID = RosaTech.MODID,
        modDependencies = RosaMods.Names.BLOOD_MAGIC,
        name = "RosaTech Blood Magic Integration",
        description = "RosaTech Blood Magic Integration")
public class BloodMagicModule extends IntegrationSubmodule {

}
