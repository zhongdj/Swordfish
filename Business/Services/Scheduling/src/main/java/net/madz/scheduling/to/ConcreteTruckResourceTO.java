package net.madz.scheduling.to;

import net.madz.binding.annotation.Binding;

import org.eclipse.persistence.oxm.annotations.XmlPath;

public class ConcreteTruckResourceTO  {

    @Binding(name = "id")
    @XmlPath("id/text()")
    private Long id;

    @Binding(name = "concreteTruck.licencePlateNumber")
    @XmlPath("licencePlateNumber/text()")
    private String licencePlateNumber;

    @XmlPath("ratedCapacity/text()")
    @Binding(name = "concreteTruck.ratedCapacity")
    private double ratedCapacity;

    @Binding(name = "concreteTruck.driverName")
    @XmlPath("driverName/text()")
    private String driverName;

    @Binding(name = "concreteTruck.driverPhoneNumber")
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

