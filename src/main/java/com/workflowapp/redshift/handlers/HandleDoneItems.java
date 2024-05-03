// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.workflowapp.redshift.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.workflowapp.redshift.CompleteDetails;
import com.workflowapp.redshift.WorkflowService;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// This gets done items for the PageCards in client.
public class HandleDoneItems implements RequestHandler<Map<String, Object>, String> {
    @Override
    public String handleRequest(Map<String, Object> event, Context context) {
        WorkflowService wf = new WorkflowService();
        List<CompleteDetails> myList = wf.getCompleteDetails();
        Map<String, Map<String, Integer>> engTypeCounts = new HashMap<>();
        java.util.Set<String> processedEng = new java.util.HashSet<>();

        // Iterate over the list to count Scout and Follow for each Eng
        for (CompleteDetails details : myList) {
            String engName = details.getEngName();
            String typeValue = details.getType();

            // If Eng entry is already processed, skip
            if (processedEng.contains(engName)) {
                continue;
            }

            // Get or create the inner map for the current Eng
            Map<String, Integer> typeCounts = engTypeCounts.computeIfAbsent(engName, k -> new HashMap<>());

            // Initialize counts to 0
            typeCounts.put("Scout", 0);
            typeCounts.put("Follow", 0);

            // Iterate over the list again to count Scout and Follow for the current Eng
            for (CompleteDetails innerDetails : myList) {
                if (innerDetails.getEngName().equals(engName)) {
                    String innerTypeValue = innerDetails.getType();
                    if (innerTypeValue.equals("Scout")) {
                        typeCounts.put("Scout", typeCounts.get("Scout") + 1);
                    } else if (innerTypeValue.equals("Follow")) {
                        typeCounts.put("Follow", typeCounts.get("Follow") + 1);
                    }
                }
            }

            // Mark Eng entry as processed
            processedEng.add(engName);
        }

        // Print the counts for each Eng
        for (Map.Entry<String, Map<String, Integer>> entry : engTypeCounts.entrySet()) {
            String engName = entry.getKey();
            Map<String, Integer> typeCounts = entry.getValue();
            System.out.println("Eng: " + engName);
            System.out.println("Scout count: " + typeCounts.get("Scout"));
            System.out.println("Follow count: " + typeCounts.get("Follow"));
            System.out.println();
        }

        // Get today's date in the desired format
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String currentDate = dateFormat.format(new Date());
        // Constructing JSON string dynamically
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[");
        for (Map.Entry<String, Map<String, Integer>> entry : engTypeCounts.entrySet()) {
            String engName = entry.getKey();
            Map<String, Integer> typeCounts = entry.getValue();

            // Adding Scout count, and Follow count dynamically,
            jsonBuilder.append("{")
                .append("\"Description\": \"Done items as of ").append(currentDate).append("\",")
                .append("\"Eng\": \"").append(engName).append("\",")
                .append("\"Scout\": \"").append(typeCounts.getOrDefault("Scout", 0)).append("\",")
                .append("\"Follow\": \"").append(typeCounts.getOrDefault("Follow", 0)).append("\"")
                .append("},");
        }


        if (engTypeCounts.size() > 0) {
            jsonBuilder.deleteCharAt(jsonBuilder.length() - 1);
        }
        jsonBuilder.append("]");
        return jsonBuilder.toString();
    }
}
