package dto;

public class Millenium {

    private int autonomy;
    private String departure;
    private String arrival;
    private String routes_db;

    public Millenium() {

    }

    public Millenium(int autonomy, String departure, String arrival, String routes_db) {
        this.autonomy = autonomy;
        this.departure = departure;
        this.arrival = arrival;
        this.routes_db = routes_db;
    }

    public int getAutonomy() {
        return autonomy;
    }

    public void setAutonomy(int autonomy) {
        this.autonomy = autonomy;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getArrival() {
        return arrival;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
    }

    public String getRoutes_db() {
        return routes_db;
    }

    public void setRoutes_db(String routes_db) {
        this.routes_db = routes_db;
    }
}
