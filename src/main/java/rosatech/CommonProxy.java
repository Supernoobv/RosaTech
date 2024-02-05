package rosatech;

import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rosatech.RosaTech;
import rosatech.api.RosaLog;
import rosatech.common.recipes.RosaRecipeAdditions;

@Mod.EventBusSubscriber(modid = RosaTech.MODID)

public class CommonProxy {

    public void preLoad() {

    }

    public void load() {

    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        RosaLog.logger.info("Registering recipe normal...");
        RosaRecipeAdditions.init();
    }

}
