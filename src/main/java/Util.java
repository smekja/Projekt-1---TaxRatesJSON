import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Comparator.reverseOrder;


public class Util {
    public ArrayList<State> stateList = new ArrayList<>();
    public Set<String> keys;
    public List<String> listOfNonDuplicateKeys;

    private Stream<State> streamStateSupplier() {
        return stateList.stream();
    }

    public String obtainData() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://euvatrates.com/rates.json"))
                .GET()
                .build();

        HttpResponse<String>response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    // have to add function to remove duplicate shortcuts
    public void saveData(String response) {
        JSONObject jo = new JSONObject(response);
        JSONObject rates = new JSONObject(jo.optString("rates"));

        // Getting a set of state´s shortcuts
        keys = rates.keySet();

        for (String key : keys) {
            JSONObject singleStateJSON = new JSONObject(rates.optString(key));
            State<?> state = new State();
            setStateInfo(singleStateJSON, state, key);

            stateList.add(state);
        }
        removeDuplicateStates();
    }

    private void setStateInfo(JSONObject singleStateJSON, State state, String key) {
        state.setShortcut(key);
        state.setName(singleStateJSON.optString("country"));
        state.setStandardRate(singleStateJSON.optString("standard_rate"));
        state.setReducedRate(singleStateJSON.opt("reduced_rate"));
        state.setReducedRateAlt(singleStateJSON.opt("reduced_rate_alt"));
        state.setSuperReducedRate(singleStateJSON.opt("super_reduced_rate"));
        state.setParkingRate(singleStateJSON.opt("parking_rate"));
    }
    public void readFromKeyboard() throws IOException {
        System.out.println("Available shortcuts: " + listOfNonDuplicateKeys);
        System.out.print("Enter the shortcut of a country to show it´s data. ");
        System.out.println("You can press 'q' to quit.");

        Scanner scanner = new Scanner(System.in);
        String shortcut = scanner.next().toUpperCase();
        while(!shortcut.equals("Q")) {
            tryToFindShortcut(shortcut);
            shortcut = scanner.next().toUpperCase();

        }
    }

    public void tryToFindShortcut(String shortcut) {
        boolean isFound = false;
        for (State state : stateList) {
            if(state.getShortcut().equals(shortcut)) {
                isFound = true;
                state.printAllData();
                System.out.println("-----------------------------------------");
                System.out.println("Press 'q' to quit or you can search again: ");
                break;
            }
        }
        if (!isFound) {
            System.out.println("Couldnt find " + shortcut + " shortcut.");
            System.out.println("Try again: ");
        }
    }

    private void removeDuplicateStates() {
        HashSet<Object> alreadySeenStates = new HashSet<>();
        stateList.removeIf(value -> !alreadySeenStates.add(value.getName()));
        listOfNonDuplicateKeys = streamStateSupplier().map((value) -> value.getShortcut())
        .collect(Collectors.toList());
    }


    public Comparator<State> extractTop3() {
        return Comparator.<State, Double> comparing(state -> state.getStandardRate(), reverseOrder())
                .thenComparing(state -> state.getReducedRate(), reverseOrder())
                .thenComparing(state -> state.getReducedRateAlt(), reverseOrder())
                .thenComparing(state -> state.getSuperReducedRate(), reverseOrder())
                .thenComparing(state -> state.getParkingRate(), reverseOrder());
    }

    public List<?> findHighestTaxes() {
        Stream<State> streamOfStates = streamStateSupplier();
        /*  Comparator<State> standardRateComparator = Comparator.comparing(State::getStandardRate, reverseOrder());
        Comparator<State> reducedRateComparator = Comparator.comparing(State::getReducedRate, reverseOrder());
        Comparator<State> last = standardRateComparator.thenComparing(reducedRateComparator);*/
        List<?> result = streamOfStates.sorted(extractTop3())
                .limit(3)
                .map(state -> state.getName())
                .collect(Collectors.toList());
        return result;
    }
    public void printHighestTaxes() {
        System.out.println("-----------------------------------");
        System.out.println("Top 3 EU highest taxes countries: ");
        System.out.println("-----------------------------------");
        List<?> result = findHighestTaxes();
        State stateFound;
        for(Object item : result) {
            Stream<State> streamOfStates = streamStateSupplier();
            stateFound = streamOfStates.filter(
                    (value) -> value.getName().equals(item))
                    .findAny().get();
            stateFound.printAllData();
            System.out.println("-----------------------------------");
        }
    }
    public List<?> findLowestTaxes() {
        Stream<State> streamOfStates = streamStateSupplier();
        List<?> result = streamOfStates.sorted(extractTop3().reversed())
                .limit(3)
                .map(state -> state.getName())
                .collect(Collectors.toList());
        return result;
    }

    public void printLowestTaxes() {
        System.out.println("Top 3 EU lowest taxes countries: ");
        System.out.println("-----------------------------------");
        List<?> result = findLowestTaxes();
        State stateFound;
        for(Object item : result) {
            Stream<State> streamOfStates = streamStateSupplier();
            stateFound = streamOfStates.filter(
                    (value) -> value.getName().equals(item))
                    .findAny().get();
            stateFound.printAllData();
            System.out.println("---------------------------");
        }
    }

}
