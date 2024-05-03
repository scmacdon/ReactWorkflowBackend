// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.workflowapp.redshift.handlers;

import com.workflowapp.redshift.Details;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;

public class HandleJSON {

    static String convertToJsonString(JSONObject jsonObject) {
        StringBuilder stringBuilder = new StringBuilder();
        buildJsonObject(jsonObject, stringBuilder);
        String jsonString = stringBuilder.toString();
        return StringEscapeUtils.unescapeJava(jsonString);
    }

    private static void buildJsonObject(JSONObject jsonObject, StringBuilder stringBuilder) {
        stringBuilder.append("{");
        boolean first = true;
        for (String key : jsonObject.keySet()) {
            if (!first) {
                stringBuilder.append(",");
            } else {
                first = false;
            }
            Object value = jsonObject.get(key);
            stringBuilder.append("\"").append(key).append("\":");
            if (value instanceof JSONObject) {
                buildJsonObject((JSONObject) value, stringBuilder);
            } else if (value instanceof JSONArray) {
                buildJsonArray((JSONArray) value, stringBuilder);
            } else {
                stringBuilder.append("\"").append(value.toString()).append("\"");
            }
        }
        stringBuilder.append("}");
    }

    private static void buildJsonArray(JSONArray jsonArray, StringBuilder stringBuilder) {
        stringBuilder.append("[");
        boolean first = true;
        for (Object obj : jsonArray) {
            if (!first) {
                stringBuilder.append(",");
            } else {
                first = false;
            }
            if (obj instanceof JSONObject) {
                buildJsonObject((JSONObject) obj, stringBuilder);
            } else if (obj instanceof JSONArray) {
                buildJsonArray((JSONArray) obj, stringBuilder);
            } else {
                stringBuilder.append("\"").append(obj.toString()).append("\"");
            }
        }
        stringBuilder.append("]");
    }

    JSONObject convertToJsonObject(List<Details> result) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        for (Details detail : result) {
            JSONObject item = new JSONObject();
            item.put("id", detail.getId());
            item.put("title", detail.getTitle());
            item.put("engineer", detail.getEngineer());
            item.put("summary", detail.getSummary());
            item.put("service", detail.getService());
            item.put("sme", detail.getSme());
            item.put("language", detail.getLanguage());
            item.put("guide", detail.getGuide());
            item.put("url", detail.getUrl());
            item.put("sos", detail.getSos());
            item.put("status", detail.getStatus());
            jsonArray.put(item);
        }

        jsonObject.put("results", jsonArray);
        return jsonObject;
    }
}
