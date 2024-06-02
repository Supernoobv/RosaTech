package rosatech.common

import gregtech.common.covers.CoverBehaviors.registerBehavior
import gregtech.common.covers.CoverItemFilter
import rosatech.RosaTech
import rosatech.client.renderer.textures.RosaTextures
import rosatech.common.covers.filter.BigItemFilter
import rosatech.common.items.BIG_ITEM_FILTER

class RosaCoverBehaviors {
    companion object {
        fun init() {

            registerBehavior(RosaTech.ID("big_item_filter"), BIG_ITEM_FILTER)
            { def, tile, side -> CoverItemFilter(def, tile, side, "cover.big_item_filter.title", RosaTextures.BIG_ITEM_FILTER_OVERLAY, BigItemFilter()) }
        }
    }
}