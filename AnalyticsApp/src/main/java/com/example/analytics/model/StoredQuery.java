package com.example.analytics.model;

import jakarta.persistence.*;

@Entity
@Table(name = "stored_query")
public class StoredQuery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "query_text")
    private String queryText;

    public StoredQuery(){}
    public StoredQuery(String queryText){
        this.queryText=queryText;
    }

    public Long getID(){
        return id;
    }
    public String getQueryText(){
        return queryText;
    }
    public void setQueryText(String queryText){
        this.queryText=queryText;
    }
}
