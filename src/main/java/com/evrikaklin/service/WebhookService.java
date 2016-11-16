package com.evrikaklin.service;

import com.evrikaklin.dto.site.CallRequest;
import com.evrikaklin.dto.site.Result;
import org.springframework.stereotype.Service;

/**
 * Created by berz on 28.09.2016.
 */
@Service
public interface WebhookService {
    Result newCallFromWebhook(CallRequest callRequest);
}
