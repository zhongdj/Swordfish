package net.madz.test.stochastic.core.impl.constraints;

import net.madz.test.stochastic.core.IConstraint;

public class InclusionConstraint implements IConstraint {

    private final String[] constraintSequence;

    public InclusionConstraint(String[] dimensionConstraintSequence) {
        this.constraintSequence = dimensionConstraintSequence;
    }

    @Override
    public ConstraintType getConstraintType() {
        return IConstraint.ConstraintType.Inclusion;
    }

    @Override
    public boolean skip(int depth, String[] choices) {
        // for (int i = 0; i < constraintSequence.length && i < choices.length;
        // i++) {
        if ( null == constraintSequence[depth - 1] || IConstraint.INCLUSION_PLACEHOLDER.equals(constraintSequence[depth - 1]) ) {
            return false;
        }
        if ( !constraintSequence[depth - 1].equals(choices[depth - 1]) ) {
            return true;
        }
        // }
        return false;
    }
}
