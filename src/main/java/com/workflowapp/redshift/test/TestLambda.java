package com.workflowapp.redshift.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workflowapp.redshift.Details;
import com.workflowapp.redshift.WorkflowService;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TestLambda {

    public static void main(String [] args) throws IOException {

        WorkflowService service = new WorkflowService();
        boolean ans = service.lookupItem("978226599");
        System.out.println(ans);

    }

    public static String removeFieldsFromJson(String jsonString) {
        try {
            // Create ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();

            // Deserialize JSON to List<Map<String, Object>>
            List<Map<String, Object>> jsonList = objectMapper.readValue(jsonString, new TypeReference<List<Map<String, Object>>>() {});

            // Iterate over the list and remove "sos" and "status" fields
            for (Map<String, Object> jsonMap : jsonList) {
                jsonMap.remove("sos");
            }

            // Serialize the modified list back to JSON
            return objectMapper.writeValueAsString(jsonList);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String removeNullFieldsFromJson(String json) throws IOException {
        // Create ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();

        // Convert JSON string to List<Map<String, Object>>
        List<Map<String, Object>> jsonList = objectMapper.readValue(json, new TypeReference<List<Map<String, Object>>>() {});

        // Iterate over the list and remove entries with null values
        for (Map<String, Object> jsonMap : jsonList) {
            Iterator<Map.Entry<String, Object>> iterator = jsonMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Object> entry = iterator.next();
                if (entry.getValue() == null) {
                    iterator.remove();
                }
            }
        }

        // Convert the modified list back to JSON
        return objectMapper.writeValueAsString(jsonList);
    }
}
