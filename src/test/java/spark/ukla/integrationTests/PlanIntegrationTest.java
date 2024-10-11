package spark.ukla.integrationTests;

import io.restassured.response.Response;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Properties;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class PlanIntegrationTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    private static String AccessToken;
    @BeforeAll
    public static void LoginAndGetAccessToken() throws IOException {
        Properties prop = new Properties();
        FileInputStream input = new FileInputStream("src/main/resources/config.properties");
        prop.load(input);

        String serverPort = prop.getProperty("server.port");
        String username = prop.getProperty("username");
        String password = prop.getProperty("password");


        Response loginResponse = given()
                .contentType(ContentType.URLENC)
                .formParam("username", username)
                .formParam("password", password)
                .when()
                .post("http://localhost:"+serverPort+"/ukla/login");
        AccessToken = loginResponse.path("access_token");
    }
    @BeforeEach
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Transactional(rollbackFor = Exception.class)
    @Test
    public void deletePlan_WhenExists_ReturnsSuccess() throws Exception {
        long planId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/Plan/delete/" + planId)
                        .header("Authorization", "Bearer " + AccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("deleted"));
    }

    @Transactional(rollbackFor = Exception.class)
    @Test
    public void deletePlan_WhenNotExists_ReturnsNotFound() throws Exception {
        long planId = 2L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/Plan/delete/" + planId)
                        .header("Authorization", "Bearer " + AccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("no plan found matching that id "));
    }
    @Transactional(rollbackFor = Exception.class)
    @Test
    public void followPlan_WhenValidInput_Success() throws Exception {
        long planId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.post("/Plan/follow-plan/" + planId)
                        .header("Authorization", "Bearer " + AccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("plan followed"));
    }
    @Transactional(rollbackFor = Exception.class)
    @Test
    public void followPlan_WhenInvalidId_ReturnsNotFound() throws Exception {
        long planId = 999L;

        mockMvc.perform(MockMvcRequestBuilders.post("/Plan/follow-plan/" + planId)
                        .header("Authorization", "Bearer " + AccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("plan not found"));
    }

    @Transactional(rollbackFor = Exception.class)
    @Test
    public void renamePlan_WithNewName_Succeeds() throws Exception {
        long planId = 1L;

        String newPlanName="new_plan_name";
        mockMvc.perform(MockMvcRequestBuilders.put("/Plan/renamePlan/"+ newPlanName + "/" + planId)
                        .header("Authorization", "Bearer " + AccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("plan renamed"));
    }

    @Transactional(rollbackFor = Exception.class)
    @Test
    public void addPlan_WithValidDetails_ReturnsSuccessfulResponse() throws Exception {
        LocalDate userDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String formattedDate = "\"" + userDate.format(formatter) + "\"";


        mockMvc.perform(MockMvcRequestBuilders.post("/Plan/addPLan")
                        .header("Authorization", "Bearer " + AccessToken)
                        .content(formattedDate)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("New plan"))
                .andExpect(jsonPath("$.followed").exists())
                .andExpect(jsonPath("$.calories").exists())
                .andExpect(jsonPath("$.days").isArray())
                .andExpect(jsonPath("$.days[*].name", allOf(hasItem("TUESDAY"), hasItem("WEDNESDAY"), hasItem("THURSDAY"), hasItem("FRIDAY"), hasItem("SATURDAY"), hasItem("SUNDAY"), hasItem("MONDAY"))))
                .andExpect(jsonPath("$.days[*].meals").isArray());
    }

    @Transactional(rollbackFor = Exception.class)
    @Test
    void changePlanDate_WithUpdatedDate_Succeeds() throws Exception {
        Long planIdToUpdate = 1L;

        LocalDate userDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String formattedDate = "\"" + userDate.format(formatter) + "\"";

        mockMvc.perform(MockMvcRequestBuilders.put("/Plan/changeTheDateOfThePlan/" + planIdToUpdate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formattedDate))
                .andExpect(status().isOk())
                .andExpect(content().string("Date changed"));
    }
}
