package com.evrikaklin.repository;

import com.evrikaklin.dmodel.CallTrackingSourceCondition;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;


/**
 * Created by berz on 01.03.2016.
 */
@Transactional(readOnly = true)
public interface CallTrackingSourceConditionRepository extends CrudRepository<CallTrackingSourceCondition, Long>, JpaSpecificationExecutor<CallTrackingSourceCondition> {

}
