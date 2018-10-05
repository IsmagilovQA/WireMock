import com.github.tomakehurst.wiremock.WireMockServer;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;


public class WireMock_body_jsonFile {

    private WireMockServer wireMockServer;

    @BeforeTest
    public void setup() {
        wireMockServer = new WireMockServer(8080);
        wireMockServer.start();
        setupStub();
    }

    @AfterTest
    public void teardown() {
        wireMockServer.stop();
    }

    @Test
    public void setupStub() {
        stubFor(get(urlEqualTo("/api/endpoint"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("test1.json")));
    }

    @Test
    public void test() {

        given()
                .when()
                .get("http://localhost:8080/api/endpoint")
                .then()
                .assertThat()
                .statusCode(200)
                .body("data.navigation[0].id", is(4906))
                .log().ifValidationFails();
    }
}