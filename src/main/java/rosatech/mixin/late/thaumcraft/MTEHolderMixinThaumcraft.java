package rosatech.mixin.late.thaumcraft;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import net.minecraft.util.EnumFacing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;

@Mixin(MetaTileEntityHolder.class)
public class MTEHolderMixinThaumcraft implements IAspectContainer, IEssentiaTransport {
    @Shadow
    private MetaTileEntity metaTileEntity;


    @Override
    public AspectList getAspects() {
        if (metaTileEntity instanceof IAspectContainer container) {
            return container.getAspects();
        }


        return new AspectList();
    }

    @Override
    public void setAspects(AspectList aspectList) {
        if (metaTileEntity instanceof IAspectContainer container) {
            container.setAspects(aspectList);
        }
    }

    @Override
    public boolean doesContainerAccept(Aspect aspect) {
        if (metaTileEntity instanceof IAspectContainer container) {
            container.doesContainerAccept(aspect);
        }

        return false;
    }

    @Override
    public int addToContainer(Aspect aspect, int i) {
        if (metaTileEntity instanceof IAspectContainer container) {
            return container.addToContainer(aspect, i);
        }

        return 0;
    }

    @Override
    public boolean takeFromContainer(Aspect aspect, int i) {
        if (metaTileEntity instanceof IAspectContainer container) {
            return container.takeFromContainer(aspect, i);
        }

        return false;
    }

    @Override
    public boolean takeFromContainer(AspectList aspectList) {
        if (metaTileEntity instanceof IAspectContainer container) {
            return container.takeFromContainer(aspectList);
        }
        return false;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect aspect, int i) {
        if (metaTileEntity instanceof IAspectContainer container) {
            container.doesContainerContainAmount(aspect, i);
        }

        return false;
    }

    @Override
    public boolean doesContainerContain(AspectList aspectList) {
        if (metaTileEntity instanceof IAspectContainer container) {
            container.doesContainerContain(aspectList);
        }
        return false;
    }

    @Override
    public int containerContains(Aspect aspect) {
        if (metaTileEntity instanceof IAspectContainer container) {
            return container.containerContains(aspect);
        }

        return 0;
    }

    @Override
    public boolean isConnectable(EnumFacing enumFacing) {
        if (metaTileEntity instanceof IEssentiaTransport transport) {
            return transport.isConnectable(enumFacing);
        }

        return false;
    }

    @Override
    public boolean canInputFrom(EnumFacing enumFacing) {
        if (metaTileEntity instanceof IEssentiaTransport transport) {
            return transport.canInputFrom(enumFacing);
        }

        return false;
    }

    @Override
    public boolean canOutputTo(EnumFacing enumFacing) {
        if (metaTileEntity instanceof IEssentiaTransport transport) {
            return transport.canOutputTo(enumFacing);
        }

        return false;
    }

    @Override
    public void setSuction(Aspect aspect, int i) {
        if (metaTileEntity instanceof IEssentiaTransport transport) {
            transport.setSuction(aspect, i);
        }
    }

    @Override
    public Aspect getSuctionType(EnumFacing enumFacing) {
        if (metaTileEntity instanceof IEssentiaTransport transport) {
            return transport.getSuctionType(enumFacing);
        }

        return null;
    }

    @Override
    public int getSuctionAmount(EnumFacing enumFacing) {
        if (metaTileEntity instanceof IEssentiaTransport transport) {
            return transport.getSuctionAmount(enumFacing);
        }

        return 0;
    }

    @Override
    public int takeEssentia(Aspect aspect, int i, EnumFacing enumFacing) {
        if (metaTileEntity instanceof IEssentiaTransport transport) {
            return transport.takeEssentia(aspect, i, enumFacing);
        }

        return 0;
    }

    @Override
    public int addEssentia(Aspect aspect, int i, EnumFacing enumFacing) {
        if (metaTileEntity instanceof IEssentiaTransport transport) {
            return transport.addEssentia(aspect, i, enumFacing);
        }

        return 0;
    }

    @Override
    public Aspect getEssentiaType(EnumFacing enumFacing) {
        if (metaTileEntity instanceof IEssentiaTransport transport) {
            return transport.getEssentiaType(enumFacing);
        }

        return null;
    }

    @Override
    public int getEssentiaAmount(EnumFacing enumFacing) {
        if (metaTileEntity instanceof IEssentiaTransport transport) {
            return transport.getEssentiaAmount(enumFacing);
        }

        return 0;
    }

    @Override
    public int getMinimumSuction() {
        if (metaTileEntity instanceof IEssentiaTransport transport) {
            return transport.getMinimumSuction();
        }

        return 0;
    }
}
