import com.google.gson.*;
import org.hibernate.tool.schema.extract.internal.InformationExtractorJdbcDatabaseMetaDataImpl;
import org.hyperskill.hstest.dynamic.DynamicTest;
import org.hyperskill.hstest.dynamic.input.DynamicTesting;
import org.hyperskill.hstest.exception.outcomes.WrongAnswer;
import org.hyperskill.hstest.mocks.web.response.HttpResponse;
import org.hyperskill.hstest.stage.SpringTest;
import org.hyperskill.hstest.testcase.CheckResult;
import org.junit.Before;
import webCalendarSpring.Main;

import java.io.File;
import java.time.LocalDate;
import java.util.*;
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

    Map<String,String> justToday = Map.of(
            "event", "Today is a good Day ",
            "date", LocalDate.now().toString());
    Map<String,String> newYear = Map.of(
            "event", "New Year's Day",
            "date", "2024-01-01");
    Map<String,String> goodFriday = Map.of(
            "event", "Good Friday",
            "date", "2023-04-07");
    Map<String,String> janHusDay = Map.of(
            "event", "Jan Hus Day",
            "date", "2023-07-06");
   List <Map<String,String>> listOfEvents = new ArrayList<>();
    {
        listOfEvents.add(newYear);
        listOfEvents.add(goodFriday);
        listOfEvents.add(janHusDay);
    }
}

public class webCalendarSpringTest extends SpringTest {
    EventForTest eventForTest;

    int count = 0;

     int randomCount=0;

    public webCalendarSpringTest() {
        super(Main.class);

    }

    public static final String todayEndPoint = "/event/today";

    public static final String eventEndPoint = "/event";

    private static final Gson gson = new Gson();

    private static List<EventForTest> eventsList = new ArrayList<>();


