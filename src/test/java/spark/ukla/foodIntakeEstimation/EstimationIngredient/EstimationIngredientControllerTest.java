//package spark.ukla.foodIntakeEstimation.EstimationIngredient;
//
//import io.restassured.RestAssured;
//import io.restassured.http.ContentType;
//import io.restassured.response.Response;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.http.HttpStatus;
//import spark.ukla.entities.enums.Unity;
//
//import java.util.Random;
//
//import static io.restassured.RestAssured.given;
//import static org.hamcrest.Matchers.equalTo;
//import static org.hamcrest.Matchers.greaterThan;
//public class EstimationIngredientControllerTest {
//    private static final String BASE_URL = "http://localhost:8083/ukla/EstimationIngredient";
//
//    // logging before everything
//
//    private static String AccessToken;
//    @BeforeAll
//    public static void LoginAndGetAccessToken() {
//        String username = "ahmed2";
//        String password = "azerty123";
//        // Perform login and retrieve the access token
//        Response loginResponse = given()
//                .contentType(ContentType.URLENC)
//                .formParam("username", username)
//                .formParam("password", password)
//                .when()
//                .post("http://localhost:8083/ukla/login");
//
//        // Extract the access token from the login response
//        AccessToken = loginResponse.path("access_token");
//    }
//    @BeforeEach
//    void setUp() {
//
//        RestAssured.baseURI = BASE_URL;
//    }
//
//    private String generateRandomName() {
//        // You can modify this method to generate random names as per your requirements
//        Random random = new Random();
//        return "IngredientTest" + random.nextInt(1000);
//    }
//    @Test
//    public void givenEstimationIngredientObject_whenAdd_thenReturnString() {
//        // Prepare the request body and image file
//        String name = generateRandomName();
//        Unity unity = Unity.g;
//        byte[] imageContent = new byte[0] /* get image content as byte array */;
//        EstimationIngredient estimationIngredient = new EstimationIngredient();
//        estimationIngredient.setName(name);
//        estimationIngredient.setUnity(unity);
//
//        Response response = given()
//                .header("Authorization", "Bearer " + AccessToken)
//                .multiPart("image", "filename.jpg", imageContent)
//                .multiPart("estimationIngredient", estimationIngredient, "application/json")
//                .when()
//                .post("/add");
//
//        response.then()
//                .statusCode(HttpStatus.CREATED.value())
//                .body(equalTo("EstimationIngredient saved"));
//    }
//
//    @Test
//    public void givenEstimationIngredientObject_whenUpdate_thenReturnString() {
//        // Prepare the request body
//        String name = "UpdatedIngredient";
//        Unity unity = Unity.g;
//        EstimationIngredient estimationIngredient = new EstimationIngredient();
//        estimationIngredient.setId(1L);
//        estimationIngredient.setName(name);
//        estimationIngredient.setUnity(unity);
//
//        given()
//                .header("Authorization", "Bearer " + AccessToken)
//                .contentType(ContentType.JSON)
//                .body(estimationIngredient)
//                .when()
//                .put("/update")
//                .then()
//                .statusCode(HttpStatus.OK.value())
//                .body(equalTo("EstimationIngredient updated"));
//    }
//
//    @Test
//    public void whenGetAll_thenReturnEstimationIngredientList() {
//        given()
//                .header("Authorization", "Bearer " + AccessToken)
//                .when()
//                .get("/All")
//                .then()
//                .statusCode(HttpStatus.OK.value())
//                .body("size()", greaterThan(0));
//    }
//
//    @Test
//    public void givenEstimationIngredientId_whenDelete_thenReturnTrue() {
//        Long id = 2L;
//        given()
//                .header("Authorization", "Bearer " + AccessToken)
//                .when()
//                .delete("/delete/{id}", id)
//                .then()
//                .statusCode(HttpStatus.OK.value())
//                .body(equalTo("true"));
//    }
//
//    @Test
//    public void givenEstimationIngredientName_whenSearch_thenReturnEstimationIngredientList() {
//        String name = "Ingredient";
//        given()
//                .header("Authorization", "Bearer " + AccessToken)
//                .when()
//                .get("/search/{name}", name)
//                .then()
//                .statusCode(HttpStatus.OK.value());
//    }
//}
