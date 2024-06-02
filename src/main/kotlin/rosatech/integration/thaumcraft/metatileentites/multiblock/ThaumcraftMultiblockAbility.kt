package rosatech.integration.thaumcraft.metatileentites.multiblock

import gregtech.api.metatileentity.multiblock.MultiblockAbility
import thaumcraft.api.aspects.IAspectContainer


class ThaumcraftMultiblockAbility<T> {

    companion object {
        lateinit var IMPORT_ESSENTIA: MultiblockAbility<IAspectContainer>

        lateinit var EXPORT_ESSENTIA: MultiblockAbility<IAspectContainer>

        fun preInit() {
            IMPORT_ESSENTIA = MultiblockAbility("export_essentia")
            EXPORT_ESSENTIA = MultiblockAbility("export_essentia")
        }


    }

}