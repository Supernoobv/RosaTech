package rosatech.modules.botania.metatileentities.multiblockpart;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.capability.GregtechDataCodes;
import gregtech.api.capability.GregtechTileCapabilities;
import gregtech.api.capability.IControllable;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.widgets.AdvancedTextWidget;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rosatech.client.renderer.textures.RosaTextures;
import rosatech.api.capability.IIntegerTank;
import rosatech.api.capability.impl.IntegerTank;
import rosatech.modules.botania.api.metatileentity.multiblock.BotaniaMultiblockAbility;
import vazkii.botania.api.mana.IManaPool;
import vazkii.botania.api.mana.IManaReceiver;

import java.util.List;
import java.util.function.Consumer;

public class MetaTileEntityManaHatch extends MetaTileEntityMultiblockPart implements IMultiblockAbilityPart<IIntegerTank>, IControllable, IManaPool {

    private static final int INITIAL_MANA_CAPACITY = 12500;
    private boolean workingEnabled;

    private IntegerTank manaTank;

    private boolean isExport;

    public MetaTileEntityManaHatch(ResourceLocation metaTileEntityId, int tier, boolean isExport) {
        super(metaTileEntityId, tier);
        this.manaTank = new IntegerTank(getManaCapacity(), 0);
        this.workingEnabled = true;
        this.isExport = isExport;
    }

    private void transferMana(EnumFacing side, Consumer<IManaReceiver> transfer ) {
        TileEntity tileEntity = getNeighbor(side);
        if (tileEntity == null) {
            return;
        }

        IManaReceiver manaCap = tileEntity instanceof IManaReceiver ? (IManaReceiver) tileEntity : null;

        if (manaCap == null) {
            return;
        }
        transfer.accept(manaCap);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityManaHatch(metaTileEntityId, getTier(), isExport);
    }

    private int getManaCapacity() {
        return INITIAL_MANA_CAPACITY * (1 << Math.min(9, getTier()));
    }

    @Override
    public boolean isWorkingEnabled() {
        return workingEnabled;
    }

    @Override
    public void update() {
        super.update();
        if (!getWorld().isRemote && workingEnabled) {
            if (isExport) {
                if (manaTank.getAmount() <= 0) {
                    return;
                }

                transferMana(getFrontFacing(), manaReceiver -> {
                    manaReceiver.recieveMana(manaTank.getAmount());
                    manaTank.drain(manaTank.getAmount(), true);
                });
            } else {
                transferMana(getFrontFacing(), manaReceiver -> {
                    manaTank.fill(manaReceiver.getCurrentMana(), true);
                    manaReceiver.recieveMana(-manaReceiver.getCurrentMana());
                });
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setBoolean("workingEnabled", workingEnabled);
        data.setInteger("manaStored", manaTank.getAmount());
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        if (data.hasKey("workingEnabled")) {
            this.workingEnabled = data.getBoolean("workingEnabled");
        }
        if (data.hasKey("manaStored")) {
            this.manaTank.replace(data.getInteger("manaStored"));
        }
    }



    @Override
    public void setWorkingEnabled(boolean workingEnabled) {
        this.workingEnabled = workingEnabled;
        World world = getWorld();
        if (world != null && !world.isRemote) {
            writeCustomData(GregtechDataCodes.WORKING_ENABLED, buf -> buf.writeBoolean(workingEnabled));
        }
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing side) {
        if (capability == GregtechTileCapabilities.CAPABILITY_CONTROLLABLE) {
            return GregtechTileCapabilities.CAPABILITY_CONTROLLABLE.cast(this);
        }
        return super.getCapability(capability, side);
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeBoolean(workingEnabled);
        buf.writeInt(manaTank.getAmount());
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        this.workingEnabled = buf.readBoolean();
        this.manaTank.replace(buf.readInt());
    }

    @Override
    public void receiveCustomData(int dataId, PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);
        if (dataId == GregtechDataCodes.WORKING_ENABLED) {
            this.workingEnabled = buf.readBoolean();
        }
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        if (shouldRenderOverlay()) {
            SimpleOverlayRenderer overlay = isExport ? RosaTextures.MANA_HATCH_OUTPUT_OVERLAY :
                    RosaTextures.MANA_HATCH_INPUT_OVERLAY;
            overlay.renderSided(getFrontFacing(), renderState, translation, pipeline);
        }
    }

    private Consumer<List<ITextComponent>> getManaAmountText() {
        return (list) -> {
            list.add(new TextComponentString(I18n.format("rosatech.universal.stored_mana", manaTank.getAmount())));
        };
    }

    private Consumer<List<ITextComponent>> getManaCapacityText() {
        return (list) -> {
            list.add(new TextComponentString(I18n.format("rosatech.universal.mana_capacity", manaTank.getCapacity())));
        };
    }
    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        ModularUI.Builder builder = ModularUI.defaultBuilder();
        return builder.image(7, 16, 88, 55, GuiTextures.DISPLAY)
                .label(6, 6, getMetaFullName())
                .widget(new AdvancedTextWidget(11, 20, getManaCapacityText(), 0xFFFFFF))
                .widget(new AdvancedTextWidget(11, 30, getManaAmountText(), 0xFFFFFF))
                .bindPlayerInventory(entityPlayer.inventory)
                .build(getHolder(), entityPlayer);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, @NotNull List<String> tooltip,
                               boolean advanced) {
        if (this.isExport)
            tooltip.add(I18n.format("rosatech.machine.mana_hatch.export.tooltip"));
        else
            tooltip.add(I18n.format("rosatech.machine.mana_hatch.import.tooltip"));
        tooltip.add(I18n.format("rosatech.universal.tooltip.mana_capacity", getManaCapacity()));
        tooltip.add(I18n.format("rosatech.universal.enabled"));
    }

    @Override
    public void addToolUsages(ItemStack stack, @Nullable World world, List<String> tooltip, boolean advanced) {
        tooltip.add(I18n.format("gregtech.tool_action.screwdriver.access_covers"));
        tooltip.add(I18n.format("gregtech.tool_action.wrench.set_facing"));
        super.addToolUsages(stack, world, tooltip, advanced);
    }


    @Override
    public MultiblockAbility<IIntegerTank> getAbility() {
        return isExport ? BotaniaMultiblockAbility.EXPORT_MANA : BotaniaMultiblockAbility.IMPORT_MANA;
    }

    @Override
    public void registerAbilities(List<IIntegerTank> abilityList) {
        abilityList.add(manaTank);
    }

    @Override
    public int getCurrentMana() {
        return manaTank.getAmount();
    }

    @Override
    public boolean isFull() {
        return !manaTank.canFill();
    }

    @Override
    public void recieveMana(int i) {
        manaTank.fill(i, true);
    }

    @Override
    public boolean canRecieveManaFromBursts() {
        return true;
    }

    @Override
    public boolean isOutputtingPower() {
        return isExport;
    }

    @Override
    public EnumDyeColor getColor() {
        return EnumDyeColor.CYAN;
    }

    @Override
    public void setColor(EnumDyeColor enumDyeColor) {
    }
}
