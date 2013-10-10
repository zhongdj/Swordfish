package net.madz.common.entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("C")
public class Concrete extends Mixture {

    private static final long serialVersionUID = 5253902172558137092L;

    public static enum StrengthGrade implements IStrengthGrade {
        C10(1001),
        C15(1002),
        C20(1003),
        C25(1004),
        C30(1005),
        C35(1006),
        C40(1007),
        C45(1008),
        C50(1009),
        C55(1010),
        C60(1011),
        C65(1012),
        C70(1013),
        C75(1014),
        C80(1015),
        C85(1016),
        C90(1017),
        C95(1018),
        C100(1019);

        private StrengthGrade(int value) {
            this.value = value;
        }

        private int value;

        @Override
        public String getName() {
            return name();
        }

        public int getValue() {
            return value;
        }
    }

    public StrengthGrade getGradeEnum() {
        if ( "100".equals(getGradeName()) ) {
            return StrengthGrade.C100;
        } else {
            return StrengthGrade.valueOf(getGradeName());
        }
    }

    public void setGrade(StrengthGrade grade) {
        switch (grade) {
            case C100:
                setGradeName("100");
                break;
            default:
                setGradeName(grade.name());
                break;
        }
    }
}
