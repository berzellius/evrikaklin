package com.evrikaklin.service;

import com.evrikaklin.dmodel.CallTrackingSourceCondition;
import com.evrikaklin.repository.CallTrackingSourceConditionRepository;
import com.evrikaklin.specifications.CallTrackingSourceConditionSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by berz on 01.03.2016.
 */
@Service
@Transactional
public class CallTrackingSourceConditionServiceImpl implements CallTrackingSourceConditionService {
    @Autowired
    CallTrackingSourceConditionRepository callTrackingSourceConditionRepository;

    @Override
    public void updateSources(List<CallTrackingSourceCondition> allMarketingChannelsFromCalltracking) {
        callTrackingSourceConditionRepository.deleteAll();
        callTrackingSourceConditionRepository.save(allMarketingChannelsFromCalltracking);
    }

    @Override
    public Iterable<CallTrackingSourceCondition> getAllSources(){
        return callTrackingSourceConditionRepository.findAll();
    }

    /*
    * Выбрать рекламный канал по Utm и Id проекта
     */
    @Override
    public CallTrackingSourceCondition getCallTrackingSourceConditionByUtmAndProjectId(String utmSource, String utmMedium, String utmCampaign, Integer projectId){

        Specification<CallTrackingSourceCondition> spec = CallTrackingSourceConditionSpecification.selectByUtmParamsAndProjectId(utmSource, utmMedium, utmCampaign, projectId);
        List<CallTrackingSourceCondition> callTrackingSourceConditions = callTrackingSourceConditionRepository.findAll(spec);

        if(callTrackingSourceConditions.size() > 0) {
            return callTrackingSourceConditions.get(0);
        }
        else{
            return null;
        }
    }
}
