package com.evrikaklin.dto.site;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by berz on 17.12.2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DealStatusChangedDTO {
    public DealStatusChangedDTO(){}

    private Long dealId;

    private Long statusId;

    private Long timestamp;

    public Long getDealId() {
        return dealId;
    }

    public void setDealId(Long dealId) {
        this.dealId = dealId;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString(){
        return
                "dealId: ".concat(this.getDealId().toString())
                .concat(", statusId: ".concat(this.getStatusId().toString()))
                .concat(", timestamp: ".concat(this.getTimestamp().toString()));
    }
}
