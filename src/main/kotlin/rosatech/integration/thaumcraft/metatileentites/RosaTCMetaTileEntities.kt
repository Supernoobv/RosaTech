package rosatech.integration.thaumcraft.metatileentites

import gregtech.api.GTValues
import gregtech.api.GregTechAPI
import gregtech.client.renderer.texture.Textures
import gregtech.common.metatileentities.MetaTileEntities.registerMetaTileEntity
import net.minecraftforge.fml.common.Loader
import rosatech.RosaTech
import rosatech.integration.thaumcraft.metatileentites.electric.generator.MetaTileEntityEssentiaGenerator
import rosatech.integration.thaumcraft.metatileentites.multiblock.electric.MetaTileEntityIndustrialEssentiaSmeltery
import rosatech.integration.thaumcraft.metatileentites.multiblock.part.MetaTileEntityEssentiaHatch

class RosaTCMetaTileEntities {

    companion object {

        val ESSENTIA_GENERATOR: Array<MetaTileEntityEssentiaGenerator?> = arrayOfNulls(GTValues.EV)

        val ESSENTIA_INPUT_HATCH: Array<MetaTileEntityEssentiaHatch?> = arrayOfNulls(GTValues.V.size)
        val ESSENTIA_OUTPUT_HATCH: Array<MetaTileEntityEssentiaHatch?> = arrayOfNulls(GTValues.V.size)

        var INDUSTRIAL_ESSENTIA_SMELTERY: MetaTileEntityIndustrialEssentiaSmeltery? = null

        fun init() {
            val endPos = if (GregTechAPI.isHighTier()) GTValues.V.size else GTValues.UV + 2

            if (Loader.isModLoaded("thaumcraft")) {
                ESSENTIA_GENERATOR[0] = registerMetaTileEntity(20000, MetaTileEntityEssentiaGenerator(RosaTech.ID("essentia_generator.lv"), Textures.GAS_TURBINE_OVERLAY, 1))
                ESSENTIA_GENERATOR[1] = registerMetaTileEntity(20001, MetaTileEntityEssentiaGenerator(RosaTech.ID("essentia_generator.mv"), Textures.GAS_TURBINE_OVERLAY, 2))
                ESSENTIA_GENERATOR[2] = registerMetaTileEntity(20002, MetaTileEntityEssentiaGenerator(RosaTech.ID("essentia_generator.hv"), Textures.GAS_TURBINE_OVERLAY, 3))
                ESSENTIA_GENERATOR[3] = registerMetaTileEntity(20003, MetaTileEntityEssentiaGenerator(RosaTech.ID("essentia_generator.ev"), Textures.GAS_TURBINE_OVERLAY, 4))

                // Essentia Input/Output Hatches
                // IDS 20004 - 20034
                for (i in 0 until endPos) {
                    val voltageName: String = GTValues.VN[i].lowercase()
                    ESSENTIA_INPUT_HATCH[i] = registerMetaTileEntity(20004 + i, MetaTileEntityEssentiaHatch(RosaTech.ID("essentia.input.hatch.$voltageName"), i, false))
                    ESSENTIA_OUTPUT_HATCH[i] = registerMetaTileEntity(20019 + i, MetaTileEntityEssentiaHatch(RosaTech.ID("essentia.output.hatch.$voltageName"), i, true))
                }

                INDUSTRIAL_ESSENTIA_SMELTERY = registerMetaTileEntity(20035, MetaTileEntityIndustrialEssentiaSmeltery(RosaTech.ID("industrial_essentia_smeltery")))
            }
        }
    }


}