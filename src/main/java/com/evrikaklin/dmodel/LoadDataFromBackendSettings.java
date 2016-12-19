package com.evrikaklin.dmodel;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by berz on 17.12.2016.
 */
@Entity(name = "LoadDataFromBackendSettings")
@Table(
        name = "load_data_from_backend_settings"
)
@Access(AccessType.FIELD)
public class LoadDataFromBackendSettings extends DModelEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "load_data_from_backend_settings_id_generator")
    @SequenceGenerator(name = "load_data_from_backend_settings_id_generator", sequenceName = "load_data_from_backend_settings_id_seq")
    @NotNull
    @Column(updatable = false, insertable = false, columnDefinition = "bigint")
    private Long id;

    public static enum SettingType{
        PASSWORD
    }

    @Enumerated(value = EnumType.STRING)
    @Column(name = "setting_type", unique = true)
    private SettingType settingType;

    private String value;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof LoadDataFromBackendSettings && this.getId().equals(((LoadDataFromBackendSettings) obj).getId());
    }

    @Override
    public String toString() {
        return this.getValue();
    }

    public SettingType getSettingType() {
        return settingType;
    }

    public void setSettingType(SettingType settingType) {
        this.settingType = settingType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
