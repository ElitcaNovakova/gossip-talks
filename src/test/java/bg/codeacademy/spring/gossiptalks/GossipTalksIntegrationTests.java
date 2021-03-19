package bg.codeacademy.spring.gossiptalks;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.oneOf;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import java.time.Duration;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;


// Enable the 'dev' profile - uses in-memory DB so data is cleared
// after each test class.
@ActiveProfiles("dev")
// Run the spring application on a random web port
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@Disabled
public class GossipTalksIntegrationTests {

  private static final String DEFAULT_PASS = "p@ssworD1longenough";

  // get the random port, used by the spring application
  @LocalServerPort
  int port;

  @BeforeEach
  public void beforeEachTest() {
    // init port and logging
    RestAssured.port = port;
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
  }

  @AfterEach
  public void afterEachTest() {
    RestAssured.reset();
  }

  @Test
  public void createUser_with_InvalidUsername_should_Fail() {
    given()
        // prepare
        .multiPart("email", "u1@mail.com")
        .multiPart("username", "UserIvan")//'^[a-z0-8\\.\\-]+$'
        .multiPart("password", DEFAULT_PASS)
        .multiPart("passwordConfirmation", DEFAULT_PASS)
        // do
        .when()
        .post("/api/v1/users")
        // test
        .then()
        .statusCode(not(oneOf(OK.value(), CREATED.value())));
  }

  @Test
  public void createUser_with_InvalidEmail_should_Fail() {
    given()
        // prepare
        .multiPart("email", "u1")
        .multiPart("username", "userivan")
        .multiPart("password", DEFAULT_PASS)
        .multiPart("passwordConfirmation", DEFAULT_PASS)
        // do
        .when()
        .post("/api/v1/users")
        // test
        .then()
        .statusCode(is(not(oneOf(OK.value(), CREATED.value()))));
  }

  @Test
  public void createUser_with_NotMatchingPasswords_should_Fail() {
    given()
        // prepare
        .multiPart("email", "u1@mail.com")
        .multiPart("username", "userivan")
        .multiPart("password", DEFAULT_PASS)
        .multiPart("passwordConfirmation", "hello")
        // do
        .when()
        .post("/api/v1/users")
        // test
        .then()
        .statusCode(not(oneOf(OK.value(), CREATED.value())));
  }

  @Test
  public void createUser_with_UsernameWithDash_should_Pass() {
    createValidUser("username-with-dash");
  }

  @Test
  public void createUser_with_UsernameWithDot_should_Pass() {
    createValidUser("username.with.dots");
  }

  @Test
  public void createUser_with_UsernameWithDigit_should_Pass() {
    createValidUser("username-with-digit1");
  }

  @Test
  public void getMethods_without_Authentication_should_Fail() {
    get("/api/v1/users").then().statusCode(UNAUTHORIZED.value());
    get("/api/v1/users/me").then().statusCode(UNAUTHORIZED.value());
    get("/api/v1/users/ivan/gossips").then().statusCode(UNAUTHORIZED.value());
    get("/api/v1/gossips").then().statusCode(UNAUTHORIZED.value());
  }

  @Test
  public void getUsersMe_when_Authenticated_should_Pass() {
    createValidUser("getusersme");

    given()
        // auth
        .auth()
        .basic("getusersme", DEFAULT_PASS)
        // do
        .when()
        .get("/api/v1/users/me")
        // test
        .then()
        .statusCode(OK.value())
        .body("email", is("getusersme@mail.com"))
        .body("username", is("getusersme"))
        .body("name", is("getusersme"));
  }

  @Test
  public void createGossip_with_InvalidContent_should_Fail() {
    createValidUser("cgic");

    given()
        // prepare
        .multiPart("text", "** Header\n<script type=\"javascript\">alert(1);</script>")
        // auth
        .auth()
        .basic("cgic", DEFAULT_PASS)
        // do
        .when()
        .post("/api/v1/gossips")
        // test
        .then()
        .statusCode(is(not(OK.value())));
  }


