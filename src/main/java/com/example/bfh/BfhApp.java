package com.example.bfh;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@SpringBootApplication
public class BfhApp implements CommandLineRunner {

    RestTemplate rt = new RestTemplate();

    public static void main(String[] args) {
        SpringApplication.run(BfhApp.class, args);
    }

    @Override
    public void run(String... args) {
        try {

            Map<String, String> req = Map.of(
                    "name", "Ballapuram Pavan Sai",
                    "regNo", "22BCE20366",
                    "email", "pavan.22bce20366@vitapstudent.ac.in"
            );

            ResponseEntity<Map> r = rt.postForEntity(
                    "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA",
                    req, Map.class);

            String hook = (String) r.getBody().get("webhook");
            String token = (String) r.getBody().get("accessToken");


            String sql =
                    "SELECT e.EMP_ID,e.FIRST_NAME,e.LAST_NAME,d.DEPARTMENT_NAME," +
                            "COUNT(e2.EMP_ID) AS YOUNGER_EMPLOYEES_COUNT " +
                            "FROM EMPLOYEE e " +
                            "JOIN DEPARTMENT d ON e.DEPARTMENT=d.DEPARTMENT_ID " +
                            "LEFT JOIN EMPLOYEE e2 ON e2.DEPARTMENT=e.DEPARTMENT " +
                            "AND e2.DOB>e.DOB " +
                            "GROUP BY e.EMP_ID,e.FIRST_NAME,e.LAST_NAME,d.DEPARTMENT_NAME " +
                            "ORDER BY e.EMP_ID DESC;";


            HttpHeaders h = new HttpHeaders();
            h.setContentType(MediaType.APPLICATION_JSON);
            h.set("Authorization", token);

            HttpEntity<Map<String,String>> ent =
                    new HttpEntity<>(Map.of("finalQuery", sql), h);

            ResponseEntity<String> res = rt.postForEntity(hook, ent, String.class);
            System.out.println(res.getBody());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
