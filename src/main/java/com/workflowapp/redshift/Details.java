// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.workflowapp.redshift;

import software.amazon.awssdk.services.redshiftdata.model.Field;
import java.util.List;

public class Details {

    private String id;
    private String title;
    private String engineer;
    private String summary;
    private String service;
    private String type; // Is this item a follow or scout
    private String sme;
    private String language;
    private String guide;
    private String url;
    private boolean sos;

    private String date;

    private String status;

    private String score;

    public String getScore(){
        return this.score;
    }

    public void setScore(String score){
        this.score = score;
    }

    public String getId() {
        return id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEngineer() {
        return engineer;
    }

    public void setEngineer(String engineer) {
        this.engineer = engineer;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getSme() {
        return sme;
    }

    public void setSme(String sme) {
        this.sme = sme;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getGuide() {
        return guide;
    }

    public void setGuide(String guide) {
        this.guide = guide;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setSos(boolean sos) {
        this.sos = sos;
    }

    public boolean getSos() {
        return this.sos;
    }

    public static Details from(List<Field> fields) {
        var item = new Details();
        for (int i = 0; i < fields.size(); i++) {
            String value;
            boolean val = false;
            if (i == 0) {
                value = String.valueOf(fields.get(i).longValue());
            } else {
                value = fields.get(i).stringValue();
            }
            switch (i) {
                case 0:
                    item.setId(value);
                    break;
                case 1:
                    item.setTitle(value);
                    break;
                case 2:
                    item.setEngineer(value);
                    break;
                case 3:
                    item.setSummary(value);
                    break;
                case 4:
                    item.setService(value);
                    break;
                case 5:
                    item.setSme(value);
                    break;
                case 6:
                    item.setLanguage(value);
                    break;
                case 7:
                    item.setGuide(value);
                    break;
                case 8:
                    item.setUrl(value);
                    break;
                default:
                    // Handle additional fields if needed
                    break;
            }
        }
        return item;
    }

    public static Details fromWithScore(List<Field> fields) {
        var item = new Details();
        for (int i = 0; i < fields.size(); i++) {
            String value = "";
            boolean val = false;
            if (i == 0) {
                value = String.valueOf(fields.get(i).longValue());
            } else {
                value = fields.get(i).stringValue();
            }
            switch (i) {
                case 0:
                    item.setId(value);
                    break;
                case 1:
                    item.setUrl(value);
                    break;
                case 2:
                    item.setEngineer(value);
                    break;
                case 3:
                    item.setSummary(value);
                    break;
                case 4:
                    item.setService(value);
                    break;
                case 5:
                    item.setSme(value);
                    break;
                case 6:
                    item.setLanguage(value);
                    break;
                case 7:
                    item.setGuide(value);
                    break;
                case 8:
                    item.setScore(value);
                    break;
                default:
                    // Handle additional fields if needed
                    break;
            }
        }
        return item;
    }
    public static Details fromWithStatus(List<Field> fields) {
        var item = new Details();
        for (int i = 0; i < fields.size(); i++) {
            String value = "";
            boolean val = false;
            if (i == 0) {
                value = String.valueOf(fields.get(i).longValue());
            } else {
                value = fields.get(i).stringValue();
            }
            switch (i) {
                case 0:
                    item.setId(value);
                    break;
                case 1:
                    item.setTitle(value);
                    break;
                case 2:
                    item.setEngineer(value);
                    break;
                case 3:
                    item.setType(value);
                    break;
                case 4:
                    item.setService(value);
                    break;
                case 5:
                    item.setSme(value);
                    break;
                case 6:
                    item.setLanguage(value);
                    break;
                case 7:
                    item.setStatus(value);
                    break;
                default:
                    // Handle additional fields if needed
                    break;
            }
        }
        return item;
    }

    public static Details fromWithType(List<Field> fields) {
        var item = new Details();
        for (int i = 0; i < fields.size(); i++) {
            String value = "";
            boolean val = false;
            if (i == 0) {
                value = String.valueOf(fields.get(i).longValue());
            } else {
                value = fields.get(i).stringValue();
            }
            switch (i) {
                case 0:
                    item.setId(value);
                    break;
                case 1:
                    item.setTitle(value);
                    break;
                case 2:
                    item.setEngineer(value);
                    break;
                case 3:
                    item.setSummary(value);
                    break;
                case 4:
                    item.setService(value);
                    break;
                case 5:
                    item.setSme(value);
                    break;
                case 6:
                    item.setLanguage(value);
                    break;
                case 7:
                    item.setStatus(value);
                    break;
                case 8:
                    item.setType(value);
                    break;
                default:
                    // Handle additional fields if needed
                    break;
            }
        }
        return item;
    }

    // Item Id	Status	Title	Engineer	Type	Service
    public static Details fromWithDone(List<Field> fields) {
        var item = new Details();
        for (int i = 0; i < fields.size(); i++) {
            String value = "";
            boolean val = false;
            if (i == 0) {
                value = String.valueOf(fields.get(i).longValue());
            } else {
                value = fields.get(i).stringValue();
            }
            switch (i) {
                case 0:
                    item.setId(value);
                    break;
                case 1:
                    item.setStatus(value);
                    break;
                case 2:
                    item.setTitle(value);
                    break;
                case 3:
                    item.setEngineer(value);
                    break;
                case 4:
                    item.setType(value);
                    break;
                case 5:
                    item.setService(value);
                    break;

                default:
                    // Handle additional fields if needed
                    break;
            }
        }
        return item;
    }
}
