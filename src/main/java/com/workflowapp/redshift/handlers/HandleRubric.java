// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.workflowapp.redshift.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.workflowapp.redshift.RubricScore;
import com.workflowapp.redshift.WorkflowService;
import java.io.IOException;
import java.util.Map;

// This Lambda function handles Rubric requests.
public class HandleRubric implements RequestHandler<Map<String, Object>, String> {
    @Override
    public String handleRequest(Map<String, Object> event, Context context) {
        LambdaLogger logger = context.getLogger();
        try {
            Map<String, Object> content = (Map<String, Object>) event;

            // Initialize ObjectMapper for JSON deserialization.
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(content);
            JsonNode rootNode = objectMapper.readTree(json);
            String itemId = rootNode.get("itemId").asText();
            ((ObjectNode) rootNode).remove("itemId");

            // Convert modified JSON node back to JSON string.
            String modifiedJson = objectMapper.writeValueAsString(rootNode);

            // Deserialize modified JSON string into RubricScore object.
            RubricScore rubricScore = objectMapper.readValue(modifiedJson, RubricScore.class);
            WorkflowService wfService = new WorkflowService();

            logger.log("**** JSON");
            logger.log(json);
            WorkflowService service = new WorkflowService();
            boolean ans = service.lookupItem(itemId);
            if (ans) {
                int rubScore = rubricScore.calculateTotalScore();
                int rubPer = rubScore * 10;
                wfService.updateScore(rubPer, itemId);
                String message;
                if (rubPer < 50) {
                    message = "Your rubric score is " + rubPer + "% which is below the threshold value. You cannot proceed with this getting started item";
                } else {
                    message = "Your rubric score is " + rubPer + "% which means that you can proceed with this getting started item";
                }
                return message;
            } else{
                return "Item "+itemId +" is not in Draft state. You cannot run a rubric on it";
            }

        } catch (IOException e) {
            return e.getMessage();
        }
    }
}
