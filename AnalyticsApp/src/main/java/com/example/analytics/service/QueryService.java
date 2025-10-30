package com.example.analytics.service;

import com.example.analytics.model.StoredQuery;
import com.example.analytics.repository.StoredQueryRepo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class QueryService {
    private JdbcTemplate jdbc;
    private StoredQueryRepo sqr;

    public QueryService(JdbcTemplate jdbc, StoredQueryRepo sqr){
        this.sqr=sqr;
        this.jdbc=jdbc;
    }

    public StoredQuery getStoredQuery(Long id) {
        return sqr.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Query with id " + id + " not found"));
    }

    public StoredQuery saveQuery(String queryText) {
        return sqr.save(new StoredQuery(queryText));
    }

    public List<StoredQuery> getAllQueries() {
        return sqr.findAll();
    }


    public List<Map<String, Object>> executeQuery(Long id) {
        Optional<StoredQuery> opt = sqr.findById(id);
        if (opt.isEmpty()) throw new RuntimeException("Query not found");

        String sql = opt.get().getQueryText().trim().toLowerCase();
        if (!sql.startsWith("select")) {
            throw new RuntimeException("Only SELECT queries are allowed");
        }

        return jdbc.queryForList(opt.get().getQueryText());
    }
}
