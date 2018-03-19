package com.ia.core.util.bean;

import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;

/**
 * Annotated item must be a single part HTTP request (as opposed to a Multipart request).
 *
 */
@Retention(SOURCE)
public @interface SinglePartRequest {

}
