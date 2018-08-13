package com.evrikaklin.dto.site;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by berz on 15.06.2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Lead {

    public Lead() {
    }

    /*
    * Поля заказа
    */
    private String name;
    private String email;
    private String phone;
    private String comment;
    private String area;
    private String build_type;
    private String clean_type;
    private String referer;
    private String origin;
    private String roistat_visit;
    /*
    * Utm
    */
    private String utm_source;
    private String utm_medium;
    private String utm_content;
    private String utm_campaign;
    private String utm_term;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUtm_source() {
        return utm_source;
    }

    public void setUtm_source(String utm_source) {
        this.utm_source = utm_source;
    }

    public String getUtm_medium() {
        return utm_medium;
    }

    public void setUtm_medium(String utm_medium) {
        this.utm_medium = utm_medium;
    }

    public String getUtm_content() {
        return utm_content;
    }

    public void setUtm_content(String utm_content) {
        this.utm_content = utm_content;
    }

    public String getUtm_campaign() {
        return utm_campaign;
    }

    public void setUtm_campaign(String utm_campaign) {
        this.utm_campaign = utm_campaign;
    }

    public String getUtm_term() {
        return utm_term;
    }

    public void setUtm_term(String utm_term) {
        this.utm_term = utm_term;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getBuild_type() {
        return build_type;
    }

    public void setBuild_type(String build_type) {
        this.build_type = build_type;
    }

    public String getClean_type() {
        return clean_type;
    }

    public void setClean_type(String clean_type) {
        this.clean_type = clean_type;
    }

    public String getRoistat_visit() {
        return roistat_visit;
    }

    public void setRoistat_visit(String roistat_visit) {
        this.roistat_visit = roistat_visit;
    }
}
