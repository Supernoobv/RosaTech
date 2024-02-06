package rosatech.modules.botania.api.capability;

import net.minecraftforge.fluids.FluidTank;

/**
 * A tank that holds mana (A integer).
 *
 *
 */
public interface IManaTank {

    /**
     *
     * @return The Current amount stored in this tank.
     */
    int getAmount();

    /**
     *
     * @return The total capacity of this tank.
     */
    int getCapacity();


    /**
     *
     * @param amount
     *     How much the tank will be filled.
     * @param doFill
     *     If false, the fill will only be simulated.
     * @return Amount of mana that was accepted by the tank.
     */
    int fill(int amount, boolean doFill);

    /**
     *
     * @param amount
     *     Maximum amount of mana that will be removed from the tank.
     * @param doDrain
     *     If false, the drain will only be simulated.
     * @return Amount of mana that was removed from the tank.
     */
    int drain(int amount, boolean doDrain);

    /**
     * Sets the total mana stored in this tank.
     * @param toSet How much mana will be in the tank
     */
    void replace(int toSet);

    /**
     * Initiates a request that can be filled at any time.
     * @param toChange
     */
    void pendRequest(int toChange);

    /**
     * Completes the current pending request.
     */
    void completeRequest(boolean drainOrFill);

    /**
     * Checks if this tank has a pending request.
     *
     * @return True if there's a pending request
     */
    boolean hasPendingRequest();

}
