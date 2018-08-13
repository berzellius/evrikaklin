package com.evrikaklin.service;

import com.evrikaklin.dmodel.*;
import com.evrikaklin.dto.api.amocrm.*;
import com.evrikaklin.dto.api.amocrm.response.AmoCRMCreatedEntityResponse;
import com.evrikaklin.dto.site.Lead;
import com.evrikaklin.exception.APIAuthException;
import com.evrikaklin.exception.LogicException;
import com.evrikaklin.exception.LogicNotSuccessfullException;
import com.evrikaklin.exception.LogicSuccessfullException;
import com.evrikaklin.repository.ChangedEntityStatusRepository;
import com.evrikaklin.repository.LeadFromSiteRepository;
import com.evrikaklin.repository.SiteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by berz on 06.03.2016.
 */
@Service
@Transactional
public class AmoCRMLeadsFromSiteServiceImpl implements AmoCRMLeadsFromSiteService {

    private Long utmSourceCustomFieldId;
    private Long utmMediumCustomFieldId;
    private Long utmCampaignCustomFieldId;
    private Long marketingChannelCustomFieldId;
    private Long newLeadFromSiteStatusCustomFieldId;
    private Long newLeadFromSiteStatusCustomFieldEnumNotProcessed;

    private Long phoneNumberCustomFieldLeads;
    private Long phoneNumberCustomField;
    private Long commentCustomField;
    private Long defaultUserID;
    private Long marketingChannelContactsCustomField;
    private Long marketingChannelLeadsCustomField;
    private Long emailContactCustomField;
    private String emailContactEnum;
    private Long phoneNumberContactStockField;
    private String phoneNumberStockFieldContactEnumWork;
    private Long areaLeadField;
    private Long buildTypeLeadField;
    private Long cleanTypeLeadField;

    private Long leadFromSiteTagId;


    private Long roistatVisitField;

    private Long[] closedStatusesIds;
    private Long[] successfullyClosedStatusesIds;


    private Long sourceLeadsCustomField;
    private Long sourceContactsCustomField;

    private HashMap<Integer, Long> projectIdToLeadsSource;

    private static final Logger log = LoggerFactory.getLogger(AmoCRMLeadsFromSiteServiceImpl.class);

    @Autowired
    CallTrackingSourceConditionService callTrackingSourceConditionService;

    @Autowired
    IncomingCallBusinessProcess incomingCallBusinessProcess;

    @Autowired
    AmoCRMService amoCRMService;

    @Autowired
    LeadFromSiteRepository leadFromSiteRepository;

    @Autowired
    SiteRepository siteRepository;

    @Autowired
    ChangedEntityStatusRepository changedEntityStatusRepository;


