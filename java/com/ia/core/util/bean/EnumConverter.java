package com.ia.core.util.bean;

import static java.lang.Enum.valueOf;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import org.apache.commons.beanutils.Converter;

public final class EnumConverter implements Converter {

    @SuppressWarnings("unchecked")
    @Override
    public Object convert(@SuppressWarnings("rawtypes") final Class type, final Object value) {

        /* Cast value to String first */
        final String string = (String) value;

        /* If not blank, try to convert to appropriate enum */
        return isNotBlank(string) ? valueOf(type, string) : null;
    }

}
