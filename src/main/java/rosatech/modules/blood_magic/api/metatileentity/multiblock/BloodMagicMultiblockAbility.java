package rosatech.modules.blood_magic.api.metatileentity.multiblock;

import WayofTime.bloodmagic.core.data.SoulNetwork;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import rosatech.api.capability.IIntegerTank;

@SuppressWarnings("InstantiationOfUtilityClass")
public class BloodMagicMultiblockAbility<T> {

    public static final MultiblockAbility<IIntegerTank> IMPORT_LIFE_ESSENCE = new MultiblockAbility<>("import_life_essence");
    public static final MultiblockAbility<IIntegerTank> EXPORT_LIFE_ESSENCE = new MultiblockAbility<>("export_life_essence");

}