    @Override
    public void processLead(AmoCRMLead amoCRMLead) {
        System.out.println("lead #" + amoCRMLead.getId());

        String utmSource = "";
        String utmMedium = "";
        String utmCampaign = "";

        Integer projectId = null;

        for(AmoCRMCustomField amoCRMCustomField : amoCRMLead.getCustom_fields()){
            if(amoCRMCustomField.getId().equals(this.getUtmSourceCustomFieldId()) && amoCRMCustomField.getValues().size() > 0){
                utmSource = amoCRMCustomField.getValues().get(0).getValue();
            }

            if(amoCRMCustomField.getId().equals(this.getUtmMediumCustomFieldId()) && amoCRMCustomField.getValues().size() > 0){
                utmMedium = amoCRMCustomField.getValues().get(0).getValue();
            }

            if(amoCRMCustomField.getId().equals(this.getUtmCampaignCustomFieldId()) && amoCRMCustomField.getValues().size() > 0){
                utmCampaign = amoCRMCustomField.getValues().get(0).getValue();
            }

            if(amoCRMCustomField.getId().equals(this.getSourceLeadsCustomField()) && amoCRMCustomField.getValues().size() > 0){
                Long projectIdFromLead = Long.decode(amoCRMCustomField.getValues().get(0).getEnumerated());

                for(Integer project : this.getProjectIdToLeadsSource().keySet()){
                    if(this.getProjectIdToLeadsSource().get(project).equals(projectIdFromLead)){
                        projectId = project;
                    }
                }
            }
        }

        if(projectId == null){
            System.out.println("Project custom field is empty or has wrong value!");
            return;
        }

        if(!amoCRMLead.checkCustomFieldEnumValue(this.getNewLeadFromSiteStatusCustomFieldId(), this.getNewLeadFromSiteStatusCustomFieldEnumNotProcessed().toString())){
            System.out.println("'new lead from site status' field has wrong value");
            return;
        }


        System.out.println("utm = {" + utmSource + ", " + utmMedium + ", " + utmCampaign + "}, project = " + projectId);

        CallTrackingSourceCondition callTrackingSourceCondition = callTrackingSourceConditionService.getCallTrackingSourceConditionByUtmAndProjectId(utmSource, utmMedium, utmCampaign, projectId);

        if(callTrackingSourceCondition != null){
            String[] values = {callTrackingSourceCondition.getSourceName()};
            amoCRMLead.addStringValuesToCustomField(this.getMarketingChannelCustomFieldId(), values);

            amoCRMLead.setEmptyValueToField(this.getNewLeadFromSiteStatusCustomFieldId());
        }
    }

    @Override
    public LeadFromSite processLeadFromSite(LeadFromSite leadFromSite) throws APIAuthException {
        if(leadFromSite.getSite() != null && leadFromSite.getLead() != null) {
            log.info("Started processing lead from site " + leadFromSite.getSite().getUrl() + "; contacts: " + leadFromSite.getLead().getPhone() + " / " + leadFromSite.getLead().getEmail());

            // Приводим номер к общему формату
            if(leadFromSite.getLead().getPhone() != null){
                leadFromSite.getLead().setPhone("7" + this.phoneExec(leadFromSite.getLead().getPhone()));
            }

            String utmSource = (leadFromSite.getLead().getUtm_source() != null)? leadFromSite.getLead().getUtm_source() : "";
            String utmMedium = (leadFromSite.getLead().getUtm_medium() != null)? leadFromSite.getLead().getUtm_medium() : "";
            String utmCampaign = (leadFromSite.getLead().getUtm_campaign() != null)? leadFromSite.getLead().getUtm_campaign() : "";

            log.info("utm = {" + utmSource + ", " + utmMedium + ", " + utmCampaign + "}, project = " + leadFromSite.getSite().getCallTrackingProjectId());
            CallTrackingSourceCondition callTrackingSourceCondition = callTrackingSourceConditionService.getCallTrackingSourceConditionByUtmAndProjectId(utmSource, utmMedium, utmCampaign, leadFromSite.getSite().getCallTrackingProjectId());

            if(callTrackingSourceCondition != null){
                if(leadFromSite.getLead().getEmail() != null || leadFromSite.getLead().getPhone() != null) {
                    AmoCRMContact contact = contactForLeadFromSite(leadFromSite, callTrackingSourceCondition.getSourceName());

                    if (contact == null) {
                        throw new RuntimeException("seems to be contact was not created and not exists for leadFromSite#" + leadFromSite.getId());
                    }

                    this.workWithContact(leadFromSite, contact, callTrackingSourceCondition.getSourceName());
                }
                else{
                    log.info("there is no phone number or email => pass");
                }

                leadFromSite.setState(LeadFromSite.State.DONE);
                leadFromSiteRepository.save(leadFromSite);
            }
            else{
                log.error("failed to find proper callTracking source condition by utm fields!");
            }
        }
        else{
            log.error("bad leadFromSite#" + leadFromSite.getId().toString());
        }

        return leadFromSite;
    }

