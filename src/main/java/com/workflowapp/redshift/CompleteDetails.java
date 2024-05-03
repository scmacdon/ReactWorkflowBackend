package com.workflowapp.redshift;

import software.amazon.awssdk.services.redshiftdata.model.Field;

import java.util.List;

public class CompleteDetails {

    private String engName;
    private String type;

    public String getEngName() {
        return engName;
    }

    public void setEngName(String engName) {
        this.engName = engName;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static CompleteDetails from(List<Field> fields) {
        CompleteDetails item = new CompleteDetails();
        for (int i = 0; i < fields.size(); i++) {
            String value = fields.get(i).stringValue();
            switch (i) {
                case 0:
                    item.setEngName(value);
                    break;
                case 1:
                    item.setType(value);
                    break;
                default:
                    // Handle additional fields if needed
                    break;
            }
        }
        return item;
    }
}