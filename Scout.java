package aws.workflowapp.redshift;

import software.amazon.awssdk.services.redshiftdata.model.Field;

import java.util.List;

public class Scout {

    private String id;
    private String title;
    private String engineer;
    private String summary;
    private String service;
    private String service2; // Assuming service2 is a separate field in the JSON
    private String sme;
    private String language;
    private String guide;
    private String url;
    private boolean isIncludedInSOS;

    private String status;

    // Getter and setter methods for each field

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getService2() {
        return service2;
    }

    public void setService2(String service2) {
        this.service2 = service2;
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

    public boolean isIncludedInSOS() {
        return isIncludedInSOS;
    }

    public void setIncludedInSOS(boolean includedInSOS) {
        isIncludedInSOS = includedInSOS;
    }

    public static Scout from(List<Field> fields) {

        // id, title, engineer, summary,service, sme, language, guide, url, status
        var item = new Scout();
        for (int i = 0; i <= 9; i++) {
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
                    item.setGuide(value);
                    break;
                case 8:
                    item.setUrl(value);
                    break;
                case 9:
                    item.setStatus(value);
                    break;
            }
        }
        return item;
    }
}
