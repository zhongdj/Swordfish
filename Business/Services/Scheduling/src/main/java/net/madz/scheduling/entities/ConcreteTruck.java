package net.madz.scheduling.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import net.madz.authorization.entities.MultiTenancyEntity;

@Entity
@Table(name = "concrete_truck")
public class ConcreteTruck extends MultiTenancyEntity {

	private static final long serialVersionUID = -1132231092160514575L;

	@Column(name = "LICENCE_PLATE_NUMBER", nullable = false)
	private String licencePlateNumber;

	@Column(name = "RATED_CAPACITY", nullable = false)
	private double ratedCapacity;
	
	@Column(name = "DRIVER_NAME", nullable = true)
	private String driverName;
	
	@Column(name = "DRIVER_PHONE_NUMBER", nullable = true)
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
