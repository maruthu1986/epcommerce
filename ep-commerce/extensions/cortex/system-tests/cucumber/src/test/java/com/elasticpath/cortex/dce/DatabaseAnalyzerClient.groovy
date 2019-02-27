package com.elasticpath.cortex.dce

import com.elasticpath.performancetools.queryanalyzer.LogParser
import com.elasticpath.performancetools.queryanalyzer.QueryAnalyzerConfigurator
import com.elasticpath.performancetools.queryanalyzer.beans.QueryStatistics

@Singleton
class DatabaseAnalyzerClient {

	boolean isRunning = false
	QueryStatistics stats

	/**
	 * Starts Database query analyzer. Clears application's ehcache.
	 */
	def synchronized start() {
		if (isRunning) {
			throw new IllegalStateException("Database query analyzer has been already started!")
		}
		stats = null
		QueryAnalyzerConfigurator.INSTANCE
				.enableTraceLogLevelViaJMX()
				.setLogFileFromLogbackConfiguration()
				.prepareLogFile()
				.clearEhCache()
		isRunning = true
	}

	/**
	 * Stops Database query analyzer and generates statistic
	 */
	def synchronized stop() {
		if (!isRunning) {
			throw new IllegalStateException("Database query analyzer is not running!")
		}
		QueryAnalyzerConfigurator configurator = QueryAnalyzerConfigurator.INSTANCE
				.restoreLogLevels()
		LogParser logParser = LogParser.INSTANCE
		QueryStatistics statistics = logParser.parse(configurator.getLogFile())
		logParser.generateStatistics(statistics)
		stats = statistics
		isRunning = false
	}

	/**
	 * Get overall amount of database calls.
	 *
	 * @throws IllegalStateException when called before calling {@link #stop() stop}
	 * @return an amount of db calls as int
	 */
	int getOverallDBCalls() {
		ensureState()
		return stats.getOverallDBCalls() != 0 ? stats.getOverallDBCalls() : -1
	}

	/**
	 * Get amount of database calls for table.
	 *
	 * @param table database table to get amount of db calls for
	 * @throws IllegalStateException when called before calling {@link #stop() stop}
	 * @return an amount of db calls for table as int
	 */
	int getDBCallsByTableName(String table) {
		ensureState()
		return stats.getTotalDBCallsPerTable().getOrDefault(table, -1)
	}

	def ensureState() {
		if (isRunning || stats == null) {
			throw new IllegalStateException("Database query analyzer is running. Stop the query analyzer before calling for stats.")
		}
	}

}
