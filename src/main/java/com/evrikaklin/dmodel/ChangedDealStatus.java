package com.evrikaklin.dmodel;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created by berz on 17.12.2016.
 */
@Entity(name="ChangedDealStatus")
@DiscriminatorValue("deal")
public class ChangedDealStatus extends ChangedEntityStatus {

    public ChangedDealStatus(){}

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ChangedDealStatus && this.getId().equals(((ChangedDealStatus) obj).getId());
    }
}
