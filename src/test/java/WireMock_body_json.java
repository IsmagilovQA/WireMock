import com.github.tomakehurst.wiremock.WireMockServer;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;


public class WireMock_body_json {

    private WireMockServer wireMockServer = new WireMockServer();

    @BeforeSuite
    public void setupWireMockServer() {

        wireMockServer.start();
        configureFor("localhost", 8080);

        stubFor(get(urlEqualTo("/api/message"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json; charset=UTF-8")
                        .withBody("{\n" +
                                "    \"data\":\n" +
                                "        {\n" +
                                "            \"id\": 5848\n" +
                                "        }\n" +
                                "}")));                          // http://jsoneditoronline.org/ helps to convert
    }

    @AfterSuite
    public void closeWireMockServer() {
        wireMockServer.stop();
    }

    @Test
    public void shouldReturnHttpConfiguredHttpResponse() {

        given()
                .when()
                .get("http://localhost:8080/api/message")
                .then()
                .assertThat()
                .statusCode(200)
                .body("data.id", is(5848))
                .log().ifValidationFails();
    }
}
