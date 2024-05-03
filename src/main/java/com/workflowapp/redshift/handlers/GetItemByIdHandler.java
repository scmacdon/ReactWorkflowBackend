// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.workflowapp.redshift.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.workflowapp.redshift.Details;
import com.workflowapp.redshift.WorkflowService;
import org.json.JSONObject;
import java.util.List;
import java.util.Map;


// This Lambda function returns an Item that corresponds to the Id value.
public class GetItemByIdHandler implements RequestHandler<Map<String,String>, String> {

    @Override
    public String handleRequest(Map<String, String> event, Context context) {
        LambdaLogger logger = context.getLogger();
        String itemId = event.get("id");
        logger.log("Item id is " + itemId);
        WorkflowService service = new WorkflowService();
        List<Details> result = service.getDataById(Long.valueOf(itemId));

        HandleJSON handleJSON = new HandleJSON();
        JSONObject jOb = handleJSON.convertToJsonObject(result);
        String json1 = HandleJSON.convertToJsonString(jOb);
        String jsonStr = removeSos(json1);
        logger.log("JSON:");
        logger.log(jsonStr);
        return jsonStr;
    }

    // Assuming Details class has a method to get the JSONObject representation
    public String removeSos(String originalJson) {
        // Remove "sos": "false" from the JSON string
        String modifiedJson = originalJson.replace(",\"sos\":\"false\"", "");
        return modifiedJson;
    }
}