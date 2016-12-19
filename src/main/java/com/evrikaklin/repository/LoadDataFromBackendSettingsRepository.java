package com.evrikaklin.repository;

import com.evrikaklin.dmodel.LoadDataFromBackendSettings;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import com.evrikaklin.dmodel.LoadDataFromBackendSettings.SettingType;

import java.util.List;

/**
 * Created by berz on 17.12.2016.
 */
@Transactional
public interface LoadDataFromBackendSettingsRepository extends CrudRepository<LoadDataFromBackendSettings, Long> {
    public List<LoadDataFromBackendSettings> findBySettingType(SettingType settingType);

    public LoadDataFromBackendSettings findOneBySettingType(SettingType settingType);
}
