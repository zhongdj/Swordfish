package net.madz.test.stochastic.core.impl.constraints;

import net.madz.test.stochastic.core.IConstraint;

public class ConstraintCombination implements IConstraint {

    private final IConstraint[] inclusions;
    private final IConstraint[] exclusions;

    public ConstraintCombination(IConstraint[] inclusions, IConstraint[] exclusions) {
        super();
        this.inclusions = inclusions;
        this.exclusions = exclusions;
    }

    @Override
    public ConstraintType getConstraintType() {
        return ConstraintType.Combination;
    }

    @Override
    public boolean skip(int depth, String[] choices) {
        if ( null != inclusions && 0 < inclusions.length ) {
            boolean skip = true;
            for ( IConstraint inclusion : inclusions ) {
                skip &= inclusion.skip(depth, choices);
            }
            if ( skip ) {
                return true;
            }
        }
        if ( null != exclusions && 0 < exclusions.length ) {
            for ( IConstraint exclusion : exclusions ) {
                if ( exclusion.skip(depth, choices) ) {
                    return true;
                }
            }
        }
        return false;
    }
}
