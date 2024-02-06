package rosatech.modules.blood_magic.metatileentities.multiblockpart;

import WayofTime.bloodmagic.core.data.Binding;
import WayofTime.bloodmagic.core.data.SoulNetwork;
import WayofTime.bloodmagic.core.data.SoulTicket;
import WayofTime.bloodmagic.item.ItemBindableBase;
import WayofTime.bloodmagic.orb.IBloodOrb;
import WayofTime.bloodmagic.util.helper.NetworkHelper;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.capability.GregtechDataCodes;
import gregtech.api.capability.GregtechTileCapabilities;
import gregtech.api.capability.IControllable;
import gregtech.api.capability.impl.NotifiableItemStackHandler;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.widgets.AdvancedTextWidget;
import gregtech.api.gui.widgets.SlotWidget;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rosatech.client.renderer.textures.RosaTextures;
import rosatech.modules.blood_magic.api.capability.ISoulTank;
import rosatech.modules.blood_magic.api.capability.impl.SoulTank;
import rosatech.modules.blood_magic.api.metatileentity.multiblock.BloodMagicMultiblockAbility;

import java.util.List;
import java.util.function.Consumer;

public class MetaTileEntityLifeEssenceHatch extends MetaTileEntityMultiblockPart implements IMultiblockAbilityPart<ISoulTank>, IControllable {

    private static final int INITAL_CAPACITY = 6250;
    private static final int INITIAL_TRANSFER_RATE = 1750;
    private boolean workingEnabled;

    private SoulTank soulTank;

    private SoulNetwork network;

    private boolean isExport;
    private final IItemHandlerModifiable orbHandler;

    public MetaTileEntityLifeEssenceHatch(ResourceLocation metaTileEntityId, int tier, boolean isExport) {
        super(metaTileEntityId, tier);
        this.soulTank = new SoulTank(getLifeEssenceCapacity(), 0);
        this.workingEnabled = true;
        this.isExport = isExport;
        this.orbHandler = new BloodOrbItemHandler(this);
        initializeInventory();

    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityLifeEssenceHatch(metaTileEntityId, getTier(), isExport);
    }

    private int getLifeEssenceCapacity() {
        return INITAL_CAPACITY * (1 << Math.min(9, getTier()));
    }

    private int getLifeEssenceTransferRate() {
        return INITIAL_TRANSFER_RATE * (1 << Math.min(9, getTier()));
    }

    @Override
    public boolean isWorkingEnabled() {
        return workingEnabled;
    }

    public void transferLifeEssence() {
        int toTransfer = Math.min(getLifeEssenceTransferRate(), network.getCurrentEssence());

        if (isExport) {
            toTransfer = getLifeEssenceTransferRate();
            network.add(new SoulTicket(soulTank.drain(toTransfer, true)), toTransfer);
        } else {
            network.syphon(new SoulTicket(toTransfer));
            soulTank.fill(toTransfer, true);
        }
    }


    @Override
    public void update() {
        super.update();
        if (getOffsetTimer() % 20 != 0) {
            return;
        }

        if (!getWorld().isRemote && workingEnabled && network != null) {
            transferLifeEssence();
        }
    }

    public SoulNetwork getSoulNetwork(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemBindableBase bindableItem) {
            Binding binding = bindableItem.getBinding(stack);
            if (binding != null) {
                return NetworkHelper.getSoulNetwork(binding);
            }
        }

        return null;
    }
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setBoolean("workingEnabled", workingEnabled);
        data.setInteger("lifeEssenceStored", soulTank.getAmount());
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        if (data.hasKey("workingEnabled")) {
            this.workingEnabled = data.getBoolean("workingEnabled");
        }
        if (data.hasKey("lifeEssenceStored")) {
            this.soulTank.replace(data.getInteger("lifeEssenceStored"));
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
        buf.writeInt(soulTank.getAmount());
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        this.workingEnabled = buf.readBoolean();
        this.soulTank.replace(buf.readInt());
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
            SimpleOverlayRenderer overlay = isExport ? RosaTextures.LIFE_ESSENCE_OUTPUT_OVERLAY :
                    RosaTextures.LIFE_ESSENCE_INPUT_OVERLAY;
            overlay.renderSided(getFrontFacing(), renderState, translation, pipeline);
        }
    }

    private Consumer<List<ITextComponent>> getLifeEssenceAmountText() {
        return (list) -> {
            list.add(new TextComponentString(I18n.format("rosatech.universal.stored_life_essence", soulTank.getAmount())));
        };
    }

    private Consumer<List<ITextComponent>> getLifeEssenceCapacityText() {
        return (list) -> {
            list.add(new TextComponentString(I18n.format("rosatech.universal.life_essence_capacity", soulTank.getCapacity())));
        };
    }

    @Override
    protected IItemHandlerModifiable createImportItemHandler() {
        return orbHandler;
    }

    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        ModularUI.Builder builder = ModularUI.defaultBuilder();
        return builder.image(7, 16, 88, 55, GuiTextures.DISPLAY)
                .label(6, 6, getMetaFullName())
                .widget(new AdvancedTextWidget(11, 20, getLifeEssenceCapacityText(), 0xFFFFFF))
                .widget(new AdvancedTextWidget(11, 30, getLifeEssenceAmountText(), 0xFFFFFF))
                .widget(new SlotWidget(orbHandler, 0, 118, 35, true, true))
                .bindPlayerInventory(entityPlayer.inventory)
                .build(getHolder(), entityPlayer);
    }

    private class BloodOrbItemHandler extends NotifiableItemStackHandler {

        public BloodOrbItemHandler(MetaTileEntity metaTileEntity) {
            super(metaTileEntity, 1, null, false);
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @NotNull
        @Override
        public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if (!(stack.getItem() instanceof IBloodOrb)) {
                return stack;
            }

            if (stack.isEmpty()) {
                return ItemStack.EMPTY;
            }

            return ItemStack.EMPTY;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            boolean slotMatches = this.getStackInSlot(slot).isEmpty();

            return slotMatches && stack.getItem() instanceof IBloodOrb;
        }

        @Override
        public void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            ItemStack stack = this.getStackInSlot(slot);
            network = getSoulNetwork(stack);
        }
    }
    @Override
    public void addInformation(ItemStack stack, @Nullable World player, @NotNull List<String> tooltip,
                               boolean advanced) {
        if (this.isExport)
            tooltip.add(I18n.format("rosatech.machine.life_essence.export.tooltip"));
        else
            tooltip.add(I18n.format("rosatech.machine.life_essence.import.tooltip"));
        tooltip.add(I18n.format("rosatech.universal.tooltip.life_essence_capacity", getLifeEssenceCapacity()));
        tooltip.add(I18n.format("rosatech.universal.enabled"));
    }

    @Override
    public void addToolUsages(ItemStack stack, @Nullable World world, List<String> tooltip, boolean advanced) {
        tooltip.add(I18n.format("gregtech.tool_action.screwdriver.access_covers"));
        tooltip.add(I18n.format("gregtech.tool_action.wrench.set_facing"));
        super.addToolUsages(stack, world, tooltip, advanced);
    }

    @Override
    public void registerAbilities(List<ISoulTank> abilityList) {
        abilityList.add(soulTank);
    }

    @Override
    public MultiblockAbility<ISoulTank> getAbility() {
        return isExport ? BloodMagicMultiblockAbility.EXPORT_LIFE_ESSENCE : BloodMagicMultiblockAbility.IMPORT_LIFE_ESSENCE;
    }


}
