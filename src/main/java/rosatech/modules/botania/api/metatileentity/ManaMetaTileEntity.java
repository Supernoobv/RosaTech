package rosatech.modules.botania.api.metatileentity;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.ColourMultiplier;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.util.GTUtility;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.cube.SimpleSidedCubeRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.ArrayUtils;
import rosatech.client.renderer.textures.RosaTextures;
import rosatech.api.capability.impl.IntegerTank;
import rosatech.modules.botania.api.recipes.RecipeLogicManaPowered;
import vazkii.botania.api.mana.IManaReceiver;

public class ManaMetaTileEntity extends MetaTileEntity implements IManaReceiver {

    protected final ICubeRenderer renderer;
    protected RecipeLogicManaPowered workableHandler;
    protected IntegerTank manaTank;


    public ManaMetaTileEntity(ResourceLocation metaTileEntityId, RecipeMap<?> recipeMap, ICubeRenderer renderer) {
        super(metaTileEntityId);
        this.manaTank = new IntegerTank(35000);
        this.workableHandler = new RecipeLogicManaPowered(this, recipeMap, manaTank, 1.0);
        this.renderer = renderer;
    }

    @SideOnly(Side.CLIENT)
    protected SimpleSidedCubeRenderer getBaseRenderer() {
        return RosaTextures.MAGICAL_CASING;
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        IVertexOperation[] colouredPipeline = ArrayUtils.add(pipeline,
                new ColourMultiplier(GTUtility.convertRGBtoOpaqueRGBA_CL(getPaintingColorForRendering())));
        getBaseRenderer().render(renderState, translation, colouredPipeline);
        renderer.renderOrientedState(renderState, translation, pipeline, getFrontFacing(), workableHandler.isActive(),
                workableHandler.isWorkingEnabled());
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new ManaMetaTileEntity(metaTileEntityId, getRecipeMap(), renderer);
    }

    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        return null;
    }

    @Override
    public int getCurrentMana() {
        return manaTank.getAmount();
    }

    @Override
    public boolean isFull() {
        return !manaTank.canFill();
    }

    @Override
    public void recieveMana(int i) {
        manaTank.fill(i, true);
    }

    @Override
    public boolean canRecieveManaFromBursts() {
        return true;
    }
}
