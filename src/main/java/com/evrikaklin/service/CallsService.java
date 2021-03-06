package com.evrikaklin.service;

import com.evrikaklin.dto.site.Lead;
import com.evrikaklin.dto.site.Result;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by berz on 27.09.2015.
 */
@Service
public interface CallsService {

    public Long callsAlreadyLoaded();

    Long callsAlreadyLoaded(Integer projectId);

    Long callsAlreadyLoaded(Integer project, Date from, Date to);

    Result newLeadFromSite(List<Lead> leads, String origin, String password);
}
