package rosatech

import gregtech.GTInternalTags
import gregtech.api.block.VariantItemBlock
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.item.crafting.IRecipe
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.FMLConstructionEvent
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import rosatech.api.capability.RosaSimpleCapabilityManager
import rosatech.api.world.data.DimensionalNetData
import rosatech.client.renderer.textures.RosaGuiTextures
import rosatech.client.renderer.textures.RosaTextures
import rosatech.common.RosaCoverBehaviors
import rosatech.common.RosaEventHandlers
import rosatech.common.blocks.RosaMetaBlocks
import rosatech.common.items.RosaMetaItems
import rosatech.common.metatileentities.RosaMetaTileEntities
import rosatech.common.metatileentities.RosaMultiblockAbility
import rosatech.common.recipes.RosaRecipeMaps
import rosatech.common.world.data.net.DimensionalNetBehaviors
import rosatech.integration.thaumcraft.recipe.RTThaumcraftRecipeAdditions
import java.util.function.Function

const val MODID = Tags.MODID

@Mod(
    modid = Tags.MODID,
    version = Tags.VERSION,
    name = Tags.MODNAME,
    dependencies = GTInternalTags.DEP_VERSION_STRING,
    acceptedMinecraftVersions = "[1.12.2]",
    modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter"
)
class RosaTech {

    @EventHandler
    fun onConstruction(event: FMLConstructionEvent) {
        MinecraftForge.EVENT_BUS.register(this)
        MinecraftForge.EVENT_BUS.register(RosaEventHandlers())
    }

    @EventHandler
    fun preInit(event: FMLPreInitializationEvent) {

        RosaTextures.preInit()
        RosaGuiTextures.preInit()

        RosaRecipeMaps.preInit()
        RosaMultiblockAbility.preInit()

        RosaSimpleCapabilityManager.init()

        DimensionalNetBehaviors.init()
        
        RosaMetaTileEntities.init()
        RosaMetaItems.preInit()

        RosaMetaBlocks.init()
    }

    @EventHandler
    fun init(event: FMLInitializationEvent) {
        RosaCoverBehaviors.init()
    }

    @EventHandler
    fun serverStopped(event: FMLServerStoppedEvent) {
        DimensionalNetData.netMap.clear()
    }

    @SubscribeEvent
    fun registerItems(event: RegistryEvent.Register<Item>) {
        RosaMetaItems.registerItems()

        val registry = event.registry

        if (Loader.isModLoaded("thaumcraft")) {
            registry.register(createItemBlock(RosaMetaBlocks.LARGE_MULTIBLOCK_CASING_THAUM, ::VariantItemBlock))
        }
    }

    fun <T: Block> createItemBlock(block: T, producer: Function<T, ItemBlock>): ItemBlock {
        val itemBlock = producer.apply(block)
        itemBlock.setRegistryName(block.registryName)
        return itemBlock
    }

    @SubscribeEvent
    fun registerBlocks(event: RegistryEvent.Register<Block>) {
        val registry = event.registry

        if (Loader.isModLoaded("thaumcraft")) {
            registry.register(RosaMetaBlocks.LARGE_MULTIBLOCK_CASING_THAUM)
        }
    }

    @SubscribeEvent
    fun registerRecipes(event: RegistryEvent.Register<IRecipe>) {
        if (Loader.isModLoaded("thaumcraft")) {
            RTThaumcraftRecipeAdditions.init()
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    fun registerModels(event: ModelRegistryEvent) {
        RosaMetaBlocks.registerItemModels()
    }

    companion object {

        fun ID(path: String): ResourceLocation {
            return ResourceLocation(MODID, path)
        }

    }
}
