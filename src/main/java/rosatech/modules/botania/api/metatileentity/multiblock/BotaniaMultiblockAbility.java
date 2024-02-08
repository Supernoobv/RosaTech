package rosatech.modules.botania.api.metatileentity.multiblock;

import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import rosatech.api.capability.IIntegerTank;

@SuppressWarnings("InstantiationOfUtilityClass")
public class BotaniaMultiblockAbility<T> {

    public static final MultiblockAbility<IIntegerTank> IMPORT_MANA = new MultiblockAbility<>("import_mana");

    public static final MultiblockAbility<IIntegerTank> EXPORT_MANA = new MultiblockAbility<>("export_mana");

}
