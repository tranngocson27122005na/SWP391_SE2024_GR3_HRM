package com.hrm.dto.response;

/**
 * One tile on the /home hub grid.
 */
public class HomeTile {
    private String code;
    private String label;
    private String url;
    private String iconKey;

    public HomeTile() {
    }

    public HomeTile(String code, String label, String url, String iconKey) {
        this.code = code;
        this.label = label;
        this.url = url;
        this.iconKey = iconKey;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIconKey() {
        return iconKey;
    }

    public void setIconKey(String iconKey) {
        this.iconKey = iconKey;
    }
}
