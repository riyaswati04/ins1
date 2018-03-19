package com.ia.actions.paths.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.ia.actions.paths.Paths;
import com.ia.beans.Organisation;
import com.ia.core.annotations.Path;
import com.ia.enums.HTTP_METHOD;
import com.ia.enums.MODE;

public abstract class AbstractPathsImpl implements Paths {

    /**
     * Some {@link Path}s don't require signatures for certain {@link Organisation}s.
     */
    private final Table<String, String, Boolean> noSignatureRequiredFor;

    /**
     * Set of all Action {@link Path}s that require verified referers.
     */
    private final Map<String, HTTP_METHOD> pathsToHTTPMethod;

    /**
     * Maps {@link Path}s to the {@link PERMISSION} required to access them.
     */
    private final Map<String, MODE> pathsToPermissions;

    /**
     * Set of all Action {@link Path}s that require a signature.
     */
    private final Set<String> requiresSignature;

    /**
     * Set of all Action {@link Path}s that require verified referers.
     */
    private final Set<String> verifyReferer;

    protected AbstractPathsImpl() {
        super();
        noSignatureRequiredFor = HashBasedTable.create();
        pathsToPermissions = newHashMap();
        requiresSignature = newHashSet();
        verifyReferer = newHashSet();
        pathsToHTTPMethod = newHashMap();
    }

    protected final void exemptSignatureFor(final String path, final String organisation) {
        checkArgument(isNotBlank(path));
        checkArgument(isNotBlank(organisation));
        noSignatureRequiredFor.put(path, organisation, true);
    }

    @Override
    public HTTP_METHOD getMethodExpected(final String path) {
        checkArgument(isNotBlank(path));
        return pathsToHTTPMethod.get(path);
    }

    private boolean isExemptFromSignature(final String path, final String organisation) {
        return noSignatureRequiredFor.contains(path, organisation);
    }

    protected final void mustVerifyReferer(final String path) {
        checkArgument(isNotBlank(path));
        verifyReferer.add(path);
    }

    protected final void requireSignatureFor(final String path) {
        checkArgument(isNotBlank(path));
        requiresSignature.add(path);
    }

    @Override
    public final boolean requiresSignature(final String path) {
        checkArgument(isNotBlank(path));
        return requiresSignature.contains(path);
    }

    @Override
    public final boolean requiresSignature(final String path, final String organisation) {
        checkArgument(isNotBlank(path));
        checkArgument(isNotBlank(organisation));
        /*
         * Return true if (1) this path requires signature, AND (2) this path does NOT exempt this
         * particular organisation from requiring a signature.
         */
        return requiresSignature(path) && !isExemptFromSignature(path, organisation);
    }

    protected final void setHTTPMethod(final String path, final HTTP_METHOD method) {
        checkArgument(isNotBlank(path));
        pathsToHTTPMethod.put(path, method);
    }

    protected final void setPermission(final String path, final MODE permission) {
        checkArgument(isNotBlank(path));
        pathsToPermissions.put(path, permission);
    }

    @Override
    public final boolean verifyMethod(final String path, final HTTP_METHOD method) {
        checkArgument(isNotBlank(path));
        return method == pathsToHTTPMethod.get(path);
    }

    @Override
    public boolean verifyReferer(final String path) {
        checkArgument(isNotBlank(path));
        return verifyReferer.contains(path);
    }
    @Override
    public MODE getPermissionExpected(final String path){
        checkArgument(isNotBlank(path));
        return pathsToPermissions.get(path);
    }

}
