package rosatech.modules.botania.metatileentities.single.mana;

import gregtech.api.GTValues;
import gregtech.api.capability.IGhostSlotConfigurable;
import gregtech.api.capability.impl.*;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.resources.TextureArea;
import gregtech.api.gui.widgets.*;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.gui.ModularUI;
import gregtech.api.util.GTTransferUtils;
import gregtech.client.renderer.ICubeRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import rosatech.api.metatileentity.RosaProgressIndicator;
import rosatech.client.renderer.textures.RosaGuiTextures;
import rosatech.modules.botania.api.metatileentity.ManaMetaTileEntity;
import vazkii.botania.api.mana.IManaReceiver;

import java.util.Arrays;
import java.util.List;

// CREDITS: Supersymmetry Devs
public class RosaSimpleManaMetaTileEntity extends ManaMetaTileEntity implements IGhostSlotConfigurable, IManaReceiver {


    protected GhostCircuitItemStackHandler circuitInventory;
    protected IItemHandler outputItemInventory;
    protected IFluidHandler outputFluidInventory;
    private IItemHandlerModifiable actualImportItems;
    protected RosaProgressIndicator progressIndicator;



    public RosaSimpleManaMetaTileEntity(ResourceLocation metaTileEntityId, RecipeMap<?> recipeMap, ICubeRenderer renderer, RosaProgressIndicator progressIndicator) {
        super(metaTileEntityId, recipeMap, renderer);
        if (hasGhostCircuitInventory()) {
            this.circuitInventory = new GhostCircuitItemStackHandler(this);
            this.circuitInventory.addNotifiableMetaTileEntity(this);
        }
        this.progressIndicator = progressIndicator;
        initializeInventory();
    }

    @Override
    public boolean hasGhostCircuitInventory() {
        return true;
    }

    @Override
    public void setGhostCircuitConfig(int config) {
        if (this.circuitInventory == null || this.circuitInventory.getCircuitValue() == config) {
            return;
        }
        this.circuitInventory.setCircuitValue(config);
        if (!getWorld().isRemote) {
            markDirty();
        }



    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new RosaSimpleManaMetaTileEntity(metaTileEntityId, workableHandler.getRecipeMap(), renderer, progressIndicator);
    }

    @Override
    protected void initializeInventory() {
        super.initializeInventory();
        this.outputItemInventory = new ItemHandlerProxy(new ItemStackHandler(0), exportItems);
        this.outputFluidInventory = new FluidHandlerProxy(new FluidTankList(false), exportFluids);
        this.actualImportItems = null;
    }

    @Override
    public IItemHandlerModifiable getImportItems() {
        if (actualImportItems == null) this.actualImportItems = circuitInventory == null ? super.getImportItems() : new ItemHandlerList(Arrays.asList(super.getImportItems(), this.circuitInventory));
        return this.actualImportItems;
    }

    @Override
    protected IItemHandlerModifiable createImportItemHandler() {
        if (workableHandler == null) return new ItemStackHandler(0);
        return new NotifiableItemStackHandler(this, workableHandler.getRecipeMap().getMaxInputs(), this, false);
    }


