package rosatech.client.renderer.textures;

import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import gregtech.client.renderer.texture.cube.SimpleSidedCubeRenderer;


public class RosaTextures {

    public static SimpleSidedCubeRenderer MAGICAL_CASING;
    public static SimpleOverlayRenderer MANA_HATCH_INPUT_OVERLAY;
    public static SimpleOverlayRenderer MANA_HATCH_OUTPUT_OVERLAY;
    public static SimpleOverlayRenderer LIFE_ESSENCE_INPUT_OVERLAY;
    public static SimpleOverlayRenderer LIFE_ESSENCE_OUTPUT_OVERLAY;


    public static void preInit() {
        MAGICAL_CASING = new SimpleSidedCubeRenderer("casings/magical_casing");
        MANA_HATCH_INPUT_OVERLAY = new SimpleOverlayRenderer("overlay/machine/overlay_mana_input_hatch");
        MANA_HATCH_OUTPUT_OVERLAY = new SimpleOverlayRenderer("overlay/machine/overlay_mana_output_hatch");
        LIFE_ESSENCE_INPUT_OVERLAY = new SimpleOverlayRenderer("overlay/machine/overlay_life_essence_hatch");
        LIFE_ESSENCE_OUTPUT_OVERLAY = new SimpleOverlayRenderer("overlay/machine/overlay_life_essence_hatch");

    }



}
