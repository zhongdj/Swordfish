package net.madz.common.entities;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Address {

    @Column(name = "PROVINCE_NAME", nullable = false, updatable = true, length = 10)
    private String provinceName;
    @Column(name = "CITY_NAME", nullable = false, updatable = true, length = 10)
    private String cityName;
    @Column(name = "ZIP_CODE", nullable = false, updatable = true, length = 10)
    private String zipCode;
    @Column(name = "DISTRICT", nullable = true, updatable = true, length = 20)
    private String district;
    @Column(name = "STREET", nullable = false, updatable = true, length = 40)
    private String street;
    @Column(name = "NUMBER", nullable = false, updatable = true, length = 10)
    private String number;
    private GPSPosition gpsPosition;

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public GPSPosition getGpsPosition() {
        return gpsPosition;
    }

    public void setGpsPosition(GPSPosition gpsPosition) {
        this.gpsPosition = gpsPosition;
    }
}
