import org.hyperskill.hstest.dynamic.DynamicTest;
import org.hyperskill.hstest.dynamic.input.DynamicTesting;
import org.hyperskill.hstest.exception.outcomes.WrongAnswer;
import org.hyperskill.hstest.mocks.web.response.HttpResponse;
import org.hyperskill.hstest.stage.SpringTest;
import org.hyperskill.hstest.testcase.CheckResult;
import webCalendarSpring.Main;

import static org.hyperskill.hstest.testing.expect.Expectation.expect;
import static org.hyperskill.hstest.testing.expect.json.JsonChecker.isObject;


public class webCalendarSpringTest extends SpringTest {

    int count = 0;

    public webCalendarSpringTest() {

        super(Main.class, "../d.mv.db");

    }

    public static final String todayEndPoint = "/event/today";


    CheckResult testEndpoint(String url, int status) {
        HttpResponse response = get(url).send();

        System.out.println("\n"+response.getContent() + " \n"  + response.getStatusCode() +  "\n "
                + response.getRequest().getLocalUri() + "\n " + response.getRequest().getMethod())
               ;

        checkStatusCode(response, 400);
        if (!response.getJson().isJsonObject()) {
            return CheckResult.wrong("Wrong object in response, expected JSON but was \n" +
                    response.getContent().getClass());

        }


        expect(response.getContent()).asJson().check(
                isObject()
                        .value("data", "There are no events for today!")

        );
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


    @DynamicTest
    DynamicTesting[] dynamicTests = new DynamicTesting[]{


            () -> testEndpoint(todayEndPoint, 400), //#1

    };


}
