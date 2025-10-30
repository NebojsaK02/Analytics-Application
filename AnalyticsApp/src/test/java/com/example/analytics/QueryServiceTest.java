package com.example.analytics;

import com.example.analytics.model.StoredQuery;
import com.example.analytics.repository.StoredQueryRepo;
import com.example.analytics.service.QueryService;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class QueryServiceTest {
    @Test
    void executesSelectQuerySuccessfully() {
        StoredQueryRepo repo = mock(StoredQueryRepo.class);
        JdbcTemplate jdbc = mock(JdbcTemplate.class);

        StoredQuery q = new StoredQuery("SELECT * FROM passengers;");
        q.setQueryText("SELECT * FROM passengers;");
        when(repo.findById(1L)).thenReturn(Optional.of(q));
        when(jdbc.queryForList(anyString())).thenReturn(List.of(Map.of("PassengerId", 1)));

        QueryService service = new QueryService(jdbc, repo);
        List<Map<String, Object>> result = service.executeQuery(1L);

        assertEquals(1, result.size());
        verify(jdbc, times(1)).queryForList(anyString());
    }

    @Test
    void rejectsNonSelectQuery() {
        StoredQueryRepo repo = mock(StoredQueryRepo.class);
        JdbcTemplate jdbc = mock(JdbcTemplate.class);

        StoredQuery q = new StoredQuery("DELETE FROM passengers;");
        when(repo.findById(1L)).thenReturn(Optional.of(q));

        QueryService service = new QueryService(jdbc, repo);

        assertThrows(RuntimeException.class, () -> service.executeQuery(1L));
    }
}
