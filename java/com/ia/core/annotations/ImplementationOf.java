package com.ia.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ia.core.forms.Form;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface ImplementationOf {

    Class<? extends Form> form();

    String[] forOrganisations() default {};

}
