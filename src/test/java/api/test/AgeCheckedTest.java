package api.test;

import api.endpoints.Endpoints;
import api.payload.Person;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;

public class AgeCheckedTest {

    private Person personPayload;
    private static long Max_Response = 5000;
    private static String Merchant_Key ="Cm9U48CxDuG7ntq3TAWJvGKJtkKnLvPwijb3nVg329Ly8ipafLcAx0s035y3x12f";

    @BeforeClass
    public void setupData() {

        personPayload = new Person();

        personPayload.setMerchantSecretKey(Merchant_Key);
        personPayload.setName("Sheldon");
        personPayload.setSurname("COMPLETELYFOUND");
        personPayload.setBuilding("10");
        personPayload.setStreet("High Street");
        personPayload.setPostcode("SW1A 1AA");
        personPayload.setDob("15/06/1967");
        personPayload.setCountrycode("GB");
        personPayload.setEmail("name123456@email.com");
    }

    // Create a Map from the existing Person payload
    private Map<String, Object> createRequest() {

        Map<String, Object> body = new HashMap<>();

        body.put("merchantSecretKey", personPayload.getMerchantSecretKey());
        body.put("name", personPayload.getName());
        body.put("surname", personPayload.getSurname());
        body.put("building", personPayload.getBuilding());
        body.put("street", personPayload.getStreet());
        body.put("postcode", personPayload.getPostcode());
        body.put("dob", personPayload.getDob());
        body.put("countrycode", personPayload.getCountrycode());
        body.put("email", personPayload.getEmail());

        return body;
    }

    // TC01: Verify approved user
    @Test(priority=1,description = "TC01: Verify approved user")
    public void testApprovedUser() {

        Response response = Endpoints.postRequest(personPayload);

        response.then()
                .statusCode(200)
                .body("avstatus.status", equalTo(6))
                .body("avstatus.statusText", equalTo("Approved"))
                .body("avstatus.acmethod", equalTo("AC0101"))
                .body("authenticated", equalTo(true))
                .body("avstatus.agecheckid", notNullValue())
                .body("avstatus.ageverifiedid", notNullValue());
    }

    // TC02: Verify unrecognised user
    @Test(priority=2,description = "TC02: Verify unrecognised user")
    public void testUnrecognisedUser() {

        Map<String, Object> body = createRequest();

        body.put("surname", "NOTFOUND");

        Response response = Endpoints.postRequest(body);

        response.then()
                .statusCode(200)
                .body("avstatus.status", equalTo(16))
                .body("avstatus.statusText", equalTo("NotRecognised"))
                .body("avstatus.acmethod", equalTo("AC0101"))
                .body("avstatus.agecheckid", notNullValue())
                .body("authenticated", equalTo(false));
    }

    // TC03: Verify response time
    @Test(priority=3,description = "TC03: Verify response time")
    public void testResponseTime() {

        long responseTime =Endpoints.postRequest(personPayload).getTime();

        Assert.assertTrue(responseTime < Max_Response, "Response time exceeded " +Max_Response+ " milliseconds. Actual time: " + responseTime + " ms");
    }

    // TC04: Verify missing merchant key
    @Test(priority=4,description = "TC04: Verify missing merchant key")
    public void testMissingMerchantKey() {

        Map<String, Object> body = createRequest();

        body.remove("merchantSecretKey");

        Response response = Endpoints.postRequest(body);

        response.then()
                .statusCode(200)
                .body("avstatus", nullValue())
                .body("authenticated", nullValue())
                .body("removemethod", nullValue())
                .body("errormessage", equalTo("Invalid Merchant Key"));
    }

    // TC05: Verify invalid merchant key
    @Test(priority=5,description = "TC05: Verify invalid merchant key")
    public void testInvalidMerchantKey() {

        Map<String, Object> body = createRequest();

        body.put("merchantSecretKey","INVALID_KEY");

        Response response = Endpoints.postRequest(body);

        response.then()
                .statusCode(200)
                .body("avstatus", nullValue())
                .body("authenticated", nullValue())
                .body("removemethod", nullValue())
                .body("errormessage", equalTo("Invalid Merchant Key"));
    }

    @DataProvider(name = "mandatoryFields")
    public Object[][] mandatoryFields() {

        return new Object[][] {
                {"name"},
                {"surname"},
                {"postcode"},
                {"dob"},
                {"countrycode"},
                {"email"}
        };
    }

