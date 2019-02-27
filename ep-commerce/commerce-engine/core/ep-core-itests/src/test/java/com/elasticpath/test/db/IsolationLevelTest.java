/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.test.db;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.SQLException;

import org.apache.openjpa.persistence.jdbc.IsolationLevel;
import org.junit.Test;

/**
 * Verify that the isolation level is set to READ_COMMITTED (rather than the DB default REPEATABLE_READ)
 */
public class IsolationLevelTest extends DbTestCase {

	@Test
	public void isolationLevelShouldBeReadCommitted() {
		try {
			assertThat(getPersistenceEngine().getConnection().getTransactionIsolation())
					.isEqualTo(IsolationLevel.READ_COMMITTED.getConnectionConstant());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
