/**
 * This class is generated by jOOQ
 */
package com.ia.generated;

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
public class Plp extends org.jooq.impl.SchemaImpl {

	private static final long serialVersionUID = -1954263954;

	/**
	 * The reference instance of <code>plp</code>
	 */
	public static final Plp PLP = new Plp();

	/**
	 * No further instances allowed
	 */
	private Plp() {
		super("plp");
	}

	@Override
	public final java.util.List<org.jooq.Table<?>> getTables() {
		java.util.List result = new java.util.ArrayList();
		result.addAll(getTables0());
		return result;
	}

	private final java.util.List<org.jooq.Table<?>> getTables0() {
		return java.util.Arrays.<org.jooq.Table<?>>asList(
			com.ia.generated.tables.IaIpaddressForOrganisation.IA_IPADDRESS_FOR_ORGANISATION,
			com.ia.generated.tables.IaOrganisations.IA_ORGANISATIONS,
			com.ia.generated.tables.IaTransactions.IA_TRANSACTIONS,
			com.ia.generated.tables.IaUser.IA_USER);
	}
}
