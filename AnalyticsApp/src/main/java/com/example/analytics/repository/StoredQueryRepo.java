package com.example.analytics.repository;

import com.example.analytics.model.StoredQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoredQueryRepo extends JpaRepository<StoredQuery,Long> {}