    private void workWithContact(LeadFromSite leadFromSite, AmoCRMContact contact, String sourceName) throws APIAuthException {
        log.info("searching leads for contact#" + contact.getId().toString());

        ArrayList<Long> leadIds = contact.getLinked_leads_id();
        if (leadIds != null && leadIds.size() != 0) {
            log.info("Leads found. Checking statuses");

            for (Long leadId : leadIds){
                log.info("work with lead#" + leadId);
                AmoCRMLead lead = amoCRMService.getLeadById(leadId);

                if(lead != null){
                    if(amoCRMService.getLeadClosedStatusesIDs().contains(lead.getStatus_id())){
                        log.info("lead is closed. next..");
                    }
                    else{
                        log.info("lead is open. ok");
                        return;
                    }
                }
            }
        }

        // Если лид не найден, то попадаем сюда и создаем лид
        this.createLead(leadFromSite, contact, sourceName);

    }

    private void createLead(LeadFromSite leadFromSite, AmoCRMContact contact, String sourceName) throws APIAuthException {
        if(leadFromSite.getLead() == null)
            return;

        AmoCRMLead lead = new AmoCRMLead();

        lead.setName("Заявка с сайта -> " + this.contactStrByLead(leadFromSite.getLead()));
        lead.setResponsible_user_id(this.getDefaultUserID());

        if(leadFromSite.getLead().getPhone() != null){
            String[] numberField = {leadFromSite.getLead().getPhone()};
            lead.addStringValuesToCustomField(this.getPhoneNumberCustomFieldLeads(), numberField);
        }

        if(leadFromSite.getLead().getComment() != null){
            String[] commentField = {leadFromSite.getLead().getComment()};
            lead.addStringValuesToCustomField(this.getCommentCustomField(), commentField);
        }

        if(leadFromSite.getLead().getArea() != null){
            String[] areaField = {leadFromSite.getLead().getArea()};
            lead.addStringValuesToCustomField(this.getAreaLeadField(), areaField);
        }

        if(leadFromSite.getLead().getBuild_type() != null){
            String[] buildTypeField = {leadFromSite.getLead().getBuild_type()};
            lead.addStringValuesToCustomField(this.getBuildTypeLeadField(), buildTypeField);
        }

        if(leadFromSite.getLead().getClean_type() != null){
            String[] cleanTypeField = {leadFromSite.getLead().getClean_type()};
            lead.addStringValuesToCustomField(this.getCleanTypeLeadField(), cleanTypeField);
        }

        String[] fieldProject = {leadFromSite.getSite().getCrmLeadSourceId()};
        lead.addStringValuesToCustomField(this.getSourceLeadsCustomField(), fieldProject);

        String[] fieldSource = {sourceName};
        lead.addStringValuesToCustomField(this.getMarketingChannelLeadsCustomField(), fieldSource);


        lead.tag(this.getLeadFromSiteTagId(), "Заявка с сайта");

        // roistat visit
        log.info("setting roistat_visit field");
        String roistatVisit = leadFromSite.getLead().getRoistat_visit();

        if(roistatVisit != null){
            String[] roistatVisitData = {roistatVisit};
            lead.addStringValuesToCustomField(this.getRoistatVisitField(), roistatVisitData);
        }

        log.info("creating lead for leadFromSite..");

        AmoCRMCreatedEntityResponse amoCRMCreatedEntityResponse = amoCRMService.addLead(lead);
        if(amoCRMCreatedEntityResponse.getId() != null){
            log.info("created lead#" + amoCRMCreatedEntityResponse.getId().toString());

            AmoCRMLead lead1 = amoCRMService.getLeadById(amoCRMCreatedEntityResponse.getId());
            amoCRMService.addContactToLead(contact, lead1);
        }
        else{
            log.error("error creating lead!");
            throw new RuntimeException("Lead was not created with unknown reason!");
        }
    }

    private String contactStrByLead(Lead lead){
        if(lead == null)
            return "";

        String contact = "";
        if(lead.getPhone() != null){
            contact = contact.concat(lead.getPhone());
        }

        if(lead.getEmail() != null){
            contact = contact.concat((contact.equals(""))? lead.getEmail() : " / ".concat(lead.getEmail()));
        }

        return contact;
    }

