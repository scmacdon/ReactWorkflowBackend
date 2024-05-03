// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.workflowapp.redshift.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workflowapp.redshift.Details;
import com.workflowapp.redshift.WorkflowService;
import java.io.IOException;
import java.util.Map;

// THis Lamdba hanlder creates a follow for the Getting Started Item tracker.
public class HandleFollow implements RequestHandler<Map<String, Object>, String> {

    public String handleRequest(Map<String, Object> event, Context context) {
        LambdaLogger logger = context.getLogger();
        try {
            // Retrieve JSON data from the event
            Map<String, Object> content = (Map<String, Object>) event;

            // Initialize ObjectMapper for JSON deserialization.
            ObjectMapper objectMapper = new ObjectMapper();

            // Convert content map to JSON string.
            String requestBody = objectMapper.writeValueAsString(content);
            logger.log("**** JSON");
            logger.log(requestBody);

            // Deserialize JSON payload into Details object.
            Details details = objectMapper.readValue(requestBody, Details.class);

            // Add the data to the Redshift table.
            WorkflowService service = new WorkflowService();
            boolean ans = service.lookupItem(details.getId());
            logger.log("Is ITEM "+details.getId() +" in DRAFT "+ans);
            if (ans) {
                String msg = "Item "+details.getId() +" is in Draft state. You cannot create a follow based on a Draft item" ;
                logger.log(msg);
                return msg;
            } else{
                String id = service.popTable(details, false); // pass false for a follow
                return "The id of the new follow is "+id;
            }

        } catch (IOException e) {
            return e.getMessage();
        }
    }
}
