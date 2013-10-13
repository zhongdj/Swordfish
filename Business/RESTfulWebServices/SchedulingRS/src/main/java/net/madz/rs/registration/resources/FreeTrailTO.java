package net.madz.rs.registration.resources;

import java.io.Serializable;

import net.madz.common.entities.Address;

import org.eclipse.persistence.oxm.annotations.XmlPath;

public class FreeTrailTO implements Serializable {

    private static final long serialVersionUID = 5165402349203097026L;

    @XmlPath("user-info/userName/text()")
    private String userName;

    @XmlPath("user-info/email/text()")
    private String email;

    @XmlPath("user-info/fullName/text()")
    private String fullName;

    @XmlPath("user-info/password/text()")
    private String password;

    @XmlPath("user-info/passConfirm/text()")
    private String passConfirm;

    @XmlPath("tenant-info/companyName/text()")
    private String companyName;

    @XmlPath("tenant-info/address-info")
    private Address address;

    @XmlPath("tenant-info/phoneNumber/text()")
    private String phoneNumber;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassConfirm() {
        return passConfirm;
    }

    public void setPassConfirm(String passConfirm) {
        this.passConfirm = passConfirm;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "FreeTrailTO [userName=" + userName + ", email=" + email + ", fullName=" + fullName + ", password="
                + password + ", passConfirm=" + passConfirm + ", companyName=" + companyName + ", address=" + address
                + ", phoneNumber=" + phoneNumber + "]";
    }
}
