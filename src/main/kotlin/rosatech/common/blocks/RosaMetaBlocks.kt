package rosatech.common.blocks

import gregtech.common.blocks.MetaBlocks.statePropertiesToString
import net.minecraft.block.Block
import net.minecraft.block.properties.IProperty
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import rosatech.RosaTech
import rosatech.integration.thaumcraft.block.BlockLargeMultiblockCasingThaum
import java.util.stream.Collectors

class RosaMetaBlocks {

    companion object {
        lateinit var LARGE_MULTIBLOCK_CASING_THAUM: BlockLargeMultiblockCasingThaum

        fun init() {
            if (Loader.isModLoaded("thaumcraft")) {
                LARGE_MULTIBLOCK_CASING_THAUM = BlockLargeMultiblockCasingThaum()
                LARGE_MULTIBLOCK_CASING_THAUM.setRegistryName("large_multiblock_casing_thaumcraft")
            }

        }

        @SideOnly(Side.CLIENT)
        fun registerItemModels() {
            if (Loader.isModLoaded("thaumcraft")) {
                registerItemModel(LARGE_MULTIBLOCK_CASING_THAUM)
            }
        }


        @SideOnly(Side.CLIENT)
        private fun registerItemModel(block: Block) {
            for (state in block.getBlockState().validStates) {
                ModelLoader.setCustomModelResourceLocation(
                    Item.getItemFromBlock(block),
                    block.getMetaFromState(state),
                    ModelResourceLocation(block.registryName!!, statePropertiesToString(state.properties))
                )
            }
        }

    }
}
