package io.my.app;

import static org.junit.Assert.*;
import org.junit.*; 
import java.util.*;
import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;
import static io.restassured.module.jsv.JsonSchemaValidator.*;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;

/**
 * Sample tests for testing the APIs of https://api.github.com.
 */
public class AppTest 
{
    final String userId = "weishiuntsai";
    // Enter the password to run the tests that require authentication.
    final String userPass = "";

    @BeforeClass
    public static void setup() {

        // set RestAssured.baseURI.
        baseURI = "https://api.github.com";

    }
    
    /**
     * Test RESTAssured request (with GET) on 
     * https://api.github.com.
     */
    @Test
    public void test_request()
    {
        /* The URI "https://api.github.com" should return a list of 
         * available APIs and their endpoints.
         */
        Response rs =
        given()
            .headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON)
        .when()
            .request("GET", "")
        .then()
            .statusCode(200) // OK: 200
            .contentType(ContentType.JSON)
            .extract()
            .response();

        // Convert the JSON result to a Map.
        Map<String, String> m = rs.jsonPath().getMap("$"); // "$": root element

        // size must > 0
        assertTrue(m.size() > 0);

        // Some sample APIs and endpoints that we expect to see
        Map<String, String> exp = new HashMap<String, String>() {
            {
                put("current_user_url", "https://api.github.com/user");
                put("hub_url", "https://api.github.com/hub");
                put("issues_url", "https://api.github.com/issues");
                put("user_url", "https://api.github.com/users/{user}");
            }
        };

        // Expect to see all these expected APIs and endpoints in the result
        // set.
        for (Map.Entry e : exp.entrySet()) {
            String endpoint = m.get(e.getKey());
            // The API must exist; the endpoints must match.
            assertTrue(endpoint != null); 
            assertTrue(endpoint.equals(e.getValue()));
        }
    }

    /**
     * Test HTTP GET on 
     * https://api.github.com/users/{user} 
     * by accessing my own repo.
     */
    @Test
    public void test_get()
    {
        String test_user = "weishiuntsai"; // a.k.a. me. the guinea pig. :)  

        Response rs =
        given()
            .headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON)
        .when()
            .get("/users/" + test_user)
        .then()
            .statusCode(200) // OK: 200
            .contentType(ContentType.JSON)
            .extract()
            .response();

        // expect the login to be myself
        assertTrue(rs.jsonPath().getString("login").equals(test_user)); 
       
        // expect the repos_url to point to my own repo. 
        assertTrue(rs.jsonPath().getString("repos_url").equals("https://api.github.com/users/" + test_user + "/repos"));
    }

    /**
     * Test HTTP HEAD on 
     * https://api.github.com/feeds (API exists),
     * https://api.github.com/doesntexist (API doesnt exist).
     */
    @Test
    public void test_head()
    {
        // https://api.github.com/feeds exists. Expect status code=200
        given()
            .headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON)
        .when()
            .head("/feeds")
        .then()
            .assertThat().statusCode(200) // OK: 200
            .assertThat().contentType(ContentType.JSON);

        // https://api.github.com/doesntexist does not exist.  Expect status
        // code=404
        given()
            .headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON)
        .when()
            .head("/doesntexist")
        .then()
            .statusCode(404) // Not Found: 404
            .contentType(ContentType.JSON);
    }

    /**
     * Test HTTP PUT on 
     * https://api.github.com/repos/:owner/:repo/contents/:path
     * to update a file test.txt in my own repo.
     * This test requires setting userId and userPass for authentication.
     */
    @Test
    public void test_put()
    {
        String file="/repos/weishiuntsai/test_repo/contents/test.txt";

        // Get the file sha
        Response rs1 =
        given()
            .headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON)
        .when()
            .get(file)
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .extract()
            .response();

        String sha = rs1.jsonPath().getString("sha");
        String content = "new content";
        String encoded_content = Base64.getEncoder().encodeToString(content.getBytes());

        String bodyData = "{\n" +
        "  \"message\": \"update an existing file\"," +
        "  \"committer\": {" + 
        "    \"name\": \"Weishiun Tsai\"," + 
        "    \"email\": \"me@gmail.com\"" + 
        "  }," +
        " \"sha\": \"" + sha + "\"," +
        " \"content\": \"" + encoded_content + "\"" + 
        "}";

        Response rs2 =
        given()
            .headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON)
            .auth()
            .preemptive()
            .basic(userId, userPass)
            .body(bodyData)
        .when()
            .put(file)
        .then()
            .assertThat().statusCode(200) // OK: 200
            .assertThat().contentType(ContentType.JSON)
            .extract()
            .response();

        // returned body should have the right file name and sha.
        assertTrue(rs2.jsonPath().getString("content.name").equals("test.txt"));
        assertTrue(rs2.jsonPath().getString("content.sha").equals(sha));
    }

    /**
     * Test HTTP DELETE on
     * https://api.github.com/repos/:owner/:repo/contents/:path
     * to delete a file test.txt in my own repo.
     * This test requires setting userId and userPass for authentication.
     */
    @Test
    public void test_delete()
    {
        String file="/repos/weishiuntsai/test_repo/contents/new_test.txt";

        String content = "some content";
        String encoded_content = Base64.getEncoder().encodeToString(content.getBytes());

        // Use Put to create a new file first

        String bodyData1 = "{\n" +
        "  \"message\": \"create a new file\"," +
        "  \"committer\": {" +
        "    \"name\": \"Weishiun Tsai\"," +
        "    \"email\": \"me@gmail.com\"" +
        "  }," +
        " \"content\": \"" + encoded_content + "\"" +
        "}";

        Response rs1 =
        given()
            .headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON)
            .auth()
            .preemptive()
            .basic(userId, userPass)
            .body(bodyData1)
        .when()
            .put(file)
        .then()
            .assertThat().statusCode(201) // Created: 201
            .assertThat().contentType(ContentType.JSON)
            .extract()
            .response();

        // get the sha of the creaeted file
        String sha = rs1.jsonPath().getString("content.sha");

        String bodyData2 = "{\n" +
        "  \"message\": \"delete a file\"," +
        "  \"committer\": {" +
        "    \"name\": \"Weishiun Tsai\"," +
        "    \"email\": \"me@gmail.com\"" +
        "  }," +
        "  \"sha\": \"" + sha + "\"" +
        "}";

        Response rs2 =
        given()
            .headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON)
            .auth()
            .preemptive()
            .basic(userId, userPass)
            .body(bodyData2)
        .when()
            .delete(file)
        .then()
            .assertThat().statusCode(200) // OK: 200
            .assertThat().contentType(ContentType.JSON)
            .extract()
            .response();
    }
}
