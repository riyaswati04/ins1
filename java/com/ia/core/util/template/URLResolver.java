package com.ia.core.util.template;

import static com.google.common.base.Joiner.on;
import static com.google.common.collect.ImmutableList.builder;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList.Builder;

public final class URLResolver implements Function<String, String> {

    private static final String SEPARATOR = "/";

    private final String siteUrlPrefix;

    private final List<String> urlParts;

    public URLResolver() {
        this(null, null);
    }

    public URLResolver(final String organisationName) {
        this(null, organisationName);

    }

    public URLResolver(final String siteUrlPrefix, final String organisationName) {

        /*
         * An absolute siteUrlPrefix is Used where the complete (as opposed to relative) URL is
         * needed. E.g. mails.
         */
        this.siteUrlPrefix = isNotBlank(siteUrlPrefix) ? siteUrlPrefix : "/";

        final Builder<String> builder = builder();
        builder.add("IAServer");
        builder.add("ia");

        if (isNotBlank(organisationName)) {
            builder.add(organisationName);
        }

        urlParts = builder.build();

    }

    @Override
    public String apply(final String urlFragment) {
        return siteUrlPrefix + on(SEPARATOR).join(urlParts).concat(SEPARATOR).concat(urlFragment);
    }
}
