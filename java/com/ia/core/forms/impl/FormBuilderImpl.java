package com.ia.core.forms.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.ImmutableList.builder;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.inject.Key.get;
import static com.google.inject.name.Names.named;
import static com.ia.util.http.HttpUtil.deSensitise;
import static com.ia.util.http.HttpUtil.isSensitiveParameter;
import static org.apache.commons.collections4.map.DefaultedMap.defaultedMap;
import static org.apache.commons.fileupload.servlet.ServletFileUpload.isMultipartContent;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.Factory;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList.Builder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.inject.Key;
import com.ia.common.IARuntimeException;
import com.ia.core.annotations.Nullable;
import com.ia.core.forms.Form;
import com.ia.core.forms.FormBuilder;
import com.ia.core.forms.FormFactory;
import com.ia.core.forms.RuntimeTypeAdapterFactory;
import com.ia.core.util.HttpBeanUtil;
import com.ia.core.util.bean.MultiPartRequest;
import com.ia.core.util.bean.SinglePartRequest;
import com.ia.forms.LoanForm;
import com.ia.forms.LoginForm;
import com.ia.forms.impl.LoanFormImpl;
import com.ia.forms.impl.LoginFormImpl;

@Singleton
public final class FormBuilderImpl implements FormBuilder {

    private final class FormCreator<T extends Form> implements InstanceCreator<T> {

        private final Class<T> formClass;

        private final String organisation;

        private final HttpServletRequest request;

        private FormCreator(final Class<T> formClass, final String organisation,
                final HttpServletRequest request) {
            super();
            this.formClass = formClass;
            this.organisation = organisation;
            this.request = request;
        }

        @Override
        public T createInstance(final Type type) {
            try {
                return createFormInstance(formClass, organisation, request);
            }
            catch (final Exception e) {
                throw new IARuntimeException(
                        "Cannot create form instance " + formClass.getCanonicalName());
            }
        }
    }

    private final class HashMapFactory implements Factory<Map<String, Object>> {
        @Override
        public Map<String, Object> create() {
            return newHashMap();
        }
    }

    private final class NameContainsDash implements Predicate<Entry<String, String[]>> {
        @Override
        public boolean apply(final Entry<String, String[]> me) {
            final String paramName = me.getKey();
            return paramName.contains("-");
        }
    }

    private static final String MSG_INVALID_PARAMETER_NAME =
            "Invalid request parameter name. Must have exactly two parts separated by a dash [got %s]";

    private static final String MSG_NO_FACTORY_FOUND =
            "No factory for FormClass %s, Organisation %s";

    private final HttpBeanUtil beanUtil;

    private final Map<Key<? extends Form>, FormFactory> factoryMap;

    @Inject
    public FormBuilderImpl(final HttpBeanUtil beanUtil,
            final Map<Key<? extends Form>, FormFactory> factoryMap) {
        super();
        this.beanUtil = beanUtil;
        this.factoryMap = factoryMap;
    }

    private void assertIsMultipart(final HttpServletRequest request) throws ContentTypeException {
        if (!isMultipartContent(request)) {
            throw new ContentTypeException("request must be multi-part.");
        }
    }

    private void assertIsSinglepart(final HttpServletRequest request) throws ContentTypeException {
        if (isMultipartContent(request)) {
            throw new ContentTypeException("request cannot be multi-part.");
        }
    }

    @Override
    public <T extends Form> T build(final Class<T> formClass,
            @SinglePartRequest final HttpServletRequest request) throws Exception {
        return build(formClass, null, request);
    }

    @Override
    public <T extends Form> T build(final Class<T> formClass,
            @MultiPartRequest final HttpServletRequest request, final long maxFileSizeInBytes)
            throws Exception {

        /* Assert request is multi-part. */
        assertIsMultipart(request);

        /* Create an instance of the Form class. */
        final T formBean = createFormInstance(formClass, request);

        /* Populate bean using request parameters. */
        beanUtil.populate(formBean, request, maxFileSizeInBytes);

        return formBean;
    }

    @Override
    public <T extends Form> T build(final Class<T> formClass,
            @SinglePartRequest final HttpServletRequest request,
            final Map<String, Object> initialData) throws Exception {

        /* Assert request is not multi-part. */
        assertIsSinglepart(request);

        /* Assert initialData is not null. */
        checkArgument(null != initialData, "initialData cannot be null.");

        /* Create an instance of the Form class. */
        final T formBean = createFormInstance(formClass, request);

        /* Populate bean using initial data. */
        beanUtil.populate(formBean, initialData);

        /* Populate bean using request parameters. */
        beanUtil.populate(formBean, request);

        return formBean;

    }

    @Override
    public <T extends Form> T build(final Class<T> formClass, @Nullable final String organisation,
            final HttpServletRequest request) throws Exception {

        /* Assert request is not multi-part. */
        assertIsSinglepart(request);

        /* Create an instance of the Form class. */
        final T formBean = createFormInstance(formClass, organisation, request);

        /* Populate bean using request parameters. */
        beanUtil.populate(formBean, request);

        return formBean;
    }

    @Override
    public <T extends Form> T build(final Class<T> formClass, @Nullable final String organisation,
            @MultiPartRequest final HttpServletRequest request, final long maxFileSizeInBytes)
            throws Exception {

        /* Assert request is multi-part. */
        assertIsMultipart(request);

        /* Create an instance of the Form class. */
        final T formBean = createFormInstance(formClass, organisation, request);

        /* Populate bean using request parameters. */
        beanUtil.populate(formBean, request, maxFileSizeInBytes);

        return formBean;
    }

