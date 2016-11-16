package com.evrikaklin.service;

import com.evrikaklin.dto.site.Call;
import com.evrikaklin.dto.site.CallRequest;
import com.evrikaklin.dto.site.Result;
import com.evrikaklin.repository.CallRepository;
import com.evrikaklin.scheduling.ScheduledTasks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
