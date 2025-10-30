package com.example.analytics.util;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;


@Component
public class CsvLoader {
    private JdbcTemplate jdbc;

    public CsvLoader(JdbcTemplate jdbc){
        this.jdbc = jdbc;
    }

    @PostConstruct
    public void loadData() {
        try {
            jdbc.execute("DROP TABLE IF EXISTS passengers");

            jdbc.execute("""
                CREATE TABLE passengers (
                    PassengerId INT PRIMARY KEY,
                    Survived INT,
                    Pclass INT,
                    Name VARCHAR(255),
                    Sex VARCHAR(10),
                    Age DOUBLE,
                    SibSp INT,
                    Parch INT,
                    Ticket VARCHAR(50),
                    Fare DOUBLE,
                    Cabin VARCHAR(50),
                    Embarked VARCHAR(5)
                )
            """);

            ClassPathResource resource = new ClassPathResource("data/titanic.csv");
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

                String line;
                boolean header = true;

                while ((line = reader.readLine()) != null) {
                    if (header) {
                        header = false;
                        continue;
                    }

                    String[] parts = parseCsvLine(line);

                    if (parts.length < 12) continue;

                    String ageStr = parts[5];
                    Double age = ageStr.isEmpty() ? null : Double.parseDouble(ageStr);

                    String fareStr = parts[9];
                    Double fare = fareStr.isEmpty() ? null : Double.parseDouble(fareStr);

                    jdbc.update("""
                        INSERT INTO passengers (PassengerId, Survived, Pclass, Name, Sex, Age, SibSp, Parch, Ticket, Fare, Cabin, Embarked)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """,
                            Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]),
                            parts[3], parts[4], age,
                            Integer.parseInt(parts[6]), Integer.parseInt(parts[7]), parts[8],
                            fare, parts[10], parts[11]
                    );
                }
            }

            System.out.println("Successfully Loaded");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String[] parseCsvLine(String line) {
        return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
    }
}
