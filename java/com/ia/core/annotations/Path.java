package com.ia.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ia.beans.Organisation;
import com.ia.enums.MODE;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Path {

    /**
     * Specify the {@link Organisation}s for which this action does not require requests to be
     * signed. Defaults to <code>{}</code>.
     *
     * @return {@link Organisation}s for which this action does not require requests to be signed.
     */
    String[] noSignatureRequiredFor() default {};

    /**
     * Identifies the {@link MODE} required to access this action.
     *
     * @return {@link MODE} required to access this action.
     */
    MODE permissionRequired();

    /**
     * Should requests to this action be signed? Defaults to <code>false</code>.
     *
     * @return <code>true</code> if the action requires requests to be signed.
     */
    boolean requiresSignature() default false;

    /**
     * Returns the Path of an action relative to <code>IAServer/ia</code>.
     *
     * @return Path of the action relative to <code>IAServer/ia</code>.
     */
    String value();

    /**
     * Should requests to this action have a particular, pre-approved referer?
     *
     * @see Organisation#isExpectedReferer(String)
     * @see OrganisationOnlineRefererUrl
     *
     * @return <code>true</code> if requests to this action have a particular, pre-approved referer.
     */
    boolean verifyReferer() default false;

}
