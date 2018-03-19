package com.ia.core.util.bean;

import java.math.BigDecimal;

import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.commons.beanutils.converters.IntegerConverter;

public final class IAConvertUtilsBean extends ConvertUtilsBean {

    public IAConvertUtilsBean() {
        super();

        /* Set default value of BigDecimal fields to null. */
        register(new BigDecimalConverter(null), BigDecimal.class);

        /* Set default value of Integer fields to null. */
        register(new IntegerConverter(null), Integer.class);
    }

    @Override
    public Converter lookup(@SuppressWarnings("rawtypes") final Class clazz) {
        /* Superclass does not handle Enumerated types. */
        return clazz.isEnum() ? new EnumConverter() : super.lookup(clazz);
    }

}
