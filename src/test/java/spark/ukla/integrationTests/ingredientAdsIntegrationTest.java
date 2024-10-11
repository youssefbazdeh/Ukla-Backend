package spark.ukla.integrationTests;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.restassured.response.Response;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static io.restassured.RestAssured.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import spark.ukla.ad_feature.CountryCode;
import spark.ukla.ad_feature.ingrediantAd.IngredientAd;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class ingredientAdsIntegrationTest {
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
    public void testgetIngredientAds() throws Exception {
        // Example
        int pageNo = 1;
        int pageSize=6;
        Long idCampaign= 1l;

        mockMvc.perform(MockMvcRequestBuilders.get("/ingredientAd/get/" + pageNo+"/"+pageSize+"/"+idCampaign)
                        .header("Authorization", "Bearer " + AccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Transactional(rollbackFor = Exception.class)
    @Test
    public void testAddSuccess() throws Exception {
        String ingredientName ="butter";
        Long idCampaign= 1l;

        MockMultipartFile imageFile = new MockMultipartFile("image", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "image content".getBytes());

        IngredientAd ingredientAd=new IngredientAd();




        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        String ingredientAdJson = mapper.writeValueAsString(ingredientAd);
        MockMultipartFile ingredientAdFile = new MockMultipartFile("ingredientAd", "ingredientAd.json", "application/json", ingredientAdJson.getBytes());

        mockMvc.perform(multipart("/ingredientAd/add/" + idCampaign+"/"+ingredientName)
                        .file(ingredientAdFile)
                        .file(imageFile)
                        .header("Authorization", "Bearer " + AccessToken))
                .andExpect(status().isCreated())
                .andExpect(content().string("Created"));
    }

    @Transactional(rollbackFor = Exception.class)
    @Test
    public void testAddSameCountryCodeAndIngredient() throws Exception {
        String ingredientName ="bananas";
        Long idCampaign= 1l;

        MockMultipartFile imageFile = new MockMultipartFile("image", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "image content".getBytes());

        IngredientAd ingredientAd=new IngredientAd();
        ingredientAd.setCountryCode(CountryCode.TN);



        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        String ingredientAdJson = mapper.writeValueAsString(ingredientAd);
        MockMultipartFile ingredientAdFile = new MockMultipartFile("ingredientAd", "ingredientAd.json", "application/json", ingredientAdJson.getBytes());

        mockMvc.perform(multipart("/ingredientAd/add/" + idCampaign+"/"+ingredientName)
                        .file(ingredientAdFile)
                        .file(imageFile)
                        .header("Authorization", "Bearer " + AccessToken))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("error"));
    }


    @Transactional(rollbackFor = Exception.class)
    @Test
    public void testIncrementVues() throws Exception {
        // Example ingredients ads IDs {1,2}

        mockMvc.perform(post("/ingredientAd/incrementView")
                        .param("ids", "1,2")
                        .header("Authorization", "Bearer " + AccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Transactional(rollbackFor = Exception.class)
    @Test
    public void testFailIncrementVues() throws Exception {
        // Example empty list of banner ads IDs


        mockMvc.perform(post("/ingredientAd/incrementView")
                        .param("ids", "")
                        .header("Authorization", "Bearer " + AccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("false"));
    }


    @Transactional(rollbackFor = Exception.class)
    @Test
    public void testDelete() throws Exception {
        long ingredientId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/ingredientAd/delete/" + ingredientId))
                .andExpect(status().isOk())
                .andExpect(content().string("deleted"));
    }

}
