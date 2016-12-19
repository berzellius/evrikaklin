package com.evrikaklin.dto.site;

import java.util.List;

/**
 * Created by berz on 17.12.2016.
 */
public class DealsStatusesChangedRequest {
    public DealsStatusesChangedRequest(){}

    private List<DealStatusChangedDTO> statusChanges;

    private String password;

    public List<DealStatusChangedDTO> getStatusChanges() {
        return statusChanges;
    }

    public void setStatusChanges(List<DealStatusChangedDTO> statusChanges) {
        this.statusChanges = statusChanges;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
