import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.hyperskill.hstest.dynamic.DynamicTest;
import org.hyperskill.hstest.dynamic.input.DynamicTesting;
import org.hyperskill.hstest.exception.outcomes.WrongAnswer;
import org.hyperskill.hstest.mocks.web.response.HttpResponse;
import org.hyperskill.hstest.stage.SpringTest;
import org.hyperskill.hstest.testcase.CheckResult;
import webCalendarSpring.Main;

import java.time.LocalDate;
import java.util.Map;

import static org.hyperskill.hstest.testing.expect.Expectation.expect;
import static org.hyperskill.hstest.testing.expect.json.JsonChecker.isObject;
import static org.hyperskill.hstest.testing.expect.json.JsonChecker.isString;

public class webCalendarSpringTest extends SpringTest {

    public webCalendarSpringTest() {
        super(Main.class);

    }

    public static final String todayEndPoint = "/event/today";

    public static final String eventtEndPoint = "/eventt";
    public static final String eventEndPoint = "/event";

    private static final Gson gson = new Gson();


    CheckResult testEndpoint(String url, int status) {
        HttpResponse response = get(url).send();
        checkStatusCode(response, status);
        System.out.println(response.getContent() +" "+response.getRequest()+" "+ response.getStatusCode()+" "+
                response.getHeaders() +" "+response.getRequest().getLocalUri()+" "+response.getRequest().getMethod()
                +" "+response.getRequest().getContent());
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

    CheckResult testEndpointWithParams(String url, int status, String startDay, String endDay) {
        HttpResponse response = get(url+"?start_time="+startDay+"&end_time="+endDay).send();
        checkStatusCode(response, status);
        System.out.println(response.getContent() +" "+response.getRequest()+" "+ response.getStatusCode()+" "+
                response.getHeaders() +" "+response.getRequest().getLocalUri()+" "+response.getRequest().getMethod()
                +" "+response.getRequest().getContent());
        return CheckResult.correct();
    }

    CheckResult testEndpoinById(String url, int status, int id) {
        HttpResponse response = get(url+"/"+id).send();
        checkStatusCode(response, status);
        System.out.println(response.getContent() +" "+response.getRequest()+" "+ response.getStatusCode()+" "+
                response.getHeaders() +" "+response.getRequest().getLocalUri()+" "+response.getRequest().getMethod()
                +" "+response.getRequest().getContent());
        return CheckResult.correct();
    }

    CheckResult testEndpoinDeleteById(String url, int status, int id) {
        HttpResponse response = delete(url+"/"+id).send();
        checkStatusCode(response, status);
        System.out.println(response.getContent() +" "+response.getRequest()+" "+ response.getStatusCode()+" "+
                response.getHeaders() +" "+response.getRequest().getLocalUri()+" "+response.getRequest().getMethod()
                +" "+response.getRequest().getContent());
        return CheckResult.correct();
    }
    CheckResult testPostEvent() {
        HttpResponse response = post(
                eventEndPoint,
                gson.toJson(Map.of(
                        "event", "New Year Party",
                "date", LocalDate.now().toString()
                ))
        ).send();

        checkStatusCode(response, 200);

        System.out.println(response.getContent() +" "+response.getRequest()+" "+ response.getStatusCode()+" "+
                response.getHeaders() +" "+response.getRequest().getLocalUri()+" "+response.getRequest().getMethod()
                +" "+response.getRequest().getContent());

//        expect(response.getContent()).asJson()
//                .check(
//                        isObject()
//                                .value("token", isString())
//                                .value("ticket",
//                                        isObject()
//                                                .value("row", 1)
//                                                .value("column", 1)
//                                                .value("price", 10)
//                                )
//                );
//
//        JsonObject object = gson.fromJson(response.getContent(), JsonObject.class);
//        token = object.get("token").getAsString();

        return CheckResult.correct();
    }
    @DynamicTest
    DynamicTesting[] dynamicTests = new DynamicTesting[]{
            ()->testEndpoint(todayEndPoint, 400),
            ()->testPostEvent(),
            ()->testEndpoint(todayEndPoint, 200),
            ()->testPostEvent(),
            ()->testPostEvent(),
            ()->testEndpoint(eventEndPoint, 200),
            ()->testEndpointWithParams(eventEndPoint, 200, "2021-08-26", LocalDate.now().toString()),
            ()->testEndpoinById(eventEndPoint, 200, 1),
            ()->testEndpoinDeleteById(eventEndPoint, 200, 1)
    };

}
