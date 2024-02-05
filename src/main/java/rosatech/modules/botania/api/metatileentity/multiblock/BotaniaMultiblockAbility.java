package rosatech.modules.botania.api.metatileentity.multiblock;

import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import rosatech.modules.botania.api.capability.IManaTank;

@SuppressWarnings("InstantiationOfUtilityClass")
public class BotaniaMultiblockAbility<T> {

    public static final MultiblockAbility<IManaTank> IMPORT_MANA = new MultiblockAbility<>("import_mana");

    public static final MultiblockAbility<IManaTank> EXPORT_MANA = new MultiblockAbility<>("export_mana");

}
