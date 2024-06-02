package rosatech.common.items.behavior

import gregtech.api.capability.impl.FluidTankList
import gregtech.api.capability.impl.GTFluidHandlerItemStack
import gregtech.api.gui.GuiTextures
import gregtech.api.gui.ModularUI
import gregtech.api.gui.ModularUI.Builder
import gregtech.api.gui.widgets.TankWidget
import gregtech.api.gui.widgets.ToggleButtonWidget
import gregtech.api.gui.widgets.WidgetGroup
import gregtech.api.items.gui.PlayerInventoryHolder
import gregtech.api.items.metaitem.MetaItem
import gregtech.api.items.metaitem.stats.IItemCapabilityProvider
import gregtech.api.util.GTTransferUtils
import gregtech.api.util.IDirtyNotifiable
import gregtech.common.covers.filter.ItemFilterContainer
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.fluids.FluidTank
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemStackHandler
import rosatech.api.capability.RosaSimpleCapabilityManager
import rosatech.client.renderer.textures.RosaGuiTextures


class SmartBackpackBehavior(inventorySize: Int, val tier: Int, val fluidInventorysize: Int) :
    BackpackBehavior(inventorySize), IDirtyNotifiable {


    override fun createUI(playerHolder: PlayerInventoryHolder, playerIn: EntityPlayer): ModularUI? {
        val heldItem: ItemStack = playerHolder.currentItem

        val inventory: ItemStackHandler =
            heldItem.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null) as ItemStackHandler
        val fluidInventory: FluidTankList =
            heldItem.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null) as FluidTankList
        val itemFilterContainer: ItemFilterContainer = heldItem.getCapability(
            RosaSimpleCapabilityManager.CAPABILITY_ITEM_FILTER_CONTAINER!!,
            null
        ) as ItemFilterContainer

        initNBT2(heldItem, inventory, fluidInventory, itemFilterContainer)
        if (!playerIn.isSneaking) {

            val factor: Int = if (inventorySize / 9 > 8) 18 else 9
            val builder: Builder = ModularUI
                .builder(
                    GuiTextures.BACKGROUND,
                    224 + (if (factor == 18) 176 else 0),
                    8 + inventorySize / factor * 18 + 104
                )
                .label(5, 5, heldItem.displayName)

            for (i in 0 until inventorySize) {
                builder.slot(
                    inventory, i, (7 * 4) * (if (factor == 18) 2 else 1) + i % factor * 18, 18 + i / factor * 18,
                    GuiTextures.SLOT
                )
            }


            for (i in 0 until fluidInventorysize) {
                builder.widget(
                    TankWidget(fluidInventory.getTankAt(i), 190, 18 + (18 * i), 18, 18)
                        .setBackgroundTexture(GuiTextures.FLUID_SLOT)
                        .setContainerClicking(true, true)
                        .setAlwaysShowFull(true)
                )
            }

            builder.widget(
                ToggleButtonWidget(196, (20 * 8) + 10, 20, 20,
                    { heldItem.tagCompound?.getBoolean("autoPickup")!! },
                    { value -> heldItem.tagCompound?.setBoolean("autoPickup", value) })
                    .setTooltipText("rosatech.gui.auto_pickup.tooltip")
                    .setButtonTexture(RosaGuiTextures.BACKPACK_AUTOPICKUP)
                    .shouldUseBaseBackground()
            )

            builder.widget(
                ToggleButtonWidget(196, (20 * 9) + 15, 20, 20,
                    { heldItem.tagCompound?.getBoolean("depositing")!! },
                    { value ->
                        heldItem.tagCompound?.setBoolean("depositing", value)
                        if (value) heldItem.tagCompound?.setBoolean("restocking", false)
                    })
                    .setTooltipText("rosatech.gui.depositing.tooltip")
                    .setButtonTexture(RosaGuiTextures.BACKPACK_DEPOSITING)
                    .shouldUseBaseBackground()
                    .setPredicate { tier >= 2 })

            builder.widget(
                ToggleButtonWidget(196, (20 * 10) + 20, 20, 20,
                    { heldItem.tagCompound?.getBoolean("restocking")!! },
                    { value ->
                        heldItem.tagCompound?.setBoolean("restocking", value)
                        if (value) heldItem.tagCompound?.setBoolean("depositing", false)
                    })
                    .setTooltipText("rosatech.gui.restocking.tooltip")
                    .setButtonTexture(RosaGuiTextures.BACKPACK_RESTOCKING)
                    .shouldUseBaseBackground()
                    .setPredicate { tier >= 2 })

            builder.bindPlayerInventory(
                playerIn.inventory,
                GuiTextures.SLOT,
                (7 * 4) + (if (factor == 18) 88 else 0),
                18 + inventorySize / factor * 18 + 11
            )
            builder.bindCloseListener {
                heldItem.tagCompound?.setTag("Inventory", inventory.serializeNBT()!!)
                heldItem.tagCompound?.setTag("FluidInventory", fluidInventory.serializeNBT()!!)
            }

            return builder.build(playerHolder, playerIn)

        } else {
            val builder: Builder = ModularUI
                .builder(GuiTextures.BACKGROUND, 176, 156 + 82)
                .bindPlayerInventory(playerIn.inventory, GuiTextures.SLOT, 7, 156)


            val widgetGroup: WidgetGroup = WidgetGroup()

            itemFilterContainer.initUI(7, widgetGroup::addWidget)

            builder.bindCloseListener {
                heldItem.tagCompound?.setTag(
                    "Filter",
                    itemFilterContainer.serializeNBT()!!
                )
            }

            builder.widget(widgetGroup)

            return builder.build(playerHolder, playerIn)
        }
    }


    override fun onItemUse(
        player: EntityPlayer,
        world: World,
        pos: BlockPos,
        hand: EnumHand,
        facing: EnumFacing,
        hitX: Float,
        hitY: Float,
        hitZ: Float
    ): ActionResult<ItemStack?>? {
        val stack: ItemStack = player.getHeldItem(hand)
        if (!player.canPlayerEdit(pos, facing, stack))
            return ActionResult.newResult(EnumActionResult.FAIL, player.getHeldItem(hand))


        if (!isSmartBackpack(stack))
            return ActionResult.newResult(EnumActionResult.FAIL, player.getHeldItem(hand))

        if (stack.tagCompound == null) return ActionResult.newResult(EnumActionResult.FAIL, player.getHeldItem(hand))

        if (stack.tagCompound?.getBoolean("restocking")!! || stack.tagCompound?.getBoolean("depositing")!!) {
            val behavior = getSmartBackpack(stack)!!
            val inventory: ItemStackHandler =
                stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null) as ItemStackHandler
            inventory.deserializeNBT(stack.tagCompound?.getCompoundTag("Inventory"))

            val tileEntity = world.getTileEntity(pos)
            val handler: IItemHandler? =
                tileEntity?.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing)
            if (handler == null) return ActionResult.newResult(EnumActionResult.FAIL, player.getHeldItem(hand))


            if (stack.tagCompound?.getBoolean("restocking")!!) {
                moveInventoryItems(stack, handler, inventory)
            } else if (stack.tagCompound?.getBoolean("depositing")!!) {
                moveInventoryItems(stack, inventory, handler)
            }

            stack.tagCompound?.setTag("Inventory", inventory.serializeNBT())
        }

        return ActionResult.newResult(EnumActionResult.SUCCESS, player.getHeldItem(hand))
    }

    override fun onItemRightClick(world: World, player: EntityPlayer, hand: EnumHand): ActionResult<ItemStack> {
        val heldItem: ItemStack = player.getHeldItem(hand)

        if (!world.isRemote) {
            val holder: PlayerInventoryHolder = PlayerInventoryHolder(player, hand)

            holder.openUI()
        }

        return ActionResult.newResult(EnumActionResult.SUCCESS, heldItem)
    }

    fun moveInventoryItems(stack: ItemStack, sourceInventory: IItemHandler, targetInventory: IItemHandler) {
        for (i in 0 until sourceInventory.slots) {
            var item: ItemStack = sourceInventory.extractItem(i, Integer.MAX_VALUE, true)
            if (item.isEmpty || !testItemStack(stack, item)) continue
            val remainder: ItemStack = GTTransferUtils.insertItem(targetInventory, item, true)
            val amountToInsert = item.count - remainder.count
            if (amountToInsert > 0) {
                item = sourceInventory.extractItem(i, amountToInsert, false)
                GTTransferUtils.insertItem(targetInventory, item, false)
            }
        }
    }

    override fun markAsDirty() {

    }

    override fun createProvider(itemStack: ItemStack): ICapabilityProvider {
        return object : ICapabilityProvider {

            var inventory: ItemStackHandler = BackpackItemHandler(inventorySize)
            var fluidInventory: FluidTankList =
                FluidTankList(false, Array<FluidTank>(fluidInventorysize) { FluidTank(20000 shl tier) }.toList())
            val itemFilterContainer: ItemFilterContainer = ItemFilterContainer(this@SmartBackpackBehavior)

            override fun hasCapability(
                capability: Capability<*>,
                facing: EnumFacing?
            ): Boolean {
                if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return true
                if (capability == RosaSimpleCapabilityManager.CAPABILITY_ITEM_FILTER_CONTAINER) return true
                if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) return true

                return false
            }

            override fun <T : Any> getCapability(
                capability: Capability<T>,
                facing: EnumFacing?
            ): T? {
                if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return inventory as T
                if (capability == RosaSimpleCapabilityManager.CAPABILITY_ITEM_FILTER_CONTAINER) return itemFilterContainer as T
                if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) return fluidInventory as T

                return null
            }

        }
    }


    internal fun initNBT2(
        itemStack: ItemStack,
        inventory: ItemStackHandler,
        fluidInventory: FluidTankList,
        itemFilterContainer: ItemFilterContainer
    ) {
        if (itemStack.tagCompound == null) {
            val compound: NBTTagCompound = NBTTagCompound()
            compound.setTag("Inventory", inventory.serializeNBT())
            compound.setTag("FluidInventory", fluidInventory.serializeNBT())
            compound.setTag("Filter", itemFilterContainer.serializeNBT())
            compound.setBoolean("autoPickup", false)
            compound.setBoolean("restocking", false)
            compound.setBoolean("depositing", false)
            itemStack.tagCompound = compound
        } else {
            inventory.deserializeNBT(itemStack.tagCompound?.getCompoundTag("Inventory"))
            fluidInventory.deserializeNBT(itemStack.tagCompound?.getCompoundTag("FluidInventory"))
            itemFilterContainer.deserializeNBT(itemStack.tagCompound?.getCompoundTag("Filter"))
        }
    }


    companion object {

        fun insertIntoInventory(stack: ItemStack, other: ItemStack): Boolean {
            if (!isSmartBackpack(stack)) return false
            var item = other.copy()

            val newInventory: ItemStackHandler =
                stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null) as ItemStackHandler
            newInventory.deserializeNBT(stack.tagCompound?.getCompoundTag("Inventory"))
            val remainder: ItemStack = GTTransferUtils.insertItem(newInventory, item, true)
            val amountToInsert = item.count - remainder.count

            var succeeded: Boolean = false

            if (amountToInsert > 0) {
                GTTransferUtils.insertItem(newInventory, item, false)
                succeeded = true
            }
            stack.tagCompound?.setTag("Inventory", newInventory.serializeNBT())
            other.count = remainder.count
            return succeeded
        }

        fun testItemStack(stack: ItemStack, other: ItemStack): Boolean {
            if (!isSmartBackpack(stack)) return false

            val filter: ItemFilterContainer = stack.getCapability(
                RosaSimpleCapabilityManager.CAPABILITY_ITEM_FILTER_CONTAINER!!,
                null
            ) as ItemFilterContainer
            filter.deserializeNBT(stack.tagCompound?.getCompoundTag("Filter"))

            return filter.testItemStack(other)
        }


        fun isSmartBackpack(stack: ItemStack): Boolean {
            if (stack.item is MetaItem<*>) {
                val metaItem = stack.item as MetaItem<*>
                for (behavior in metaItem.getBehaviours(stack)) {
                    if (behavior is SmartBackpackBehavior) return true
                }
            }

            return false
        }

        fun getSmartBackpack(stack: ItemStack): SmartBackpackBehavior? {
            if (stack.item is MetaItem<*>) {
                val metaItem = stack.item as MetaItem<*>
                for (behavior in metaItem.getBehaviours(stack)) {
                    if (behavior is SmartBackpackBehavior) return behavior
                }
            }

            return null
        }
    }
}