    CheckResult testEndpoint(String url, int status) {
        HttpResponse response = get(url).send();
        checkStatusCode(response, status);

        if (count == 0 && !response.getJson().isJsonObject()) {
            return CheckResult.wrong("Wrong object in response, expected JSON but was \n" +
                    response.getContent().getClass());

        }


        if (response.getStatusCode() == 400) {

            expect(response.getContent()).asJson().check(
                    isObject()
                            .value("data", "There are no events for today!")

            );

        }

        if ( response.getStatusCode() == 200) {

            if (!response.getJson().isJsonArray()) {
                return CheckResult.wrong("Wrong object in response, expected array of JSON but was \n" +
                        response.getContent().getClass());
            }

            List<String> eventsToString = new ArrayList<>();

            if(url.equals("/event/today")) {
                eventsToString=  eventsList.stream().filter(it -> it.date.equals(LocalDate.now().toString())).map(it -> it.toString()).collect(Collectors.toList());
            }
            else { eventsToString=  eventsList.stream().map(it -> it.toString()).collect(Collectors.toList());
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


        System.out.println(response.getContent() + " " + response.getRequest() + " " + response.getStatusCode() + " " +
                response.getHeaders() + " " + response.getRequest().getLocalUri() + " " + response.getRequest().getMethod()
                + " " + response.getRequest().getContent());
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

    CheckResult testEndpointWithParams(String url, int status, String startDay, String endDay) {
        HttpResponse response = get(url + "?start_time=" + startDay + "&end_time=" + endDay).send();
        checkStatusCode(response, status);
        System.out.println(response.getContent() + " " + response.getRequest() + " " + response.getStatusCode() + " " +
                response.getHeaders() + " " + response.getRequest().getLocalUri() + " " + response.getRequest().getMethod()
                + " " + response.getRequest().getContent());


        if (count == 0 && !response.getJson().isJsonObject()) {
            return CheckResult.wrong("Wrong object in response, expected JSON but was \n" +
                    response.getContent().getClass());

        }


        if (response.getStatusCode() == 400) {

            expect(response.getContent()).asJson().check(
                    isObject()
                            .value("data", "There are no events for today!")

            );

        }

        if ( response.getStatusCode() == 200) {

            if (!response.getJson().isJsonArray()) {
                return CheckResult.wrong("Wrong object in response, expected array of JSON but was \n" +
                        response.getContent().getClass());
            }

            List<String> eventsToString;


                eventsToString = eventsList.stream().filter(it -> LocalDate.parse(it.date).equals(LocalDate.parse(startDay))
                        ||LocalDate.parse(it.date).isAfter(LocalDate.parse(startDay))
                        &&(LocalDate.parse(it.date).equals(LocalDate.parse(endDay))
                        ||LocalDate.parse(it.date).isBefore(LocalDate.parse(endDay)))
                ).map(it -> it.toString()).collect(Collectors.toList());

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

    CheckResult testEndpoinById(String url, int status, int id) {
        HttpResponse response = get(url + "/" + id).send();
        checkStatusCode(response, status);
        System.out.println(response.getContent() + " " + response.getRequest() + " " + response.getStatusCode() + " " +
                response.getHeaders() + " " + response.getRequest().getLocalUri() + " " + response.getRequest().getMethod()
                + " " + response.getRequest().getContent());

        if (response.getStatusCode() == 400) {

            expect(response.getContent()).asJson().check(
                    isObject()
                            .value("message", "The event doesn't exist!")

            );

        }

        if ( response.getStatusCode() == 200) {

            if (!response.getJson().isJsonObject()) {
                return CheckResult.wrong("Wrong object in response, expected array of JSON but was \n" +
                        response.getContent().getClass());
            }

            List<String> eventsToString;


            eventsToString = eventsList.stream().filter(it -> it.id == id).map(it -> it.toString()).collect(Collectors.toList());

            eventsToString.stream().forEach(System.out::println);

            String convertJsonToString = eventsToString.get(0).toString();


            JsonObject correctJson = getJson(convertJsonToString).getAsJsonObject();

            JsonObject responseJson = getJson(response.getContent()).getAsJsonObject();


            expect(responseJson.toString()).asJson()
                    .check(isObject()
                            .value("id", correctJson.getAsJsonObject().get("id").getAsInt())
                            .value("event", correctJson.getAsJsonObject().get("event").getAsString())
                            .value("date", correctJson.getAsJsonObject().get("date").getAsString()));


        }


        return CheckResult.correct();
    }

    CheckResult testEndpoinDeleteById(String url, int status, int id) {
        HttpResponse response = delete(url + "/" + id).send();
        checkStatusCode(response, status);
        System.out.println(response.getContent() + " " + response.getRequest() + " " + response.getStatusCode() + " " +
                response.getHeaders() + " " + response.getRequest().getLocalUri() + " " + response.getRequest().getMethod()
                + " " + response.getRequest().getContent());

        eventsList =  eventsList.stream().filter(it -> it.id != id).collect(Collectors.toList());
        return CheckResult.correct();
    }

    CheckResult testPostEvent(Map<String, String> body, int status) {

        String jsonBody = gson.toJson(body);

        HttpResponse response = post(eventEndPoint, jsonBody).send();
        checkStatusCode(response, status);
        System.out.println(response.getContent() + " " + response.getRequest() + " " + response.getStatusCode() + " " +
                response.getHeaders() + " " + response.getRequest().getLocalUri() + " " + response.getRequest().getMethod()
                + " " + response.getRequest().getContent());
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
           if(body.get("event")==null||(body.get("event").trim().equals("")

           )) {
              expect(response.getContent()).asJson().check(
                      isObject()
                              .value("message", isObject()
                                      .value("event","The event name is required!" ))

              );


              if(body.get("event")!=null){ System.out.println("WE ARE HERE"+body.get("event").trim()+"<>");}
              else System.out.println("NULL");
           }
           else if (body.get("date")==null||(body.get("date").trim().equals(""))){
                expect(response.getContent()).asJson().check(
                        isObject()
                                .value("message", isObject()
                                        .value("date","The event date with the correct format is required! The correct format is YYYY-MM-DD!" ))

                );

           }
           }


        return CheckResult.correct();
    }

    private int randomReturn( List <Map<String,String>> list) {
        int toReturn = (int) Math.round(Math.random()*(list.size()-1));
        System.out.println(toReturn);

        return toReturn;
    }

    @DynamicTest
    DynamicTesting[] dynamicTests = new DynamicTesting[]{


            () -> testEndpoint(todayEndPoint, 400), //#1
            () -> testPostEvent(eventForTest.justToday

            , 200), //#2
            () -> testPostEvent(eventForTest.justToday, 200), //#3

            () -> testPostEvent( eventForTest.listOfEvents.get(randomReturn(eventForTest.listOfEvents)), 200), //#4

            () -> testEndpoint(todayEndPoint, 200),//#5
            () -> testEndpoint(eventEndPoint, 200),//#6
            () -> testPostEvent(Map.of(
                    "event", "",
                    "date", LocalDate.now().toString()
            ), 400), //#7
            () -> testPostEvent(Map.of(
                    "event", "     ",
                    "date", LocalDate.now().toString()
            ), 400), //#8
            () -> testPostEvent(Map.of(
                    "date", LocalDate.now().toString()
            ), 400), //#9
            () -> testPostEvent(Map.of(
                    "event", "New Year Party"
            ), 400),//#10
            () -> testPostEvent(Map.of(
                    "event", ""
            ), 400),//#11
            () -> testPostEvent(Map.of(
                    "event", "",
                    "date", ""
            ), 400), //#12
            () -> testPostEvent(Map.of(
                    "date", "    ",
                    "event", ""

            ), 400), //#13
            () -> testPostEvent(Map.of(
                    "date", "    ",
                    "event", "New Year Party"
            ), 400),//#14
            () -> testPostEvent(Map.of(
                    "date", "",
                    "event", "New Year Party"
            ), 400),//#15

//            () -> testEndpoint(eventEndPoint, 200),//#10
            () -> testEndpoint(todayEndPoint, 200),//#16
            () -> testEndpoint(eventEndPoint, 200),//#17

            () -> testEndpointWithParams(eventEndPoint, 200,
                    "2020-08-26", LocalDate.now().toString()),//#18
            () -> testEndpoinById(eventEndPoint, 200, 1),//#19
            () -> testEndpoinDeleteById(eventEndPoint, 200, 2),//#20
            () -> testEndpoinById(eventEndPoint, 404, 2),//#21
            () -> testEndpoinById(eventEndPoint, 200, 1),//#22
            () -> testEndpoint(todayEndPoint, 200),//#23
            () -> testEndpoint(eventEndPoint, 200),//#24
            () -> testEndpoinDeleteById(eventEndPoint, 200, 1),//#25
            () -> testEndpoinById(eventEndPoint, 404, 1),//#26
            () -> testEndpoint(todayEndPoint, 400),//#27
            () -> testEndpoint(eventEndPoint, 200),//#28

            () -> testPostEvent( eventForTest.listOfEvents.get(randomReturn(eventForTest.listOfEvents)), 200),
            () -> testPostEvent( eventForTest.listOfEvents.get(randomReturn(eventForTest.listOfEvents)), 200),
            () -> testPostEvent( eventForTest.listOfEvents.get(randomReturn(eventForTest.listOfEvents)), 200),
            () -> testPostEvent( eventForTest.listOfEvents.get(randomReturn(eventForTest.listOfEvents)), 200),
            () -> testPostEvent( eventForTest.listOfEvents.get(randomReturn(eventForTest.listOfEvents)), 200),
            () -> testPostEvent( eventForTest.listOfEvents.get(randomReturn(eventForTest.listOfEvents)), 200),
            () -> testPostEvent( eventForTest.listOfEvents.get(randomReturn(eventForTest.listOfEvents)), 200),
            () -> testPostEvent( eventForTest.listOfEvents.get(randomReturn(eventForTest.listOfEvents)), 200),
            () -> testPostEvent( eventForTest.listOfEvents.get(randomReturn(eventForTest.listOfEvents)), 200),

            () -> testEndpoint(eventEndPoint, 200),//#28
    };

    @Before
    public  void createEvents() {
        eventForTest = new EventForTest();
        eventForTest.listOfEvents.stream().forEach(System.out::println);

    }

}
