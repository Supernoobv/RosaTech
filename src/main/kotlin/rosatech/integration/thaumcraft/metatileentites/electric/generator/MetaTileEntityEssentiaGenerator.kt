package rosatech.integration.thaumcraft.metatileentites.electric.generator

import gregtech.api.capability.impl.RecipeLogicEnergy
import gregtech.api.metatileentity.MetaTileEntity
import gregtech.api.metatileentity.SimpleGeneratorMetaTileEntity
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity
import gregtech.api.recipes.RecipeMap
import gregtech.client.renderer.ICubeRenderer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.PacketBuffer
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import rosatech.api.capability.RosaTechDataCodes
import rosatech.api.util.RosaThaumcraftUtils
import rosatech.integration.thaumcraft.recipe.ESSENTIA_GENERATOR_RECIPES
import rosatech.integration.thaumcraft.recipe.logic.EssentiaFuelRecipeLogic
import thaumcraft.api.aspects.Aspect
import thaumcraft.api.aspects.AspectList
import thaumcraft.api.aspects.IAspectContainer
import thaumcraft.api.aspects.IEssentiaTransport
import kotlin.math.min

class MetaTileEntityEssentiaGenerator(
    metaTileEntityId: ResourceLocation,
    renderer: ICubeRenderer, tier: Int
) : SimpleGeneratorMetaTileEntity(metaTileEntityId, ESSENTIA_GENERATOR_RECIPES, renderer, tier, { 0 }, false), IAspectContainer, IEssentiaTransport {

    var essentia: AspectList = AspectList()

    val ESSENTIA_CAPACITY = 100 shl tier

    override fun update() {
        super.update()
        if (!world.isRemote && this.offsetTimer.toInt() % 5 == 0) {
            RosaThaumcraftUtils.fill(this, world, pos, false)

            if (this.offsetTimer.toInt() % 20 == 0) {
                writeCustomData(RosaTechDataCodes.UPDATE_ESSENTIA) { buf ->
                    val tag = NBTTagCompound()
                    essentia.writeToNBT(tag)

                    buf.writeCompoundTag(tag)
                }
            }

        }
    }

    override fun createMetaTileEntity(tileEntity: IGregTechTileEntity): MetaTileEntity {
        return MetaTileEntityEssentiaGenerator(metaTileEntityId, renderer, tier)
    }

    override fun createWorkable(recipeMap: RecipeMap<*>): RecipeLogicEnergy {
        return EssentiaFuelRecipeLogic(this, recipeMap) { energyContainer }
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



    override fun getAspects(): AspectList = essentia.copy()

    override fun setAspects(aspectList: AspectList) {
        if (aspectList != null) {
            essentia = aspectList.copy()
        }
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

    override fun writeToNBT(data: NBTTagCompound): NBTTagCompound {
        val tagCompound = super.writeToNBT(data)
        essentia.writeToNBT(tagCompound, "essentia")

        return tagCompound
    }

    override fun readFromNBT(data: NBTTagCompound) {
        super.readFromNBT(data)
        essentia.readFromNBT(data, "essentia")
    }

    override fun isConnectable(facing: EnumFacing): Boolean {
        return facing != frontFacing
    }

    override fun canInputFrom(facing: EnumFacing): Boolean {
        return facing != frontFacing
    }

    override fun canOutputTo(facing: EnumFacing): Boolean {
        return false
    }

    override fun setSuction(aspect: Aspect, amount: Int) {
        // Do nothing
    }

    // Every aspect is accepted
    override fun getSuctionType(facing: EnumFacing): Aspect? {
        return null
    }

    override fun getSuctionAmount(facing: EnumFacing): Int {
        return 32 shl tier
    }

    override fun takeEssentia(
        aspect: Aspect,
        amount: Int,
        facing: EnumFacing
    ): Int {
        return 0
    }

    override fun addEssentia(
        aspect: Aspect,
        amount: Int,
        facing: EnumFacing
    ): Int {
        if (this.canInputFrom(facing)) {
            return (amount - this.addToContainer(aspect, amount))
        } else {
            return 0
        }
    }

    override fun getEssentiaType(facing: EnumFacing): Aspect? {
        val aspectList = this.aspects
        return if (aspectList == null || aspectList.size() == 0)  null else this.aspects.aspectsSortedByAmount[0]
    }

    override fun getEssentiaAmount(facing: EnumFacing): Int {
        val type = this.getEssentiaType(facing)
        if (type == null) return 0

        return this.aspects.getAmount(type)
    }

    override fun getMinimumSuction(): Int {
        return 0
    }

}