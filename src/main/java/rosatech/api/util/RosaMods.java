package rosatech.api.util;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public enum RosaMods {

    Botania(Names.BOTANIA),
    BloodMagic(Names.BLOOD_MAGIC);
    public static class Names {

        public static final String BOTANIA = "botania";
        public static final String BLOOD_MAGIC = "bloodmagic";
    }

    private final String ID;
    private final Function<RosaMods, Boolean> extraCheck;
    protected Boolean modLoaded;

    RosaMods(String ID) {
        this.ID = ID;
        this.extraCheck = null;
    }


    public boolean isModLoaded() {
        if (this.modLoaded == null) {
            this.modLoaded = Loader.isModLoaded(this.ID);
            if (this.modLoaded) {
                if (this.extraCheck != null && !this.extraCheck.apply(this)) {
                    this.modLoaded = false;
                }
            }
        }
        return this.modLoaded;
    }

    /**
     * Throw an exception if this mod is found to be loaded.
     * <strong>This must be called in or after
     * {@link net.minecraftforge.fml.common.event.FMLPreInitializationEvent}!</strong>
     */
    public void throwIncompatibilityIfLoaded(String... customMessages) {
        if (isModLoaded()) {
            String modName = TextFormatting.BOLD + ID + TextFormatting.RESET;
            List<String> messages = new ArrayList<>();
            messages.add(modName + " mod detected, this mod is incompatible with GregTech CE Unofficial.");
            messages.addAll(Arrays.asList(customMessages));
            if (FMLLaunchHandler.side() == Side.SERVER) {
                throw new RuntimeException(String.join(",", messages));
            } else {
                throwClientIncompatibility(messages);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private static void throwClientIncompatibility(List<String> messages) {
        throw new ModIncompabilityException(messages);
    }

    public ItemStack getItem(@NotNull String name) {
        return getItem(name, 0, 1, null);
    }

    @NotNull
    public ItemStack getItem(@NotNull String name, int meta) {
        return getItem(name, meta, 1, null);
    }

    @NotNull
    public ItemStack getItem(@NotNull String name, int meta, int amount) {
        return getItem(name, meta, amount, null);
    }

    @NotNull
    public ItemStack getItem(@NotNull String name, int meta, int amount, @Nullable String nbt) {
        if (!isModLoaded()) {
            return ItemStack.EMPTY;
        }
        return GameRegistry.makeItemStack(ID + ":" + name, meta, amount, nbt);
    }

    // Helpers for the extra checker

    /** Test if the mod version string contains the passed value. */
    private static Function<RosaMods, Boolean> versionContains(String versionPart) {
        return mod -> {
            if (mod.ID == null) return false;
            if (!mod.isModLoaded()) return false;
            ModContainer container = Loader.instance().getIndexedModList().get(mod.ID);
            if (container == null) return false;
            return container.getVersion().contains(versionPart);
        };
    }

    /** Test if the mod version string does not contain the passed value. */
    private static Function<RosaMods, Boolean> versionExcludes(String versionPart) {
        return mod -> {
            if (mod.ID == null) return false;
            if (!mod.isModLoaded()) return false;
            ModContainer container = Loader.instance().getIndexedModList().get(mod.ID);
            if (container == null) return false;
            return !container.getVersion().contains(versionPart);
        };
    }
}
