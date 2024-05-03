// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.workflowapp.redshift.handlers;

import com.workflowapp.redshift.Details;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workflowapp.redshift.WorkflowService;

import java.io.IOException;
import java.util.Map;

// This Lambda function adds a new item to the Redshift table.
public class AddItemHandler implements RequestHandler<Map<String, Object>, String> {
    @Override
    public String handleRequest(Map<String, Object> event, Context context) {
        LambdaLogger logger = context.getLogger();
        try {
            // Retrieve JSON data from the event
            Map<String, Object> content = (Map<String, Object>) event;

            // Initialize ObjectMapper for JSON deserialization
            ObjectMapper objectMapper = new ObjectMapper();

            // Convert content map to JSON string
            String requestBody = objectMapper.writeValueAsString(content);
            logger.log("**** JSON");
            logger.log(requestBody);

            // Deserialize JSON payload into Details object
            Details details = objectMapper.readValue(requestBody, Details.class);

            // Add the data to the Redshift table.
            WorkflowService service = new WorkflowService();
            String id = service.popTable(details, true); // pass true for a Scout
            return "The id of the new item is " + id;

        } catch (IOException e) {
            return e.getMessage();
        }
    }
}
