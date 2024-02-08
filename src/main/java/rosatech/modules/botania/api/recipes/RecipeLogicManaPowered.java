package rosatech.modules.botania.api.recipes;

import gregtech.api.GTValues;
import gregtech.api.capability.impl.AbstractRecipeLogic;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import rosatech.api.capability.IIntegerTank;

import org.jetbrains.annotations.NotNull;

public class RecipeLogicManaPowered extends AbstractRecipeLogic {

    private final IIntegerTank manaTank;

    private final double conversionRate;

    public RecipeLogicManaPowered(MetaTileEntity tileEntity, RecipeMap<?> recipeMap, IIntegerTank tank, double conversionRate) {
        super(tileEntity, recipeMap);
        this.manaTank = tank;
        this.conversionRate = conversionRate;
    }

    @Override
    public void writeInitialSyncData(@NotNull PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeInt(manaTank.getAmount());
    }

    @Override
    public void receiveInitialSyncData(@NotNull PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        manaTank.replace(buf.readInt());
    }

    @Override
    public @NotNull NBTTagCompound serializeNBT() {
        NBTTagCompound compound = super.serializeNBT();
        compound.setInteger("StoredMana", manaTank.getAmount());
        return compound;
    }

    @Override
    public void deserializeNBT(@NotNull NBTTagCompound compound) {
        super.deserializeNBT(compound);
        manaTank.replace(compound.getInteger("StoredMana"));
    }


    @NotNull
    @Override
    protected int[] calculateOverclock(@NotNull Recipe recipe) {
        // EUt, Duration
        int[] result = new int[2];

        result[0] = manaTank.getAmount() >= (manaTank.getCapacity() * 0.75) ? recipe.getEUt() * 2 : recipe.getEUt();
        result[1] = manaTank.getAmount() >= (manaTank.getCapacity() * 0.75) ? recipe.getDuration() / 2 : recipe.getDuration();

        return result;
    }

    @Override
    protected long getEnergyInputPerSecond() {
        return 0;
    }

    @Override
    protected long getEnergyStored() {
        return (long) Math.ceil(manaTank.getAmount() * conversionRate);
    }

    @Override
    protected long getEnergyCapacity() {
        return (long) Math.floor(manaTank.getAmount() * conversionRate);
    }



    @Override
    protected boolean drawEnergy(int recipeEUt, boolean simulate) {
        int resultDraw = (int) Math.ceil(recipeEUt / conversionRate);
        return resultDraw >= 0 && manaTank.getAmount() >= resultDraw && manaTank.drain(resultDraw, !simulate) != 0;
    }

    @Override
    public long getMaxVoltage() {
        return GTValues.V[GTValues.LV];
    }
}
