package com.ia.services.impl;

import static com.google.common.base.Optional.fromNullable;
import static com.google.common.collect.HashBiMap.create;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.ia.core.util.IAConstants.DUPLICATE_EXCEPTION;
import static com.ia.core.util.IAConstants.FAILED_TO_CREATE_DATA;
import static com.ia.core.util.IAConstants.FAILED_TO_UPDATE_DATA;
import static com.ia.log.LogUtil.getLogger;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jooq.DSLContext;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.BiMap;
import com.google.common.collect.ListMultimap;
import com.google.inject.Inject;
import com.ia.beans.Organisation;
import com.ia.beans.OrganisationIpAddressRange;
import com.ia.beans.OrganisationMetadata;
import com.ia.beans.impl.OrganisationImpl;
import com.ia.core.util.IAConstants;
import com.ia.crypto.KuberaKeys;
import com.ia.exception.IACommonException;
import com.ia.generated.tables.IaOrganisations;
import com.ia.log.Logger;
import com.ia.services.OrganisationService;
import com.ia.util.KeyReader;

public class OrganisationServiceImpl implements OrganisationService {

    @SuppressWarnings("unused")
    private class GetOrganisationId implements Function<OrganisationMetadata, Integer> {
        @Override
        public Integer apply(final OrganisationMetadata metadata) {
            return metadata.getOrganisationId();
        }
    }

    private static final Object LOCK = new Object();

    private static final String LOG_EXCEPTION_COULD_NOT_CREATE_TRANSACTION =
            "Could not store to database for request=[%s]";

    private static final String LOG_EXCEPTION_COULD_NOT_UPDATE_TRANSACTION =
            "Could not update in database for request=[%s]";

    private static final String LOG_EXCEPTION_COULD_NOT_DELETE_TRANSACTION =
            "Could not delete in database for request=[%s]";

    private static final String LOG_INFO_STARTED = "Started. Cached details of %s organisations.";

    private static final String LOG_INFO_STOPPING = "Stopping. Cleared organisations cache.";

    private final String LOG_EXCEPTION_GET_ENC_KEYS = "Error in getting keys for organisation=%s";

    private final DSLContext dsl;

    private final KeyReader keyReader;

    private final Map<Integer, Organisation> idToOrganisationMap;

    private final Logger logger = getLogger(getClass());

    private final BiMap<String, Integer> nameToIdMap;

    @Inject
    public OrganisationServiceImpl(final DSLContext dsl, final KeyReader keyReader) {
        super();

        this.dsl = dsl;

        this.keyReader = keyReader;

        nameToIdMap = create();

        idToOrganisationMap = newHashMap();
    }

    private void cacheOrganisations() {
        final List<OrganisationImpl> listOfOrganisations = fetchOrganisations();

        /* Fetch OrganisationIpAddressRange grouped by organisationId */
        @SuppressWarnings("unused")
        final ListMultimap<Integer, OrganisationIpAddressRange> mapOfRanges =
                fetchOrganisationIPAddressRanges();

        synchronized (LOCK) {
            clearOrganisationCache();

            for (final OrganisationImpl organisation : listOfOrganisations) {

                final String organisationName = organisation.getOrganisationName();

                final int organisationId = organisation.getOrganisationId();

                /* Set OrganisationIpAddressRanges */
                // final List<OrganisationIpAddressRange> ipAddressRanges =
                // mapOfRanges.get(organisationId);

                // organisation.setIpAddressRanges(ipAddressRanges);

                // organisation.setModes();

                idToOrganisationMap.put(organisationId, organisation);

                nameToIdMap.put(organisationName, organisationId);

                logger.debug("%s", organisation.toString());
            }
        }
    }

    private void clearOrganisationCache() {
        synchronized (LOCK) {
            idToOrganisationMap.clear();
            nameToIdMap.clear();
        }
    }

