public class State <T> {
    private String shortcut;
    private String name;
    private T standardRate;
    private T reducedRate;
    private T reducedRateAlt;
    private T superReducedRate;
    private T parkingRate;

    public String getShortcut() {
        return shortcut;
    }

    public void setShortcut(String shortcut) {
        this.shortcut = shortcut;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getStandardRate() {
        return Double.parseDouble(standardRate.toString());
    }

    public void setStandardRate(T standardRate) {
        this.standardRate = standardRate;
    }

    public Double getReducedRate() {

        return changeFalseToDouble(reducedRate);
    }

    public void setReducedRate(T reducedTax) {
        this.reducedRate = reducedTax;
    }

    public Double getReducedRateAlt() {

        return changeFalseToDouble(reducedRateAlt);
    }

    public void setReducedRateAlt(T reducedRateAlt) {
        this.reducedRateAlt = reducedRateAlt;
    }

    public Double getSuperReducedRate() {
        return changeFalseToDouble(superReducedRate);
    }

    public void setSuperReducedRate(T superReducedRate) {
        this.superReducedRate = superReducedRate;
    }

    public Double getParkingRate() {
        return changeFalseToDouble(parkingRate);
    }

    public void setParkingRate(T parkingRate) {
        this.parkingRate = parkingRate;
    }
    public void printAllData() {
        System.out.println("Shortcut: " + shortcut);
        System.out.println("Name: " + name);
        System.out.println("Standard rate: " + standardRate);
        System.out.println("Reduced rate: " + reducedRate);
        System.out.println("Reduced rate alt: " + reducedRateAlt);
        System.out.println("Super reduced rate: " + superReducedRate);
        System.out.println("Parking rate: " + parkingRate);
    }
    private <T> Double changeFalseToDouble(T object) {
        if (object.toString().toLowerCase().equals("false")) return Double.valueOf(0);
        return Double.parseDouble(object.toString());
    }
}
