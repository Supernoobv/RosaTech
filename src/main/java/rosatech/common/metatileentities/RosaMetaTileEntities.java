package rosatech.common.metatileentities;

import gregtech.api.GTValues;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import rosatech.RosaTech;
import rosatech.api.util.RosaMods;
import rosatech.modules.blood_magic.metatileentities.multiblock.MetaTileEntityTestVacuumLF;
import rosatech.modules.blood_magic.metatileentities.multiblockpart.MetaTileEntityLifeEssenceHatch;
import rosatech.modules.botania.metatileentities.multi.MetaTileEntityTestVacuum;
import rosatech.modules.botania.metatileentities.multiblockpart.MetaTileEntityManaHatch;
import rosatech.modules.botania.metatileentities.single.mana.RosaSimpleManaMetaTileEntity;

import static gregtech.common.metatileentities.MetaTileEntities.registerMetaTileEntity;

public class RosaMetaTileEntities {

    public static RosaSimpleManaMetaTileEntity MAGICAL_EXTRACTOR;
    public static RosaSimpleManaMetaTileEntity MAGICAL_MACERATOR;
    public static RosaSimpleManaMetaTileEntity MAGICAL_COMPRESSOR;
    public static RosaSimpleManaMetaTileEntity MAGICAL_HAMMER;
    public static RosaSimpleManaMetaTileEntity MAGICAL_FURNACE;
    public static RosaSimpleManaMetaTileEntity MAGICAL_ALLOY_SMELTER;

    public static MetaTileEntityTestVacuum TEST_VACUUM;
    public static MetaTileEntityTestVacuumLF TEST_VACUUM_LF;

    public static MetaTileEntityManaHatch[] MANA_IMPORT_HATCH;
    public static MetaTileEntityManaHatch[] MANA_EXPORT_HATCH;

    public static MetaTileEntityLifeEssenceHatch[] LIFE_ESSENCE_IMPORT_HATCH;
    public static MetaTileEntityLifeEssenceHatch[] LIFE_ESSENCE_EXPORT_HATCH;


    public static void init() {

        if (RosaMods.Botania.isModLoaded()) {
            // Debugging tools


            TEST_VACUUM = registerMetaTileEntity(15004, new MetaTileEntityTestVacuum(rosaId("test_vacuum")));


            MANA_IMPORT_HATCH = new MetaTileEntityManaHatch[GTValues.UHV + 1];
            MANA_EXPORT_HATCH = new MetaTileEntityManaHatch[GTValues.UHV + 1];

            // IDS 15005 - 15035
            for (int i = 0; i < MANA_IMPORT_HATCH.length; i++) {
                String voltageName = GTValues.VN[i].toLowerCase();
                MANA_IMPORT_HATCH[i] = new MetaTileEntityManaHatch(rosaId("mana_hatch.import." + voltageName), i, false);
                MANA_EXPORT_HATCH[i] = new MetaTileEntityManaHatch(rosaId("mana_hatch.export." + voltageName), i, true);

                registerMetaTileEntity(15005 + i, MANA_IMPORT_HATCH[i]);
                registerMetaTileEntity(15020 + i, MANA_EXPORT_HATCH[i]);
            }
        }

        if (RosaMods.BloodMagic.isModLoaded()) {
            TEST_VACUUM_LF = registerMetaTileEntity(15036, new MetaTileEntityTestVacuumLF(rosaId("test_vacuum_lf")));

            LIFE_ESSENCE_IMPORT_HATCH = new MetaTileEntityLifeEssenceHatch[GTValues.UHV + 1];
            LIFE_ESSENCE_EXPORT_HATCH = new MetaTileEntityLifeEssenceHatch[GTValues.UHV + 1];

            // IDS 15037 - 15067
            for (int i = 0; i < MANA_IMPORT_HATCH.length; i++) {
                String voltageName = GTValues.VN[i].toLowerCase();
                LIFE_ESSENCE_IMPORT_HATCH[i] = new MetaTileEntityLifeEssenceHatch(rosaId("life_essence_hatch.import." + voltageName), i, false);
                LIFE_ESSENCE_EXPORT_HATCH[i] = new MetaTileEntityLifeEssenceHatch(rosaId("life_essence_hatch.export." + voltageName), i, true);

                registerMetaTileEntity(15037 + i, LIFE_ESSENCE_IMPORT_HATCH[i]);
                registerMetaTileEntity(15053 + i, LIFE_ESSENCE_EXPORT_HATCH[i]);
            }
        }
    }

    @NotNull
    public static ResourceLocation rosaId(@NotNull String path) {
        return new ResourceLocation(RosaTech.MODID, path);
    }

}