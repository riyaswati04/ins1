package com.ia.beans.impl;

import static com.google.common.collect.Iterables.any;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import com.google.common.base.Predicate;
import com.ia.beans.Organisation;
import com.ia.beans.OrganisationIpAddressRange;
import com.ia.enums.MODE;

public final class OrganisationImpl implements Organisation {

    private final class MatchModeAndRemoteAddress implements Predicate<OrganisationIpAddressRange> {
        private final MODE mode;

        private final String remoteAddress;

        private MatchModeAndRemoteAddress(final String remoteAddress, final MODE mode) {
            this.remoteAddress = remoteAddress;
            this.mode = mode;
        }

        @Override
        public boolean apply(final OrganisationIpAddressRange range) {
            return range.getMode().equals(mode) && range.contains(remoteAddress);

        }
    }

    private boolean enabled;

    private String enabledForType;

    private Set<MODE> enabledModes;

    private List<OrganisationIpAddressRange> ipAddressRanges;

    private String key;

    private int organisationId;

    private String organisationName;

    private String passphrase;

    private String pubKey;

    private String organisationKey;

    public String getEnabledForType() {
        return enabledForType;
    }

    public Set<MODE> getEnabledModes() {
        return enabledModes;
    }

    public List<OrganisationIpAddressRange> getIpAddressRanges() {
        return ipAddressRanges;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public int getOrganisationId() {
        return organisationId;
    }

    @Override
    public String getOrganisationKey() {
        return organisationKey;
    }

    @Override
    public String getOrganisationName() {
        return organisationName;
    }

    @Override
    public String getPassphrase() {
        return passphrase;
    }

    public String getPubKey() {
        return pubKey;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isExpectedRemoteAddress(final MODE mode, final String remoteAddress) {
        return any(ipAddressRanges, new MatchModeAndRemoteAddress(remoteAddress, mode));
    }

    @Override
    public boolean isModeEnabled(final MODE mode) {
        if (enabledModes != null && mode != null) {
            return enabledModes.contains(mode);
        }
        return true;
    }

    @Override
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public void setEnabledForType(final String enabledForType) {
        this.enabledForType = enabledForType;
    }

    public void setEnabledModes(final Set<MODE> enabledModes) {
        this.enabledModes = enabledModes;
    }

    public void setIpAddressRanges(final List<OrganisationIpAddressRange> ipAddressRanges) {
        this.ipAddressRanges = ipAddressRanges;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public void setModes() {
        final StringTokenizer st = new StringTokenizer(enabledForType, ",");
        enabledModes = new HashSet<MODE>();
        while (st.hasMoreTokens()) {
            final MODE mode = MODE.valueOf(st.nextToken());
            enabledModes.add(mode);
        }
    }

    public void setOrganisationId(final int organisationId) {
        this.organisationId = organisationId;
    }

    public void setOrganisationKey(final String organisationKey) {
        this.organisationKey = organisationKey;
    }

    public void setOrganisationName(final String organisationName) {
        this.organisationName = organisationName;
    }

    public void setPassphrase(final String passphrase) {
        this.passphrase = passphrase;
    }

    @Override
    public void setPubKey(final String pubKey) {
        this.pubKey = pubKey;
    }

    @Override
    public String toString() {
        return "OrganisationImpl [enabled=" + enabled + ", enabledForType=" + enabledForType
                + ", enabledModes=" + enabledModes + ", ipAddressRanges=" + ipAddressRanges
                + ", organisationId=" + organisationId + ", organisationName=" + organisationName
                + "]";
    }

}
