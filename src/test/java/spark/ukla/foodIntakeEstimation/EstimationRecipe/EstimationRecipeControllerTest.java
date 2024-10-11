//package spark.ukla.foodIntakeEstimation.EstimationRecipe;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.SerializationFeature;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import io.restassured.RestAssured;
//import io.restassured.http.ContentType;
//import io.restassured.response.Response;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.HttpStatus;
//import spark.ukla.foodIntakeEstimation.EstimationIngredientQuantity.EstimationIngredientQuantityRepository;
//import spark.ukla.foodIntakeEstimation.EstimationMeal.EstimationMealRepository;
//
//import java.util.Arrays;
//import java.util.List;
//
//import static io.restassured.RestAssured.given;
//import static org.hamcrest.Matchers.equalTo;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//public class EstimationRecipeControllerTest {
//
//    @Mock
//    private EstimationRecipeRepository estimationRecipeRepository;
//
//    @Mock
//    private EstimationIngredientQuantityRepository estimationIngredientQuantityRepository;
//
//    @Mock
//    private EstimationMealRepository estimationMealRepository;
//
//    @InjectMocks
//    private EstimationRecipeService estimationRecipeService;
//    private static final String BASE_URL = "http://localhost:8083/ukla/EstimationRecipe";
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
//
//                .post("http://localhost:8083/ukla/login");
//
//        // Extract the access token from the login response
//        AccessToken = loginResponse.path("access_token");
//    }
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.initMocks(this);
//        estimationRecipeService = new EstimationRecipeService(estimationRecipeRepository, estimationIngredientQuantityRepository, estimationMealRepository);
//        RestAssured.baseURI = BASE_URL;
//    }
//
//    @Test
//    public void givenEstimationRecipeObject_whenAdd_thenResponseWithCreatedStatus() {
//        // Prepare the request body
//        EstimationRecipe estimationRecipe = new EstimationRecipe();
//        // Set estimationRecipe properties
//        estimationRecipe.setFrequency(1);
//        estimationRecipe.setName("test");
//
//        given()
//                .header("Authorization", "Bearer " + AccessToken)
//                .contentType(ContentType.JSON)
//                .body(estimationRecipe)
//                .when()
//                .post("/add/{idEstMeal}", 1L)
//                .then()
//                .statusCode(HttpStatus.CREATED.value())
//                .body(equalTo("EstimationRecipe saved"));
//    }
//
//    @Test
//    public void givenEstimationRecipeObject_whenUpdate_thenResponseWithOkStatus() {
//        // Prepare the request body
//        EstimationRecipe estimationRecipe = new EstimationRecipe();
//        estimationRecipe.setId(1L);
//        estimationRecipe.setFrequency(2);
//        estimationRecipe.setName("testupdate");
//        // Set estimationRecipe properties
//
//        given()
//                .header("Authorization", "Bearer " + AccessToken)
//                .contentType(ContentType.JSON)
//                .body(estimationRecipe)
//                .when()
//                .put("/update")
//                .then()
//                .statusCode(HttpStatus.OK.value());
//    }
//    @Test
//    public void testGetAllEstimationRecipes() throws JsonProcessingException {
//        // Make a GET request to /All and retrieve the response
//        Response response = given()
//                .header("Authorization", "Bearer " + AccessToken)
//                .when()
//                .get("/All")
//                .then()
//                .statusCode(HttpStatus.OK.value())
//                .extract()
//                .response();
//
//        // Get the response body as a JSON string
//        String responseBody = response.asString();
//
//        // Convert the response body JSON string to a list of EstimationRecipe objects
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.registerModule(new JavaTimeModule());
//        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//        List<EstimationRecipe> estimationRecipeListFromEndpoint = objectMapper.readValue(responseBody, new TypeReference<List<EstimationRecipe>>() {});
//
//        // Get the list of EstimationRecipe objects from the estimationRecipeService.getAll() method
//        List<EstimationRecipe> estimationRecipeListFromService = estimationRecipeService.getAll();
//
//        // Assert that the lists are equal
//        assertEquals(estimationRecipeListFromService, estimationRecipeListFromEndpoint);
//    }
//    @Test
//    public void givenEstimationRecipeId_whenDelete_thenResponseWithOkStatus() {
//        given()
//                .header("Authorization", "Bearer " + AccessToken)
//                .when()
//                .delete("/delete/{id}", 1L)
//                .then()
//                .statusCode(HttpStatus.OK.value());
//    }
//    @Test
//    public void givenEstimationRecipeIdAndQuantityId_whenRemoveQuantity_thenResponseWithOkStatus() {
//        given()
//                .header("Authorization", "Bearer " + AccessToken)
//                .when()
//                .put("/removeQuantity/{id}/{quantityId}", 1L, 2L)
//                .then()
//                .statusCode(HttpStatus.OK.value());
//    }
//    @Test
//    public void givenEstimationRecipeIdAndEstimationQuantityIds_whenAddListQuantity_thenResponseWithOkStatus() {
//        // Prepare the request body
//        List<Long> estimationQuantityIds = Arrays.asList(1L, 2L, 3L);
//
//        given()
//                .header("Authorization", "Bearer " + AccessToken)
//                .contentType(ContentType.JSON)
//                .body(estimationQuantityIds)
//                .when()
//                .put("/addQuantity/{id}", 1L)
//                .then()
//                .statusCode(HttpStatus.OK.value());
//    }
//}
