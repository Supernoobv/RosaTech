package rosatech.integration.thaumcraft.block

import gregtech.api.block.VariantBlock
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLiving
import net.minecraft.util.IStringSerializable
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

class BlockLargeMultiblockCasingThaum : VariantBlock<BlockLargeMultiblockCasingThaum.CasingType> {

    constructor() : super(Material.IRON) {
        setTranslationKey("large_multiblock_casing_thaumcraft")
        setHardness(5.0f)
        setResistance(10.0f)
        setSoundType(SoundType.METAL)
        setHarvestLevel("wrench", 2)
        defaultState = getState(CasingType.ARCANE_SEALED_THAUMIUM_CASING)
    }

    override fun canCreatureSpawn(
        state: IBlockState,
        world: IBlockAccess,
        pos: BlockPos,
        type: EntityLiving.SpawnPlacementType
    ): Boolean = false

    enum class CasingType(private val Name: String) : IStringSerializable {

        ARCANE_SEALED_THAUMIUM_CASING("arcane_sealed_casing"),
        ELDRITCH_VOID_METAL_CASING("eldritch_casing");

        override fun getName(): String {
            return Name
        }
    }
}