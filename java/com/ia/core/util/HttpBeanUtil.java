package com.ia.core.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Maps.newTreeMap;
import static com.google.common.collect.Multimaps.index;
import static com.ia.util.http.HttpUtil.deSensitise;
import static com.ia.util.http.HttpUtil.isSensitiveParameter;
import static org.apache.commons.fileupload.servlet.ServletFileUpload.isMultipartContent;
import static org.apache.commons.lang3.StringUtils.containsAny;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;

import com.google.common.base.Function;
import com.google.common.collect.ListMultimap;
import com.ia.core.util.bean.BeanHelper;
import com.ia.core.util.bean.IAConvertUtilsBean;
import com.ia.core.util.bean.MultiPartRequest;
import com.ia.core.util.bean.ServletFileUploadUtil;
import com.ia.core.util.bean.SinglePartRequest;

@Singleton
public final class HttpBeanUtil {

    private final class GetFieldName implements Function<FileItem, String> {
        @Override
        public String apply(final FileItem fileItem) {
            return fileItem.getFieldName();

        }
    }

    private final class IsFormField implements Function<FileItem, Boolean> {

        @Override
        public Boolean apply(final FileItem fileItem) {
            return fileItem.isFormField();

        }

    }

    private final BeanUtilsBean beanUtilsBean;

    public HttpBeanUtil() {

        beanUtilsBean = new BeanUtilsBean(new IAConvertUtilsBean());

    }

    private Map<String, Object> createNameValueMap(final HttpServletRequest request) {

        /* Create empty map. */
        final Map<String, Object> nameValueMap = newTreeMap();

        /* Request parameters. */
        @SuppressWarnings("unchecked")
        final Map<String, String[]> parameterMap = request.getParameterMap();

        /* Iterate through parameters, add to nameValueMap. */
        for (final Entry<String, String[]> me : parameterMap.entrySet()) {

            /* Map key is parameter name. */
            String name = me.getKey();
            final String[] value = me.getValue();

            /*
             * Sensitive parameter are prefixed with the _ character. Bean attributes will not have
             * the prefix.
             */
            if (isSensitiveParameter(name)) {
                name = deSensitise(name);
            }

            /* Add to nameValueMap. */
            nameValueMap.put(name, value);

        }

        return nameValueMap;

    }

    private Map<String, Object> createNameValueMap(final Iterable<FileItem> formFields) {
        /* Create empty map. */
        final Map<String, Object> nameValueMap = newTreeMap();

        /* Iterate through formFields, add to nameValueMap. */
        for (final FileItem item : formFields) {

            /* Map key is field name. */
            String name = item.getFieldName();
            final String value = item.getString();

            /*
             * Sensitive parameter are prefixed with the _ character. Bean attributes will not have
             * the prefix.
             */
            if (isSensitiveParameter(name)) {
                name = deSensitise(name);

            }

            /* Add to nameValueMap. */
            nameValueMap.put(name, value);

        }

        return nameValueMap;
    }

    public Map<String, Method> getAttributesAndSetMethods(final Class<?> klass) {
        final Map<String, Method> attributesToGetMethods = new HashMap<String, Method>();
        final BeanInfo info = BeanHelper.getBeanInfo(klass);

        for (final PropertyDescriptor pd : info.getPropertyDescriptors()) {
            attributesToGetMethods.put(pd.getName(), pd.getWriteMethod());
        }

        return attributesToGetMethods;
    }

    private List<FileItem> parseRequestAndSaveAttachments(
            @MultiPartRequest final HttpServletRequest request, final long maxFileSize)
            throws FileUploadException {

        /* Utility to parse multi-part requests. */
        final ServletFileUploadUtil fileUploadUtil = new ServletFileUploadUtil(maxFileSize);

        /* Parse request. */
        return fileUploadUtil.parse(request);

    }

    public void populate(final Object bean, @SinglePartRequest final HttpServletRequest request)
            throws IllegalAccessException, InvocationTargetException {

        /* Assert request is not multi-part. */
        checkArgument(!isMultipartContent(request), "request cannot be multi-part.");

        /* Create a name-value map of the request parameters. */
        final Map<String, Object> nameValueMap = createNameValueMap(request);

        /* Set attributes of this bean using the name value map. */
        populate(bean, nameValueMap);

    }

    public void populate(final Object bean, @MultiPartRequest final HttpServletRequest request,
            final long maxFileSizeInBytes)
            throws FileUploadException, IllegalAccessException, InvocationTargetException {

        /* Assert request is multi-part. */
        checkArgument(isMultipartContent(request), "request must be multi-part.");

        /* Parse request and save attached files. */
        final List<FileItem> fileItems =
                parseRequestAndSaveAttachments(request, maxFileSizeInBytes);

        /* Group into form fields and attachments. */
        final ListMultimap<Boolean, FileItem> grouped = index(fileItems, new IsFormField());

        /* Create a name-value map of the non-attachment request parameters. */
        final Map<String, Object> nameValueMap = createNameValueMap(grouped.get(true));

        /* Group the attachments by the field name. */
        final ListMultimap<String, FileItem> attachments =
                index(grouped.get(false), new GetFieldName());

        /* Add them to the name value map of attributes. */
        for (final String fieldName : attachments.keys()) {
            final List<FileItem> value = attachments.get(fieldName);
            nameValueMap.put(fieldName, containsAny(fieldName, "[]") ? value.get(0) : value);
        }

        /* Set attributes of this bean using the name value map. */
        populate(bean, nameValueMap);

    }

    public void populate(final Object bean, final Map<String, Object> nameValueMap)
            throws IllegalAccessException, InvocationTargetException {
        beanUtilsBean.populate(bean, nameValueMap);

    }

}
