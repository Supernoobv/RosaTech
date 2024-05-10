package rosatech.common.metatileentities

import gregtech.api.GTValues
import gregtech.api.GregTechAPI
import gregtech.common.metatileentities.MetaTileEntities.registerMetaTileEntity
import rosatech.RosaTech
import rosatech.common.metatileentities.multiblockpart.MetaTileEntityDEnergyHatch

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
            // IDS 17074 - IDK
            val endPos = if (GregTechAPI.isHighTier()) GTValues.V.size else GTValues.UV + 2
            for (i in 0 until endPos) {
                val voltageName: String = GTValues.VN[i].lowercase()
                DIMENSIONAL_ENERGY_INPUT_HATCH[i] = registerMetaTileEntity(19300 + i, MetaTileEntityDEnergyHatch(RosaTech.ID("dimensional_energy_hatch.input.$voltageName"), i, 2, false))
                DIMENSIONAL_ENERGY_OUTPUT_HATCH[i] = registerMetaTileEntity(19315 + i, MetaTileEntityDEnergyHatch(RosaTech.ID("dimensional_energy_hatch.output.$voltageName"), i, 2, true))

                if (i >= GTValues.IV) {
                    DIMENSIONAL_ENERGY_INPUT_HATCH_4A[i] = registerMetaTileEntity(19325 + i, MetaTileEntityDEnergyHatch(RosaTech.ID("dimensional_energy_hatch.input_4a.$voltageName"), i, 4, false))
                    DIMENSIONAL_ENERGY_OUTPUT_HATCH_4A[i] = registerMetaTileEntity(19335 + i, MetaTileEntityDEnergyHatch(RosaTech.ID("dimensional_energy_hatch.output_4a.$voltageName"), i, 4, true))

                    DIMENSIONAL_ENERGY_INPUT_HATCH_16A[i] = registerMetaTileEntity(19345 + i, MetaTileEntityDEnergyHatch(RosaTech.ID("dimensional_energy_hatch.input_16a.$voltageName"), i, 16, false))
                    DIMENSIONAL_ENERGY_OUTPUT_HATCH_16A[i] = registerMetaTileEntity(19355 + i, MetaTileEntityDEnergyHatch(RosaTech.ID("dimensional_energy_hatch.output_16a.$voltageName"), i, 16, true))

                    DIMENSIONAL_ENERGY_INPUT_HATCH_64A[i] = registerMetaTileEntity(19365 + i, MetaTileEntityDEnergyHatch(RosaTech.ID("dimensional_energy_hatch.input_64a.$voltageName"), i, 64, false))
                    DIMENSIONAL_ENERGY_OUTPUT_HATCH_64A[i] = registerMetaTileEntity(19375 + i, MetaTileEntityDEnergyHatch(RosaTech.ID("dimensional_energy_hatch.output_64a.$voltageName"), i, 64, true))

                    DIMENSIONAL_ENERGY_INPUT_HATCH_256A[i] = registerMetaTileEntity(19385 + i, MetaTileEntityDEnergyHatch(RosaTech.ID("dimensional_energy_hatch.input_256a.$voltageName"), i, 256, false))
                    DIMENSIONAL_ENERGY_OUTPUT_HATCH_256A[i] = registerMetaTileEntity(19395 + i, MetaTileEntityDEnergyHatch(RosaTech.ID("dimensional_energy_hatch.output_256a.$voltageName"), i, 256, true))

                    DIMENSIONAL_ENERGY_INPUT_HATCH_1024A[i] = registerMetaTileEntity(19405 + i, MetaTileEntityDEnergyHatch(RosaTech.ID("dimensional_energy_hatch.input_256a.$voltageName"), i, 256, false))
                    DIMENSIONAL_ENERGY_OUTPUT_HATCH_1024A[i] = registerMetaTileEntity(19415 + i, MetaTileEntityDEnergyHatch(RosaTech.ID("dimensional_energy_hatch.output_256a.$voltageName"), i, 256, true))
                }
            }
        }
    }
}