    private AmoCRMContact contactForLeadFromSite(LeadFromSite leadFromSite, String sourceName) throws APIAuthException {
        Lead lead = leadFromSite.getLead();
        if(lead == null){
            return null;
        }

        String contacts = contactStrByLead(lead);

        if(lead.getPhone() != null) {
            List<AmoCRMContact> contactsByPhone = amoCRMService.getContactsByQuery(lead.getPhone());
            if(contactsByPhone.size() > 0){
                return contactsByPhone.get(0);
            }
        }

        if(lead.getEmail() != null){
            List<AmoCRMContact> contactsByEmail = amoCRMService.getContactsByQuery(lead.getEmail());
            if(contactsByEmail.size() > 0){
                return contactsByEmail.get(0);
            }
        }

        AmoCRMContact amoCRMContact = new AmoCRMContact();
        amoCRMContact.setName(lead.getName() + " (с сайта " + lead.getOrigin() + ") :[" + contacts + "]");
        amoCRMContact.setResponsible_user_id(this.getDefaultUserID());

        if(lead.getPhone() != null){
            String[] fieldNumber = {lead.getPhone()};
            amoCRMContact.addStringValuesToCustomField(this.getPhoneNumberCustomField(), fieldNumber);
            amoCRMContact.addStringValuesToCustomField(this.getPhoneNumberContactStockField(), fieldNumber, this.getPhoneNumberStockFieldContactEnumWork());
        }

        if(lead.getEmail() != null){
            String[] fieldEmail = {lead.getEmail()};
            amoCRMContact.addStringValuesToCustomField(this.getEmailContactCustomField(), fieldEmail, this.getEmailContactEnum());
        }

        String[] fieldProject = {leadFromSite.getSite().getCrmContactSourceId()};
        amoCRMContact.addStringValuesToCustomField(this.getSourceContactsCustomField(), fieldProject);

        String[] fieldSource = {sourceName};
        amoCRMContact.addStringValuesToCustomField(this.getMarketingChannelContactsCustomField(), fieldSource);


        AmoCRMCreatedEntityResponse response = amoCRMService.addContact(amoCRMContact);

        if(response.getId() != null){
            log.info("Contact was created: #" + response.getId());
            AmoCRMContact contact = amoCRMService.getContactById(response.getId());
            return contact;
        }

        return null;
    }

    private String phoneExec(String phone){
        log.info("parsing phone: ".concat(phone));

        if(phone.matches("^[\\d]+$")){
            if(phone.length() >= 11 &&
                    (
                            phone.charAt(0) == '7' ||
                                    phone.charAt(0) == '8'
                    )
                    ){
                return phone.substring(1);
            }
            return phone;
        }
        else{
            String parsed = "";
            for (Integer i = 0; i < phone.length(); i++){
                String ch = String.valueOf(phone.charAt(i));
                if(ch.matches("\\d")){
                    parsed = parsed.concat(ch);
                }
            }

            if(parsed.length() == 0){
                log.error("Seems to be that number contains no digits. Cant parse it");
                return "";
            }

            return phoneExec(parsed);
        }
    }

    public Long getUtmSourceCustomFieldId() {
        return utmSourceCustomFieldId;
    }

    @Override
    public void setUtmSourceCustomFieldId(Long utmSourceCustomFieldId) {
        this.utmSourceCustomFieldId = utmSourceCustomFieldId;
    }

    public Long getUtmMediumCustomFieldId() {
        return utmMediumCustomFieldId;
    }

    @Override
    public void setUtmMediumCustomFieldId(Long utmMediumCustomFieldId) {
        this.utmMediumCustomFieldId = utmMediumCustomFieldId;
    }

    public Long getUtmCampaignCustomFieldId() {
        return utmCampaignCustomFieldId;
    }

    @Override
    public void setUtmCampaignCustomFieldId(Long utmCampaignCustomFieldId) {
        this.utmCampaignCustomFieldId = utmCampaignCustomFieldId;
    }

