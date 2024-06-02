package rosatech.integration.thaumcraft.metatileentites.multiblock.electric

import gregtech.api.GTValues
import gregtech.api.GregTechAPI
import gregtech.api.block.IHeatingCoilBlockStats
import gregtech.api.capability.IHeatingCoil
import gregtech.api.capability.impl.HeatingCoilRecipeLogic
import gregtech.api.metatileentity.MetaTileEntity
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity
import gregtech.api.metatileentity.multiblock.IMultiblockPart
import gregtech.api.metatileentity.multiblock.MultiblockAbility
import gregtech.api.metatileentity.multiblock.MultiblockDisplayText
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController
import gregtech.api.pattern.BlockPattern
import gregtech.api.pattern.FactoryBlockPattern
import gregtech.api.pattern.MultiblockShapeInfo
import gregtech.api.pattern.PatternMatchContext
import gregtech.api.recipes.Recipe
import gregtech.api.util.GTUtility
import gregtech.api.util.TextComponentUtil
import gregtech.api.util.TextFormattingUtil
import gregtech.client.renderer.ICubeRenderer
import gregtech.common.ConfigHolder
import gregtech.common.blocks.BlockWireCoil
import gregtech.common.metatileentities.MetaTileEntities
import net.minecraft.block.state.IBlockState
import net.minecraft.client.resources.I18n
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentUtils
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.World
import rosatech.client.renderer.textures.RosaTextures
import rosatech.common.blocks.RosaMetaBlocks
import rosatech.common.metatileentities.RosaMetaTileEntities
import rosatech.common.metatileentities.RosaMultiblockAbility
import rosatech.integration.thaumcraft.block.BlockLargeMultiblockCasingThaum
import rosatech.integration.thaumcraft.metatileentites.RosaTCMetaTileEntities
import rosatech.integration.thaumcraft.metatileentites.multiblock.ThaumcraftMultiblockAbility
import rosatech.integration.thaumcraft.recipe.ESSENTIA_SMELTERY_RECIPES
import rosatech.integration.thaumcraft.recipe.logic.EssentiaSmelteryRecipeLogic
import kotlin.math.max

