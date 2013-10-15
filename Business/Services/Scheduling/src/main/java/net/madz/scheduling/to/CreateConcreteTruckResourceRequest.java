package net.madz.scheduling.to;

import org.eclipse.persistence.oxm.annotations.XmlPath;

public class CreateConcreteTruckResourceRequest  {

    @XmlPath("licencePlateNumber/text()")
    private String licencePlateNumber;

    @XmlPath("ratedCapacity/text()")
    private double ratedCapacity;

    @XmlPath("driverName/text()")
    private String driverName;

    @XmlPath("driverPhoneNumber/text()")
    private String driverPhoneNumber;

    public String getLicencePlateNumber() {
        return licencePlateNumber;
    }

    public void setLicencePlateNumber(String licencePlateNumber) {
        this.licencePlateNumber = licencePlateNumber;
    }

    public double getRatedCapacity() {
        return ratedCapacity;
    }

    public void setRatedCapacity(double ratedCapacity) {
        this.ratedCapacity = ratedCapacity;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverPhoneNumber() {
        return driverPhoneNumber;
    }

    public void setDriverPhoneNumber(String driverPhoneNumber) {
        this.driverPhoneNumber = driverPhoneNumber;
    }

}

