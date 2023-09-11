import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.hyperskill.hstest.dynamic.DynamicTest;
import org.hyperskill.hstest.dynamic.input.DynamicTesting;
import org.hyperskill.hstest.exception.outcomes.WrongAnswer;
import org.hyperskill.hstest.mocks.web.response.HttpResponse;
import org.hyperskill.hstest.stage.SpringTest;
import org.hyperskill.hstest.testcase.CheckResult;
import org.junit.Before;
import webCalendarSpring.Main;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hyperskill.hstest.common.JsonUtils.getJson;
import static org.hyperskill.hstest.testing.expect.Expectation.expect;
import static org.hyperskill.hstest.testing.expect.json.JsonChecker.isObject;

class EventForTest {
    int id;
    String event;
    String date;

    public EventForTest(int id, String event, String date) {
        this.id = id;
        this.event = event;
        this.date = date;
    }

    public EventForTest() {
    }

    @Override
    public String toString() {
        return "{ \"id\":" + "\"" + id + "\"" +
                ", \"event\":" + "\"" + event + "\"" +
                ", \"date\":" + "\"" + date + "\"" + "}";
    }


}

public class webCalendarSpringTest extends SpringTest {


    int count = 0;

    public webCalendarSpringTest() {

        super(Main.class, "../d.mv.db");

    }

    public static final String todayEndPoint = "/event/today";

    public static final String eventEndPoint = "/event";

    private static final Gson gson = new Gson();

    private static List<EventForTest> eventsList = new ArrayList<>();
    Map<String, String> justToday = Map.of(
            "event", "Today is a good Day ",
            "date", LocalDate.now().toString());
    Map<String, String> newYear = Map.of(
            "event", "New Year's Day",
            "date", "2024-01-01");
    Map<String, String> goodFriday = Map.of(
            "event", "Good Friday",
            "date", "2023-04-07");
    Map<String, String> janHusDay = Map.of(
            "event", "Jan Hus Day",
            "date", "2023-07-06");

    Map<String, String> justaPerfectDay = Map.of(
            "event", "Perfect Day",
            "date", randomDate(-20, 15));
    Map<String, String> anotherGoodDay = Map.of(
            "event", "Another Good Day",
            "date", randomDate(-10, 5));
    List<Map<String, String>> listOfEvents = new ArrayList<>();

    {
        listOfEvents.add(newYear);
        listOfEvents.add(goodFriday);
        listOfEvents.add(janHusDay);
        listOfEvents.add(justaPerfectDay);
        listOfEvents.add(anotherGoodDay);

    }

    Map<String, String> emptyEvent1 = Map.of("event", "", "date", LocalDate.now().toString());
    Map<String, String> blankEvent2 = Map.of("event", "     ", "date", LocalDate.now().toString());

    Map<String, String> nullEvent3 = Map.of("date", LocalDate.now().toString());

    Map<String, String> nullDate4 = Map.of("event", "New Year Party");
    Map<String, String> emptyEventNullDate5 = Map.of("event", "");

    Map<String, String> emptyEventEmptyDate6 = Map.of("event", "", "date", "");

    Map<String, String> blankDateEmptyEvent7 = Map.of("date", "    ", "event", "");

    Map<String, String> blankDate8 = Map.of("date", "    ", "event", "New Year Party");
    Map<String, String> blankDate9 = Map.of("event", "New Year Party", "date", "    ");

    Map<String, String> emptyDate10 = Map.of("date", "", "event", "New Year Party");
    Map<String, String> emptyDate11 = Map.of("event", "New Year Party", "date", "");

    CheckResult testEndpoint(String url, int status) {
        HttpResponse response = get(url).send();

        checkStatusCode(response, status);

        if (count == 0 && !response.getJson().isJsonObject()) {
            return CheckResult.wrong("Wrong object in response, expected JSON but was \n" +
                    response.getContent().getClass());

        }

        System.out.println(response.getContent() + "\n " + response.getStatusCode() +
                "\n " + response.getRequest().getLocalUri() + "\n " + response.getRequest().getMethod());


        if (response.getStatusCode() == 400 && url.equals(todayEndPoint)) {

            expect(response.getContent()).asJson().check(
                    isObject()
                            .value("data", "There are no events for today!")

            );

        }
        if (response.getStatusCode() == 400 && url.equals(eventEndPoint)) {

            expect(response.getContent()).asJson().check(
                    isObject()
                            .value("data", "There are no events!")

            );

        }

        if (response.getStatusCode() == 200) {

            if (!response.getJson().isJsonArray()) {
                return CheckResult.wrong("Wrong object in response, expected array of JSON but was \n" +
                        response.getContent().getClass());
            }

            List<String> eventsToString;

            if (url.equals("/event/today")) {
                eventsToString = eventsList.stream().filter(it -> it.date.equals(LocalDate.now().toString())).map(it -> it.toString()).collect(Collectors.toList());
//                eventsToString.stream().forEach(System.out::println);

                if (eventsToString.size() == 0) {
                    checkStatusCode(response, 400);
                } else {
                    checkStatusCode(response, 200);
                }

            } else {
                eventsToString = eventsList.stream().map(it -> it.toString()).collect(Collectors.toList());
                if (eventsToString.size() == 0) {
                    checkStatusCode(response, 400);
                } else {
                    checkStatusCode(response, 200);
                }
            }
            eventsToString.stream().forEach(System.out::println);

            String convertJsonToString = convert(eventsToString);
            JsonArray correctJson = getJson(convertJsonToString).getAsJsonArray();

            JsonArray responseJson = getJson(response.getContent()).getAsJsonArray();

            if (responseJson.size() != correctJson.size()) {
                return CheckResult.wrong("Correct json array size should be " +
                        correctJson.size() + "\n" +
                        "Response array size is: " + responseJson.size() + "\n");
            }


            for (int i = 0; i < responseJson.size(); i++) {


                expect(responseJson.get(i).getAsJsonObject().toString()).asJson()
                        .check(isObject()
                                .value("id", correctJson.get(i).getAsJsonObject().get("id").getAsInt())
                                .value("event", correctJson.get(i).getAsJsonObject().get("event").getAsString())
                                .value("date", correctJson.get(i).getAsJsonObject().get("date").getAsString()));

            }

        }


        return CheckResult.correct();
    }

