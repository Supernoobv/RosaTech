package rosatech.api.capability.impl

import gregtech.api.capability.impl.EnergyContainerHandler
import gregtech.api.metatileentity.MetaTileEntity
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import rosatech.api.world.data.DimensionalNetData
import rosatech.api.world.net.nets.DimensionalEnergyNet
import kotlin.math.min

class WirelessEnergyContainer(
    tileEntity: MetaTileEntity,
    var net: DimensionalEnergyNet,

    maxCapacity: Long,
    maxInputVoltage: Long,
    maxInputAmperage: Long,

    maxOutputVoltage: Long,
    maxOutputAmperage: Long
) : EnergyContainerHandler(tileEntity, maxCapacity, maxInputVoltage, maxInputAmperage, maxOutputVoltage, maxOutputAmperage) {

    override fun getEnergyStored(): Long {

        return if (outputVoltage > 0L || outputAmperage >        0L) energyStored
        else min(net.getStored().toLong(), energyCapacity)
    }


    override fun serializeNBT(): NBTTagCompound {
        val compound = NBTTagCompound()
        compound.setInteger("frequency", net.frequency)
        compound.setString("netId", net.definition.resourceLocation.toString())
        return compound
    }

    override fun deserializeNBT(compound: NBTTagCompound) {
        val frequency = compound.getInteger("frequency")
        val netId = ResourceLocation(compound.getString("netId"))

        net = DimensionalNetData.getOrCreateNetwork(frequency, netId) as DimensionalEnergyNet
    }

    override fun setEnergyStored(energyStored: Long) {
        if (energyStored > this.getEnergyStored()) {
            energyInputPerSec += energyStored - this.getEnergyStored()

            val toFill = energyStored - this.getEnergyStored()
            this.energyStored = toFill - net.fill(toFill)
        } else {
            energyOutputPerSec += this.getEnergyStored() - energyStored

            val toDrain = this.getEnergyStored() - energyStored
            this.energyStored = toDrain - net.drain(toDrain)
        }
        if (!metaTileEntity.world.isRemote) {
            metaTileEntity.markDirty()
            notifyEnergyListener(false)
        }
    }

    companion object {
        fun emitterContainer(
            tileEntity: MetaTileEntity,
            net: DimensionalEnergyNet,
            maxCapacity: Long,
            maxOutputVoltage: Long,
            maxOutputAmperage: Long
        ): WirelessEnergyContainer = WirelessEnergyContainer(tileEntity, net, maxCapacity, 0, 0, maxOutputVoltage, maxOutputAmperage)

        fun receiverContainer(
            tileEntity: MetaTileEntity,
            net: DimensionalEnergyNet,
            maxCapacity: Long,
            maxInputVoltage: Long,
            maxInputAmperage: Long
        ): WirelessEnergyContainer = WirelessEnergyContainer(tileEntity, net, maxCapacity, maxInputVoltage, maxInputAmperage, 0, 0)
    }

    override fun outputsEnergy(side: EnumFacing): Boolean = false

}