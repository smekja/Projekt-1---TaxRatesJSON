
public class Main {



    public static void main(String[] args) {
        try {
            Util util = new Util();
            util.saveData(util.obtainData());
            util.printHighestTaxes();
            util.printLowestTaxes();
            util.readFromKeyboard();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
