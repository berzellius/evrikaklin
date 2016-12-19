package com.evrikaklin.service;

import com.evrikaklin.dmodel.ChangedDealStatus;
import com.evrikaklin.dmodel.ChangedEntityStatus;
import com.evrikaklin.dmodel.LoadDataFromBackendSettings;
import com.evrikaklin.dto.site.*;
import com.evrikaklin.repository.CallRepository;
import com.evrikaklin.repository.ChangedEntityStatusRepository;
import com.evrikaklin.repository.LoadDataFromBackendSettingsRepository;
import com.evrikaklin.scheduling.ScheduledTasks;
import com.evrikaklin.settings.APISettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by berz on 28.09.2016.
 */
@Service
public class WebhookServiceImpl implements WebhookService {

    @Autowired
    CallRepository callRepository;

    @Autowired
    ScheduledTasks scheduledTasks;

    @Autowired
    CallTrackingAPIService callTrackingAPIService;

    @Autowired
    LoadDataFromBackendSettingsRepository loadDataFromBackendSettingsRepository;

    @Autowired
    ChangedEntityStatusRepository changedEntityStatusRepository;

    private static final Logger log = LoggerFactory.getLogger(WebhookService.class);

    /*
    *
    * Не является transactional, так как из transactional метода не запустится scheduledTask
     */
    @Override
    public Result newCallFromWebhook(CallRequest callRequest) {
        List<Call> calls = callRequest.getCalls();

        if(calls != null){
            processCalls(calls);
            scheduledTasks.runImportCallsToCRM();
        }

        return new Result("success");
    }

    @Override
    @Transactional
    public Result newDealsStatusesChanges(DealsStatusesChangedRequest dealsStatusesChangedRequest) {
        LoadDataFromBackendSettings loadDataFromBackendSettings = loadDataFromBackendSettingsRepository.findOneBySettingType(LoadDataFromBackendSettings.SettingType.PASSWORD);
        String password = loadDataFromBackendSettings.getValue();

        if(
                dealsStatusesChangedRequest.getPassword() == null ||
                        password == null ||
                        !dealsStatusesChangedRequest.getPassword().equals(password)
                ){
            log.error("Wrong password!");
            return new Result("error with password");
        }

        if(
                dealsStatusesChangedRequest.getStatusChanges() == null ||
                        dealsStatusesChangedRequest.getStatusChanges().size() == 0
                ){
            log.error("Empty changes list!");
            return new Result("error: empty changes list!");
        }

        ArrayList<ChangedDealStatus> changedDealStatuses = new ArrayList<>();

        for(DealStatusChangedDTO dealStatusChangedDTO : dealsStatusesChangedRequest.getStatusChanges()){
            log.info("dealId: " + dealStatusChangedDTO.getDealId().toString());
            log.info("statusId: " + dealStatusChangedDTO.getStatusId().toString());
            log.info("timestamp: " + dealStatusChangedDTO.getTimestamp().toString());

            Date date = new Date();
            date.setTime(dealStatusChangedDTO.getTimestamp() * 1000);

            if(
                    dealStatusChangedDTO.getStatusId() == null ||
                            dealStatusChangedDTO.getDealId() == null ||
                            dealStatusChangedDTO.getTimestamp() == null
                    ){
                throw new IllegalArgumentException("empty argument in " + dealStatusChangedDTO.toString());
            }

            if(Arrays.asList(APISettings.AmoCRMLeadSuccesfullyClosedStatuses).contains(dealStatusChangedDTO.getStatusId())){

                // Закрываем все изменения, произошедшие до текущего
                List<ChangedEntityStatus> changedDealStatusListBefore = changedEntityStatusRepository.findByEntityTypeAndEntityIdAndReactionStateAndDtmCreateLessThan(
                        "deal", dealStatusChangedDTO.getDealId(), ChangedEntityStatus.ReactionState.NEW, date
                );
                for(ChangedEntityStatus changedEntityStatus : changedDealStatusListBefore){
                    changedEntityStatus.setReactionState(ChangedEntityStatus.ReactionState.DONE);
                }
                changedEntityStatusRepository.save(changedDealStatusListBefore);

                ChangedDealStatus changedDealStatus = new ChangedDealStatus();
                changedDealStatus.setStatusId(dealStatusChangedDTO.getStatusId());
                // Если уже есть более поздние изменения, то сразу ставим DONE
                changedDealStatus.setReactionState(
                        (
                                changedEntityStatusRepository.countByEntityTypeAndEntityIdAndReactionStateAndDtmCreateGreaterThan(
                                "deal", dealStatusChangedDTO.getDealId(), ChangedEntityStatus.ReactionState.NEW, date
                            ) > 0
                        )? ChangedEntityStatus.ReactionState.DONE : ChangedEntityStatus.ReactionState.NEW
                );
                changedDealStatus.setEntityId(dealStatusChangedDTO.getDealId());
                changedDealStatus.setDtmCreate(date);

                changedDealStatuses.add(changedDealStatus);
            }
        }

        changedEntityStatusRepository.save(changedDealStatuses);

        return new Result("success");

    }


    @Transactional
    private void processCalls(List<Call> calls){
        for(Call call : calls){
            if(call.getCaller() == null || call.getCaller().equals("")){
                call.setCaller("unknown");
            }
            com.evrikaklin.dmodel.Call c = new com.evrikaklin.dmodel.Call();
            c.setNumber(call.getCaller());
            c.setDt(call.getDatetime());
            c.setProjectId(call.getProject_id());
            c.setState(com.evrikaklin.dmodel.Call.State.NEW);

            callTrackingAPIService.processCallOnImport(c);
            callRepository.save(c);
        }
    }

}
