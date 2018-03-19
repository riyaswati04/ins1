package com.ia.services;

import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Optional;
import com.ia.beans.Organisation;
import com.ia.core.services.Service;
import com.ia.exception.IACommonException;

public interface OrganisationService extends Service {

    Integer createOrganisation(String name, String passPhrase, String modes, String enabled,
            String privateKey, String organisationKey) throws IACommonException;

    Integer createOrganisationIpRange(Integer organisationId, String mode, Long startIpRange,
            Long endIpRange) throws IACommonException;

    boolean deleteOrganisationIpRange(Integer id) throws IACommonException;

    List<Organisation> getAllOrganisations();

    Organisation getOrganisation(int id);

    Organisation getOrganisation(String name);

    Optional<Pair<String, String>> getPublicKeyModulusAndExponent(final Organisation organisation);

    Optional<PrivateKey> getSignatureKey(Organisation organisation);

    Optional<RSAPublicKey> getSignaturePublicKey(Organisation organisation);

    boolean isOrganisationEnabled(int id);

    boolean updateOrganisation(Integer id, String name, String passPhrase, String modes,
            String enabled) throws IACommonException;
}
