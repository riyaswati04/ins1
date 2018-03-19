package com.ia.beans;

import com.ia.enums.MODE;

public interface Organisation {

    String getKey();

    int getOrganisationId();

    String getOrganisationKey();

    String getOrganisationName();

    String getPassphrase();

    boolean isEnabled();

    boolean isExpectedRemoteAddress(final MODE mode, final String remoteAddress);

    boolean isModeEnabled(MODE mode);

    void setEnabled(boolean enabled);

    void setPubKey(String pubKey);

}
