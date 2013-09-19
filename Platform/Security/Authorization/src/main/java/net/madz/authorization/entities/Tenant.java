package net.madz.authorization.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import net.madz.core.entities.AbstractBaseEntity;

/**
 * 
 * @author Barry Zhong
 */
@Entity
@Table(name = "tenant")
public class Tenant extends AbstractBaseEntity implements Serializable {
	private static final long serialVersionUID = 476545537345379412L;

	public static final long DAY_MILLIS = 24L * 60 * 60 * 1000;
	public static final int EVALUATE_DAYS = 180;
	public static final long MONTH_MILLIS = 30L * 24 * 60 * 60 * 1000;

	private String name;
	private String address;
	@Column(name = "ARTIFICIAL_PERSON_NAME")
	private String artificialPersonName;
	private boolean locked;
	private boolean freezen;
	@Column(name = "PAYMENT_DATE")
	private Timestamp paymentDate;
	private double payment;

	@Column(name = "HISTORY_SERVICE_DAYS")
	private int historyServiceDays;
	@Column(name = "SERVICE_DAYS_PAID")
	private int serviceDaysPaid;
	@Column(name = "SERVICE_DAYS_LEFT")
	private int serviceDaysLeft;
	@Column(name = "MATURITY_DATE")
	private Timestamp maturityDate;
	private boolean arrearage;
	private boolean evaluated;

	@JoinColumn(name = "UPDATED_BY", referencedColumnName = "ID")
	@ManyToOne(fetch = FetchType.LAZY)
	protected User updatedBy;

	@Column(name = "UPDATED_ON")
	@Temporal(value = TemporalType.TIMESTAMP)
	protected Date updatedOn;

	@JoinColumn(name = "CREATED_BY", referencedColumnName = "ID", nullable = true)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	protected User createdBy;

	@Column(name = "CREATED_ON")
	@Temporal(value = TemporalType.TIMESTAMP)
	protected Date createdOn;

	@Column(name = "DELETED")
	protected Boolean deleted;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "PARENT_TENANT_ID", referencedColumnName = "ID", nullable = true)
	private Tenant parentTenant;

	public Tenant() {
	}

	public Tenant(String name) {
		this.name = name;
		setArrearage(false);
		setEvaluated(true);
		setFreezen(false);
		setHistoryServiceDays(0);
		setLocked(false);
		setMaturityDate(new Timestamp(System.currentTimeMillis() + 60 * 60 * 24 * EVALUATE_DAYS));
		setPayment(0);
		setPaymentDate(null);
		setServiceDaysLeft(EVALUATE_DAYS);
		setServiceDaysPaid(0);
	}

	public void setAddress(String companyAddress) {
		this.address = companyAddress;
	}

	public void setArtificialPersonName(String companyArtificialPersonName) {
		this.artificialPersonName = companyArtificialPersonName;
	}

	public void setName(String companyName) {
		this.name = companyName;
	}

	public void setFreezen(boolean freezen) {
		this.freezen = freezen;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public Tenant getParentTenant() {
		return parentTenant;
	}

	public void setParentTenant(Tenant parentCompany) {
		this.parentTenant = parentCompany;
	}

	public void setArrearage(boolean arrearage) {
		this.arrearage = arrearage;
	}

	public void setHistoryServiceDays(int historyServiceDays) {
		this.historyServiceDays = historyServiceDays;
	}

	public Timestamp getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(Timestamp maturityDate) {
		this.maturityDate = maturityDate;
	}

	public void setServiceDaysLeft(int serviceDaysLeft) {
		this.serviceDaysLeft = serviceDaysLeft;
	}

	public void setPayment(double payment) {
		this.payment = payment;
	}

	public void setPaymentDate(Timestamp paymentDate) {
		this.paymentDate = paymentDate;
	}

	public void setServiceDaysPaid(int serviceDaysPaid) {
		this.serviceDaysPaid = serviceDaysPaid;
	}

	public void setEvaluated(boolean evaluated) {
		this.evaluated = evaluated;
	}

	public String getAddress() {
		return address;
	}

	public String getArtificialPersonName() {
		return artificialPersonName;
	}

	public boolean isFreezen() {
		return freezen;
	}

	public String getName() {
		return name;
	}

	public boolean isArrearage() {
		return arrearage;
	}

	public int getHistoryServiceDays() {
		return historyServiceDays;
	}

	public double getPayment() {
		return payment;
	}

	public Timestamp getPaymentDate() {
		return paymentDate;
	}

	public int getServiceDaysLeft() {
		return serviceDaysLeft;
	}

	public boolean isEvaluated() {
		return evaluated;
	}

	public int getServiceDaysPaid() {
		return serviceDaysPaid;
	}

	@Override
	public String toString() {
		return "Tenant [id=" + getId() + ", name=" + name + ", address=" + address + ", artificialPersonName=" + artificialPersonName
				+ ", locked=" + locked + ", freezen=" + freezen + ", paymentDate=" + paymentDate + ", payment=" + payment
				+ ", historyServiceDays=" + historyServiceDays + ", serviceDaysPaid=" + serviceDaysPaid + ", serviceDaysLeft="
				+ serviceDaysLeft + ", maturityDate=" + maturityDate + ", arrearage=" + arrearage + ", evaluated=" + evaluated
				+ ", parentTenant=" + parentTenant + "]";
	}

	// Business Methods
	public boolean needWarnToPay() {
		if (System.currentTimeMillis() + MONTH_MILLIS > maturityDate.getTime()) {
			return true;
		} else {
			return false;
		}
	}

	public boolean validateServiceAvailiablity() {
		if (System.currentTimeMillis() >= maturityDate.getTime()) {
			arrearage = true;
			rotate();
			return false;
		} else {
			arrearage = false;
			serviceDaysLeft = (int) ((maturityDate.getTime() - System.currentTimeMillis()) / DAY_MILLIS);
			return true;
		}
	}

	public void pay(double customerPayment, int customerServiceDaysPaid) {
		if (customerPayment >= 0 && customerServiceDaysPaid >= 0) {
			if (customerServiceDaysPaid > 0) {
				rotate();
			}

			long current = System.currentTimeMillis();
			paymentDate = new Timestamp(current);
			this.serviceDaysPaid = customerServiceDaysPaid;
			this.maturityDate = new Timestamp(customerServiceDaysPaid * DAY_MILLIS + current);
			this.serviceDaysLeft = customerServiceDaysPaid;
			this.payment = customerPayment;
			arrearage = false;
		}
	}

	public void evaluate() {
		pay(0d, EVALUATE_DAYS);
	}

	public void freeze() {
		setFreezen(true);
	}

	public void unfreeze() {
		setFreezen(false);
	}

	private void rotate() {
		historyServiceDays += serviceDaysPaid;
		serviceDaysPaid = 0;
		serviceDaysLeft = 0;
	}

	public void lock() {
		setLocked(true);
	}

	public void unlock() {
		setLocked(false);
	}

}