package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "routeTable")
public class RouteTable {

    @Column(name = "origin")
    private String origin;

    @Column(name = "destination")
    private String destination;

    @Column(name = "travelTime")
    private Integer travelTime;

    @Id
    @Column(name = "id")
    private Long id;

    public RouteTable() {

    }

    public RouteTable(String origin, String destination, Integer travelTime) {
        this.origin = origin;
        this.destination = destination;
        this.travelTime = travelTime;
    }

    public String getOrigin() {
        return this.origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Integer getTravelTime() {
        return this.travelTime;
    }

    public void setTravelTime(Integer travelTime) {
        this.travelTime = travelTime;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    public Long getId() {
        return id;
    }
}
