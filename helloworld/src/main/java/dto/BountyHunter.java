package dto;

public class BountyHunter {

    private String planet;
    private int day;

    public BountyHunter() {

    }

    public BountyHunter(String planet, int day) {
        this.planet = planet;
        this.day = day;
    }

    public String getPlanet() {
        return planet;
    }

    public void setPlanet(String planet) {
        this.planet = planet;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }
}
