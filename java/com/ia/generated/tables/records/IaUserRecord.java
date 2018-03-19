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
public class IaUserRecord extends org.jooq.impl.UpdatableRecordImpl<com.ia.generated.tables.records.IaUserRecord> implements org.jooq.Record8<java.lang.Integer, java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, java.sql.Timestamp, java.sql.Timestamp, java.sql.Timestamp> {

	private static final long serialVersionUID = -1114596321;

	/**
	 * Setter for <code>plp.ia_user.user_id</code>.
	 */
	public void setUserId(java.lang.Integer value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>plp.ia_user.user_id</code>.
	 */
	public java.lang.Integer getUserId() {
		return (java.lang.Integer) getValue(0);
	}

	/**
	 * Setter for <code>plp.ia_user.organisation_id</code>.
	 */
	public void setOrganisationId(java.lang.Integer value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>plp.ia_user.organisation_id</code>.
	 */
	public java.lang.Integer getOrganisationId() {
		return (java.lang.Integer) getValue(1);
	}

	/**
	 * Setter for <code>plp.ia_user.email_id</code>.
	 */
	public void setEmailId(java.lang.String value) {
		setValue(2, value);
	}

	/**
	 * Getter for <code>plp.ia_user.email_id</code>.
	 */
	public java.lang.String getEmailId() {
		return (java.lang.String) getValue(2);
	}

	/**
	 * Setter for <code>plp.ia_user.password</code>.
	 */
	public void setPassword(java.lang.String value) {
		setValue(3, value);
	}

	/**
	 * Getter for <code>plp.ia_user.password</code>.
	 */
	public java.lang.String getPassword() {
		return (java.lang.String) getValue(3);
	}

	/**
	 * Setter for <code>plp.ia_user.status</code>.
	 */
	public void setStatus(java.lang.String value) {
		setValue(4, value);
	}

	/**
	 * Getter for <code>plp.ia_user.status</code>.
	 */
	public java.lang.String getStatus() {
		return (java.lang.String) getValue(4);
	}

	/**
	 * Setter for <code>plp.ia_user.updated</code>.
	 */
	public void setUpdated(java.sql.Timestamp value) {
		setValue(5, value);
	}

	/**
	 * Getter for <code>plp.ia_user.updated</code>.
	 */
	public java.sql.Timestamp getUpdated() {
		return (java.sql.Timestamp) getValue(5);
	}

	/**
	 * Setter for <code>plp.ia_user.created</code>.
	 */
	public void setCreated(java.sql.Timestamp value) {
		setValue(6, value);
	}

	/**
	 * Getter for <code>plp.ia_user.created</code>.
	 */
	public java.sql.Timestamp getCreated() {
		return (java.sql.Timestamp) getValue(6);
	}

	/**
	 * Setter for <code>plp.ia_user.last_login</code>.
	 */
	public void setLastLogin(java.sql.Timestamp value) {
		setValue(7, value);
	}

	/**
	 * Getter for <code>plp.ia_user.last_login</code>.
	 */
	public java.sql.Timestamp getLastLogin() {
		return (java.sql.Timestamp) getValue(7);
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
	public org.jooq.Row8<java.lang.Integer, java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, java.sql.Timestamp, java.sql.Timestamp, java.sql.Timestamp> fieldsRow() {
		return (org.jooq.Row8) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row8<java.lang.Integer, java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, java.sql.Timestamp, java.sql.Timestamp, java.sql.Timestamp> valuesRow() {
		return (org.jooq.Row8) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Integer> field1() {
		return com.ia.generated.tables.IaUser.IA_USER.USER_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Integer> field2() {
		return com.ia.generated.tables.IaUser.IA_USER.ORGANISATION_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.String> field3() {
		return com.ia.generated.tables.IaUser.IA_USER.EMAIL_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.String> field4() {
		return com.ia.generated.tables.IaUser.IA_USER.PASSWORD;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.String> field5() {
		return com.ia.generated.tables.IaUser.IA_USER.STATUS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.sql.Timestamp> field6() {
		return com.ia.generated.tables.IaUser.IA_USER.UPDATED;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.sql.Timestamp> field7() {
		return com.ia.generated.tables.IaUser.IA_USER.CREATED;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.sql.Timestamp> field8() {
		return com.ia.generated.tables.IaUser.IA_USER.LAST_LOGIN;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.Integer value1() {
		return getUserId();
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
	public java.lang.String value3() {
		return getEmailId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.String value4() {
		return getPassword();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.String value5() {
		return getStatus();
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
	public java.sql.Timestamp value8() {
		return getLastLogin();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IaUserRecord value1(java.lang.Integer value) {
		setUserId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IaUserRecord value2(java.lang.Integer value) {
		setOrganisationId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IaUserRecord value3(java.lang.String value) {
		setEmailId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IaUserRecord value4(java.lang.String value) {
		setPassword(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IaUserRecord value5(java.lang.String value) {
		setStatus(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IaUserRecord value6(java.sql.Timestamp value) {
		setUpdated(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IaUserRecord value7(java.sql.Timestamp value) {
		setCreated(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IaUserRecord value8(java.sql.Timestamp value) {
		setLastLogin(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IaUserRecord values(java.lang.Integer value1, java.lang.Integer value2, java.lang.String value3, java.lang.String value4, java.lang.String value5, java.sql.Timestamp value6, java.sql.Timestamp value7, java.sql.Timestamp value8) {
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached IaUserRecord
	 */
	public IaUserRecord() {
		super(com.ia.generated.tables.IaUser.IA_USER);
	}

	/**
	 * Create a detached, initialised IaUserRecord
	 */
	public IaUserRecord(java.lang.Integer userId, java.lang.Integer organisationId, java.lang.String emailId, java.lang.String password, java.lang.String status, java.sql.Timestamp updated, java.sql.Timestamp created, java.sql.Timestamp lastLogin) {
		super(com.ia.generated.tables.IaUser.IA_USER);

		setValue(0, userId);
		setValue(1, organisationId);
		setValue(2, emailId);
		setValue(3, password);
		setValue(4, status);
		setValue(5, updated);
		setValue(6, created);
		setValue(7, lastLogin);
	}
}