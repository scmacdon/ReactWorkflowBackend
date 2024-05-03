// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.workflowapp.redshift.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.workflowapp.redshift.WorkflowService;

import java.util.Map;

// This Lambda function simply a count of Closed items.
public class HandleCount implements RequestHandler<Map<String, Object>, String> {
    @Override
    public String handleRequest(Map<String, Object> event, Context context) {
        WorkflowService wf = new WorkflowService();
        return wf.countItems();
    }
}
