package rosatech.api.capability.impl;

import rosatech.api.capability.IIntegerTank;

/**
 * Reference implementation of {@link IIntegerTank}
 */
public class IntegerTank implements IIntegerTank {

    protected int capacity;
    protected int amount;
    protected int pendingRequest;


    public IntegerTank(int capacity, int amount) {
        this.amount = amount;
        this.capacity = capacity;
    }

    public IntegerTank(int capacity) {
        this(capacity, 0);
    }


    public boolean canFill() {
        return amount < capacity;
    }

    public boolean canDrain() {
        return amount > 0;
    }

    public int fillInternal(int toFill, boolean doFill) {
        if (toFill <= 0) {
            return 0;
        }

        if (!doFill) {
            return Math.min(capacity - amount, toFill);
        }

        int filled = capacity - amount;

        if (toFill < filled) {
            amount += toFill;
            filled = amount;
        }
        else {
            amount = capacity;
        }

        return filled;
    }

    public int drainInternal(int maxDrain, boolean doDrain) {
        if (maxDrain <= 0) {
            return 0;
        }

        int drained = maxDrain;

        if (amount < maxDrain) {
            maxDrain = amount;
        }

        if (doDrain) {
            amount -= drained;
            if (amount <= 0) {
                amount = 0;
            }
        }

        return drained;

    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public int fill(int amount, boolean doFill) {
        if (!canFill()) {
            return 0;
        }

        return fillInternal(amount, doFill);
    }

    @Override
    public int drain(int amount, boolean doDrain) {
        if (!canDrain()) {
            return 0;
        }

        return drainInternal(amount, doDrain);
    }

    public void replace(int toSet) {
        if (toSet > capacity) {
            toSet = capacity;
        }

        amount = toSet;
    }

    @Override
    public void pendRequest(int toChange) {
        if (toChange < 0) {
            return;
        }

        pendingRequest = toChange;
    }

    @Override
    public void completeRequest(boolean drainOrFill) {
        if (drainOrFill) {
            fill(pendingRequest, true);
        } else {
            drain(pendingRequest, true);
        }

        pendingRequest = 0;
    }

    @Override
    public boolean hasPendingRequest() {
        return pendingRequest > 0;
    }

}