 // TC06: Test missing mandatory fields
    @Test(priority=6,dataProvider = "mandatoryFields",description = "TC06: Missing mandatory field")
    public void testMissingField(String field) {

        Map<String, Object> body = createRequest();

        body.remove(field);

        Response response = Endpoints.postRequest(body);
        
        String expectedField = field;
        
        if (field.equals("postcode")) 
        { 
        	expectedField = "postCode"; 
        } 
        else if (field.equals("countrycode")) 
        { 
        	expectedField = "countryCode"; 
        }

        response.then()
                .statusCode(200)
                .body("avstatus", nullValue())
                .body("authenticated", nullValue())
                .body("removemethod", nullValue())
                .body("errormessage", equalTo("Required field(s): " + expectedField));
    }

    
    @DataProvider(name = "invalidValues")
    public Object[][] invalidValues() {

        return new Object[][] {
                {"dob", "1967-06-15"},
                {"dob", "31/02/1967"},
                {"email", "userexample.com"},
                {"email", "user@@"},
                {"countrycode", "US"},
                {"countrycode", ""},
                {"name", "   "}
        };
    }

 // TC07: Test invalid field values
    @Test(priority=7,dataProvider = "invalidValues",description = "TC07: Invalid field value")
    public void testInvalidValues(String field, String value) {

        Map<String, Object> body = createRequest();

        body.put(field, value);

        Response response = Endpoints.postRequest(body);

        response.then()
        		.statusCode(200)
        		.body("avstatus", nullValue())
        		.body("errormessage", notNullValue());
    }
   
 @DataProvider(name = "surnames")
 public Object[][] surnames() {
     return new Object[][] {
         {"COMPLETELYFOUND"},
         {"NOTFOUND"},
         {" NOTFOUND "},
         {""}
     };
 }

 // TC08: Test different surname values
 @Test(priority=8,dataProvider = "surnames",description = "TC08: Different surnames")
 public void testDifferentSurnames(String surname) {

     Map<String, Object> body = createRequest();

     body.put("surname", surname);

     Response response = Endpoints.postRequest(body);

     response.then().statusCode(200);

     // Check the response based on the surname
     if (surname.equals("COMPLETELYFOUND")) {

         response.then()
                 .body("avstatus.statusText", equalTo("Approved"))
                 .body("authenticated", equalTo(true));

     } else if (surname.equals("NOTFOUND") || surname.equals(" NOTFOUND ")) {

         response.then()
                 .body("avstatus.statusText", equalTo("NotRecognised"))
                 .body("authenticated", equalTo(false));

     } else if (surname.isEmpty()) {

         response.then()
                 .body("avstatus", nullValue())
                 .body("errormessage",equalTo("Required field(s): surname"));
     }
 }

    @DataProvider(name = "injectionPayloads")
    public Object[][] injectionPayloads() {

        return new Object[][] {
                {"' OR '1'='1"},
                {"'; DROP TABLE users;--"}
        };
    }

    // TC09: SQL injection in name
    @Test(priority=9,dataProvider = "injectionPayloads",description = "TC09: Injection in name")
    public void testInjectionInName(String payload) {

        Map<String, Object> body = createRequest();

        body.put("name", payload);

        Response response = Endpoints.postRequest(body);

        Assert.assertEquals(response.getStatusCode(),403,"Expected HTTP 403 for SQL injection payload in name");
    }

    // TC10: SQL injection in email
    @Test(priority=10,dataProvider = "injectionPayloads",description = "TC10: Injection in email")
    public void testInjectionInEmail(String payload) {

        Map<String, Object> body = createRequest();

        body.put("email", payload);

        Response response = Endpoints.postRequest(body);

        Assert.assertEquals(response.getStatusCode(),403,"Expected HTTP 403 for SQL injection payload in email");
    }

    // TC11: SQL injection in merchant key
    @Test(priority=11,dataProvider = "injectionPayloads",description = "TC11: Injection in merchant key")
    public void testInjectionInMerchantKey(String payload) {

        Map<String, Object> body = createRequest();

        body.put("merchantSecretKey", payload);

        Response response = Endpoints.postRequest(body);

        Assert.assertEquals(response.getStatusCode(),403,"Expected HTTP 403 for SQL injection payload in merchant key");
    }

    // TC12: Verify GET is not allowed
    @Test(priority=12,description = "TC12: Verify GET is not allowed")
    public void testGetNotAllowed() {

        Response response = Endpoints.getRequest();

        Assert.assertEquals(response.getStatusCode(),405,"GET request should not be allowed");
    }

    // TC13: Test malformed JSON
    @Test(priority=13,description = "TC13: Test malformed JSON")
    public void testMalformedJson() {

        Response response =Endpoints.postRawJson("{ \"name\": ");

        Assert.assertEquals(response.getStatusCode(),400,"Malformed JSON should return HTTP 400");
    }
  
 // TC14: Test empty JSON object
 @Test(priority=14,description = "TC14: Test empty JSON object")
 public void testEmptyBody() {

     Response response = Endpoints.postRawJson("{}");

     response.then()
             .statusCode(200)

             .body("avstatus", nullValue())

             .body("errormessage",equalTo("Required field(s): name, surname, email, dob, countryCode, postCode"));
    }

}