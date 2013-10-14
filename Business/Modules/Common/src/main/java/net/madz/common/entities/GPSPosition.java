package net.madz.common.entities;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class GPSPosition {

    @Column(name = "ALTITUDE", nullable = true)
    private double altitude;
    @Column(name = "LATITUDE", nullable = true)
    private double latitude;
    @Column(name = "LONGITUDE", nullable = true)
    private double longitude;

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