class MetaTileEntityIndustrialEssentiaSmeltery(metaTileEntityId: ResourceLocation) : RecipeMapMultiblockController(metaTileEntityId,
    ESSENTIA_SMELTERY_RECIPES
), IHeatingCoil {

    init {
        this.recipeMapWorkable = EssentiaSmelteryRecipeLogic(this)
    }

    private var blastFurnaceTemperature: Int = 0

    override fun createStructurePattern(): BlockPattern {
        return FactoryBlockPattern.start()
            .aisle("XXXXX", "CCCCC", "CCCCC", "CCCCC", "XXXXX")
            .aisle("XXXXX", "C###C", "C###C", "C###C", "XXXXX")
            .aisle("XXXXX", "C###C", "C###C", "C###C", "XXMXX")
            .aisle("XXXXX", "C###C", "C###C", "C###C", "XXXXX")
            .aisle("XXSXX", "CCCCC", "CCCCC", "CCCCC", "XXXXX")
            .where('S', selfPredicate())
            .where('X', states(getCasingState())
                .setMinGlobalLimited(40)
                .or(autoAbilities(true, true, true, false, false, false, false))
                .or(abilities(ThaumcraftMultiblockAbility.EXPORT_ESSENTIA))
            )
            .where('#', air())
            .where('M', abilities(MultiblockAbility.MUFFLER_HATCH))
            .where('C', heatingCoils())
            .build()
    }

    override fun getMatchingShapes(): List<MultiblockShapeInfo> {
        val shapeInfo: MutableList<MultiblockShapeInfo> = mutableListOf()
        val builder = MultiblockShapeInfo.builder()
            .aisle("XEEXX", "CCCCC", "CCCCC", "CCCCC", "XXXXX")
            .aisle("XXXXX", "C###C", "C###C", "C###C", "XXXXX")
            .aisle("XXXXX", "C###C", "C###C", "C###C", "XXHXX")
            .aisle("XXXXX", "C###C", "C###C", "C###C", "XXXXX")
            .aisle("IXSMO", "CCCCC", "CCCCC", "CCCCC", "XXXXX")
            .where('X', RosaMetaBlocks.LARGE_MULTIBLOCK_CASING_THAUM.getState(BlockLargeMultiblockCasingThaum.CasingType.ARCANE_SEALED_THAUMIUM_CASING))
            .where('S', RosaTCMetaTileEntities.INDUSTRIAL_ESSENTIA_SMELTERY, EnumFacing.SOUTH)
            .where('#', Blocks.AIR.defaultState)
            .where('E', MetaTileEntities.ENERGY_INPUT_HATCH[GTValues.MV], EnumFacing.NORTH)
            .where('I', MetaTileEntities.ITEM_IMPORT_BUS[GTValues.LV], EnumFacing.SOUTH)
            .where('O', RosaTCMetaTileEntities.ESSENTIA_OUTPUT_HATCH[GTValues.MV], EnumFacing.SOUTH)
            .where('H', MetaTileEntities.MUFFLER_HATCH[GTValues.LV], EnumFacing.UP)
            .where('M', {
                if (ConfigHolder.machines.enableMaintenance) MetaTileEntities.MAINTENANCE_HATCH
                else RosaMetaBlocks.LARGE_MULTIBLOCK_CASING_THAUM.getState(BlockLargeMultiblockCasingThaum.CasingType.ARCANE_SEALED_THAUMIUM_CASING)
            }, EnumFacing.SOUTH)

        GregTechAPI.HEATING_COILS.entries
            .sortedBy { it.value.tier }
            .forEach { (key, value) -> shapeInfo.add(builder.where('C', key).build())}


        return shapeInfo
    }

    protected fun getCasingState(): IBlockState = RosaMetaBlocks.LARGE_MULTIBLOCK_CASING_THAUM.getState(
        BlockLargeMultiblockCasingThaum.CasingType.ARCANE_SEALED_THAUMIUM_CASING
    )

    override fun getBaseTexture(part: IMultiblockPart?): ICubeRenderer = RosaTextures.ARCANE_SEALED_CASING

    override fun createMetaTileEntity(tileEntity: IGregTechTileEntity): MetaTileEntity {
        return MetaTileEntityIndustrialEssentiaSmeltery(metaTileEntityId)
    }

    override fun formStructure(context: PatternMatchContext) {
        super.formStructure(context)
        val type: Any = context.get("CoilType")
        if (type is IHeatingCoilBlockStats) {
            this.blastFurnaceTemperature = type.coilTemperature
        } else {
            this.blastFurnaceTemperature = BlockWireCoil.CoilType.CUPRONICKEL.coilTemperature
        }

        this.blastFurnaceTemperature += 250 *
                max(0, GTUtility.getFloorTierByVoltage(getEnergyContainer().getInputVoltage()) - GTValues.HV);
    }

    override fun addDisplayText(textList: List<ITextComponent>) {
        MultiblockDisplayText.builder(textList, isStructureFormed)
            .setWorkingStatus(recipeMapWorkable.isWorkingEnabled, recipeMapWorkable.isActive)
            .addEnergyUsageLine(energyContainer)
            .addEnergyTierLine(GTUtility.getTierByVoltage(recipeMapWorkable.maxVoltage).toInt())
            .addCustom { tl ->
                if (isStructureFormed) {
                    val heatString = TextComponentUtil.stringWithColor(TextFormatting.RED,
                        TextFormattingUtil.formatNumbers(blastFurnaceTemperature) + "K"
                    )

                    tl.add(TextComponentUtil.translationWithColor(
                        TextFormatting.GRAY,
                        "gregtech.multiblock.blast_furnace.max_temperature",
                        heatString
                    ))
                }
            }
            .addParallelsLine(recipeMapWorkable.parallelLimit)
            .addWorkingStatusLine()
            .addProgressLine(recipeMapWorkable.progressPercent)
    }

    override fun addInformation(stack: ItemStack, world: World?, tooltip: MutableList<String>, advanced: Boolean) {
        super.addInformation(stack, world, tooltip, advanced)
        tooltip.add(I18n.format("rosatech.machine.industrial_essentia_smeltery.coil"))
        tooltip.add(I18n.format("rosatech.machine.industrial_essentia_smeltery.tc_smeltery"))
        tooltip.add(I18n.format("rosatech.machine.industrial_essentia_smeltery.energy_minimum"))
        tooltip.add(I18n.format("rosatech.machine.industrial_essentia_smeltery.energy_bonus"))
    }

    override fun invalidateStructure() {
        super.invalidateStructure()
        this.blastFurnaceTemperature = 0
    }

    override fun getCurrentTemperature(): Int = blastFurnaceTemperature

}