  @Test
  public void createGossip_with_ValidContent_should_Pass() {
    createValidUser("cgvc");

    // create the gossip
    String id = createValidGossip("cgvc", "** Header\n Some markdown here\n A > B\n\n");

    // test if I can see my gossip for /users/{username}/gossips
    given()
        // auth
        .auth()
        .basic("cgvc", DEFAULT_PASS)
        // do
        .when()
        .get("/api/v1/users/cgvc/gossips")
        // test
        .then()
        .statusCode(OK.value())
        .body("pageNumber", is(0))
        .body("pageSize", is(20))
        .body("count", is(1))
        .body("total", is(1))
        .body("content[0].username", is("cgvc"))
        .body("content[0].text", is("** Header\n Some markdown here\n A > B\n\n"))
        .body("content[0].id", is(id));

    // test if I don't see my gossips globally
    given()
        // auth
        .auth()
        .basic("cgvc", DEFAULT_PASS)
        // do
        .when()
        .get("/api/v1/gossips")
        // test
        .then()
        .statusCode(OK.value())
        .body("pageNumber", is(0))
        .body("pageSize", is(20))
        .body("count", is(0))
        .body("total", is(0));
  }

  @Test
  public void getGossips_should_RequireFollow() {
    createValidUser("follow1");
    createValidUser("follow2");

    String id = createValidGossip("follow1", "hello world");

    // test that user2 doesn't see gossips globally from user 1
    given()
        // auth
        .auth()
        .basic("follow2", DEFAULT_PASS)
        // do
        .when()
        .get("/api/v1/gossips")
        // test
        .then()
        .statusCode(OK.value())
        .body("pageNumber", is(0))
        .body("pageSize", is(20))
        .body("count", is(0))
        .body("total", is(0));

    // now follow the user1
    given()
        .multiPart("follow", "true")
        // auth
        .auth()
        .basic("follow2", DEFAULT_PASS)
        // do
        .when()
        .post("/api/v1/users/follow1/follow")
        // test
        .then()
        .statusCode(OK.value())
        .body("username", is("follow1"))
        .body("following", is(true));

    // test if can see the gossip now
    given()
        // auth
        .auth()
        .basic("follow2", DEFAULT_PASS)
        // do
        .when()
        .get("/api/v1/gossips")
        // test
        .then()
        .statusCode(OK.value())
        .body("pageNumber", is(0))
        .body("pageSize", is(20))
        .body("count", is(1))
        .body("total", is(1))
        .body("content[0].username", is("follow1"))
        .body("content[0].text", is("hello world"))
        .body("content[0].id", is(id));

    // test unfollow
    given()
        .multiPart("follow", "false")
        // auth
        .auth()
        .basic("follow2", DEFAULT_PASS)
        // do
        .when()
        .post("/api/v1/users/follow1/follow")
        // test
        .then()
        .statusCode(OK.value())
        .body("username", is("follow1"))
        .body("following", is(false));
  }

  // utilities
  private void createValidUser(String name) {
    given()
        // prepare
        .multiPart("email", name + "@mail.com")
        .multiPart("username", name)//'^[a-z0-8\\.\\-]+$'
        .multiPart("name", name)
        .multiPart("password", DEFAULT_PASS)
        .multiPart("passwordConfirmation", DEFAULT_PASS)
        // do
        .when()
        .post("/api/v1/users")
        // test
        .then()
        .statusCode(oneOf(OK.value(), CREATED.value()));
  }

  private String createValidGossip(String username, String text) {
    // create the gossip
    JsonPath jsonPath = given()
        // prepare
        .multiPart("text", text)
        // auth
        .auth()
        .basic(username, DEFAULT_PASS)
        // do
        .when()
        .post("/api/v1/gossips")
        // test
        .then()
        .statusCode(OK.value())
        .body("text", is(text))
        .body("username", is(username))
        .body("id", matchesPattern("[A-Z0-9]+"))
        //extract
        .extract()
        .body()
        .jsonPath();

    final String datetime = jsonPath.getString("dateTime");
    OffsetDateTime _then = OffsetDateTime.parse(datetime);
    OffsetDateTime _now = OffsetDateTime.now();
    Assertions.assertTrue(Duration.between(_then, _now).toMillis() < 3000);

    return jsonPath.getString("id");
  }
}
