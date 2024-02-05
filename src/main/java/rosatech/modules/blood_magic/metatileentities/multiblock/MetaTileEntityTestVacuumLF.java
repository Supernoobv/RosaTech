package rosatech.modules.blood_magic.metatileentities.multiblock;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.core.sound.GTSoundEvents;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import rosatech.modules.blood_magic.api.metatileentity.multiblock.BloodMagicMultiblockAbility;
import rosatech.modules.blood_magic.api.recipes.RecipeLogicLifeEssence;
import rosatech.modules.botania.api.metatileentity.multiblock.BotaniaMultiblockAbility;
import rosatech.modules.botania.api.recipes.RecipeLogicMana;
import rosatech.modules.botania.recipes.BotaniaRecipeMaps;

public class MetaTileEntityTestVacuumLF extends RecipeMapMultiblockController {
    public MetaTileEntityTestVacuumLF(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, BotaniaRecipeMaps.MANA_VACUUM_RECIPES);
        this.recipeMapWorkable = new RecipeLogicLifeEssence(this);
    }


    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityTestVacuumLF(metaTileEntityId);
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("XXX", "XXX", "XXX")
                .aisle("XXX", "X#X", "XXX")
                .aisle("XXX", "XSX", "XXX")
                .where('S', selfPredicate())
                .where('X', states(getCasingState()).setMinGlobalLimited(14).or(autoAbilities()).or(abilities(BloodMagicMultiblockAbility.IMPORT_LIFE_ESSENCE).or(abilities(BloodMagicMultiblockAbility.EXPORT_LIFE_ESSENCE))))
                .where('#', air())
                .build();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return Textures.FROST_PROOF_CASING;
    }

    protected IBlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.ALUMINIUM_FROSTPROOF);
    }

    @Override
    public SoundEvent getBreakdownSound() {
        return GTSoundEvents.BREAKDOWN_ELECTRICAL;
    }

    @SideOnly(Side.CLIENT)
    @NotNull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.VACUUM_FREEZER_OVERLAY;
    }


}
