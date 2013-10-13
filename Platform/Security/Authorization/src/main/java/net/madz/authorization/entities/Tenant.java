package net.madz.authorization.entities;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlIDREF;

import net.madz.core.entities.AbstractBaseEntity;

/**
 * 
 * @author Barry Zhong
 */
@Entity
@Table(name = "tenant")
public class Tenant extends AbstractBaseEntity {

    private static final long serialVersionUID = 476545537345379412L;

    public static final long DAY_MILLIS = 24L * 60 * 60 * 1000;

    public static final int EVALUATE_DAYS = 180;

    public static final long MONTH_MILLIS = 30L * 24 * 60 * 60 * 1000;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(name = "ARTIFICIAL_PERSON_NAME", nullable = false)
    private String artificialPersonName;

    @Column(columnDefinition = "BOOL NOT NULL DEFAULT 0")
    private boolean locked;

    @Column(columnDefinition = "BOOL NOT NULL DEFAULT 0")
    private boolean freezen;

    @Column(name = "PAYMENT_DATE", nullable = true)
    private Timestamp paymentDate;

    @Column(columnDefinition = "DOUBLE NOT NULL DEFAULT 0.0")
    private double payment;

    @Column(name = "HISTORY_SERVICE_DAYS", columnDefinition = "INT NOT NULL DEFAULT 0")
    private int historyServiceDays;

    @Column(name = "SERVICE_DAYS_PAID", columnDefinition = "INT NOT NULL DEFAULT 0")
    private int serviceDaysPaid;

    @Column(name = "SERVICE_DAYS_LEFT", columnDefinition = "INT NOT NULL DEFAULT 0")
    private int serviceDaysLeft;

    @Column(name = "MATURITY_DATE", nullable = false)
    private Timestamp maturityDate;

    @Column(columnDefinition = "BOOL NOT NULL DEFAULT 0")
    private boolean arrearage;

    @Column(columnDefinition = "BOOL NOT NULL DEFAULT 0")
    private boolean evaluated;

    @JoinColumns(value = {
            @JoinColumn(name = "ID", nullable = false, insertable = false, updatable = false,
                    referencedColumnName = "TENANT_ID"),
            @JoinColumn(name = "UPDATED_BY", nullable = false, insertable = true, updatable = false,
                    referencedColumnName = "ID") })
    @ManyToOne(fetch = FetchType.LAZY)
    @XmlIDREF
    protected User updatedBy;

    @Column(name = "UPDATED_ON")
    @Temporal(value = TemporalType.TIMESTAMP)
    protected Date updatedOn;

    @JoinColumns(value = {
            @JoinColumn(name = "ID", nullable = false, insertable = false, updatable = false,
                    referencedColumnName = "TENANT_ID"),
            @JoinColumn(name = "CREATED_BY", nullable = false, insertable = true, updatable = false,
                    referencedColumnName = "ID") })
    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @XmlIDREF
    protected User createdBy;

    @Column(name = "CREATED_ON")
    @Temporal(value = TemporalType.TIMESTAMP)
    protected Date createdOn;

    @Column(name = "DELETED", columnDefinition = "BOOL NOT NULL DEFAULT 0")
    protected Boolean deleted;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PARENT_TENANT_ID", referencedColumnName = "ID", nullable = true)
    private Tenant parentTenant;

    @OneToOne
    @JoinColumns(value = {
            @JoinColumn(name = "ID", nullable = false, insertable = false, updatable = false,
                    referencedColumnName = "TENANT_ID"),
            @JoinColumn(name = "ADMIN_USER_ID", nullable = false, insertable = true, updatable = false,
                    referencedColumnName = "ID") })
    @XmlIDREF
    private User adminUser;

    public Tenant() {
    }

    public Tenant(String name) {
        this.name = name;
        setArrearage(false);
        setFreeTrial(true);
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

    public void setFreeTrial(boolean evaluated) {
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
        return "Tenant [id=" + getId() + ", name=" + name + ", address=" + address + ", artificialPersonName="
                + artificialPersonName + ", locked=" + locked + ", freezen=" + freezen + ", paymentDate=" + paymentDate
                + ", payment=" + payment + ", historyServiceDays=" + historyServiceDays + ", serviceDaysPaid="
                + serviceDaysPaid + ", serviceDaysLeft=" + serviceDaysLeft + ", maturityDate=" + maturityDate
                + ", arrearage=" + arrearage + ", evaluated=" + evaluated + ", parentTenant=" + parentTenant + "]";
    }

    // Business Methods
    public boolean needWarnToPay() {
        if ( System.currentTimeMillis() + MONTH_MILLIS > maturityDate.getTime() ) {
            return true;
        } else {
            return false;
        }
    }

    public boolean validateServiceAvailiablity() {
        if ( System.currentTimeMillis() >= maturityDate.getTime() ) {
            arrearage = true;
            rotate();
            return false;
        } else {
            arrearage = false;
            serviceDaysLeft = (int) ( ( maturityDate.getTime() - System.currentTimeMillis() ) / DAY_MILLIS );
            return true;
        }
    }

    public void pay(double customerPayment, int customerServiceDaysPaid) {
        if ( customerPayment >= 0 && customerServiceDaysPaid >= 0 ) {
            if ( customerServiceDaysPaid > 0 ) {
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

    public User getCreatedBy() {
        return createdBy;
    }

    public User getAdminUser() {
        return adminUser;
    }

    public void setAdminUser(User adminUser) {
        this.adminUser = adminUser;
    }

    public User getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
}