    public Long getMarketingChannelCustomFieldId() {
        return marketingChannelCustomFieldId;
    }

    @Override
    public void setMarketingChannelCustomFieldId(Long marketingChannelCustomFieldId) {
        this.marketingChannelCustomFieldId = marketingChannelCustomFieldId;
    }

    public HashMap<Integer, Long> getProjectIdToLeadsSource() {
        if(projectIdToLeadsSource == null){
            this.setProjectIdToLeadsSource(new HashMap<>());
        }

        if(projectIdToLeadsSource.size() == 0){
            log.debug("updating projectIdToLeadsSource in AmoCRMLeadsFromSiteServiceImpl");
            List<Site> sites = (List<Site>) siteRepository.findAll();
            for(Site site : sites){
                projectIdToLeadsSource.put(site.getCallTrackingProjectId(), Long.decode(site.getCrmLeadSourceId()));
            }
        }
        return projectIdToLeadsSource;
    }

    @Override
    public void setProjectIdToLeadsSource(HashMap<Integer, Long> projectIdToLeadsSource) {
        this.projectIdToLeadsSource = projectIdToLeadsSource;
    }

    public Long getSourceLeadsCustomField() {
        return sourceLeadsCustomField;
    }

    @Override
    public void setSourceLeadsCustomField(Long sourceLeadsCustomField) {
        this.sourceLeadsCustomField = sourceLeadsCustomField;
    }

    public Long getNewLeadFromSiteStatusCustomFieldId() {
        return newLeadFromSiteStatusCustomFieldId;
    }

    @Override
    public void setNewLeadFromSiteStatusCustomFieldId(Long newLeadFromSiteStatusCustomFieldId) {
        this.newLeadFromSiteStatusCustomFieldId = newLeadFromSiteStatusCustomFieldId;
    }

    public Long getNewLeadFromSiteStatusCustomFieldEnumNotProcessed() {
        return newLeadFromSiteStatusCustomFieldEnumNotProcessed;
    }

    @Override
    public void setNewLeadFromSiteStatusCustomFieldEnumNotProcessed(Long newLeadFromSiteStatusCustomFieldEnumNotProcessed) {
        this.newLeadFromSiteStatusCustomFieldEnumNotProcessed = newLeadFromSiteStatusCustomFieldEnumNotProcessed;
    }

    public Long getPhoneNumberCustomFieldLeads() {
        return phoneNumberCustomFieldLeads;
    }

    @Override
    public void setPhoneNumberCustomFieldLeads(Long phoneNumberCustomFieldLeads) {
        this.phoneNumberCustomFieldLeads = phoneNumberCustomFieldLeads;
    }

    public Long getPhoneNumberCustomField() {
        return phoneNumberCustomField;
    }

    @Override
    public void setPhoneNumberCustomField(Long phoneNumberCustomField) {
        this.phoneNumberCustomField = phoneNumberCustomField;
    }

    public Long getDefaultUserID() {
        return defaultUserID;
    }

    @Override
    public void setDefaultUserID(Long defaultUserID) {
        this.defaultUserID = defaultUserID;
    }

    public Long getMarketingChannelContactsCustomField() {
        return marketingChannelContactsCustomField;
    }

    @Override
    public void setMarketingChannelContactsCustomField(Long marketingChannelContactsCustomField) {
        this.marketingChannelContactsCustomField = marketingChannelContactsCustomField;
    }

    public Long getEmailContactCustomField() {
        return emailContactCustomField;
    }

    @Override
    public void setEmailContactCustomField(Long emailContactCustomField) {
        this.emailContactCustomField = emailContactCustomField;
    }


    public Long getRoistatVisitField() {
        return roistatVisitField;
    }

    @Override
    public void setRoistatVisitField(Long roistatVisitField) {
        this.roistatVisitField = roistatVisitField;
    }

    public String getEmailContactEnum() {
        return emailContactEnum;
    }

