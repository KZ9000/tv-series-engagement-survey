package com.example.tvseriesengagementsurvey;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ApiIntegrationTest {

    private static final String USER_EMAIL = "user@test.com";
    private static final String USER_PASSWORD = "Password123";
    private static final String ADMIN_EMAIL = "admin@test.com";
    private static final String ADMIN_PASSWORD = "Admin1234";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void flujoCompleto_registro_login_serie_rating_dashboard() throws Exception {
        registerUser(USER_EMAIL, USER_PASSWORD);
        String userToken = login(USER_EMAIL, USER_PASSWORD);
        String adminToken = login(ADMIN_EMAIL, ADMIN_PASSWORD);

        MvcResult seriesResult = mockMvc.perform(post("/api/series")
                        .header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Serie A","description":"Descripcion",
                                 "releaseDate":"2026-01-01"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.active").value(true))
                .andReturn();
        long seriesId = objectMapper.readTree(
                seriesResult.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(post("/api/ratings")
                        .header("Authorization", bearer(userToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"seriesId\":" + seriesId + ",\"score\":5}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.seriesId").value(seriesId))
                .andExpect(jsonPath("$.score").value(5));

        mockMvc.perform(post("/api/ratings")
                        .header("Authorization", bearer(userToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"seriesId\":" + seriesId + ",\"score\":4}"))
                .andExpect(status().isConflict());

        mockMvc.perform(get("/api/dashboard")
                        .header("Authorization", bearer(userToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].seriesId").value(seriesId))
                .andExpect(jsonPath("$[0].averageScore").value(5.0))
                .andExpect(jsonPath("$[0].totalVotes").value(1));
    }

    @Test
    void usuarioSinRolAdmin_noPuedeCrearSerie() throws Exception {
        registerUser(USER_EMAIL, USER_PASSWORD);
        String userToken = login(USER_EMAIL, USER_PASSWORD);

        mockMvc.perform(post("/api/series")
                        .header("Authorization", bearer(userToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Serie no permitida\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void registrarEmailDuplicado_devuelveConflict() throws Exception {
        registerUser(USER_EMAIL, USER_PASSWORD);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"%s"}
                                """.formatted(USER_EMAIL, USER_PASSWORD)))
                .andExpect(status().isConflict());
    }

    @Test
    void scoreFueraDeRango_devuelveBadRequest() throws Exception {
        registerUser(USER_EMAIL, USER_PASSWORD);
        String userToken = login(USER_EMAIL, USER_PASSWORD);

        mockMvc.perform(post("/api/ratings")
                        .header("Authorization", bearer(userToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"seriesId\":1,\"score\":9}"))
                .andExpect(status().isBadRequest());
    }

    private void registerUser(String email, String password) throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"%s\",\"password\":\"%s\"}"
                                .formatted(email, password)))
                .andExpect(status().isCreated());
    }

    private String login(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"%s\",\"password\":\"%s\"}"
                                .formatted(email, password)))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(
                result.getResponse().getContentAsString()).get("token").asText();
    }

    private static String bearer(String token) {
        return "Bearer " + token;
    }
}
