import com.github.tomakehurst.wiremock.WireMockServer;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import wiremock.com.google.common.net.MediaType;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;


public class WireMock_withCookie_Header_Param_Auth {

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
        stubFor(get(urlPathEqualTo("/api/endpoint"))    // urlPathEqualTo for working with queryParam
                .withCookie("name", equalTo("Cookie"))
                .withHeader("Accept", equalTo("application/json"))
                .withQueryParam("search", equalTo("test"))
                .withBasicAuth("user", "123456")
                .willReturn(aResponse()
                        .withStatus(200)));
    }

    @Test
    public void test_Cookie_Header_queryParam_auth() {
        given()
                .auth().preemptive().basic("user", "123456")
                .queryParam("search", "test")
                .cookie("name", "Cookie")
                .header("Accept", "application/json")
                .when()
                .get("http://localhost:8080/api/endpoint")
                .then().assertThat()
                .statusCode(200);
    }
}
