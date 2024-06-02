package rosatech.common.metatileentities

import gregtech.api.GTValues
import gregtech.api.GregTechAPI
import gregtech.client.renderer.texture.Textures
import gregtech.common.metatileentities.MetaTileEntities.registerMetaTileEntity
import net.minecraftforge.fml.common.Loader
import rosatech.RosaTech
import rosatech.common.metatileentities.multiblockpart.MetaTileEntityDEnergyHatch
import rosatech.integration.thaumcraft.metatileentites.RosaTCMetaTileEntities
import rosatech.integration.thaumcraft.metatileentites.electric.generator.MetaTileEntityEssentiaGenerator
import rosatech.integration.thaumcraft.metatileentites.multiblock.electric.MetaTileEntityIndustrialEssentiaSmeltery
import rosatech.integration.thaumcraft.metatileentites.multiblock.part.MetaTileEntityEssentiaHatch

class RosaMetaTileEntities {

    companion object {
        val DIMENSIONAL_ENERGY_INPUT_HATCH: Array<MetaTileEntityDEnergyHatch?> = arrayOfNulls(GTValues.V.size)
        val DIMENSIONAL_ENERGY_INPUT_HATCH_4A: Array<MetaTileEntityDEnergyHatch?> = arrayOfNulls(GTValues.V.size)
        val DIMENSIONAL_ENERGY_INPUT_HATCH_16A: Array<MetaTileEntityDEnergyHatch?> = arrayOfNulls(GTValues.V.size)
        val DIMENSIONAL_ENERGY_INPUT_HATCH_64A: Array<MetaTileEntityDEnergyHatch?> = arrayOfNulls(GTValues.V.size)
        val DIMENSIONAL_ENERGY_INPUT_HATCH_256A: Array<MetaTileEntityDEnergyHatch?> = arrayOfNulls(GTValues.V.size)
        val DIMENSIONAL_ENERGY_INPUT_HATCH_1024A: Array<MetaTileEntityDEnergyHatch?> = arrayOfNulls(GTValues.V.size)

        val DIMENSIONAL_ENERGY_OUTPUT_HATCH: Array<MetaTileEntityDEnergyHatch?> = arrayOfNulls(GTValues.V.size)
        val DIMENSIONAL_ENERGY_OUTPUT_HATCH_4A: Array<MetaTileEntityDEnergyHatch?> = arrayOfNulls(GTValues.V.size)
        val DIMENSIONAL_ENERGY_OUTPUT_HATCH_16A: Array<MetaTileEntityDEnergyHatch?> = arrayOfNulls(GTValues.V.size)
        val DIMENSIONAL_ENERGY_OUTPUT_HATCH_64A: Array<MetaTileEntityDEnergyHatch?> = arrayOfNulls(GTValues.V.size)
        val DIMENSIONAL_ENERGY_OUTPUT_HATCH_256A: Array<MetaTileEntityDEnergyHatch?> = arrayOfNulls(GTValues.V.size)
        val DIMENSIONAL_ENERGY_OUTPUT_HATCH_1024A: Array<MetaTileEntityDEnergyHatch?> = arrayOfNulls(GTValues.V.size)



        fun init() {

            // Dimensional Energy Input/Output Hatches
            // IDS 19300 - 19480
            val endPos = if (GregTechAPI.isHighTier()) GTValues.V.size else GTValues.UV + 2
            for (i in 0 until endPos) {
                val voltageName: String = GTValues.VN[i].lowercase()
                DIMENSIONAL_ENERGY_INPUT_HATCH[i] = registerMetaTileEntity(19300 + i, MetaTileEntityDEnergyHatch(RosaTech.ID("dimensional_energy_hatch.input.$voltageName"), i, 2, false))
                DIMENSIONAL_ENERGY_OUTPUT_HATCH[i] = registerMetaTileEntity(19315 + i, MetaTileEntityDEnergyHatch(RosaTech.ID("dimensional_energy_hatch.output.$voltageName"), i, 2, true))

                if (i >= GTValues.IV) {
                    DIMENSIONAL_ENERGY_INPUT_HATCH_4A[i] = registerMetaTileEntity(19330 + i, MetaTileEntityDEnergyHatch(RosaTech.ID("dimensional_energy_hatch.input_4a.$voltageName"), i, 4, false))
                    DIMENSIONAL_ENERGY_OUTPUT_HATCH_4A[i] = registerMetaTileEntity(19345 + i, MetaTileEntityDEnergyHatch(RosaTech.ID("dimensional_energy_hatch.output_4a.$voltageName"), i, 4, true))

                    DIMENSIONAL_ENERGY_INPUT_HATCH_16A[i] = registerMetaTileEntity(19360 + i, MetaTileEntityDEnergyHatch(RosaTech.ID("dimensional_energy_hatch.input_16a.$voltageName"), i, 16, false))
                    DIMENSIONAL_ENERGY_OUTPUT_HATCH_16A[i] = registerMetaTileEntity(19375 + i, MetaTileEntityDEnergyHatch(RosaTech.ID("dimensional_energy_hatch.output_16a.$voltageName"), i, 16, true))

                    DIMENSIONAL_ENERGY_INPUT_HATCH_64A[i] = registerMetaTileEntity(19390 + i, MetaTileEntityDEnergyHatch(RosaTech.ID("dimensional_energy_hatch.input_64a.$voltageName"), i, 64, false))
                    DIMENSIONAL_ENERGY_OUTPUT_HATCH_64A[i] = registerMetaTileEntity(19405 + i, MetaTileEntityDEnergyHatch(RosaTech.ID("dimensional_energy_hatch.output_64a.$voltageName"), i, 64, true))

                    DIMENSIONAL_ENERGY_INPUT_HATCH_256A[i] = registerMetaTileEntity(19420 + i, MetaTileEntityDEnergyHatch(RosaTech.ID("dimensional_energy_hatch.input_256a.$voltageName"), i, 256, false))
                    DIMENSIONAL_ENERGY_OUTPUT_HATCH_256A[i] = registerMetaTileEntity(19435 + i, MetaTileEntityDEnergyHatch(RosaTech.ID("dimensional_energy_hatch.output_256a.$voltageName"), i, 256, true))

                    DIMENSIONAL_ENERGY_INPUT_HATCH_1024A[i] = registerMetaTileEntity(19450 + i, MetaTileEntityDEnergyHatch(RosaTech.ID("dimensional_energy_hatch.input_256a.$voltageName"), i, 256, false))
                    DIMENSIONAL_ENERGY_OUTPUT_HATCH_1024A[i] = registerMetaTileEntity(19465 + i, MetaTileEntityDEnergyHatch(RosaTech.ID("dimensional_energy_hatch.output_256a.$voltageName"), i, 256, true))
                }
            }

            if (Loader.isModLoaded("thaumcraft")) {
                RosaTCMetaTileEntities.init()
            }
        }
    }
}