    @Override
    public void setEmailContactEnum(String emailContactEnum) {
        this.emailContactEnum = emailContactEnum;
    }

    public Long getSourceContactsCustomField() {
        return sourceContactsCustomField;
    }

    @Override
    public void setSourceContactsCustomField(Long sourceContactsCustomField) {
        this.sourceContactsCustomField = sourceContactsCustomField;
    }

    public Long getMarketingChannelLeadsCustomField() {
        return marketingChannelLeadsCustomField;
    }

    @Override
    public void setMarketingChannelLeadsCustomField(Long marketingChannelLeadsCustomField) {
        this.marketingChannelLeadsCustomField = marketingChannelLeadsCustomField;
    }

    @Override
    public void setPhoneNumberContactStockField(Long amoCRMPhoneNumberStockFieldContact) {
        this.phoneNumberContactStockField = amoCRMPhoneNumberStockFieldContact;
    }

    public Long getPhoneNumberContactStockField() {
        return phoneNumberContactStockField;
    }

    public String getPhoneNumberStockFieldContactEnumWork() {
        return phoneNumberStockFieldContactEnumWork;
    }

    @Override
    public void setPhoneNumberStockFieldContactEnumWork(String phoneNumberStockFieldContactEnumWork) {
        this.phoneNumberStockFieldContactEnumWork = phoneNumberStockFieldContactEnumWork;
    }

    public Long getCommentCustomField() {
        return commentCustomField;
    }

    @Override
    public void setCommentCustomField(Long commentCustomField) {
        this.commentCustomField = commentCustomField;
    }

    public Long getLeadFromSiteTagId() {
        return leadFromSiteTagId;
    }

    @Override
    public void setLeadFromSiteTagId(Long leadFromSiteTagId) {
        this.leadFromSiteTagId = leadFromSiteTagId;
    }

    public Long getAreaLeadField() {
        return areaLeadField;
    }

    @Override
    public void setAreaLeadField(Long areaLeadField) {
        this.areaLeadField = areaLeadField;
    }

    public Long getBuildTypeLeadField() {
        return buildTypeLeadField;
    }

    @Override
    public void setBuildTypeLeadField(Long buildTypeLeadField) {
        this.buildTypeLeadField = buildTypeLeadField;
    }

    public Long getCleanTypeLeadField() {
        return cleanTypeLeadField;
    }

    @Override
    public void setCleanTypeLeadField(Long cleanTypeLeadField) {
        this.cleanTypeLeadField = cleanTypeLeadField;
    }

    @Override
    public ChangedDealStatus processSuccessfullyClosedDeal(ChangedDealStatus changedDealStatus) throws APIAuthException {
        log.info("work with deal#" + changedDealStatus.getEntityId());

        try{
            this.workWithChangedDealStatus(changedDealStatus);
        }
        catch (LogicSuccessfullException e){
            changedDealStatus.setReactionState(ChangedEntityStatus.ReactionState.DONE);
            changedEntityStatusRepository.save(changedDealStatus);

            return changedDealStatus;
        }
        catch (LogicException e){
            throw new IllegalStateException(e.getMessage());
        }

        changedDealStatus.setReactionState(ChangedEntityStatus.ReactionState.DONE);
        changedEntityStatusRepository.save(changedDealStatus);
        return changedDealStatus;
    }

