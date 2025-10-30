package com.example.analytics;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class FlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testFullFlow() throws Exception {
        mockMvc.perform(post("/queries")
                        .content("SELECT * FROM passengers LIMIT 2;")
                        .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk());

        mockMvc.perform(get("/queries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].query", containsString("SELECT * FROM passengers")));

        mockMvc.perform(get("/queries/execute")
                .param("id","1"))
                .andExpect(status().isOk());
    }
}
