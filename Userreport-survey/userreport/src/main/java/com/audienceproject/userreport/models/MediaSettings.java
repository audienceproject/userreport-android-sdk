package com.audienceproject.userreport.models;

import java.util.Map;

/**
 * POJO model describing customer of sdk
 */

public class MediaSettings extends Settings {

    private String companyId;
    private String kitTcode;
    private int toolBarColor;
    private Map<String, String> sections;

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getKitTcode() {
        return kitTcode;
    }

    public void setKitTcode(String kitTcode) {
        this.kitTcode = kitTcode;
    }

    public Integer getToolBarColor() {
        return toolBarColor;
    }

    public void setToolBarColor(int toolBarColor) {
        this.toolBarColor = toolBarColor;
    }

    public Map<String, String> getSections() {
        return sections;
    }

    public void setSections(Map<String, String> sections) {
        this.sections = sections;
    }
}