    private void workWithChangedDealStatus(ChangedDealStatus changedDealStatus) throws APIAuthException, LogicException {
        AmoCRMLead lead = amoCRMService.getLeadById(changedDealStatus.getEntityId());
        if(lead == null){
            log.error("Lead#".concat(changedDealStatus.getEntityId().toString()).concat(" not found in CRM. Nothing to do"));
            throw new LogicSuccessfullException("Lead#".concat(changedDealStatus.getEntityId().toString()).concat(" not found in CRM. Nothing to do"));
        }

        List<AmoCRMContactsLeadsLink> amoCRMContactsLeadsLinks = amoCRMService.getContactsLeadsLinksByLead(lead);

        // Находим все связанные контакты и убеждаемся, что нет открытых сделок
        if(amoCRMContactsLeadsLinks != null && amoCRMContactsLeadsLinks.size() > 0){
            for(AmoCRMContactsLeadsLink amoCRMContactsLeadsLink : amoCRMContactsLeadsLinks){
                log.info("contact found: id#" + amoCRMContactsLeadsLink.getContact_id().toString());

                AmoCRMContact crmContact = amoCRMService.getContactById(amoCRMContactsLeadsLink.getContact_id());
                List<AmoCRMContactsLeadsLink> amoCRMContactsLeadsLinksForContact = amoCRMService.getContactsLeadsLinksByContact(crmContact);

                for(AmoCRMContactsLeadsLink leadsLink : amoCRMContactsLeadsLinksForContact){
                    AmoCRMLead crmLead = amoCRMService.getLeadById(leadsLink.getLead_id());
                    if(crmLead != null && !Arrays.asList(this.closedStatusesIds).contains(crmLead.getStatus_id())){
                        // Сделка не закрыта
                        log.info("Not closed lead#" + leadsLink.getLead_id() + " found");
                        throw new LogicSuccessfullException("Not closed lead#" + leadsLink.getLead_id() + " found");
                    }
                }
            }

            // Создаем новую сделку
            log.info("Creating new Lead from lead#" + lead.getId());
            AmoCRMLead newLead = new AmoCRMLead();
            newLead.setResponsible_user_id(lead.getResponsible_user_id());
            newLead.setName("[Автоматически пересоздана из:] " + lead.getName());

            for(AmoCRMCustomField amoCRMCustomField : lead.getCustom_fields()){
                // Если у исходного Lead заполнен источник, берем его
                if(amoCRMCustomField.getId().equals(this.getSourceLeadsCustomField()) && amoCRMCustomField.getValues().size() > 0){
                    String[] val = {amoCRMCustomField.getValues().get(0).getValue()};
                    newLead.addStringValuesToCustomField(this.getSourceLeadsCustomField(), val);
                }

                // Если заполнен Рекламный канал, берем его
                if(amoCRMCustomField.getId().equals(this.getMarketingChannelLeadsCustomField()) && amoCRMCustomField.getValues().size() > 0){
                    String[] val = {amoCRMCustomField.getValues().get(0).getValue()};
                    newLead.addStringValuesToCustomField(this.getMarketingChannelLeadsCustomField(), val);
                }
            }

            AmoCRMCreatedEntityResponse amoCRMCreatedEntityResponse = amoCRMService.addLead(newLead);
            if(amoCRMCreatedEntityResponse.getId() != null){
                log.info("created lead#" + amoCRMCreatedEntityResponse.getId().toString());

                AmoCRMLead lead1 = amoCRMService.getLeadById(amoCRMCreatedEntityResponse.getId());
                for(AmoCRMContactsLeadsLink amoCRMContactsLeadsLink : amoCRMContactsLeadsLinks) {
                    AmoCRMContact contact = amoCRMService.getContactById(amoCRMContactsLeadsLink.getContact_id());
                    if(contact != null) {
                        amoCRMService.addContactToLead(contact, lead1);
                    }
                }
            }
            else{
                log.error("error creating lead!");
                throw new LogicNotSuccessfullException("Lead was not created with unknown reason!");
            }
        }
    }

    public Long[] getClosedStatusesIds() {
        return closedStatusesIds;
    }

    @Override
    public void setClosedStatusesIds(Long[] closedStatusesIds) {
        this.closedStatusesIds = closedStatusesIds;
    }

    public Long[] getSuccessfullyClosedStatusesIds() {
        return successfullyClosedStatusesIds;
    }

    @Override
    public void setSuccessfullyClosedStatusesIds(Long[] successfullyClosedStatusesIds) {
        this.successfullyClosedStatusesIds = successfullyClosedStatusesIds;
    }
}
