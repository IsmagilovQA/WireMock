import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.http.HttpHeaders;
import io.restassured.response.Response;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


public class WireMock_basic_get {

    private WireMockServer wireMockServer = new WireMockServer(); // the server host defaults to localhost and the server port to 8080


    @BeforeSuite(description = "GET request")
    public void setupWireMockServer() {

        HttpHeaders multiple_headers = new HttpHeaders(             // in case Headers
                new HttpHeader("Name 1", "Value 1"),
                new HttpHeader("Name 2", "Value 2")
        );

        wireMockServer.start(); // start server
        configureFor("localhost", 8080);

        stubFor(get(urlEqualTo("/an/endpoint"))
                .willReturn(aResponse()
                        //.withHeader("Content-Type", "text/plain")
                        .withHeaders(multiple_headers)
                        .withStatus(200)
                        .withStatusMessage("Good to go!")
                        .withBody("You've reached a valid WireMock endpoint")));
    }

    @AfterSuite
    public void closeWireMockServer() {
        wireMockServer.stop(); // stop server
    }


    @Test(description = "Status code | Header | Headers | Status line | Returned body ")
    public void testStatusCodePositive() {
        Response response = given()
                .when()
                .get("http://localhost:8080/an/endpoint")
                .then()
                .assertThat()
                .statusCode(200)
                //.header("Content-Type", "text/plain")
                .headers("Name 1", "Value 1",
                        "Name 2", "Value 2")
                .statusLine("HTTP/1.1 200 Good to go!")
                .log().ifValidationFails()
                .extract().response();

        String text = response.asString();
        assertThat("You've reached a valid WireMock endpoint", is(text));
    }
}
