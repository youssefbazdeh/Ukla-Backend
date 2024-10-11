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
import spark.ukla.ad_feature.bannerAd.BannerAd;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class bannerAdsIntegrationTest {
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
    public void testgetBannerAds() throws Exception {
        // Example
        int pageNo = 1;
        int pageSize=6;
        Long idCampaign= 1l;

        mockMvc.perform(MockMvcRequestBuilders.get("/bannerAd/get/" + pageNo+"/"+pageSize+"/"+idCampaign)
                        .header("Authorization", "Bearer " + AccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Transactional(rollbackFor = Exception.class)
    @Test
    public void testAddSuccess() throws Exception {
        Long idCampaign= 1l;

        MockMultipartFile imageFile = new MockMultipartFile("image", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "image content".getBytes());
        MockMultipartFile videoFile = new MockMultipartFile("video", "video.mp4", "video.mp4", "video content".getBytes());

        BannerAd bannerAd=new BannerAd();


        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        String bannerJson = mapper.writeValueAsString(bannerAd);
        MockMultipartFile bannerFile = new MockMultipartFile("bannerAd", "bannerAd.json", "application/json", bannerJson.getBytes());

        mockMvc.perform(multipart("/bannerAd/add/" + idCampaign)
                        .file(bannerFile)
                        .file(imageFile)
                        .file(videoFile)
                        .header("Authorization", "Bearer " + AccessToken))
                .andExpect(status().isCreated())
                .andExpect(content().string("Created"));
    }

    @Transactional(rollbackFor = Exception.class)
    @Test
    public void testIncrementVues() throws Exception {
        Long bannerId = 1L;

        mockMvc.perform(post("/bannerAd/incrementView/"+bannerId)
                        .header("Authorization", "Bearer " + AccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
    @Transactional(rollbackFor = Exception.class)
    @Test
    public void testFailIncrementVues() throws Exception {
        Long bannerId = 3L;

        mockMvc.perform(post("/bannerAd/incrementView/"+bannerId)
                        .param("ids", "")
                        .header("Authorization", "Bearer " + AccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("false"));
    }

    @Transactional(rollbackFor = Exception.class)
    @Test
    public void testIncrementClicks() throws Exception {
        Long bannerId = 1L;

        mockMvc.perform(post("/bannerAd/incrementClick/"+bannerId)
                        .header("Authorization", "Bearer " + AccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
    @Transactional(rollbackFor = Exception.class)
    @Test
    public void testFailIncrementClicks() throws Exception {
        Long bannerId = 3L;

        mockMvc.perform(post("/bannerAd/incrementClick/"+bannerId)
                        .param("ids", "")
                        .header("Authorization", "Bearer " + AccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("false"));
    }

    @Transactional(rollbackFor = Exception.class)
    @Test
    public void testDelete() throws Exception {
        long bannerId = 2L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/bannerAd/delete/" + bannerId))
                .andExpect(status().isOk())
                .andExpect(content().string("deleted"));
    }
}
