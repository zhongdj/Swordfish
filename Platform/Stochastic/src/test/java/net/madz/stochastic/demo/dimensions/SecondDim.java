package net.madz.stochastic.demo.dimensions;

import net.madz.stochastic.demo.annotations.Action;

public enum SecondDim {
    @Action("Jump")
    X,
    @Action("Yield")
    Y,
    @Action("Zigzag")
    Z
}
