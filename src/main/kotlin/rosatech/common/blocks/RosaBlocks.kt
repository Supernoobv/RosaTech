package rosatech.common.blocks

import net.minecraft.block.Block
import net.minecraft.block.properties.IProperty
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.stream.Collectors

object RosaBlocks {
    fun init() {}

    @SideOnly(Side.CLIENT)
    fun registerItemModels() {
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

    fun statePropertiesToString(properties: Map<IProperty<*>, Comparable<*>?>): String {
        val stringbuilder = StringBuilder()

        val entries =
            properties.entries
                .stream()
                .sorted(
                    Comparator.comparing { c: Map.Entry<IProperty<*>, Comparable<*>?> -> c.key.name })
                .collect(Collectors.toList())

        for ((property, _) in entries) {
            if (stringbuilder.length != 0) {
                stringbuilder.append(",")
            }

            stringbuilder.append(property.name)
            stringbuilder.append("=")
            stringbuilder.append(getPropertyName(property))
        }

        if (stringbuilder.length == 0) {
            stringbuilder.append("normal")
        }

        return stringbuilder.toString()
    }

    private fun getPropertyName(property: IProperty<*>): String {
        return property.getName()
    }
}
