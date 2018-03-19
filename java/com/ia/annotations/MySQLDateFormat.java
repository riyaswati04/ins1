package com.ia.annotations;

import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;

import com.ia.util.DateUtil;

/**
 * Denotes that the annotated String should be in <code>yyyy-MM-dd</code> format.
 *
 * @see DateUtil#MYSQL_DATE_FORMAT
 *
 */
@Retention(SOURCE)
public @interface MySQLDateFormat {
    /* Denotes that the annotated String should be in yyyy-MM-dd format. */
}
