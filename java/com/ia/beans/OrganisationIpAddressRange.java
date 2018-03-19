package com.ia.beans;

import com.ia.enums.MODE;

public interface OrganisationIpAddressRange extends OrganisationMetadata {
    boolean contains(String remoteAddress);

    int getId();

    long getIpAddressRangeEnd();

    long getIpAddressRangeStart();

    MODE getMode();
}
