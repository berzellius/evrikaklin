package com.evrikaklin.service;

import com.evrikaklin.dmodel.LeadFromSite;
import com.evrikaklin.dmodel.Site;
import com.evrikaklin.dto.site.Lead;
import com.evrikaklin.dto.site.Result;
import com.evrikaklin.repository.CallRepository;
import com.evrikaklin.repository.LeadFromSiteRepository;
import com.evrikaklin.repository.SiteRepository;
import com.evrikaklin.specifications.CallSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by berz on 27.09.2015.
 */
@Service
@Transactional
public class CallsServiceImpl implements CallsService {
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    CallRepository callRepository;

    @Autowired
    SiteRepository siteRepository;

    @Autowired
    LeadFromSiteRepository leadFromSiteRepository;

   // @Autowired
    //ScheduledTasks scheduledTasks;

    @Override
    public Long callsAlreadyLoaded() {
        return callRepository.count(CallSpecifications.byDates(new Date(), new Date()));
    }

    @Override
    public Long callsAlreadyLoaded(Integer projectId) {
        return callsAlreadyLoaded(projectId, null, null);
    }

    @Override
    public Long callsAlreadyLoaded(Integer project, Date from, Date to) {
        Long count = callRepository.count(
                Specifications.where(CallSpecifications.byDates((from != null)? from : new Date(), (to != null)? to : new Date()))
                        .and(CallSpecifications.byProjectId(project))
                        .and(CallSpecifications.byStatusNotNull())
        );
        return count;
    }

    @Override
    public Result newLeadFromSite(List<Lead> leads, String url, String password) {
        List<Site> sites = siteRepository.findByUrlAndPassword(url, password);
        if(sites.size() == 0){
            return new Result("error");
        }

        Site site = sites.get(0);

        List<LeadFromSite> leadFromSiteList = new ArrayList<>();
        for(Lead lead : leads){
            LeadFromSite leadFromSite = new LeadFromSite();
            leadFromSite.setDtmCreate(new Date());
            leadFromSite.setSite(site);
            leadFromSite.setLead(lead);
            leadFromSite.setState(LeadFromSite.State.NEW);

            leadFromSiteList.add(leadFromSite);
        }

        leadFromSiteRepository.save(leadFromSiteList);
        return new Result("success");
    }


}
