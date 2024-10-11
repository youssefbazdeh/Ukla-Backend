//package spark.ukla.foodIntakeEstimation.EstimationMeal;
//
//import io.restassured.RestAssured;
//import io.restassured.http.ContentType;
//import io.restassured.response.Response;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import spark.ukla.foodIntakeEstimation.EstimationRecipe.EstimationRecipeRepository;
//import spark.ukla.repositories.UserRepository;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static io.restassured.RestAssured.given;
//import static org.hamcrest.Matchers.equalTo;
//
//
//public class EstimationMealControllerTest {
//
//    @Mock
//    private EstimationMealRepository estimationMealRepository;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private EstimationRecipeRepository estimationRecipeRepository;
//
//    private EstimationMealService estimationMealService;
//    private static String AccessToken;
//    private static final String BASE_URL = "http://localhost:8083/ukla/EstimationMeal";
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
//        estimationMealService = new EstimationMealService(estimationMealRepository, userRepository, estimationRecipeRepository);
//        RestAssured.baseURI = BASE_URL;
//    }
//
//    @Test
//    public void testAddMultipleItems() {
//        List<EstimationMeal> meals= new ArrayList<>();
//        String name1 = "test1";
//        String name2 = "test2";
//        EstimationMeal estimationMeal = new EstimationMeal();
//        estimationMeal.setName(name1);
//        meals.add(estimationMeal);
//        EstimationMeal estimationMeal2 = new EstimationMeal();
//        estimationMeal2.setName(name2);
//        meals.add(estimationMeal2);
//
//        given()
//                .header("Authorization", "Bearer " + AccessToken)
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .body(meals)
//                .when()
//                .post("/add")
//                .then()
//                .statusCode(HttpStatus.OK.value());
//
//    }
//    @Test
//    public void givenIdAndName_WhenUpdate_Then() {
//        String name = "name";
//        Long id = 1L;
//        given()
//                .header("Authorization", "Bearer " + AccessToken)
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .pathParam("id", id)
//                .pathParam("Name", name)
//                .when()
//                .put("/update/{id}/{Name}")
//                .then()
//                .statusCode(HttpStatus.OK.value())
//                .body(equalTo("true"));
//
//    }
//    @Test
//    public void givenId_WhenGetOne_ThenReturnEstimationMeal(){
//        Long id = 100L;
//        String name = "test";
//        EstimationMeal estimationMeal = new EstimationMeal();
//        estimationMeal.setId(id);
//        estimationMeal.setName(name);
//
//        // Perform the add operation
//        estimationMealService.add(estimationMeal, "ahmed2");
//
//        // Perform the getOne operation using RestAssured
//        given()
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .header("Authorization", "Bearer " + AccessToken)
//                .when()
//                .get("/get/{id}", id)
//                .then()
//                .statusCode(HttpStatus.OK.value())
//                .body("id", equalTo(id.intValue()))
//                .body("name", equalTo(name));
//    }
//    @Test
//    public void givenEstimationMealsListAndUserId_whenFindAllByUserId_thenReturnEstimationMealsListByUserId(){
//        Long userId = 1L;
//        given()
//                .header("Authorization", "Bearer " + AccessToken)
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .when()
//                .get("/All")
//                .then()
//                .statusCode(HttpStatus.OK.value());
//    }
//    @Test
//    public void givenEstimationMealId_whenDelete_thenReturnTrue(){
//        Long id = 1L;
//        given()
//                .header("Authorization", "Bearer " + AccessToken)
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .pathParam("id", id)
//                .when()
//                .delete("/delete/{id}")
//                .then()
//                .statusCode(HttpStatus.OK.value());
//    }
//    @Test
//    public void givenEstimationMealIdAndEstimationRecipeId_whenDeleteEstimationRecipeFromEstimationMeal_thenReturnTrue(){
//        Long idMeal = 1L;
//        Long estimationRecipeId = 1L;
//        given()
//                .header("Authorization", "Bearer " + AccessToken)
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .pathParam("idMeal", idMeal)
//                .pathParam("idrecipe", estimationRecipeId)
//                .when()
//                .delete("/deleteEstimationRecipe/{idMeal}/{idrecipe}")
//                .then()
//                .statusCode(HttpStatus.OK.value())
//                .body(equalTo("Estimation Recipe deleted"));
//    }
//}
