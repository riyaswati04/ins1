/**
 * This class is generated by jOOQ
 */
package com.ia.generated.tables;

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
public class IaIpaddressForOrganisation extends org.jooq.impl.TableImpl<com.ia.generated.tables.records.IaIpaddressForOrganisationRecord> {

	private static final long serialVersionUID = 677765164;

	/**
	 * The reference instance of <code>plp.ia_ipaddress_for_organisation</code>
	 */
	public static final com.ia.generated.tables.IaIpaddressForOrganisation IA_IPADDRESS_FOR_ORGANISATION = new com.ia.generated.tables.IaIpaddressForOrganisation();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<com.ia.generated.tables.records.IaIpaddressForOrganisationRecord> getRecordType() {
		return com.ia.generated.tables.records.IaIpaddressForOrganisationRecord.class;
	}

	/**
	 * The column <code>plp.ia_ipaddress_for_organisation.id</code>.
	 */
	public final org.jooq.TableField<com.ia.generated.tables.records.IaIpaddressForOrganisationRecord, java.lang.Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>plp.ia_ipaddress_for_organisation.organisation_id</code>.
	 */
	public final org.jooq.TableField<com.ia.generated.tables.records.IaIpaddressForOrganisationRecord, java.lang.Integer> ORGANISATION_ID = createField("organisation_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>plp.ia_ipaddress_for_organisation.ip_address_range_start</code>.
	 */
	public final org.jooq.TableField<com.ia.generated.tables.records.IaIpaddressForOrganisationRecord, java.lang.Long> IP_ADDRESS_RANGE_START = createField("ip_address_range_start", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

	/**
	 * The column <code>plp.ia_ipaddress_for_organisation.ip_address_range_end</code>.
	 */
	public final org.jooq.TableField<com.ia.generated.tables.records.IaIpaddressForOrganisationRecord, java.lang.Long> IP_ADDRESS_RANGE_END = createField("ip_address_range_end", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

	/**
	 * Create a <code>plp.ia_ipaddress_for_organisation</code> table reference
	 */
	public IaIpaddressForOrganisation() {
		this("ia_ipaddress_for_organisation", null);
	}

	/**
	 * Create an aliased <code>plp.ia_ipaddress_for_organisation</code> table reference
	 */
	public IaIpaddressForOrganisation(java.lang.String alias) {
		this(alias, com.ia.generated.tables.IaIpaddressForOrganisation.IA_IPADDRESS_FOR_ORGANISATION);
	}

	private IaIpaddressForOrganisation(java.lang.String alias, org.jooq.Table<com.ia.generated.tables.records.IaIpaddressForOrganisationRecord> aliased) {
		this(alias, aliased, null);
	}

	private IaIpaddressForOrganisation(java.lang.String alias, org.jooq.Table<com.ia.generated.tables.records.IaIpaddressForOrganisationRecord> aliased, org.jooq.Field<?>[] parameters) {
		super(alias, com.ia.generated.Plp.PLP, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Identity<com.ia.generated.tables.records.IaIpaddressForOrganisationRecord, java.lang.Integer> getIdentity() {
		return com.ia.generated.Keys.IDENTITY_IA_IPADDRESS_FOR_ORGANISATION;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.UniqueKey<com.ia.generated.tables.records.IaIpaddressForOrganisationRecord> getPrimaryKey() {
		return com.ia.generated.Keys.KEY_IA_IPADDRESS_FOR_ORGANISATION_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.List<org.jooq.UniqueKey<com.ia.generated.tables.records.IaIpaddressForOrganisationRecord>> getKeys() {
		return java.util.Arrays.<org.jooq.UniqueKey<com.ia.generated.tables.records.IaIpaddressForOrganisationRecord>>asList(com.ia.generated.Keys.KEY_IA_IPADDRESS_FOR_ORGANISATION_PRIMARY, com.ia.generated.Keys.KEY_IA_IPADDRESS_FOR_ORGANISATION_IA_ORGANISATION_IP_ADDRESSES_1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.List<org.jooq.ForeignKey<com.ia.generated.tables.records.IaIpaddressForOrganisationRecord, ?>> getReferences() {
		return java.util.Arrays.<org.jooq.ForeignKey<com.ia.generated.tables.records.IaIpaddressForOrganisationRecord, ?>>asList(com.ia.generated.Keys.IA_IPADDRESS_FOR_ORGANISATION_KYC_ORGANISATIONS);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public com.ia.generated.tables.IaIpaddressForOrganisation as(java.lang.String alias) {
		return new com.ia.generated.tables.IaIpaddressForOrganisation(alias, this);
	}

	/**
	 * Rename this table
	 */
	public com.ia.generated.tables.IaIpaddressForOrganisation rename(java.lang.String name) {
		return new com.ia.generated.tables.IaIpaddressForOrganisation(name, null);
	}
}
