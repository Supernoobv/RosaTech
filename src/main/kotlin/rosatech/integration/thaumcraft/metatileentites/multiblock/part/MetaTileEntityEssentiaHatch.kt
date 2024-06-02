package rosatech.integration.thaumcraft.metatileentites.multiblock.part

import codechicken.lib.render.CCRenderState
import codechicken.lib.render.pipeline.IVertexOperation
import codechicken.lib.vec.Matrix4
import gregtech.api.gui.ModularUI
import gregtech.api.metatileentity.MetaTileEntity
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart
import gregtech.api.metatileentity.multiblock.MultiblockAbility
import gregtech.client.renderer.texture.Textures
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockNotifiablePart
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.PacketBuffer
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import rosatech.api.capability.RosaTechDataCodes
import rosatech.api.util.RosaThaumcraftUtils
import rosatech.client.renderer.textures.RosaTextures
import rosatech.integration.thaumcraft.metatileentites.multiblock.ThaumcraftMultiblockAbility
import thaumcraft.api.aspects.Aspect
import thaumcraft.api.aspects.AspectList
import thaumcraft.api.aspects.IAspectContainer
import thaumcraft.api.aspects.IEssentiaTransport
import kotlin.math.min

class MetaTileEntityEssentiaHatch(
    metaTileEntity: ResourceLocation,
    tier: Int,
    isExportHatch: Boolean
) : MetaTileEntityMultiblockNotifiablePart(metaTileEntity, tier, isExportHatch), IAspectContainer, IEssentiaTransport, IMultiblockAbilityPart<IAspectContainer> {

    lateinit var essentia: AspectList

    val ESSENTIA_CAPACITY = 100 shl tier

    init {
        this.initializeInventory()
    }

    override fun createMetaTileEntity(tileEntity: IGregTechTileEntity): MetaTileEntity {
        return MetaTileEntityEssentiaHatch(metaTileEntityId, tier, isExportHatch)
    }

    override fun initializeInventory() {
        super.initializeInventory()
        essentia = AspectList()
    }

    override fun createUI(player: EntityPlayer): ModularUI? = null

    override fun update() {
        super.update()
        if (!world.isRemote && this.offsetTimer.toInt() % 5 == 0) {
            if (isExportHatch) {
                RosaThaumcraftUtils.drain(this, world, pos, false)
            } else {
                RosaThaumcraftUtils.fill(this, world, pos, false)
            }

            if (this.offsetTimer.toInt() % 20 == 0) {
                writeCustomData(RosaTechDataCodes.UPDATE_ESSENTIA) { buf ->
                    val tag = NBTTagCompound()
                    essentia.writeToNBT(tag)

                    buf.writeCompoundTag(tag)
                }
            }
        }
    }

    override fun writeToNBT(data: NBTTagCompound): NBTTagCompound {
        val tagCompound = super.writeToNBT(data)
        essentia.writeToNBT(tagCompound, "essentia")

        return tagCompound
    }

    override fun readFromNBT(data: NBTTagCompound) {
        super.readFromNBT(data)
        essentia.readFromNBT(data.getCompoundTag("essentia"))
    }

    override fun renderMetaTileEntity(
        renderState: CCRenderState,
        translation: Matrix4,
        pipeline: Array<out IVertexOperation>
    ) {
        super.renderMetaTileEntity(renderState, translation, pipeline)
        if (shouldRenderOverlay()) {
            val renderer = if (isExportHatch) Textures.PIPE_OUT_OVERLAY else Textures.PIPE_IN_OVERLAY
            renderer.renderSided(frontFacing, renderState, translation, pipeline)
            val overlay = if (isExportHatch) RosaTextures.ESSENTIA_OUTPUT_OVERLAY else RosaTextures.ESSENTIA_INPUT_OVERLAY
            overlay.renderSided(frontFacing, renderState, translation, pipeline)
        }
    }

    override fun addInformation(stack: ItemStack, world: World?, tooltip: MutableList<String>, advanced: Boolean) {
        if (isExportHatch) {
            tooltip.add(I18n.format("rosatech.machine.essentia_hatch.output.tooltip"))
        } else {
            tooltip.add(I18n.format("rosatech.machine.essentia_hatch.input.tooltip"))
        }
        tooltip.add(I18n.format("rosatech.universal.tooltip.essentia_storage_capacity", ESSENTIA_CAPACITY))
        tooltip.add(I18n.format("gregtech.universal.enabled"));
    }

    override fun addToolUsages(stack: ItemStack, world: World?, tooltip: MutableList<String>, advanced: Boolean) {
        tooltip.add(I18n.format("gregtech.tool_action.screwdriver.access_covers"));
        tooltip.add(I18n.format("gregtech.tool_action.wrench.set_facing"));

        super.addToolUsages(stack, world, tooltip, advanced)
    }

    override fun writeInitialSyncData(buf: PacketBuffer) {
        super.writeInitialSyncData(buf)
        val tag = NBTTagCompound()
        essentia.writeToNBT(tag)

        buf.writeCompoundTag(tag)
    }

    override fun receiveInitialSyncData(buf: PacketBuffer) {
        super.receiveInitialSyncData(buf)
        essentia.readFromNBT(buf.readCompoundTag())
    }

    override fun receiveCustomData(dataId: Int, buf: PacketBuffer) {
        super.receiveCustomData(dataId, buf)
        if (dataId == RosaTechDataCodes.UPDATE_ESSENTIA) {
            essentia.readFromNBT(buf.readCompoundTag())
        }
    }

    override fun getAbility(): MultiblockAbility<IAspectContainer> = if (isExportHatch) ThaumcraftMultiblockAbility.EXPORT_ESSENTIA else ThaumcraftMultiblockAbility.IMPORT_ESSENTIA

    override fun registerAbilities(abilityList: MutableList<IAspectContainer>) {
        abilityList.add(this as IAspectContainer)
    }


    override fun getAspects(): AspectList = essentia.copy()

    override fun setAspects(aspect: AspectList) {
        essentia = aspect.copy()
    }

    override fun doesContainerAccept(aspect: Aspect): Boolean {
        return (essentia.getAmount(aspect) <= ESSENTIA_CAPACITY)
    }

    override fun addToContainer(aspect: Aspect, amount: Int): Int {
        if (amount == 0) {
            return 0
        }
        else if (this.essentia.getAmount(aspect) >= ESSENTIA_CAPACITY || !this.doesContainerAccept(aspect)){
            this.markDirty()
            return amount
        } else {
            val added = min(amount, ESSENTIA_CAPACITY - this.essentia.getAmount(aspect))
            this.essentia.add(aspect, added)
            this.markDirty()
            return (amount - added)
        }
    }

    override fun takeFromContainer(aspect: Aspect, amount: Int): Boolean {
        if (this.doesContainerContainAmount(aspect, amount)) {
            this.essentia.reduce(aspect, amount)
            this.markDirty()
            return true
        } else return false
    }

    override fun takeFromContainer(aspectList: AspectList): Boolean {
        if (!this.doesContainerContain(aspectList)) {
            return false
        } else {
            var satisfied = false
            for (aspectEntry in aspectList.aspects) {
                satisfied = satisfied && this.takeFromContainer(aspectEntry.key, aspectEntry.value)
            }
            return satisfied
        }
    }

    override fun doesContainerContainAmount(aspect: Aspect, amount: Int): Boolean = this.essentia.getAmount(aspect) >= amount

    override fun doesContainerContain(aspectList: AspectList): Boolean {
        var satisfied = false
        for (aspectEntry in aspectList.aspects) {
            satisfied = satisfied && this.doesContainerContainAmount(aspectEntry.key, aspectEntry.value)
        }

        return satisfied
    }

    override fun containerContains(aspect: Aspect): Int {
        if (essentia.aspects.containsKey(aspect)) {
            return essentia.getAmount(aspect)
        }

        return 0
    }

    override fun isConnectable(facing: EnumFacing): Boolean = facing == frontFacing

    override fun canInputFrom(facing: EnumFacing): Boolean = (facing == frontFacing && !isExportHatch)

    override fun canOutputTo(facing: EnumFacing): Boolean = (facing == frontFacing && isExportHatch)

    override fun setSuction(aspect: Aspect, amount: Int) {
        // do nothing
    }

    override fun getSuctionType(face: EnumFacing): Aspect? = null

    override fun getSuctionAmount(face: EnumFacing): Int = 32 shl tier

    override fun takeEssentia(
        aspect: Aspect,
        amount: Int,
        face: EnumFacing
    ): Int {
        return if (this.canOutputTo(face) && this.takeFromContainer(aspect, amount)) amount else 0
    }

    override fun addEssentia(
        aspect: Aspect,
        amount: Int,
        face: EnumFacing
    ): Int {
        return if (this.canInputFrom(face)) amount - this.addToContainer(aspect, amount) else 0
    }

    override fun getEssentiaType(face: EnumFacing): Aspect? {
        val aspectList = this.aspects
        return if (aspectList.size() == 0) null else this.aspects.aspectsSortedByAmount[0]
    }

    override fun getEssentiaAmount(face: EnumFacing): Int {
        val type = this.getEssentiaType(face)
        if (type == null) return 0

        return this.aspects.getAmount(type)
    }

    override fun getMinimumSuction(): Int = 0


}