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


public class StateHandler {
    private Set<State> stateList = new HashSet<>();
    private Set<String> keys = new HashSet<>();

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

    public void saveData(String response) {
        JSONObject jo = new JSONObject(response);
        JSONObject rates = new JSONObject(jo.optString("rates"));

        // Getting a set of state´s abbreviations
        keys = rates.keySet();

        for (String key : keys) {
            JSONObject singleStateJSON = new JSONObject(rates.optString(key));
            State<?> state = new State();
            setStateInfo(singleStateJSON, state, key);
            stateList.add(state);
        }
    }

    private void setStateInfo(JSONObject singleStateJSON, State state, String key) {
        state.setAbbreviation(key);
        state.setName(singleStateJSON.optString("country"));
        state.setStandardRate(singleStateJSON.optString("standard_rate"));
        state.setReducedRate(singleStateJSON.opt("reduced_rate"));
        state.setReducedRateAlt(singleStateJSON.opt("reduced_rate_alt"));
        state.setSuperReducedRate(singleStateJSON.opt("super_reduced_rate"));
        state.setParkingRate(singleStateJSON.opt("parking_rate"));
    }

    public void readFromKeyboard() throws IOException {
        System.out.println("Available state abbreviations: " + keys);
        System.out.print("Enter the abbreviation of a country to show it´s data. ");
        System.out.println("You can press 'q' to quit.");

        Scanner scanner = new Scanner(System.in);
        String abbreviation = scanner.next().toUpperCase();
        while (!abbreviation.equals("Q")) {
            tryToFindAbbreviation(abbreviation);
            abbreviation = scanner.next().toUpperCase();
        }
    }

    private State findState(String stateAbbreviation) {
        for (State state : stateList) {
            if (state.getAbbreviation().equals(stateAbbreviation)) {
                return state;
            }
        }
        return null;
    }

    private void tryToFindAbbreviation(String abbreviation) {
        State state = findState(abbreviation);
        if ( state != null) {
                state.printAllData();
                System.out.println("-----------------------------------------");
                System.out.println("Press 'q' to quit or you can search again: ");
        } else {
            System.out.println("Couldnt find " + abbreviation + " shortcut.");
            System.out.println("Try again: ");
        }
    }

    public Comparator<State> sortStandardRate() {
        return Comparator.<State, Double> comparing(state -> state.getStandardRate(), reverseOrder())
                .thenComparing(state -> state.getReducedRate(), reverseOrder())
                .thenComparing(state -> state.getReducedRateAlt(), reverseOrder())
                .thenComparing(state -> state.getSuperReducedRate(), reverseOrder())
                .thenComparing(state -> state.getParkingRate(), reverseOrder());
    }

    public List<State> findHighestTaxesTop3() {
        Stream<State> streamOfStates = streamStateSupplier();
        List<State> result = streamOfStates.sorted(sortStandardRate())
                .limit(3)
                .collect(Collectors.toList());
        return result;
    }

    public void printHighestTaxes() {
        System.out.println("-----------------------------------");
        System.out.println("Top 3 EU highest taxes countries: ");
        System.out.println("-----------------------------------");
        List<State> result = findHighestTaxesTop3();
       for (State state : result) {
            state.printAllData();
            System.out.println("-----------------------------------");
        }
    }

    public List<State> findLowestTaxesLast3() {
        Stream<State> streamOfStates = streamStateSupplier();
        List<State> result = streamOfStates.sorted(sortStandardRate().reversed())
                .limit(3)
                .collect(Collectors.toList());
        return result;
    }

    public void printLowestTaxes() {
        System.out.println("Top 3 EU lowest taxes countries: ");
        System.out.println("-----------------------------------");
        List<State> result = findLowestTaxesLast3();
        for (State state : result) {
            state.printAllData();
            System.out.println("---------------------------");
        }
    }

}
