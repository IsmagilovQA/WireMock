import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import org.hamcrest.Matchers;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;


public class WireMock_stateful_scenario {

    private WireMockServer wireMockServer = new WireMockServer();

    @BeforeSuite
    public void setupWireMockServer() {

        wireMockServer.start();
        configureFor("localhost", 8080);

        stubFor(get(urlEqualTo("/todolist"))
                .inScenario("addItem")
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/xml")
                        .withBody("<list>Empty</list>")));

        stubFor(post(urlEqualTo("/todolist"))
                .inScenario("addItem")
                .whenScenarioStateIs(Scenario.STARTED)
                .willSetStateTo("itemAdded")
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/xml")
                        .withStatus(201)));

        stubFor(get(urlEqualTo("/todolist"))
                .inScenario("addItem")
                .whenScenarioStateIs("itemAdded")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/xml")
                        .withBody("<list><item>Item added to list</item></list>")));
    }

    @AfterSuite
    public void closeWireMockServer() {
        wireMockServer.stop();
    }

    @Test(description = "Perform scenarios GET-POST-GET")
    public void testStatefulMock() {

        given()
                .when()
                .get("http://localhost:8080/todolist")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .assertThat().body("list", Matchers.equalTo("Empty"));

        given()
                .when()
                .post("http://localhost:8080/todolist")
                .then()
                .assertThat()
                .statusCode(201);

        given()
                .when()
                .get("http://localhost:8080/todolist")
                .then()
                .assertThat()
                .statusCode(200)
                .body("list", Matchers.not("Empty"))
                .body("list.item", Matchers.equalTo("Item added to list"));
    }
}

/*
// First StubMapping
stubFor(get(urlEqualTo("/my/resource"))
        .withHeader("Accept", equalTo("text/xml"))
        .inScenario("Retry Scenario")
        .whenScenarioStateIs(STARTED)
        .willReturn(aResponse()
            .withStatus(500) // request unsuccessful with status code 500
            .withHeader("Content-Type", "text/xml")
            .withBody("<response>Some content</response>"))
        .willSetStateTo("Cause Success")););

// Second StubMapping
stubFor(get(urlEqualTo("/my/resource"))
        .withHeader("Accept", equalTo("text/xml"))
        .inScenario("Retry Scenario")
        .whenScenarioStateIs("Cause Success")
        .willReturn(aResponse()
            .withStatus(200)  // request successful with status code 200
            .withHeader("Content-Type", "text/xml")
            .withBody("<response>Some content</response>")));

https://github.com/mmcc007/wiremock-example/blob/master/src/test/java/com/ontestautomation/wiremock/StatefulMockTest.java
 */
