package api.endpoints;

import static io.restassured.RestAssured.given;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class Endpoints {
	
    // POST request 
    public static Response postRequest(Object payload) {

        return given()
                .contentType(ContentType.JSON)
                .body(payload)
        .when()
                .post(Routes.post_url);
    }

    // GET request
    public static Response getRequest() {

        return given()
        .when()
                .get(Routes.post_url);
    }

    // POST request 
    public static Response postRawJson(String json) {

        return given()
                .contentType(ContentType.JSON)
                .body(json)
        .when()
                .post(Routes.post_url);
    }
}