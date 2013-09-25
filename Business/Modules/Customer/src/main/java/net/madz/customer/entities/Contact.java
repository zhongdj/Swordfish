package net.madz.customer.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import net.madz.authorization.entities.StandardObject;

@Entity
@Table(name="contact")
public class Contact extends StandardObject {

    private static final long serialVersionUID = 1L;
    @Column(nullable = false, length = 40)
    private String name;
    @Column(nullable = false, length = 80)
    private String email;
    @Column(name = "BIRTH_YEAR")
    private int birthYear;
    @Column(nullable = false)
    private boolean male;
    @Column(name = "CELL_PHONE")
    private String cellPhone;
    @Column(name = "WORK_CELL_PHONE", length = 20)
    private String workCellPhone;
    @Column(name = "OFFICE_PHONE", length = 30)
    private String officePhone;
    @Column(name = "HOME_PHONE", length = 30)
    private String homePhone;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }

    public boolean isMale() {
        return male;
    }

    public void setMale(boolean male) {
        this.male = male;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public String getWorkCellPhone() {
        return workCellPhone;
    }

    public void setWorkCellPhone(String workCellPhone) {
        this.workCellPhone = workCellPhone;
    }

    public String getOfficePhone() {
        return officePhone;
    }

    public void setOfficePhone(String officePhone) {
        this.officePhone = officePhone;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }
}
