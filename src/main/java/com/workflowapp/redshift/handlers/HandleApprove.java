// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.workflowapp.redshift.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.workflowapp.redshift.WorkflowService;
import java.util.Map;

// This Lambda function considers the score and puts the Item into Approve state if 50% or over.
public class HandleApprove implements RequestHandler<Map<String, Object>, String> {
    @Override
    public String handleRequest(Map<String, Object> event, Context context) {
        LambdaLogger logger = context.getLogger();
        try {
            String itemId = (String) event.get("id");
            String score = (String) event.get("score");
            logger.log("Item id is " + itemId);
            logger.log("Score is " + score);
            int intScore = Integer.parseInt(score);
            if (intScore < 49) {
                return ("Your Score is "+ score +" and does not meet minimum score. This item cannot be approved.");
            } else {
                WorkflowService wf = new WorkflowService();
                wf.modItem(itemId, "Approved");
                return "The score is " + score +" the item has been approved.";
            }

        } catch (NumberFormatException e) {
            return e.getMessage();
        }
    }
}
