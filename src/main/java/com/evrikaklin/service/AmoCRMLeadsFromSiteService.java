package com.evrikaklin.service;

import com.evrikaklin.dmodel.ChangedDealStatus;
import com.evrikaklin.dmodel.LeadFromSite;
import com.evrikaklin.dto.api.amocrm.AmoCRMLead;
import com.evrikaklin.exception.APIAuthException;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * Created by berz on 06.03.2016.
 */
@Service
public interface AmoCRMLeadsFromSiteService {
    public void processLead(AmoCRMLead amoCRMLead);

    void setUtmSourceCustomFieldId(Long utmSourceCustomFieldId);

    void setUtmMediumCustomFieldId(Long utmMediumCustomFieldId);

    void setUtmCampaignCustomFieldId(Long utmCampaignCustomFieldId);

    void setMarketingChannelCustomFieldId(Long marketingChannelCustomFieldId);

    void setProjectIdToLeadsSource(HashMap<Integer, Long> projectIdToLeadsSource);

    void setSourceLeadsCustomField(Long sourceLeadsCustomField);

    void setNewLeadFromSiteStatusCustomFieldId(Long newLeadFromSiteStatusCustomFieldId);

    void setNewLeadFromSiteStatusCustomFieldEnumNotProcessed(Long newLeadFromSiteStatusCustomFieldEnumNotProcessed);

    LeadFromSite processLeadFromSite(LeadFromSite leadFromSite) throws APIAuthException;

    void setPhoneNumberCustomFieldLeads(Long phoneNumberCustomFieldLeads);

    void setPhoneNumberCustomField(Long phoneNumberCustomField);

    void setDefaultUserID(Long defaultUserID);

    void setMarketingChannelContactsCustomField(Long marketingChannelContactsCustomField);

    void setEmailContactCustomField(Long emailContactCustomField);

    void setRoistatVisitField(Long roistatVisitField);

    void setEmailContactEnum(String emailContactEnum);

    void setSourceContactsCustomField(Long sourceContactsCustomField);

    void setMarketingChannelLeadsCustomField(Long marketingChannelLeadsCustomField);

    void setPhoneNumberContactStockField(Long amoCRMPhoneNumberStockFieldContact);

    void setPhoneNumberStockFieldContactEnumWork(String phoneNumberStockFieldContactEnumWork);

    void setCommentCustomField(Long commentCustomField);

    void setLeadFromSiteTagId(Long leadFromSiteTagId);

    void setAreaLeadField(Long areaLeadField);

    void setBuildTypeLeadField(Long buildTypeLeadField);

    void setCleanTypeLeadField(Long cleanTypeLeadField);

    ChangedDealStatus processSuccessfullyClosedDeal(ChangedDealStatus changedDealStatus) throws APIAuthException;

    void setClosedStatusesIds(Long[] closedStatusesIds);

    void setSuccessfullyClosedStatusesIds(Long[] successfullyClosedStatusesIds);
}
