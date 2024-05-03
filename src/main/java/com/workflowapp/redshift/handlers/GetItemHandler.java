// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.workflowapp.redshift.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workflowapp.redshift.Details;
import com.workflowapp.redshift.WorkflowService;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


// Gets all items based on status. For example, if status is Draft, then it gets all items based on Draft.
public class GetItemHandler {

    public String handleRequest(Map<String, String> event, Context context) throws IOException {
        LambdaLogger logger = context.getLogger();
        String status = event.get("status");
        logger.log("STATUS IS " + status);
        WorkflowService service = new WorkflowService();
        List<Details> data = service.getData(status);
        logger.log("RESULT SET IS " + data.size());

        // Create an ObjectMapper.
        ObjectMapper objectMapper = new ObjectMapper();

        // Convert the List of Details objects to JSON.
        String json = objectMapper.writeValueAsString(data);

        // Remove unwanted fields from the JSON
        json = removeFieldsFromJson(json);
        json = removeNullFieldsFromJson(json);
        logger.log("JSON IS " + json);
        return json;
    }

    public String removeFieldsFromJson(String jsonString) {
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



