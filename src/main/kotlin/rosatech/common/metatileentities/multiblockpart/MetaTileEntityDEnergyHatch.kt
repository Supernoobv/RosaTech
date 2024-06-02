package rosatech.common.metatileentities.multiblockpart

import codechicken.lib.raytracer.CuboidRayTraceResult
import codechicken.lib.render.CCRenderState
import codechicken.lib.render.pipeline.IVertexOperation
import codechicken.lib.vec.Matrix4
import gregtech.api.GTValues
import gregtech.api.capability.IEnergyContainer
import gregtech.api.capability.impl.EnergyContainerHandler
import gregtech.api.gui.GuiTextures
import gregtech.api.gui.ModularUI
import gregtech.api.gui.widgets.AdvancedTextWidget
import gregtech.api.metatileentity.MetaTileEntity
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart
import gregtech.api.metatileentity.multiblock.MultiblockAbility
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart
import net.minecraft.client.resources.I18n
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.NonNullList
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.World
import org.jetbrains.annotations.NotNull
import rosatech.client.renderer.textures.RosaTextures
import rosatech.common.metatileentities.RosaMetaTileEntities
import rosatech.common.world.data.DimensionalEnergyNet
import rosatech.common.world.data.DimensionalNetData
import java.util.UUID
import kotlin.math.min

