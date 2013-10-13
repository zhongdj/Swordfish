package net.madz.contract.sessions;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

import net.madz.common.entities.Address;

import org.eclipse.persistence.oxm.annotations.XmlPath;

public class CreateContractRequest implements Serializable {

    private static final long serialVersionUID = -4996775530902230740L;

    @XmlPath("customer-info/id/text()")
    private String customerId;

    @XmlPath("customer-info/fullName/text()")
    private String customerFullName;

    @XmlPath("customer-info/shortName/text()")
    private String customerShortName;

    @XmlPath("contact-info/name/text()")
    private String contactName;

    @XmlPath("contact-info/email/text()")
    private String contactEmail;

    @XmlPath("contact-info/male/text()")
    private boolean contactMale;

    @XmlPath("contact-info/cellPhone/text()")
    private String contactCellPhone;

    @XmlPath("contact-info/workCellPhone/text()")
    private String workCellPhone;

    @XmlPath("contact-info/officePhone/text()")
    private String officePhone;

    @XmlPath("contact-info/homePhone/text()")
    private String homePhone;

    @XmlPath("contract-info/name/text()")
    private String contractName;

    @XmlPath("contract-info/startDate/text()")
    private Date contractStartDate;

    @XmlPath("contract-info/endDate/text()")
    private Date contractEndDate;

    @XmlPath("unitProjects-info/")
    private UnitProjectInfo[] unitProjects;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerFullName() {
        return customerFullName;
    }

    public void setCustomerFullName(String customerFullName) {
        this.customerFullName = customerFullName;
    }

    public String getCustomerShortName() {
        return customerShortName;
    }

    public void setCustomerShortName(String customerShortName) {
        this.customerShortName = customerShortName;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public boolean isContactMale() {
        return contactMale;
    }

    public void setContactMale(boolean contactMale) {
        this.contactMale = contactMale;
    }

    public String getContactCellPhone() {
        return contactCellPhone;
    }

    public void setContactCellPhone(String contactCellPhone) {
        this.contactCellPhone = contactCellPhone;
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

    public String getContractName() {
        return contractName;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    public Date getContractStartDate() {
        return contractStartDate;
    }

    public void setContractStartDate(Date contractStartDate) {
        this.contractStartDate = contractStartDate;
    }

    public Date getContractEndDate() {
        return contractEndDate;
    }

    public void setContractEndDate(Date contractEndDate) {
        this.contractEndDate = contractEndDate;
    }

    public UnitProjectInfo[] getUnitProjects() {
        return unitProjects;
    }

    public void setUnitProjects(UnitProjectInfo[] unitProjects) {
        this.unitProjects = unitProjects;
    }

    public static class UnitProjectInfo implements Serializable {

        private static final long serialVersionUID = 5763561065487553409L;

        private String name;

        @XmlPath("address-info/")
        private Address address;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        @Override
        public String toString() {
            return "UnitProjectInfo [name=" + name + ", address=" + address + "]";
        }
    }

    @Override
    public String toString() {
        return "CreateContractRequestTO [customerId=" + customerId + ", customerFullName=" + customerFullName
                + ", customerShortName=" + customerShortName + ", contactName=" + contactName + ", contactEmail="
                + contactEmail + ", contactMale=" + contactMale + ", contactCellPhone=" + contactCellPhone
                + ", workCellPhone=" + workCellPhone + ", officePhone=" + officePhone + ", homePhone=" + homePhone
                + ", contractName=" + contractName + ", contractStartDate=" + contractStartDate + ", contractEndDate="
                + contractEndDate + ", unitProjects=" + Arrays.toString(unitProjects) + "]";
    }
}