    @Override
    protected IItemHandlerModifiable createExportItemHandler() {
        if (workableHandler == null) return new ItemStackHandler(0);
        return new NotifiableItemStackHandler(this, workableHandler.getRecipeMap().getMaxOutputs(), this, true);
    }

    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        return createGuiTemplate(entityPlayer).build(getHolder(), entityPlayer);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        if (circuitInventory != null) this.circuitInventory.write(data);
        return data;
    }

    public double getFillPercentage() {
        return (1.0 * manaTank.getAmount() / manaTank.getCapacity());
    }

    public void addBarHoverText(List<ITextComponent> hoverList, int index) {
        if (index == 0) {
            hoverList.add(new TextComponentString(String.format("%,d", manaTank.getAmount())));
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        if (circuitInventory != null) {
            if (data.hasKey("CircuitInventory", Constants.NBT.TAG_COMPOUND)) {
                ItemStackHandler legacyCircuitInventory = new ItemStackHandler();
                for (int i =0; i < legacyCircuitInventory.getSlots(); i++) {
                    ItemStack stack = legacyCircuitInventory.getStackInSlot(i);
                    if (stack.isEmpty()) continue;
                    stack = GTTransferUtils.insertItem(this.importItems, stack, false);
                    this.circuitInventory.setCircuitValueFromStack(stack);
                }
            } else {
                this.circuitInventory.read(data);
            }
        }
    }

    public ModularUI.Builder createUITemplate(EntityPlayer player) {
        return ModularUI.builder(RosaGuiTextures.MANA_BACKGROUND, 176, 166)
                .label(6, 6, getMetaFullName()).shouldColor(false)
                .widget(new ImageWidget(79, 42, 18, 18, GuiTextures.INDICATOR_NO_ENERGY)
                        .setPredicate(() -> workableHandler.isHasNotEnoughEnergy()))
                .bindPlayerInventory(player.inventory, GuiTextures.SLOT, 0);
    }

    protected ModularUI.Builder createGuiTemplate(EntityPlayer player) {
        RecipeMap<?> recipeMap = workableHandler.getRecipeMap();
        int yOffset = 0;

        ModularUI.Builder builder = createUITemplate(player);
        builder.widget(new ProgressWidget(this::getFillPercentage,
                72, 62 + yOffset, 30, 4,
                RosaGuiTextures.PROGRESS_BAR_STORED_MANA, ProgressWidget.MoveType.HORIZONTAL).setHoverTextConsumer(list -> addBarHoverText(list, 0)));
        addRecipeProgressBar(builder, recipeMap, yOffset);
        addInventorySlotGroup(builder, importItems, importFluids, false, yOffset);
        addInventorySlotGroup(builder, exportItems, exportFluids, true, yOffset);

        if (exportItems.getSlots() + exportFluids.getTanks() <= 9) {
            ImageWidget logo = new ImageWidget(152, 63 + yOffset, 17, 17,
                    GTValues.XMAS.get() ? GuiTextures.GREGTECH_LOGO_XMAS : GuiTextures.GREGTECH_LOGO)
                    .setIgnoreColor(true);

            if (this.circuitInventory != null) {
                SlotWidget circuitSlot = new GhostCircuitSlotWidget(circuitInventory, 0, 124, 62 + yOffset)
                        .setBackgroundTexture(GuiTextures.SLOT, getCircuitSlotOverlay());
                builder.widget(circuitSlot.setConsumer(this::getCircuitSlotTooltip)).widget(logo);
            }
        }

        return builder;
    }


    protected void addInventorySlotGroup(@NotNull ModularUI.Builder builder,
                                         @NotNull IItemHandlerModifiable itemHandler,
                                         @NotNull FluidTankList fluidHandler, boolean isOutputs, int yOffset) {
        int itemInputsCount = itemHandler.getSlots();
        int fluidInputsCount = fluidHandler.getTanks();
        boolean invertFluids = false;
        if (itemInputsCount == 0) {
            int tmp = itemInputsCount;
            itemInputsCount = fluidInputsCount;
            fluidInputsCount = tmp;
            invertFluids = true;
        }
        int[] inputSlotGrid = determineSlotsGrid(itemInputsCount);
        int itemSlotsToLeft = inputSlotGrid[0];
        int itemSlotsToDown = inputSlotGrid[1];
        int startInputsX = isOutputs ? 106 : 70 - itemSlotsToLeft * 18;
        int startInputsY = 33 - (int) (itemSlotsToDown / 2.0 * 18) + yOffset;
        boolean wasGroup = itemHandler.getSlots() + fluidHandler.getTanks() == 12;
        if (wasGroup) startInputsY -= 9;
        else if (itemHandler.getSlots() >= 6 && fluidHandler.getTanks() >= 2 && !isOutputs) startInputsY -= 9;
        for (int i = 0; i < itemSlotsToDown; i++) {
            for (int j = 0; j < itemSlotsToLeft; j++) {
                int slotIndex = i * itemSlotsToLeft + j;
                if (slotIndex >= itemInputsCount) break;
                int x = startInputsX + 18 * j;
                int y = startInputsY + 18 * i;
                addSlot(builder, x, y, slotIndex, itemHandler, fluidHandler, invertFluids, isOutputs);
            }
        }
        if (wasGroup) startInputsY += 2;
        if (fluidInputsCount > 0 || invertFluids) {
            if (itemSlotsToDown >= fluidInputsCount && itemSlotsToLeft < 3) {
                int startSpecX = isOutputs ? startInputsX + itemSlotsToLeft * 18 : startInputsX - 18;
                for (int i = 0; i < fluidInputsCount; i++) {
                    int y = startInputsY + 18 * i;
                    addSlot(builder, startSpecX, y, i, itemHandler, fluidHandler, !invertFluids, isOutputs);
                }
            } else {
                int startSpecY = startInputsY + itemSlotsToDown * 18;
                for (int i = 0; i < fluidInputsCount; i++) {
                    int x = isOutputs ? startInputsX + 18 * (i % 3) :
                            startInputsX + itemSlotsToLeft * 18 - 18 - 18 * (i % 3);
                    int y = startSpecY + (i / 3) * 18;
                    addSlot(builder, x, y, i, itemHandler, fluidHandler, !invertFluids, isOutputs);
                }
            }
        }
    }

    protected void addSlot(ModularUI.Builder builder, int x, int y, int slotIndex, IItemHandlerModifiable itemHandler, FluidTankList fluidHandler, boolean isFluid, boolean isOutputs) {
        if (!isFluid) builder.widget(new SlotWidget(itemHandler, slotIndex, x, y, true, !isOutputs)
                .setBackgroundTexture(getOverlaysForSlot(isOutputs, false)));
        else builder.widget(new TankWidget(fluidHandler.getTankAt(slotIndex), x, y, 18, 18).setAlwaysShowFull(true)
                .setBackgroundTexture(getOverlaysForSlot(isOutputs, true))
                .setContainerClicking(true, !isOutputs));
    }

    protected TextureArea[] getOverlaysForSlot(boolean isOutputs, boolean isFluid) {
        return new TextureArea[] {isFluid ? GuiTextures.FLUID_SLOT : GuiTextures.SLOT};
    }

    protected static int[] determineSlotsGrid(int itemInputsCounts) {
        if (itemInputsCounts == 3) return new int[] {3, 1};
        int slotsLeft = (int) Math.ceil(Math.sqrt(itemInputsCounts));
        int slotsDown = (int) Math.ceil(itemInputsCounts / (double) slotsLeft);
        return new int[] {slotsLeft, slotsDown};
    }

    protected TextureArea getCircuitSlotOverlay() {
        return GuiTextures.INT_CIRCUIT_OVERLAY;
    }

    protected void getCircuitSlotTooltip(SlotWidget widget) {
        String configString;
        if (circuitInventory == null || circuitInventory.getCircuitValue() == GhostCircuitItemStackHandler.NO_CONFIG) {
            configString = new TextComponentTranslation("gregtech.gui.configurator_slot.no_value").getFormattedText();
        } else {
            configString = String.valueOf(circuitInventory.getCircuitValue());
        }

        widget.setTooltipText("gregtech.gui.configurator_slot.tooltip", configString);
    }

    protected void addRecipeProgressBar(ModularUI.Builder builder, RecipeMap<?> map, int yOffset) {
        int x = 78;
        int y = 23 + yOffset;
        builder.widget(new RecipeProgressWidget(workableHandler::getProgressPercent, x, y, 20, 20, progressIndicator.progressBarTexture, progressIndicator.progressMoveType, map));
    }

}
