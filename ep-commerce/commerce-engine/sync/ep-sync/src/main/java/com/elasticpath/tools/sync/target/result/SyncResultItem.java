/*
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.tools.sync.target.result;

import com.elasticpath.tools.sync.job.Command;

/**
 * Encapsulates information about transaction job unit synchronisation.  
 */
public class SyncResultItem {
	
	private Class<?> jobEntryType;
	private String transactionJobUnitName;
	private String jobEntryGuid;
	private Command jobEntryCommand;
	
	/**
	 * @return the transactionJobUnitName
	 */
	public String getTransactionJobUnitName() {
		return transactionJobUnitName;
	}
	/**
	 * @param transactionJobUnitName the transactionJobUnitName to set
	 */
	public void setTransactionJobUnitName(final String transactionJobUnitName) {
		this.transactionJobUnitName = transactionJobUnitName;
	}
	
	/**
	 * @return the jobEntryGuid
	 */
	public String getJobEntryGuid() {
		return jobEntryGuid;
	}
	/**
	 * @param jobEntryGuid the jobEntryGuid to set
	 */
	public void setJobEntryGuid(final String jobEntryGuid) {
		this.jobEntryGuid = jobEntryGuid;
	}
	/**
	 * @return the jobEntryCommand
	 */
	public Command getJobEntryCommand() {
		return jobEntryCommand;
	}
	/**
	 * @param jobEntryCommand the jobEntryCommand to set
	 */
	public void setJobEntryCommand(final Command jobEntryCommand) {
		this.jobEntryCommand = jobEntryCommand;
	}
	/**
	 * @return the jobEntryType
	 */
	public Class<?> getJobEntryType() {
		return jobEntryType;
	}
	/**
	 * @param jobEntryType the jobEntryType to set
	 */
	public void setJobEntryType(final Class<?> jobEntryType) {
		this.jobEntryType = jobEntryType;
	}
	
	/**
	 * @return human-readable error
	 */
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("\nTransaction job unit: ").append(getName());
		if (jobEntryGuid != null) {			
			stringBuilder.append("\nTransaction job entry guid: ").append(jobEntryGuid);
			stringBuilder.append("\nTransaction job entry type: ").append(jobEntryType);
			stringBuilder.append("\nTransaction job entry command: ").append(jobEntryCommand);
		}
		return stringBuilder.toString();
	}
	
	private String getName() {
		if (transactionJobUnitName == null) {
			return "<Not Specified>";
		}
		return transactionJobUnitName;
	}
	
}
