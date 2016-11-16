package com.evrikaklin.settings;

import java.util.HashMap;

/**
 * Created by berz on 22.11.14.
 */
public interface ProjectSettings {

    public HashMap<String, String> getDatabaseConnectionConfig();

    public boolean amoCRMReadOnlyMode();

}