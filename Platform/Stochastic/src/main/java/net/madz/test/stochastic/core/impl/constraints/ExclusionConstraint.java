package net.madz.test.stochastic.core.impl.constraints;

import net.madz.test.stochastic.core.IConstraint;

public class ExclusionConstraint implements IConstraint {

    private final String[] constraintSequence;
    private final int lastExclusionIndex;

    public ExclusionConstraint(String[] constraintSequence) {
        if ( null == constraintSequence || 0 >= constraintSequence.length ) {
            throw new NullPointerException("Argument dimensionConstraintSequence cannot be null or empty.");
        }
        this.constraintSequence = constraintSequence;
        int index = constraintSequence.length - 1;
        for ( int i = constraintSequence.length - 1; i >= 0; i-- ) {
            if ( null != constraintSequence[i] && !IConstraint.EXCLUSION_PLACEHOLDER.equals(constraintSequence[i]) ) {
                index = i;
                break;
            }
        }
        this.lastExclusionIndex = index;
    }

    @Override
    public ConstraintType getConstraintType() {
        return IConstraint.ConstraintType.Exclusion;
    }

    @Override
    public boolean skip(int depth, String[] choices) {
        if ( this.lastExclusionIndex != depth - 1 ) {
            return false;
        }
        for ( int i = 0; i < depth; i++ ) {
            if ( null == constraintSequence[i] || IConstraint.EXCLUSION_PLACEHOLDER.equals(constraintSequence[i]) ) {
                continue;
            }
            if ( !constraintSequence[i].equals(choices[i]) ) {
                return false;
            }
            if ( i == ( depth - 1 ) ) {
                return true;
            }
        }
        return false;
    }
}
