package com.evrikaklin.dmodel;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by berz on 17.12.2016.
 */
@Entity(name = "ChangedEntityStatus")
@Table(name = "changed_entity_status")
@Inheritance
@DiscriminatorColumn(name = "entity_type")
public abstract class ChangedEntityStatus extends DModelEntityFiscalable {

    public ChangedEntityStatus(){}

    public static enum ReactionState{
        NEW, DONE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "changed_entity_status_id_generator")
    @SequenceGenerator(name = "changed_entity_status_id_generator", sequenceName = "changed_entity_status_id_seq")
    @NotNull
    @Column(updatable = false, insertable = false, columnDefinition = "bigint")
    private Long id;

    @Column(name = "entity_type", insertable = false, updatable = false)
    private String entityType;

    private Long entityId;

    private Long statusId;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "reaction_state")
    private ReactionState reactionState;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public ReactionState getReactionState() {
        return reactionState;
    }

    public void setReactionState(ReactionState reactionState) {
        this.reactionState = reactionState;
    }

    @Override
    public String toString() {
        return this.getEntityType().concat("#").concat(this.getId().toString());
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }
}
