package rosatech.common.metatileentities

import net.minecraftforge.fml.common.Loader
import rosatech.integration.thaumcraft.metatileentites.multiblock.ThaumcraftMultiblockAbility

class RosaMultiblockAbility {

    companion object {
        fun preInit() {

            if (Loader.isModLoaded("thaumcraft")) {
                ThaumcraftMultiblockAbility.preInit()
            }

        }
    }
}