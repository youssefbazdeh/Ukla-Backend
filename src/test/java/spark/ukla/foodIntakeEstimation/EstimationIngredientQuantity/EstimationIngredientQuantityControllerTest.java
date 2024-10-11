//package spark.ukla.foodIntakeEstimation.EstimationIngredientQuantity;
//
//import io.restassured.RestAssured;
//import io.restassured.http.ContentType;
//import io.restassured.response.Response;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.http.HttpStatus;
//
//import static io.restassured.RestAssured.given;
//import static io.restassured.http.ContentType.JSON;
//import static org.hamcrest.Matchers.equalTo;
//import static org.hamcrest.Matchers.greaterThan;
//
//public class EstimationIngredientQuantityControllerTest {
//
//    private static String AccessToken;
//    private static final String BASE_URL = "http://localhost:8083/ukla/EstimationIngredientQuantity";
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
//
//                .post("http://localhost:8083/ukla/login");
//
//        // Extract the access token from the login response
//        AccessToken = loginResponse.path("access_token");
//    }
//    @BeforeEach
//    public void setUp() {
//        RestAssured.baseURI = BASE_URL;
//    }
//
//
//    @Test
//    public void givenEstimationIngredientQuantityObject_WhenAdd_thenReturnString()
//    {
//        // Prepare the request body and image file
//        Long idEstimationIngredient = 1L;
//        EstimationIngredientQuantity estimationIngredientQuantity= new EstimationIngredientQuantity();
//        int quantity = 5;
//        byte[] imageContent = new byte[0]/* get image content as byte array */;
//        estimationIngredientQuantity.setQuantity(quantity);
//
//        given()
//                .header("Authorization", "Bearer " + AccessToken)
//                .pathParam("idEstimationIngredient", idEstimationIngredient)
//                .multiPart("estimationIngredientQuantity", estimationIngredientQuantity,"application/json")
//                .multiPart("image", "filename.jpg", imageContent)
//                .when()
//                .post("/add/{idEstimationIngredient}")
//                .then()
//                .statusCode(HttpStatus.CREATED.value())
//                .body(equalTo("EstimationIngredientQuantity saved"));
//    }
//
//    @Test
//    public void givenEstimationIngredientQuantityObject_whenUpdate_thenReturnTrue() {
//        Long idEstimationIngredient = 2L;
//        // Prepare the request body
//        int quantity = 10;
//        EstimationIngredientQuantity estimationIngredientQuantity = new EstimationIngredientQuantity();
//        estimationIngredientQuantity.setId(idEstimationIngredient);
//        estimationIngredientQuantity.setQuantity(quantity);
//
//        given()
//                .header("Authorization", "Bearer " + AccessToken)
//                .contentType(JSON)
//                .body(estimationIngredientQuantity)
//                .when()
//                .put("/update")
//                .then()
//                .statusCode(HttpStatus.OK.value())
//                .body(equalTo("true"));
//    }
//
//    @Test
//    public void givenEstimationIngredientId_whenGetEstimationIngredientQuantity_thenEstimationIngredientQuantityList() {
//        Long idEstimationIngredient = 1L;
//        given()
//                .header("Authorization", "Bearer " + AccessToken)
//                .when()
//                .get("/All?idEstimationIngredient={idEstimationIngredient}", idEstimationIngredient)
//                .then()
//                .statusCode(HttpStatus.OK.value())
//                .body("size()", greaterThan(0));
//    }
//
//    @Test
//    public void givenEstimationIngredientQuantityId_whenDelete_thenreturnTrue() {
//        Long id = 1L;
//        given()
//                .header("Authorization", "Bearer " + AccessToken)
//                .when()
//                .delete("/delete/{id}", id)
//                .then()
//                .statusCode(HttpStatus.OK.value())
//                .body(equalTo("true"));
//    }
//}
