package rosatech.modules.blood_magic.api.capability.impl;

import rosatech.modules.blood_magic.api.capability.ISoulTank;

public class SoulTank implements ISoulTank {
    protected int capacity;
    protected int amount;

    public SoulTank(int capacity, int amount) {
        this.amount = amount;
        this.capacity = capacity;
    }

    public SoulTank(int capacity) {
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
}
