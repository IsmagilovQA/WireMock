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
