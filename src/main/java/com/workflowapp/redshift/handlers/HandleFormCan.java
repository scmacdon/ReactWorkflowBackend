// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.workflowapp.redshift.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workflowapp.redshift.Details;
import com.workflowapp.redshift.WorkflowService;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

// This is used in Candidate view and returns JSON without score.
public class HandleFormCan implements RequestHandler<Map<String, Object>, String> {
    @Override
    public String handleRequest(Map<String, Object> event, Context context) {
        LambdaLogger logger = context.getLogger();
        try {
            String itemId = (String) event.get("id");
            logger.log("Item id is " + itemId);
            WorkflowService service = new WorkflowService();
            List<Details> details = service.getDataByIdWithType(Long.valueOf(itemId));

            // Create an ObjectMapper.
            ObjectMapper objectMapper = new ObjectMapper();

            // Convert the List of a Details object to JSON.
            String json = objectMapper.writeValueAsString(details);
            if (json.startsWith("[") && json.endsWith("]")) {
                json = json.substring(1, json.length() - 1);
            }

            // Return the JSON string
            json = removeFieldsFromJson(json);
            json = removeNullFieldsFromJson(json);
            logger.log("Returned JSON " + json);
            return json;

        } catch (IOException e) {
            // Return an error message for any unexpected exceptions
            return e.getMessage();
        }
    }

    public String removeFieldsFromJson(String jsonString) {
        try {
            // Create ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();

            // Deserialize JSON to Map
            Map<String, Object> jsonMap = objectMapper.readValue(jsonString, HashMap.class);

            // Remove "sos" and "status" fields
            jsonMap.remove("sos");

            // Serialize modified Map back to JSON
            return objectMapper.writeValueAsString(jsonMap);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String removeNullFieldsFromJson(String json) throws IOException {
        // Create ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();

        // Convert JSON string to Map
        Map<String, Object> map = objectMapper.readValue(json, HashMap.class);

        // Iterate over map entries and remove entries with null values
        Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            if (entry.getValue() == null) {
                iterator.remove();
            }
        }

        // Convert Map back to JSON
        return objectMapper.writeValueAsString(map);
    }

}