    @Override
    public Integer createOrganisation(final String name, final String passPhrase,
            final String modes, final String enabled, final String privateKey,
            final String organisationKey) throws IACommonException {
        final Integer orgId = null;
        try {
            // final Record org = dsl.insertInto(KYC_ORGANISATIONS)
            // /* Name */
            // .set(KYC_ORGANISATIONS.ORGANISATION_NAME, name)
            // /* Key */
            // .set(KYC_ORGANISATIONS.KEY, privateKey)
            // /* Modes */
            // .set(KYC_ORGANISATIONS.ENABLED_FOR_TYPE, modes)
            // /* Pass Phrase */
            // .set(KYC_ORGANISATIONS.PASSPHRASE, passPhrase)
            // /* organisationKey */
            // .set(KYC_ORGANISATIONS.ORGANISATION_KEY, organisationKey)
            // /* Enabled */
            // .set(KYC_ORGANISATIONS.ENABLED, Byte.valueOf(enabled))
            // /* Created Time */
            // .set(KYC_ORGANISATIONS.CREATED, currentTimestamp())
            //
            // .returning(KYC_ORGANISATIONS.ORGANISATION_ID).fetchOne();
            //
            // orgId = org.getValue(KYC_ORGANISATIONS.ORGANISATION_ID);

            restart();
        }
        catch (final Exception e) {
            logger.exception(LOG_EXCEPTION_COULD_NOT_CREATE_TRANSACTION, e, name);
            final String rootCauseMsg = getRootCauseMessage(e);
            if (rootCauseMsg.toLowerCase().contains("duplicate")
                    || rootCauseMsg.toLowerCase().contains("constraint")) {
                throw new IACommonException(DUPLICATE_EXCEPTION, e);
            }
            else {
                throw new IACommonException(FAILED_TO_CREATE_DATA, e);
            }
        }
        return orgId;
    }

    @Override
    public Integer createOrganisationIpRange(final Integer organisationId, final String mode,
            final Long startIpRange, final Long endIpRange) throws IACommonException {
        final Integer ipRangeId = null;
        try {
            // final Record org = dsl.insertInto(KYC_IPADDRESS_FOR_ORGANISATION)
            // /* organisationId */
            // .set(KYC_IPADDRESS_FOR_ORGANISATION.ORGANISATION_ID, organisationId)
            // /* mode */
            // .set(KYC_IPADDRESS_FOR_ORGANISATION.MODE, mode)
            // /* startIpRange */
            // .set(KYC_IPADDRESS_FOR_ORGANISATION.IP_ADDRESS_RANGE_START, startIpRange)
            // /* endIpRange */
            // .set(KYC_IPADDRESS_FOR_ORGANISATION.IP_ADDRESS_RANGE_END, endIpRange)
            // /* Created Time */
            // .set(KYC_IPADDRESS_FOR_ORGANISATION.CREATED, currentTimestamp())
            //
            // .returning(KYC_IPADDRESS_FOR_ORGANISATION.ID).fetchOne();
            //
            // ipRangeId = org.getValue(KYC_IPADDRESS_FOR_ORGANISATION.ID);

            /* Reload data */
            restart();
        }
        catch (final Exception e) {
            logger.exception(LOG_EXCEPTION_COULD_NOT_CREATE_TRANSACTION, e, mode);
            final String rootCauseMsg = getRootCauseMessage(e);
            if (rootCauseMsg.toLowerCase().contains("duplicate")
                    || rootCauseMsg.toLowerCase().contains("constraint")) {
                throw new IACommonException(DUPLICATE_EXCEPTION, e);
            }
            else {
                throw new IACommonException(FAILED_TO_CREATE_DATA, e);
            }
        }
        return ipRangeId;
    }

    @Override
    public boolean deleteOrganisationIpRange(final Integer id) throws IACommonException {
        final boolean success = false;
        try {
            // dsl.delete(KYC_IPADDRESS_FOR_ORGANISATION)
            // .where(KYC_IPADDRESS_FOR_ORGANISATION.ID.eq(id)).execute();
            // success = true;
            restart();
        }
        catch (final Exception e) {
            logger.exception(LOG_EXCEPTION_COULD_NOT_DELETE_TRANSACTION, e, id);
            final String rootCauseMsg = ExceptionUtils.getRootCauseMessage(e);
            if (rootCauseMsg.toLowerCase().contains("foreign")
                    || rootCauseMsg.toLowerCase().contains("constraint")) {
                throw new IACommonException(IAConstants.FOREIGN_KEY_EXCEPTION, e);
            }
            else {
                throw new IACommonException(IAConstants.FAILED_TO_UPDATE_DATA, e);
            }
        }
        return success;
    }

    private ListMultimap<Integer, OrganisationIpAddressRange> fetchOrganisationIPAddressRanges() {

        // final List<OrganisationIpAddressRangeImpl> settings = dsl
        //
        // /* i_organisation_ip_addresses */
        // .selectFrom(KYC_IPADDRESS_FOR_ORGANISATION)
        //
        // /* Order by id */
        // .orderBy(KYC_IPADDRESS_FOR_ORGANISATION.ID)
        //
        // /* Into beans */
        // .fetchInto(OrganisationIpAddressRangeImpl.class);
        //
        // final ImplToInterface<OrganisationIpAddressRange, OrganisationIpAddressRangeImpl>
        // transform =
        // new ImplToInterface<OrganisationIpAddressRange, OrganisationIpAddressRangeImpl>();
        //
        // return
        //
        // /* Convert list of *Impl references */
        // from(settings)
        //
        // /* to a list of interface references */
        // .transform(transform)
        //
        // /* Group by organisationId */
        // .index(new GetOrganisationId());

        return null;
    }

