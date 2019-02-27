package com.elaticpath.selenium.util;

import java.sql.SQLException;

import com.elasticpath.selenium.util.DBConnector;

/**
 * Class for connecting to and querying the database.
 */
public class ExtDBConnector extends DBConnector {

	/**
	 * Check whether a particualar user's status is disabled(0) or enabled(1) in the database.
	 *
	 * @param userName The userName
	 * @return status value
	 * @throws SQLException sql exception
	 */
	public int checkUserStatus(final String userName) throws SQLException {
		int status = 0;
		try {
			setConnection(this.getDBConnection());
			setStatement(getConnection().createStatement());
			setResultSet(getStatement().executeQuery("SELECT STATUS FROM TCMUSER WHERE USER_NAME LIKE '" + userName + "'"));

			while (getResultSet().next()) {
				status = getResultSet().getInt(1);
			}

		} catch (SQLException e) {
			getLOGGER().error(e);
			throw e;
		} finally {
			this.closeAll();
		}
		return status;
	}
}
