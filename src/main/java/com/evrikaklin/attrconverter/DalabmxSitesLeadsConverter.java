package com.evrikaklin.attrconverter;

import com.evrikaklin.dto.site.Lead;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import java.io.IOException;

/**
 * Created by berz on 15.06.2016.
 */
public class DalabmxSitesLeadsConverter implements AttributeConverter<Lead, String> {
    @Override
    public String convertToDatabaseColumn(Lead lead) {

        ObjectMapper objectMapper = new ObjectMapper();
        String s;

        // На null и суда null
        if(lead == null) return null;

        try {
            s = objectMapper.writeValueAsString(lead);
        } catch (IOException e) {
            s = null;
        }

        return s;
    }

    @Override
    public Lead convertToEntityAttribute(String s) {
        ObjectMapper objectMapper = new ObjectMapper();
        Lead lead;

        // просто null
        if(s == null) return null;

        try {
            lead = objectMapper.readValue(s, Lead.class);
        } catch (IOException e) {
            lead = null;
        }

        return lead;
    }
}