    @Override
    public <T extends Form> List<T> buildMultiple(final Class<T> formClass,
            @SinglePartRequest final HttpServletRequest request) throws Exception {

        /* Assert request is not multi-part. */
        assertIsSinglepart(request);

        /* Add each bean to an immutable list. */
        final Builder<T> builder = builder();

        /* Group parameters by name, each group corresponding to one bean. */
        final Collection<Map<String, Object>> grouped = groupParameters(request);

        for (final Map<String, Object> nameValueMap : grouped) {

            /* Create a bean of the given type for each group. */
            final T formBean = createFormInstance(formClass, request);

            /* Populate the bean. */
            beanUtil.populate(formBean, nameValueMap);

            /* Add to results. */
            builder.add(formBean);

        }

        return builder.build();
    }

    private <T extends Form> T createFormInstance(final Class<T> formClass,
            final HttpServletRequest request) throws Exception {
        /* Get appropriate form factory. */
        final FormFactory factory = getFactory(formClass);

        /* Use factory to create form bean. */
        return createFormInstance(factory, request);
    }

    private <T extends Form> T createFormInstance(final Class<T> formClass,
            @Nullable final String organisation, final HttpServletRequest request)
            throws Exception {
        /* Get appropriate form factory. */
        final FormFactory factory = getFactory(formClass, organisation);

        /* Use factory to create form bean. */
        return createFormInstance(factory, request);
    }

    private <T extends Form> T createFormInstance(final FormFactory factory,
            final HttpServletRequest request) throws Exception {
        @SuppressWarnings("unchecked")
        final T formBean = (T) factory.create(request);
        return formBean;
    }

    @Override
    public <T extends Form> T fromJson(final Class<T> formClass,
            @Nullable final String organisation,
            @SinglePartRequest final HttpServletRequest request) throws Exception {

        /* Assert request is not multi-part. */
        assertIsSinglepart(request);

        /* Default parameter name is "json" */
        final String formData = request.getParameter("json");

        /* Add Class here */
        /* Build from JSON. */
        final GsonBuilder builder = new GsonBuilder();
        /* Register adapter */

        final RuntimeTypeAdapterFactory<? extends Form> loginFormTypeFactory =
                RuntimeTypeAdapterFactory.of(LoginForm.class).registerSubtype(LoginFormImpl.class,
                        "IA");

        builder.registerTypeAdapter(LoginFormImpl.class,
                new FormCreator<T>(formClass, organisation, request));

        builder.registerTypeAdapterFactory(loginFormTypeFactory);

        final RuntimeTypeAdapterFactory<? extends Form> userFormTypeFactory =
                RuntimeTypeAdapterFactory.of(LoanForm.class).registerSubtype(LoanFormImpl.class,
                        "IA");

        builder.registerTypeAdapter(LoanFormImpl.class,
                new FormCreator<T>(formClass, organisation, request));

        builder.registerTypeAdapterFactory(userFormTypeFactory);

        final Gson gson = builder.create();

        return gson.fromJson(formData, formClass);
    }

    private <T extends Form> FormFactory getFactory(final Class<T> formClass) {
        return getFactory(formClass, null);
    }

    private <T extends Form> FormFactory getFactory(final Class<T> formClass,
            @Nullable final String organisation) {

        /* Start by assuming that there is no organisation-specific factory for this form. */
        Key<T> key = get(formClass);

        /* Is an organisation specified? */
        if (isNotBlank(organisation)) {
            /* Try to find an organisation-specific factory for this form. */
            final Key<T> orgSpecificKey = get(formClass, named(organisation));

            if (factoryMap.containsKey(orgSpecificKey)) {
                /* Organisation-specific factory exists. Use it. */
                key = orgSpecificKey;
            }
        }

        /* Look up a form factory using the key */
        final FormFactory factory = factoryMap.get(key);

        /* Ensure that there is a factory. */
        checkState(null != factory, MSG_NO_FACTORY_FOUND, formClass.getCanonicalName(),
                organisation);

        return factory;
    }

    private Collection<Map<String, Object>> groupParameters(final HttpServletRequest request) {

        /* Group parameters by name, each group corresponding to one bean. */
        final Map<String, Map<String, Object>> map = newHashMap();
        final Map<String, Map<String, Object>> grouped = defaultedMap(map, new HashMapFactory());

        /* Get all parameters. */
        @SuppressWarnings("unchecked")
        final Map<String, String[]> parameterMap = request.getParameterMap();

        /* Filter parameters that have a dash (-) in the name. */
        final Set<Entry<String, String[]>> parameters = parameterMap.entrySet();
        final Iterable<Entry<String, String[]>> containsDash =
                filter(parameters, new NameContainsDash());

        /* Iterate through request parameters. */
        for (final Entry<String, String[]> me : containsDash) {

            /* Get the parameter name. E.g. id-25 */
            String paramName = me.getKey();

            /* Is this a sensitive parameter? */
            if (isSensitiveParameter(paramName)) {
                /* Remove the leading _ */
                paramName = deSensitise(paramName);

            }

            /* Split parameter name into parts. E.g. [id, 25] */
            final String[] parts = paramName.split("-");

            /* There must be two parts. */
            checkState(parts.length == 2, MSG_INVALID_PARAMETER_NAME, paramName);

            /* E.g. groupId = 25, key = id */
            final String groupId = parts[1];
            final String key = parts[0];

            /* Add to grouped E.g. {25 => {id => 25}} */
            final Map<String, Object> aGroup = grouped.get(groupId);
            aGroup.put(key, me.getValue());
            grouped.put(groupId, aGroup);

        }

        return grouped.values();
    }

}
