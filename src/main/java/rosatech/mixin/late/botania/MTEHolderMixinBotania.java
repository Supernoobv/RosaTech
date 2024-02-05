package rosatech.mixin.late.botania;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import vazkii.botania.api.mana.IManaReceiver;

@Mixin(MetaTileEntityHolder.class)
public class MTEHolderMixinBotania implements IManaReceiver {
    @Shadow private MetaTileEntity metaTileEntity;

    @Override
    public boolean isFull() {
        return false;
    }

    @Override
    public void recieveMana(int i) {
        if (metaTileEntity instanceof IManaReceiver mte ) {
            mte.recieveMana(i);
        }
    }

    @Override
    public boolean canRecieveManaFromBursts() {
        if (metaTileEntity instanceof IManaReceiver mte ) {
            return mte.canRecieveManaFromBursts();
        }
        return false;
    }

    @Override
    public int getCurrentMana() {
        if (metaTileEntity instanceof IManaReceiver mte ) {
            return mte.getCurrentMana();
        }
        return 0;
    }
}