    private static void checkStatusCode(HttpResponse resp, int status) {
        if (resp.getStatusCode() != status) {
            throw new WrongAnswer(
                    resp.getRequest().getMethod() + " " +
                            resp.getRequest().getLocalUri() +
                            " should respond with status code " + status + ", " +
                            "responded: " + resp.getStatusCode() + "\n\n" +
                            "Response body:\n\n" + resp.getContent()
            );
        }
    }

    private String convert(List<String> trs) {
        JsonArray jsonArray = new JsonArray();
        for (String tr : trs) {
            JsonElement jsonObject = JsonParser.parseString(tr);
            jsonArray.add(jsonObject);
        }
        return jsonArray.toString();
    }


    CheckResult testPostEvent(Map<String, String> body, int status) {

        String jsonBody = gson.toJson(body);

        HttpResponse response = post(eventEndPoint, jsonBody).send();
        checkStatusCode(response, status);
        System.out.println(response.getContent() + "\n " + response.getStatusCode() + "\n "
                + response.getRequest().getLocalUri() + "\n " + response.getRequest().getMethod()
                + "\n" + response.getRequest().getContent());
        if (status == 200) {
            count++;
            EventForTest event = new EventForTest(count, body.get("event"), body.get("date"));
            eventsList.add(event);
            expect(response.getContent()).asJson()
                    .check(

                            isObject()
                                    .value("message", "The event has been added!")
                                    .value("event", getJson(jsonBody).getAsJsonObject().get("event").getAsString())
                                    .value("date", getJson(jsonBody).getAsJsonObject().get("date").getAsString())

                    );
        }

        if (status == 400) {
            if (body.get("event") == null || (body.get("event").trim().equals("")

            )) {
                expect(response.getContent()).asJson().check(
                        isObject()
                                .value("message", isObject()
                                        .value("event", "The event name is required!"))

                );


            } else if (body.get("date") == null || (body.get("date").trim().equals(""))) {
                expect(response.getContent()).asJson().check(
                        isObject()
                                .value("message", isObject()
                                        .value("date", "The event date with the correct format is required! The correct format is YYYY-MM-DD!"))

                );

            }
        }


        return CheckResult.correct();
    }

    private int randomReturn(List<Map<String, String>> list) {
        int toReturn = (int) Math.round(Math.random() * (list.size() - 1));
//        System.out.println(toReturn);

        return toReturn;
    }

    private String randomDate(int maxDays, int minDays) {

        LocalDate now = LocalDate.now();

        return now.plusDays((int) Math.round(Math.random() * (maxDays - minDays) + minDays)).toString();
    }

    @DynamicTest
    DynamicTesting[] dynamicTests = new DynamicTesting[]{
            () -> testEndpoint(todayEndPoint, 400), //#1
            () -> testEndpoint(eventEndPoint, 400), //#2


            //incorrect body for Post request
            () -> testPostEvent(emptyEvent1, 400), //#3
            () -> testPostEvent(blankEvent2, 400), //#4
            () -> testPostEvent(nullEvent3, 400), //#5
            () -> testPostEvent(nullDate4, 400), //#6
            () -> testPostEvent(emptyEventNullDate5, 400), //#7
            () -> testPostEvent(emptyEventEmptyDate6, 400), //#8
            () -> testPostEvent(blankDateEmptyEvent7, 400), //#9
            () -> testPostEvent(blankDate8, 400), //#10
            () -> testPostEvent(blankDate9, 400), //#11
            () -> testPostEvent(emptyDate10, 400), //#12
            () -> testPostEvent(emptyDate11, 400), //#13


            () -> testPostEvent(justToday, 200), //#14
            () -> testPostEvent(justToday, 200), //#15
            () -> testEndpoint(todayEndPoint, 200),//#16
            () -> testPostEvent(listOfEvents.get(randomReturn(listOfEvents)), 200), //#17

            () -> testEndpoint(eventEndPoint, 200),//#18


            () -> testPostEvent(listOfEvents.get(randomReturn(listOfEvents)), 200),//19
            () -> testPostEvent(listOfEvents.get(randomReturn(listOfEvents)), 200),//20
            () -> testPostEvent(listOfEvents.get(randomReturn(listOfEvents)), 200),//21
            () -> testPostEvent(listOfEvents.get(randomReturn(listOfEvents)), 200),//22
            () -> testPostEvent(listOfEvents.get(randomReturn(listOfEvents)), 200),//23
            () -> testPostEvent(listOfEvents.get(randomReturn(listOfEvents)), 200),//24

            () -> testEndpoint(eventEndPoint, 200),//#25
            this::reloadServer,//26
            () -> testEndpoint(eventEndPoint, 200),//#27
    };

    @Before
    public void createEvents() {

        listOfEvents.stream().forEach(System.out::println);

    }

    private CheckResult reloadServer() {
        try {
            reloadSpring();
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
        return CheckResult.correct();
    }

}
