package com.ia.beans.impl;

import static com.ia.util.http.HttpUtil.convertRemoteAddressToLong;

import com.ia.beans.OrganisationIpAddressRange;
import com.ia.enums.MODE;

public class OrganisationIpAddressRangeImpl implements OrganisationIpAddressRange {

    private int id;;

    private long ipAddressRangeEnd;

    private long ipAddressRangeStart;

    private MODE mode;

    private int organisationId;

    @Override
    public boolean contains(final String remoteAddress) {
        try {
            final long ipAddress = convertRemoteAddressToLong(remoteAddress);
            return ipAddressRangeStart <= ipAddress && ipAddress <= ipAddressRangeEnd;
        }
        catch (final Exception ignore) {
            return false;
        }
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public long getIpAddressRangeEnd() {
        return ipAddressRangeEnd;
    }

    @Override
    public long getIpAddressRangeStart() {
        return ipAddressRangeStart;
    }

    @Override
    public MODE getMode() {
        return mode;
    }

    @Override
    public int getOrganisationId() {
        return organisationId;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public void setIpAddressRangeEnd(final long ipAddressRangeEnd) {
        this.ipAddressRangeEnd = ipAddressRangeEnd;
    }

    public void setIpAddressRangeStart(final long ipAddressRangeStart) {
        this.ipAddressRangeStart = ipAddressRangeStart;
    }

    public void setMode(final MODE mode) {
        this.mode = mode;
    }

    public void setOrganisationId(final int organisationId) {
        this.organisationId = organisationId;
    }
}
