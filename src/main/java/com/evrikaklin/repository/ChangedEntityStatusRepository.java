package com.evrikaklin.repository;

import com.evrikaklin.dmodel.ChangedEntityStatus;
import com.evrikaklin.dmodel.ChangedEntityStatus.ReactionState;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by berz on 17.12.2016.
 */
@Repository
public interface ChangedEntityStatusRepository extends CrudRepository<ChangedEntityStatus, Long> {
    public List<ChangedEntityStatus> findByEntityTypeAndEntityIdAndReactionStateAndDtmCreateLessThan(String entityType, Long entityId, ChangedEntityStatus.ReactionState reactionState, Date dtmCreate);

    public List<ChangedEntityStatus> findByEntityTypeAndEntityIdAndReactionStateAndDtmCreateGreaterThan(String entityType, Long entityId, ChangedEntityStatus.ReactionState reactionState, Date dtmCreate);

    public Long countByEntityTypeAndEntityIdAndReactionStateAndDtmCreateGreaterThan(String entityType, Long entityId, ChangedEntityStatus.ReactionState reactionState, Date dtmCreate);
}
