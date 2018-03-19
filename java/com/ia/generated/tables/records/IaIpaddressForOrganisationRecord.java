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
public class IaIpaddressForOrganisationRecord extends org.jooq.impl.UpdatableRecordImpl<com.ia.generated.tables.records.IaIpaddressForOrganisationRecord> implements org.jooq.Record4<java.lang.Integer, java.lang.Integer, java.lang.Long, java.lang.Long> {

	private static final long serialVersionUID = -44298240;

	/**
	 * Setter for <code>plp.ia_ipaddress_for_organisation.id</code>.
	 */
	public void setId(java.lang.Integer value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>plp.ia_ipaddress_for_organisation.id</code>.
	 */
	public java.lang.Integer getId() {
		return (java.lang.Integer) getValue(0);
	}

	/**
	 * Setter for <code>plp.ia_ipaddress_for_organisation.organisation_id</code>.
	 */
	public void setOrganisationId(java.lang.Integer value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>plp.ia_ipaddress_for_organisation.organisation_id</code>.
	 */
	public java.lang.Integer getOrganisationId() {
		return (java.lang.Integer) getValue(1);
	}

	/**
	 * Setter for <code>plp.ia_ipaddress_for_organisation.ip_address_range_start</code>.
	 */
	public void setIpAddressRangeStart(java.lang.Long value) {
		setValue(2, value);
	}

	/**
	 * Getter for <code>plp.ia_ipaddress_for_organisation.ip_address_range_start</code>.
	 */
	public java.lang.Long getIpAddressRangeStart() {
		return (java.lang.Long) getValue(2);
	}

	/**
	 * Setter for <code>plp.ia_ipaddress_for_organisation.ip_address_range_end</code>.
	 */
	public void setIpAddressRangeEnd(java.lang.Long value) {
		setValue(3, value);
	}

	/**
	 * Getter for <code>plp.ia_ipaddress_for_organisation.ip_address_range_end</code>.
	 */
	public java.lang.Long getIpAddressRangeEnd() {
		return (java.lang.Long) getValue(3);
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
	// Record4 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row4<java.lang.Integer, java.lang.Integer, java.lang.Long, java.lang.Long> fieldsRow() {
		return (org.jooq.Row4) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row4<java.lang.Integer, java.lang.Integer, java.lang.Long, java.lang.Long> valuesRow() {
		return (org.jooq.Row4) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Integer> field1() {
		return com.ia.generated.tables.IaIpaddressForOrganisation.IA_IPADDRESS_FOR_ORGANISATION.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Integer> field2() {
		return com.ia.generated.tables.IaIpaddressForOrganisation.IA_IPADDRESS_FOR_ORGANISATION.ORGANISATION_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Long> field3() {
		return com.ia.generated.tables.IaIpaddressForOrganisation.IA_IPADDRESS_FOR_ORGANISATION.IP_ADDRESS_RANGE_START;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Long> field4() {
		return com.ia.generated.tables.IaIpaddressForOrganisation.IA_IPADDRESS_FOR_ORGANISATION.IP_ADDRESS_RANGE_END;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.Integer value1() {
		return getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.Integer value2() {
		return getOrganisationId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.Long value3() {
		return getIpAddressRangeStart();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.Long value4() {
		return getIpAddressRangeEnd();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IaIpaddressForOrganisationRecord value1(java.lang.Integer value) {
		setId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IaIpaddressForOrganisationRecord value2(java.lang.Integer value) {
		setOrganisationId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IaIpaddressForOrganisationRecord value3(java.lang.Long value) {
		setIpAddressRangeStart(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IaIpaddressForOrganisationRecord value4(java.lang.Long value) {
		setIpAddressRangeEnd(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IaIpaddressForOrganisationRecord values(java.lang.Integer value1, java.lang.Integer value2, java.lang.Long value3, java.lang.Long value4) {
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached IaIpaddressForOrganisationRecord
	 */
	public IaIpaddressForOrganisationRecord() {
		super(com.ia.generated.tables.IaIpaddressForOrganisation.IA_IPADDRESS_FOR_ORGANISATION);
	}

	/**
	 * Create a detached, initialised IaIpaddressForOrganisationRecord
	 */
	public IaIpaddressForOrganisationRecord(java.lang.Integer id, java.lang.Integer organisationId, java.lang.Long ipAddressRangeStart, java.lang.Long ipAddressRangeEnd) {
		super(com.ia.generated.tables.IaIpaddressForOrganisation.IA_IPADDRESS_FOR_ORGANISATION);

		setValue(0, id);
		setValue(1, organisationId);
		setValue(2, ipAddressRangeStart);
		setValue(3, ipAddressRangeEnd);
	}
}