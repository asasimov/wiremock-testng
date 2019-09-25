package ru.qa.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.hamcrest.Matchers;

import java.util.Random;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static io.restassured.RestAssured.given;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.entity.ContentType.TEXT_PLAIN;

public class WireMockTest {

    private WireMockServer mockServer;
    private byte a, b;

    @BeforeMethod
    public void startMockServer(){
        final int PORT = 8090;
        mockServer = new WireMockServer(
                wireMockConfig().port(PORT)
                .extensions("ru.qa.wiremock.StubResponseTransformer")
        );
        mockServer.start();
        WireMock.configureFor("localhost", PORT);
    }

    @BeforeMethod
    void setValues(){
        a = (byte) new Random().nextInt(Byte.MAX_VALUE);
        b = (byte) new Random().nextInt(Byte.MAX_VALUE);
    }

    @Test
    public void testWireMock(){
        this.setUpStub();
        given().when()
                .queryParam("operation", "plus")
                .queryParam("left", a)
                .queryParam("right", b)
                .get("http://localhost:8090/calculator")
                .then()
                .log().all()
                .assertThat().statusCode(SC_OK)
                .assertThat().body(Matchers.equalTo(String.valueOf(a + b)));
    }

    @AfterMethod(alwaysRun = true)
    public void stopMockServer(){
        mockServer.stop();
    }

    private void setUpStub() {
        stubFor(get(urlPathEqualTo("/calculator"))
                .withQueryParam("operation", WireMock.matching("([a-z]*)"))
                .withQueryParam("left", WireMock.matching("([0-9]*)"))
                .withQueryParam("right", WireMock.matching("^\\d+$"))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, TEXT_PLAIN.toString())
                        .withTransformers("stub-transformer")
                        .withStatus(SC_OK)
                )
        );
    }

}