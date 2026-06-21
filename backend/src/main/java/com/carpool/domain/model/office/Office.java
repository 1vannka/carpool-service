package com.carpool.domain.model.office;

import org.locationtech.jts.geom.Point;
import java.util.Objects;

public class Office {
    private Long id;
    private String name;
    private String city;
    private String address;
    private Point location;

    public Office() {
    }

    public Office(Long id, String name, String city, String address, Point location) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.address = address;
        this.location = location;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Office office = (Office) o;
        return Objects.equals(id, office.id) &&
                Objects.equals(name, office.name) &&
                Objects.equals(city, office.city) &&
                Objects.equals(address, office.address) &&
                Objects.equals(location, office.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, city, address, location);
    }
}