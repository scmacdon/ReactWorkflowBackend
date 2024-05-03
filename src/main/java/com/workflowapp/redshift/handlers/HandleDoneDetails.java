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

public class HandleDoneDetails implements RequestHandler<Map<String, Object>, String> {
    @Override
    public String handleRequest(Map<String, Object> event, Context context) {
        LambdaLogger logger = context.getLogger();
        String eng = (String) event.get("eng");
        logger.log("Item id is " + eng);
        WorkflowService service = new WorkflowService();
        List<Details> result = service.getDoneItemsByEng(eng);
        try {
            // Create an ObjectMapper.
            ObjectMapper objectMapper = new ObjectMapper();

            // Convert the List of Details objects to JSON.
            String json = objectMapper.writeValueAsString(result);
            return json;

        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
