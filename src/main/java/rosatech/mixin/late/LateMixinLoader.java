package rosatech.mixin.late;

import rosatech.api.util.RosaMods;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class LateMixinLoader implements ILateMixinLoader {

    @Override
    public List<String> getMixinConfigs() {
        String[] configs = {"mixin.rosatech.botania.json"};
        return Arrays.asList(configs);
    }

    @Override
    public boolean shouldMixinConfigQueue(String mixinConfig) {
        return switch (mixinConfig) {
            case "mixin.rosatech.botania.json" -> RosaMods.Botania.isModLoaded();
            default -> ILateMixinLoader.super.shouldMixinConfigQueue(mixinConfig);
        };
    }
}
