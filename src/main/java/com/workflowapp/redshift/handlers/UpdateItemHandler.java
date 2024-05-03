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
import java.util.List;
import java.util.Map;

public class UpdateItemHandler implements RequestHandler<Map<String, Object>, String> {

    @Override
    public String handleRequest(Map<String, Object> event, Context context) {
        String id = "";
        List<Details> result;
        LambdaLogger logger = context.getLogger();
        try {
            Map<String, Object> content = (Map<String, Object>) event;

            // Initialize ObjectMapper for JSON deserialization.
            ObjectMapper objectMapper = new ObjectMapper();

            // Convert content map to JSON string.
            String requestBody = objectMapper.writeValueAsString(content);

            // Deserialize JSON payload into Details object.
            Details details = objectMapper.readValue(requestBody, Details.class);

            // Update the Item in the Redshift table.
            WorkflowService service = new WorkflowService();
            boolean ans = service.lookupItem(details.getId());
            logger.log("Is ITEM "+details.getId() +" in DRAFT "+ans);
            if (ans) {
                logger.log("Item "+details.getId() +" is in Draft state. You can update it");
                return service.updateItem(details);
            } else{
                return "Item "+details.getId() +" is not in Draft state. You cannot update it";
            }

        } catch (IOException e) {
            return e.getMessage();
        }
    }
}
