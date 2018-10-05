import com.github.tomakehurst.wiremock.WireMockServer;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;


public class WireMock_basic_post {

    private WireMockServer wireMockServer = new WireMockServer();

    @BeforeSuite(description = "POST request")
    public void setupWireMockServer() {
        wireMockServer.start();
        configureFor("localhost", 8080);

        stubFor(post(urlEqualTo("/pingpong"))
                .withRequestBody(matching("<input>PING</input>"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/xml")
                        .withBody("<output>PONG</output>")));
    }

    @AfterSuite
    public void closeWireMockServer() {
        wireMockServer.stop();
    }


    @Test(description = "Request-response matching - XML  - return specific body when the request body equals a given value")
    public void testPingPongPositive() {
        given()
                .body("<input>PING</input>")
                .when()
                .post("http://localhost:8080/pingpong")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .assertThat()
                .body("output", is("PONG"))
                .log().body();
    }
}
