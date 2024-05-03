// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.workflowapp.redshift.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.workflowapp.redshift.WorkflowService;
import java.util.Map;

// This Lambda function changes the status of an item.
public class HandleStatusChange implements RequestHandler<Map<String, Object>, String> {
    @Override
    public String handleRequest(Map<String, Object> event, Context context) {
        LambdaLogger logger = context.getLogger();
        String itemId = (String) event.get("id");
        String status = (String) event.get("status");
        logger.log("Item id is " + itemId);
        logger.log("Status is " + status);

        WorkflowService wf = new WorkflowService();
        wf.modItem(itemId, status);
        return "Item "+itemId +" been put into status " + status ;
    }
}
