package com.example.analytics.controller;

import com.example.analytics.model.StoredQuery;
import com.example.analytics.repository.StoredQueryRepo;
import com.example.analytics.service.QueryService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/queries")
public class QueryController {

    private QueryService service;

    public QueryController(StoredQueryRepo repo,QueryService service){
        this.service = service;
    }

    @PostMapping
    public Map<String, Object> addQuery(@RequestBody String queryText) {
        StoredQuery q = service.saveQuery(queryText);
        return Map.of("id", q.getID());
    }

    @GetMapping
    public List<Map<String, Object>> listQueries() {
        return service.getAllQueries().stream()
                .map(q -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", q.getID());
                    map.put("query", q.getQueryText());
                    return map;
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/execute")
    public List<Map<String, Object>>execute(@RequestParam("id") Long id){
        StoredQuery storedQuery = service.getStoredQuery(id);

        String sql = storedQuery.getQueryText().trim().toLowerCase();
        if (!sql.startsWith("select")) {
            throw new IllegalArgumentException("Only SELECT queries are allowed");
        }

        return service.executeQuery(id);
    }
}
