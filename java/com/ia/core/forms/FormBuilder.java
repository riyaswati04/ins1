package com.ia.core.forms;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.ia.core.annotations.MultiPartRequest;
import com.ia.core.annotations.Nullable;
import com.ia.core.annotations.SinglePartRequest;

public interface FormBuilder {

    <T extends Form> T build(Class<T> formClass, @SinglePartRequest HttpServletRequest request)
            throws Exception;

    <T extends Form> T build(Class<T> formClass, @MultiPartRequest HttpServletRequest request,
            long maxFileSizeInBytes) throws Exception;

    <T extends Form> T build(Class<T> formClass, @SinglePartRequest HttpServletRequest request,
            Map<String, Object> initialData) throws Exception;

    <T extends Form> T build(Class<T> formClass, @Nullable String organisation,
            @SinglePartRequest HttpServletRequest request) throws Exception;

    <T extends Form> T build(Class<T> formClass, @Nullable String organisation,
            @MultiPartRequest HttpServletRequest request, long maxFileSizeInBytes) throws Exception;

    <T extends Form> List<T> buildMultiple(Class<T> formClass,
            @SinglePartRequest HttpServletRequest request) throws Exception;

    <T extends Form> T fromJson(Class<T> formClass, @Nullable String organisation,
            @SinglePartRequest HttpServletRequest request) throws Exception;

}