    private List<OrganisationImpl> fetchOrganisations() {
        return dsl.selectFrom(IaOrganisations.IA_ORGANISATIONS)
                .orderBy(IaOrganisations.IA_ORGANISATIONS.ORGANISATION_ID)
                .fetchInto(OrganisationImpl.class);

    }

    @Override
    public List<Organisation> getAllOrganisations() {
        synchronized (LOCK) {
            return newArrayList(idToOrganisationMap.values());
        }
    }

    @Override
    public Organisation getOrganisation(final int id) {
        synchronized (LOCK) {
            return idToOrganisationMap.get(id);
        }
    }

    @Override
    public Organisation getOrganisation(final String name) {
        synchronized (LOCK) {
            if (!nameToIdMap.containsKey(name)) {
                return null;
            }
            final int organisationId = nameToIdMap.get(name);
            return idToOrganisationMap.get(organisationId);
        }
    }

    @Override
    public Optional<Pair<String, String>> getPublicKeyModulusAndExponent(
            final Organisation organisation) {
        try {
            final KuberaKeys keys = new KuberaKeys(organisation.getKey());
            return fromNullable(keys.getModulusExponentPair());
        }
        catch (final IOException e) {
            logger.exception(LOG_EXCEPTION_GET_ENC_KEYS, e, organisation.getOrganisationName());
        }
        return null;
    }

    @Override
    public Optional<PrivateKey> getSignatureKey(final Organisation organisation) {
        try {
            final String key = organisation.getKey();
            final Pair<RSAPublicKey, PrivateKey> keyPair = keyReader.readKeyPair(key);
            return fromNullable(keyPair.getRight());
        }
        catch (final IOException e) {
            logger.error(e, "Cannot read key for organisation=%s",
                    organisation.getOrganisationName());
        }

        return fromNullable(null);
    }

    @Override
    public Optional<RSAPublicKey> getSignaturePublicKey(final Organisation organisation) {
        try {
            final String key = organisation.getKey();
            final Pair<RSAPublicKey, PrivateKey> keyPair = keyReader.readKeyPair(key);
            return fromNullable(keyPair.getLeft());
        }
        catch (final IOException e) {
            logger.error(e, "Cannot read key for organisation=%s",
                    organisation.getOrganisationName());
        }

        return fromNullable(null);
    }

    @Override
    public boolean isOrganisationEnabled(final int id) {
        synchronized (LOCK) {
            final Organisation organisation = idToOrganisationMap.get(id);
            return organisation == null ? false : organisation.isEnabled();
        }
    }

    @Override
    public void restart() throws Exception {
        cacheOrganisations();
    }

    @Override
    public void start() throws Exception {
        cacheOrganisations();
        logger.info(LOG_INFO_STARTED, idToOrganisationMap.size());
    }

    @Override
    public void stop() throws Exception {
        clearOrganisationCache();
        logger.info(LOG_INFO_STOPPING);
    }

    @Override
    public boolean updateOrganisation(final Integer id, final String name, final String passPhrase,
            final String modes, final String enabled) throws IACommonException {
        boolean success = false;
        try {
            // dsl.update(KYC_ORGANISATIONS)
            // /* Name */
            // .set(KYC_ORGANISATIONS.ORGANISATION_NAME, name)
            // /* Modes */
            // .set(KYC_ORGANISATIONS.ENABLED_FOR_TYPE, modes)
            // /* Pass Phrase */
            // .set(KYC_ORGANISATIONS.PASSPHRASE, passPhrase)
            // /* Enabled */
            // .set(KYC_ORGANISATIONS.ENABLED, Byte.valueOf(enabled))
            //
            // .where(KYC_ORGANISATIONS.ORGANISATION_ID.eq(id)).execute();

            success = true;

            /* Reload data */
            restart();
        }
        catch (final Exception e) {
            logger.exception(LOG_EXCEPTION_COULD_NOT_UPDATE_TRANSACTION, e, name);
            final String rootCauseMsg = getRootCauseMessage(e);
            if (rootCauseMsg.toLowerCase().contains("duplicate")
                    || rootCauseMsg.toLowerCase().contains("constraint")) {
                throw new IACommonException(DUPLICATE_EXCEPTION, e);
            }
            else {
                throw new IACommonException(FAILED_TO_UPDATE_DATA, e);
            }
        }
        return success;
    }
}
