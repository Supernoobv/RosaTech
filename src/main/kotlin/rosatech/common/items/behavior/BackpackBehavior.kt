package rosatech.common.items.behavior

import gregtech.api.gui.GuiTextures
import gregtech.api.gui.ModularUI
import gregtech.api.gui.ModularUI.Builder
import gregtech.api.items.gui.ItemUIFactory
import gregtech.api.items.gui.PlayerInventoryHolder
import gregtech.api.items.metaitem.MetaItem
import gregtech.api.items.metaitem.stats.IItemBehaviour
import gregtech.api.items.metaitem.stats.IItemCapabilityProvider
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.ItemStackHandler

open class BackpackBehavior(val inventorySize: Int) : IItemBehaviour, ItemUIFactory, IItemCapabilityProvider {

    override fun createUI(
        playerHolder: PlayerInventoryHolder,
        playerIn: EntityPlayer
    ): ModularUI? {
        val heldItem: ItemStack = playerHolder.currentItem

        if (!heldItem.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) return null

        val inventory: ItemStackHandler = heldItem.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null) as ItemStackHandler

        initNBT(heldItem, inventory)
        val factor: Int = if (inventorySize / 9 > 8) 18 else 9
        val builder: Builder = ModularUI
            .builder(GuiTextures.BACKGROUND, 176 + (if (factor == 18) 176 else 0), 8 + inventorySize / factor * 18 + 104)
            .label(5, 5, heldItem.displayName)

        for (i in 0 until inventorySize) {
            builder.slot(inventory, i, 7 * (if (factor == 18) 2 else 1) + i % factor * 18, 18 + i / factor * 18,
                GuiTextures.SLOT)
        }

        builder.bindPlayerInventory(playerIn.inventory, GuiTextures.SLOT, 7 + (if (factor == 18) 88 else 0), 18 + inventorySize / factor * 18 + 11)
        builder.bindCloseListener {
            heldItem.tagCompound?.setTag("Inventory", inventory.serializeNBT()!!)
        }

        return builder.build(playerHolder, playerIn)
    }

    override fun onItemRightClick(world: World, player: EntityPlayer, hand: EnumHand): ActionResult<ItemStack> {
        val heldItem: ItemStack = player.getHeldItem(hand)


        if (!world.isRemote) {
            val holder: PlayerInventoryHolder = PlayerInventoryHolder(player, hand)

            holder.openUI()
        }

        return ActionResult.newResult(EnumActionResult.SUCCESS, heldItem)
    }

    override fun createProvider(itemStack: ItemStack): ICapabilityProvider {
        return object : ICapabilityProvider {
            var inventory: ItemStackHandler = BackpackItemHandler(inventorySize)

            override fun hasCapability(
                capability: Capability<*>,
                facing: EnumFacing?
            ): Boolean {
                return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
            }

            override fun <T : Any> getCapability(
                capability: Capability<T>,
                facing: EnumFacing?
            ): T? {
                return if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) inventory as T else null
            }

        }
    }

    internal fun initNBT(itemStack: ItemStack, inventory: ItemStackHandler) {
        if (itemStack.tagCompound == null) {
            val compound: NBTTagCompound = NBTTagCompound()
            compound.setTag("Inventory", inventory.serializeNBT())
            itemStack.tagCompound = compound
        } else {
            inventory.deserializeNBT(itemStack.tagCompound?.getCompoundTag("Inventory"))
        }
    }

    internal class BackpackItemHandler(inventorySize: Int) : ItemStackHandler(inventorySize) {
        override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
            if (stack.isEmpty) return true

            var isBackpack: Boolean = false
            if (stack.item is MetaItem<*>) {
                val metaItem = stack.item as MetaItem<*>
                for (behavior in metaItem.getBehaviours(stack)) {
                    if (behavior is BackpackBehavior) {
                        isBackpack = true
                        break
                    }
                }
            }

            return !isBackpack
        }
    }


}