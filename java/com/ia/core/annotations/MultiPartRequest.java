package com.ia.core.annotations;

import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;

/**
 * Annotated item must be a multi-part HTTP request.
 *
 */
@Retention(SOURCE)
public @interface MultiPartRequest {

}
