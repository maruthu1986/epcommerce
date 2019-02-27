/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.core.messaging.dataimport;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.elasticpath.commons.util.extenum.AbstractExtensibleEnum;
import com.elasticpath.messaging.EventType;
import com.elasticpath.messaging.spi.EventTypeLookup;

/**
 * An enum representing Data Import related {@link EventType}s.
 */
public class DataImportEventType extends AbstractExtensibleEnum<DataImportEventType> implements EventType {

	private static final long serialVersionUID = 4025398581967246417L;

	/** Ordinal constant for IMPORT_JOB_COMPLETED. */
	public static final int IMPORT_JOB_COMPLETED_ORDINAL = 0;

	/**
	 * Signals that an import job has completed.
	 */
	public static final DataImportEventType IMPORT_JOB_COMPLETED = new DataImportEventType(IMPORT_JOB_COMPLETED_ORDINAL, "IMPORT_JOB_COMPLETED");

	/**
	 * Create an enum value for a enum type. Name is case-insensitive and will be stored in upper-case.
	 * 
	 * @param ordinal the ordinal value
	 * @param name the name value (this will be converted to upper-case).
	 */
	protected DataImportEventType(final int ordinal, final String name) {
		super(ordinal, name, DataImportEventType.class);
	}

	@Override
	protected Class<DataImportEventType> getEnumType() {
		return DataImportEventType.class;
	}

	@JsonIgnore
	@Override
	public int getOrdinal() {
		return super.getOrdinal();
	}

	/**
	 * Find the enum value with the specified name.
	 * 
	 * @param name the name
	 * @return the enum value
	 */
	public static DataImportEventType valueOf(final String name) {
		return valueOf(name, DataImportEventType.class);
	}

	/**
	 * DataImportEventType implementation of lookup interface.
	 */
	public static class DataImportEventTypeLookup implements EventTypeLookup<DataImportEventType> {

		@Override
		public DataImportEventType lookup(final String name) {
			try {
				return DataImportEventType.valueOf(name);
			} catch (IllegalArgumentException e) {
				return null;
			}
		}

	}

}
