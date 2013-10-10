package net.madz.common.entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("M")
public class Mortar extends Mixture {

    private static final long serialVersionUID = -8446619721481346638L;

    public void setGrade(StrengthGrade grade) {
        this.setGradeName(grade.getName());
    }

    public static enum StrengthGrade implements IStrengthGrade {
        M2_5("M2.5", 2001),
        M5_0("M5.0", 2002),
        M7_5("M7.5", 2003),
        M10("M10", 2004),
        M15("M15", 2005),
        M20("M20", 2006);

        private int value;

        private String name;

        private StrengthGrade(String name, int value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public String getName() {
            return this.name;
        }

        public int getValue() {
            return value;
        }
    }

    public StrengthGrade getGradeEnum() {
        return StrengthGrade.valueOf(getGradeName());
    }
}
