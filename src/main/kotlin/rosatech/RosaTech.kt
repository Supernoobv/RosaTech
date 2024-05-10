package rosatech

import gregtech.GTInternalTags
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLConstructionEvent
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent
import rosatech.client.renderer.textures.RosaTextures
import rosatech.common.RosaEventHandlers
import rosatech.common.blocks.RosaBlocks
import rosatech.common.metatileentities.RosaMetaTileEntities
import rosatech.common.world.data.DimensionalNetData

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

        RosaMetaTileEntities.init()
        RosaBlocks.init()
    }

    @EventHandler
    fun serverStopped(event: FMLServerStoppedEvent) {
        DimensionalNetData.netMap.clear()
    }

    companion object {

        fun ID(path: String): ResourceLocation {
            return ResourceLocation(MODID, path)
        }

    }
}
