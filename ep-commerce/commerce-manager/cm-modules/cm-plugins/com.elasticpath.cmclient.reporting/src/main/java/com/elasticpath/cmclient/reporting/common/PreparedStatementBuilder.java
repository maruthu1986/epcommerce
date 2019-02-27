/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.common;

import java.util.HashSet;
import java.util.Set;

import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.store.Store;
import com.elasticpath.persistence.openjpa.support.JpqlQueryBuilderWhereGroup;

/**
 * Encapsulates a prepared statement builder.
 * Provides a general setWhereClause method as a starting point that can be reused 
 * in the various reports.
 */
public class PreparedStatementBuilder {
	
	/** field name for created date with the order return table alias. */
	public static final String ORR_CREATED_DATE = "orr.createdDate"; //$NON-NLS-1$
	
	/** field name for created date with the order table alias. */
	public static final String O_CREATED_DATE = "o.createdDate"; //$NON-NLS-1$
	
	/** field name for transaction date with the tax journal table alias. */
	public static final String T_TRANSACTION_DATE = "t.transactionDate"; //$NON-NLS-1$
	
	/** field name for OrderImpl storeCode alias. */
	public static final String O_STORE_CODE = "o.storeCode"; //$NON-NLS-1$
	
	/** field name for StoreImpl storeCode alias. */
	public static final String STORE_CODE = "store.code"; //$NON-NLS-1$
	
	private ReportParameters parameters;
	
	/**
	 * Sets the general where clause of the query builder. All report filtering criteria
	 * will have:
	 * 
	 * Filter by Starting and Ending Time
	 * Filter by Store
	 * 
	 * Separate reports can override this method and add more parameter criteria
	 *
	 * @param whereGroup the where group of a query builder
	 * @param dateField the field for the starting and ending date
	 * @param storeField the field for the store code
	 */
	@SuppressWarnings("nls")
	protected void setWhereClauseAndParameters(final JpqlQueryBuilderWhereGroup whereGroup,
			final String dateField, final String storeField) {
		if (parameters != null) {
			whereGroup.appendWhere(dateField, ">=", parameters.getStartDate(), JpqlQueryBuilderWhereGroup.JpqlMatchType.AS_IS);
			whereGroup.appendWhere(dateField, "<=", parameters.getEndDate(), JpqlQueryBuilderWhereGroup.JpqlMatchType.AS_IS); 
			if (parameters.getStore() == null) {   // Search all stores
				CmUser user = getCurrentUser();
				
				if (hasLimitedPermissions(user)) { 
					Set<Store> allowedStores = user.getStores();
					Set<String> allowedStoreCodes = new HashSet<String>();
					for (Store store : allowedStores) {
						allowedStoreCodes.add(store.getCode());
					}
					whereGroup.appendWhereInCollection(storeField, allowedStoreCodes);
				}
			} else {
				whereGroup.appendWhereEquals(storeField, parameters.getStore());
			}
		}
	}
	
	/**
	 * super user ignores all permission restrictions.
	 *
	 * @param user user
	 * @return true if the user has limited permissions
	 */
	private boolean hasLimitedPermissions(final CmUser user) {
		return !user.isSuperUser() && !user.isAllStoresAccess();
	}
	/**
	 * Returns the current CM User.
	 *
	 * @return CM user
	 */
	public CmUser getCurrentUser() {
		return LoginManager.getCmUser();
	}
	
	/**
	 * Gets the parameter for the builder.
	 *
	 * @return parameters
	 */
	public ReportParameters getParameters() {
		return parameters;
	}
	
	/**
	 * Sets the parameter for the builder.
	 *
	 * @param parameters the parameters for the builder
	 */
	public void setParameters(final ReportParameters parameters) {
		this.parameters = parameters;
	}
}
