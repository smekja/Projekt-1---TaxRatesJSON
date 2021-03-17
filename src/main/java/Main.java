
public class Main {



    public static void main(String[] args) {
        try {
            StateHandler stateHandler = new StateHandler();
            stateHandler.saveData(stateHandler.obtainData());
            stateHandler.printHighestTaxes();
            stateHandler.printLowestTaxes();
            stateHandler.readFromKeyboard();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