open class MetaTileEntityDEnergyHatch(
    metaTileEntityId: ResourceLocation,
    tier: Int,
    val amperage: Int,
    val isExport: Boolean
) : MetaTileEntityMultiblockPart(metaTileEntityId, tier), IMultiblockAbilityPart<IEnergyContainer> {

    protected var network: DimensionalEnergyNet? = null
    protected var owner: UUID? = null

    protected var energyContainer: IEnergyContainer

    init {
        if (isExport) {
            this.energyContainer =
                EnergyContainerHandler.emitterContainer(
                    this, GTValues.V[tier] * 64 * amperage, GTValues.V[tier], amperage.toLong()
                )
        } else {
            this.energyContainer =
                EnergyContainerHandler.receiverContainer(
                    this, GTValues.V[tier] * 16 * amperage, GTValues.V[tier], amperage.toLong()
                )
        }
    }

    override fun update() {
        super.update()
        if (world.isRemote) return

        if (offsetTimer % 2 == 0L && network != null) {
            if (isExport && energyContainer.energyStored > 0) {
                val filled: Long = network!!.fill(min(energyContainer.energyCapacity, energyContainer.energyStored))
                this.energyContainer.removeEnergy(filled)
            } else if (!isExport && energyContainer.energyStored < energyContainer.energyCapacity){
                val drained: Long = network!!.drain(energyContainer.energyCapacity - energyContainer.energyStored)
                this.energyContainer.addEnergy(drained)
            }
        }
    }

    override fun createMetaTileEntity(tileEntity: IGregTechTileEntity): MetaTileEntity {
        return MetaTileEntityDEnergyHatch(metaTileEntityId, tier, amperage, isExport)
    }

    override fun renderMetaTileEntity(
        renderState: CCRenderState,
        translation: Matrix4,
        pipeline: Array<out IVertexOperation>
    ) {
        super.renderMetaTileEntity(renderState, translation, pipeline)
        if (shouldRenderOverlay()) {
            getOverlay().renderSided(frontFacing, renderState, translation, pipeline)
        }
    }

    override fun onScrewdriverClick(
        playerIn: EntityPlayer,
        hand: EnumHand,
        facing: EnumFacing,
        hitResult: CuboidRayTraceResult
    ): Boolean {
        setNetworkOwner(playerIn.uniqueID)

        if (!world.isRemote) {
            playerIn.sendStatusMessage(
                TextComponentTranslation("rosatech.dimensional_hatch.set_owner"), true
            )
        }

        return true
    }

    @NotNull
    fun getOverlay(): SimpleOverlayRenderer {
        when (amperage) {
            4 -> return RosaTextures.DIMENSIONAL_ENERGY_4A
            16 -> return RosaTextures.DIMENSIONAL_ENERGY_16A
            64 -> return RosaTextures.DIMENSIONAL_ENERGY_64A
            256 -> return RosaTextures.DIMENSIONAL_ENERGY_256A
            1024 -> return RosaTextures.DIMENSIONAL_ENERGY_1024A
            else -> return return RosaTextures.DIMENSIONAL_ENERGY_MULTI
        }
    }

    fun setNetworkOwner(player: UUID) {
        network = DimensionalNetData.getNet(player, "energy", DimensionalEnergyNet::class.qualifiedName!!) as DimensionalEnergyNet
        owner = player
    }

    fun getCapacityText(): (MutableList<ITextComponent>) -> Unit {
        return { list: MutableList<ITextComponent> ->
            list.add(TextComponentString(I18n.format("rosatech.universal.dimensional_energy_capacity", network?.getCapacity())))
        }
    }

    fun getStoredText(): (MutableList<ITextComponent>) -> Unit {
        return { list: MutableList<ITextComponent> ->
            list.add(TextComponentString(I18n.format("rosatech.universal.dimensional_energy_stored", network?.getStored())))
        }
    }

    override fun addInformation(
        stack: ItemStack,
        world: World?,
        tooltip: MutableList<String>,
        advanced: Boolean
    ) {
        val tierName: String = GTValues.VNF[tier]

        if (isExport) {
            tooltip.add(I18n.format("gregtech.universal.tooltip.voltage_out", energyContainer.outputVoltage, tierName))
            tooltip.add(I18n.format("gregtech.universal.tooltip.amperage_out_till", energyContainer.outputAmperage))
        } else {
            tooltip.add(I18n.format("gregtech.universal.tooltip.voltage_in", energyContainer.inputVoltage, tierName))
            tooltip.add(I18n.format("gregtech.universal.tooltip.amperage_in_till", energyContainer.inputAmperage))
        }
        tooltip.add(I18n.format("gregtech.universal.tooltip.energy_storage_capacity", energyContainer.energyCapacity))
        tooltip.add(I18n.format("rosatech.machine.dimensional_energy_hatch.tooltip"))
        tooltip.add(I18n.format("gregtech.universal.enabled"))
    }

    override fun addToolUsages(
        stack: ItemStack,
        world: World?,
        tooltip: MutableList<String>,
        advanced: Boolean
    ) {
        tooltip.add(I18n.format("gregtech.tool_action.screwdriver.access_covers"))
        tooltip.add(I18n.format("gregtech.tool_action.wrench.set_facing"))
        super.addToolUsages(stack, world, tooltip, advanced)
    }

    override fun canRenderFrontFaceX(): Boolean {
        return isExport
    }

    override fun writeToNBT(data: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(data)
        if (this.owner != null) data.setUniqueId("owner", owner!!)
        return data
    }

    override fun readFromNBT(data: NBTTagCompound) {
        super.readFromNBT(data)
        if (data.hasUniqueId("owner")) {
            this.owner = data.getUniqueId("owner")
            setNetworkOwner(this.owner!!)
        }
    }

    override fun getSubItems(creativeTab: CreativeTabs, subItems: NonNullList<ItemStack>) {
        if (this == RosaMetaTileEntities.DIMENSIONAL_ENERGY_INPUT_HATCH[0]) {
            for (hatch in RosaMetaTileEntities.DIMENSIONAL_ENERGY_INPUT_HATCH) {
                if (hatch != null) subItems.add(hatch.getStackForm())
            }
            for (hatch in RosaMetaTileEntities.DIMENSIONAL_ENERGY_OUTPUT_HATCH) {
                if (hatch != null) subItems.add(hatch.getStackForm())
            }
            for (hatch in RosaMetaTileEntities.DIMENSIONAL_ENERGY_INPUT_HATCH_4A) {
                if (hatch != null) subItems.add(hatch.getStackForm())
            }
            for (hatch in RosaMetaTileEntities.DIMENSIONAL_ENERGY_OUTPUT_HATCH_4A) {
                if (hatch != null) subItems.add(hatch.getStackForm())
            }
            for (hatch in RosaMetaTileEntities.DIMENSIONAL_ENERGY_INPUT_HATCH_16A) {
                if (hatch != null) subItems.add(hatch.getStackForm())
            }
            for (hatch in RosaMetaTileEntities.DIMENSIONAL_ENERGY_OUTPUT_HATCH_16A) {
                if (hatch != null) subItems.add(hatch.getStackForm())
            }
            for (hatch in RosaMetaTileEntities.DIMENSIONAL_ENERGY_INPUT_HATCH_64A) {
                if (hatch != null) subItems.add(hatch.getStackForm())
            }
            for (hatch in RosaMetaTileEntities.DIMENSIONAL_ENERGY_OUTPUT_HATCH_64A) {
                if (hatch != null) subItems.add(hatch.getStackForm())
            }
            for (hatch in RosaMetaTileEntities.DIMENSIONAL_ENERGY_INPUT_HATCH_256A) {
                if (hatch != null) subItems.add(hatch.getStackForm())
            }
            for (hatch in RosaMetaTileEntities.DIMENSIONAL_ENERGY_OUTPUT_HATCH_256A) {
                if (hatch != null) subItems.add(hatch.getStackForm())
            }
            for (hatch in RosaMetaTileEntities.DIMENSIONAL_ENERGY_INPUT_HATCH_1024A) {
                if (hatch != null) subItems.add(hatch.getStackForm())
            }
            for (hatch in RosaMetaTileEntities.DIMENSIONAL_ENERGY_OUTPUT_HATCH_1024A) {
                if (hatch != null) subItems.add(hatch.getStackForm())
            }
        }
    }

    fun addDescriptorTooltip(
        stack: ItemStack,
        world: World?,
        tooltip: MutableList<String>,
        advanced: Boolean
    ) {
        if (isExport) {
            if (amperage > 2) {
                tooltip.add(I18n.format("gregtech.machine.energy_hatch.output_hi_amp.tooltip"))
            } else {
                tooltip.add(I18n.format("gregtech.machine.energy_hatch.output.tooltip"))
            }
        } else {
            if (amperage > 2) {
                tooltip.add(I18n.format("gregtech.machine.energy_hatch.input_hi_amp.tooltip"))
            } else {
                tooltip.add(I18n.format("gregtech.machine.energy_hatch.input.tooltip"))
            }
        }
    }

    override fun createUI(entityPlayer: EntityPlayer): ModularUI {
        val builder: ModularUI.Builder = ModularUI.defaultBuilder()
        return builder
            .image(7, 16, 132, 55, GuiTextures.DISPLAY)
            .label(6, 6, metaFullName)
            .widget(AdvancedTextWidget(11, 20, getCapacityText(), 0xFFFFFF))
            .widget(AdvancedTextWidget(11, 30, getStoredText(), 0xFFFFFF))
            .bindPlayerInventory(entityPlayer.inventory)
            .build(holder, entityPlayer)
    }

    override fun getAbility(): MultiblockAbility<IEnergyContainer> {
        return if (isExport) MultiblockAbility.OUTPUT_ENERGY else MultiblockAbility.INPUT_ENERGY
    }

    override fun registerAbilities(abilityList: MutableList<IEnergyContainer>) {
        abilityList.add(energyContainer)
    }
}
