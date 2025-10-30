package com.example.analytics;

import com.example.analytics.model.StoredQuery;
import com.example.analytics.repository.StoredQueryRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests:
 *  - Adding a query
 *  - Listing all queries
 *  - Executing a query
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class QueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StoredQueryRepo repo;

    @BeforeEach
    void setup() {
        repo.deleteAll();
    }

    @Test
    void testAddQuery() throws Exception {
        mockMvc.perform(post("/queries")
                        .content("SELECT * FROM passengers LIMIT 2;")
                        .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void testListQueries() throws Exception {
        repo.save(new StoredQuery("SELECT * FROM passengers LIMIT 1;"));

        mockMvc.perform(get("/queries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].query", containsString("SELECT")));
    }

    @Test
    void testExecuteQuery() throws Exception {
        StoredQuery q = repo.save(new StoredQuery("SELECT PassengerId, Sex, Age FROM passengers LIMIT 3;"));

        mockMvc.perform(get("/queries/execute").param("id", q.getID().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[0].PassengerId").exists());
    }

    @Test
    void testRejectNonSelectQuery() throws Exception {
        StoredQuery q = repo.save(new StoredQuery("DELETE FROM passengers;"));

        mockMvc.perform(get("/queries/execute").param("id", q.getID().toString()))
                .andExpect(status().isBadRequest());
    }
}
