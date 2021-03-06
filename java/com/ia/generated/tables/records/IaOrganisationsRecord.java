/**
 * This class is generated by jOOQ
 */
package com.ia.generated.tables.records;

/**
 * This class is generated by jOOQ.
 */
@javax.annotation.Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.5.0"
	},
	comments = "This class is generated by jOOQ"
)
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class IaOrganisationsRecord extends org.jooq.impl.UpdatableRecordImpl<com.ia.generated.tables.records.IaOrganisationsRecord> implements org.jooq.Record8<java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, java.lang.Byte, java.sql.Timestamp, java.sql.Timestamp, java.sql.Date> {

	private static final long serialVersionUID = 1915345186;

	/**
	 * Setter for <code>plp.ia_organisations.organisation_id</code>.
	 */
	public void setOrganisationId(java.lang.Integer value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>plp.ia_organisations.organisation_id</code>.
	 */
	public java.lang.Integer getOrganisationId() {
		return (java.lang.Integer) getValue(0);
	}

	/**
	 * Setter for <code>plp.ia_organisations.organisation_name</code>.
	 */
	public void setOrganisationName(java.lang.String value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>plp.ia_organisations.organisation_name</code>.
	 */
	public java.lang.String getOrganisationName() {
		return (java.lang.String) getValue(1);
	}

	/**
	 * Setter for <code>plp.ia_organisations.key</code>.
	 */
	public void setKey(java.lang.String value) {
		setValue(2, value);
	}

	/**
	 * Getter for <code>plp.ia_organisations.key</code>.
	 */
	public java.lang.String getKey() {
		return (java.lang.String) getValue(2);
	}

	/**
	 * Setter for <code>plp.ia_organisations.insights_key</code>.
	 */
	public void setInsightsKey(java.lang.String value) {
		setValue(3, value);
	}

	/**
	 * Getter for <code>plp.ia_organisations.insights_key</code>.
	 */
	public java.lang.String getInsightsKey() {
		return (java.lang.String) getValue(3);
	}

	/**
	 * Setter for <code>plp.ia_organisations.enabled</code>.
	 */
	public void setEnabled(java.lang.Byte value) {
		setValue(4, value);
	}

	/**
	 * Getter for <code>plp.ia_organisations.enabled</code>.
	 */
	public java.lang.Byte getEnabled() {
		return (java.lang.Byte) getValue(4);
	}

	/**
	 * Setter for <code>plp.ia_organisations.updated</code>.
	 */
	public void setUpdated(java.sql.Timestamp value) {
		setValue(5, value);
	}

	/**
	 * Getter for <code>plp.ia_organisations.updated</code>.
	 */
	public java.sql.Timestamp getUpdated() {
		return (java.sql.Timestamp) getValue(5);
	}

	/**
	 * Setter for <code>plp.ia_organisations.created</code>.
	 */
	public void setCreated(java.sql.Timestamp value) {
		setValue(6, value);
	}

	/**
	 * Getter for <code>plp.ia_organisations.created</code>.
	 */
	public java.sql.Timestamp getCreated() {
		return (java.sql.Timestamp) getValue(6);
	}

	/**
	 * Setter for <code>plp.ia_organisations.license_end_date</code>.
	 */
	public void setLicenseEndDate(java.sql.Date value) {
		setValue(7, value);
	}

	/**
	 * Getter for <code>plp.ia_organisations.license_end_date</code>.
	 */
	public java.sql.Date getLicenseEndDate() {
		return (java.sql.Date) getValue(7);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Record1<java.lang.Integer> key() {
		return (org.jooq.Record1) super.key();
	}

	// -------------------------------------------------------------------------
	// Record8 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row8<java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, java.lang.Byte, java.sql.Timestamp, java.sql.Timestamp, java.sql.Date> fieldsRow() {
		return (org.jooq.Row8) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row8<java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, java.lang.Byte, java.sql.Timestamp, java.sql.Timestamp, java.sql.Date> valuesRow() {
		return (org.jooq.Row8) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Integer> field1() {
		return com.ia.generated.tables.IaOrganisations.IA_ORGANISATIONS.ORGANISATION_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.String> field2() {
		return com.ia.generated.tables.IaOrganisations.IA_ORGANISATIONS.ORGANISATION_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.String> field3() {
		return com.ia.generated.tables.IaOrganisations.IA_ORGANISATIONS.KEY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.String> field4() {
		return com.ia.generated.tables.IaOrganisations.IA_ORGANISATIONS.INSIGHTS_KEY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Byte> field5() {
		return com.ia.generated.tables.IaOrganisations.IA_ORGANISATIONS.ENABLED;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.sql.Timestamp> field6() {
		return com.ia.generated.tables.IaOrganisations.IA_ORGANISATIONS.UPDATED;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.sql.Timestamp> field7() {
		return com.ia.generated.tables.IaOrganisations.IA_ORGANISATIONS.CREATED;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.sql.Date> field8() {
		return com.ia.generated.tables.IaOrganisations.IA_ORGANISATIONS.LICENSE_END_DATE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.Integer value1() {
		return getOrganisationId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.String value2() {
		return getOrganisationName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.String value3() {
		return getKey();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.String value4() {
		return getInsightsKey();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.Byte value5() {
		return getEnabled();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.sql.Timestamp value6() {
		return getUpdated();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.sql.Timestamp value7() {
		return getCreated();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.sql.Date value8() {
		return getLicenseEndDate();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IaOrganisationsRecord value1(java.lang.Integer value) {
		setOrganisationId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IaOrganisationsRecord value2(java.lang.String value) {
		setOrganisationName(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IaOrganisationsRecord value3(java.lang.String value) {
		setKey(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IaOrganisationsRecord value4(java.lang.String value) {
		setInsightsKey(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IaOrganisationsRecord value5(java.lang.Byte value) {
		setEnabled(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IaOrganisationsRecord value6(java.sql.Timestamp value) {
		setUpdated(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IaOrganisationsRecord value7(java.sql.Timestamp value) {
		setCreated(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IaOrganisationsRecord value8(java.sql.Date value) {
		setLicenseEndDate(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IaOrganisationsRecord values(java.lang.Integer value1, java.lang.String value2, java.lang.String value3, java.lang.String value4, java.lang.Byte value5, java.sql.Timestamp value6, java.sql.Timestamp value7, java.sql.Date value8) {
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached IaOrganisationsRecord
	 */
	public IaOrganisationsRecord() {
		super(com.ia.generated.tables.IaOrganisations.IA_ORGANISATIONS);
	}

	/**
	 * Create a detached, initialised IaOrganisationsRecord
	 */
	public IaOrganisationsRecord(java.lang.Integer organisationId, java.lang.String organisationName, java.lang.String key, java.lang.String insightsKey, java.lang.Byte enabled, java.sql.Timestamp updated, java.sql.Timestamp created, java.sql.Date licenseEndDate) {
		super(com.ia.generated.tables.IaOrganisations.IA_ORGANISATIONS);

		setValue(0, organisationId);
		setValue(1, organisationName);
		setValue(2, key);
		setValue(3, insightsKey);
		setValue(4, enabled);
		setValue(5, updated);
		setValue(6, created);
		setValue(7, licenseEndDate);
	}
}
