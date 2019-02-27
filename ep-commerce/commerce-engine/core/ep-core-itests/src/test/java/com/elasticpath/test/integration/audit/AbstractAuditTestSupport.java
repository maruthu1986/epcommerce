/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.test.integration.audit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.persistence.EntityManager;

import org.apache.log4j.Logger;
import org.apache.openjpa.enhance.Reflection;
import org.apache.openjpa.meta.ClassMetaData;
import org.apache.openjpa.meta.FieldMetaData;
import org.apache.openjpa.persistence.JPAFacadeHelper;
import org.junit.After;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import com.elasticpath.domain.audit.ChangeOperation;
import com.elasticpath.domain.audit.DataChanged;
import com.elasticpath.persistence.api.ChangeType;
import com.elasticpath.persistence.api.Entity;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.test.integration.BasicSpringContextTest;

/**
 * An abstract superclass for audit tests.
 */
@SuppressWarnings("PMD.GodClass")
@ContextConfiguration(locations = {"classpath:integration-audit-context.xml"}, inheritLocations = true)
public abstract class AbstractAuditTestSupport extends BasicSpringContextTest {

	private static final Logger LOG = Logger.getLogger(AbstractAuditTestSupport.class);

	private static final long ONE_MINUTE = 60000;

	private DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private PersistenceEngine persistenceEngine;

	/**
	 * count field to record how many data changed records are verified.
	 */
	private int dataChangedRecordCount;

	/**
	 * The JPQL to get the change operation records.
	 */
	protected static final String FIND_CHANGE_OPERATION_BY_GUID_AND_TYPE =
			"SELECT o FROM SingleChangeOperationImpl o WHERE o.rootObjectGuid=?1 AND o.changeTypeName=?2";

	/**
	 * The JPQL to get the data changed records.
	 */
	protected static final String FIND_DATA_CHANGED_BY_GUID_AND_TYPE =
			"SELECT dc FROM DataChangedImpl dc WHERE dc.changeOperation.changeTypeName=?1";

	/**
	 * tearDown method for each test method.
	 *
	 * @throws Exception
	 */
	@After
	public void tearDown() {
		LOG.info(dataChangedRecordCount + " data change records were verified!!!");

		System.setProperty("persistenceEngineTarget", "persistenceEngineTarget");
	}

	/**
	 * Verify audit data.
	 *
	 * @param objectBefore the object before data base operation
	 * @param objectAfter the object after data base operation
	 * @param targetObjectGuid the target object guid
	 * @param changeType the change type
	 * @param expectedChangeOperationNumber the expected change operation number
	 */
	protected void verifyAuditData(final Object objectBefore, final Object objectAfter,
								   final String targetObjectGuid, final ChangeType changeType, final int expectedChangeOperationNumber) {
		List<ChangeOperation> changeOperations = persistenceEngine.retrieveWithNewSession(FIND_CHANGE_OPERATION_BY_GUID_AND_TYPE, new Object[]{
				targetObjectGuid, changeType.getName()});
		assertNotNull(changeOperations);
		assertTrue(changeOperations.size() == expectedChangeOperationNumber);
		assertNotNull("", changeOperations.get(0).getChangeTransaction());

		List<DataChanged> dataChanges = persistenceEngine.retrieveWithNewSession(FIND_DATA_CHANGED_BY_GUID_AND_TYPE,
				new Object[]{changeType.getName()});

		assertNotNull(dataChanges);

		verifyDataChanged(objectBefore, objectAfter, dataChanges, changeType);

		//assertEquals("all data changed in database are verified...", dataChanges.size(), dataChangedRecordCount);
	}

	/**
	 * Verify records of data changed.
	 *
	 * @param objectBefore the object before database operation
	 * @param objectAfter the object after database operation
	 * @param dataChanges the list of data changed
	 * @param changeType the change type
	 */
	protected void verifyDataChanged(final Object objectBefore, final Object objectAfter, final List<DataChanged> dataChanges,
									 final ChangeType changeType) {
		//EntityManager entityManager = getEntityManager();

		Object object = null;
		if (objectBefore == null) {
			object = objectAfter;
		} else {
			object = objectBefore;
		}
		ClassMetaData metaData = JPAFacadeHelper.getMetaData(entityManager, object.getClass());
		for (FieldMetaData fieldMetaData : metaData.getFields()) {
			if (fieldMetaData.isPrimaryKey() || fieldMetaData.isVersion() || fieldMetaData.isTransient()) {
				continue;
			}

			String fieldName = fieldMetaData.getName();
			//getLog().info("verifying:"+object.getClass().getName()+"."+fieldName);
			Method method = getMethod(object, fieldName);

			Object fieldBefore = getField(objectBefore, method);
			String fieldValueBefore = getFieldValue(fieldBefore);

			Object fieldAfter = getField(objectAfter, method);
			String fieldValueAfter = getFieldValue(fieldAfter);

			if (!fieldMetaData.isEmbedded()) { // for example: verify the supported currencies for catalog
				if (fieldAfter instanceof Collection<?>) {
					for (Object member : (Collection<?>) fieldAfter) {
						Object foundObject = foundObjectInCollection(member, (Collection<?>) fieldBefore);
						if (foundObject == null) {
							verifyDataChanged(null, member, dataChanges, ChangeType.CREATE);
						} else {
							verifyDataChanged(foundObject, member, dataChanges, ChangeType.UPDATE);
						}
					}
				}
				if (fieldBefore instanceof Collection<?>) {
					for (Object member : (Collection<?>) fieldBefore) {
						Object foundObject = foundObjectInCollection(member, (Collection<?>) fieldAfter);
						if (foundObject == null) {
							verifyDataChanged(member, null, dataChanges, ChangeType.DELETE);
						}
						/*This flow equlas the flow above "UPDATE" flow.
						 * else {
							verifyDataChanged(member, foundObject, dataChanges, ChangeType.UPDATE);
						}*/
					}
				}
				continue;
			}

			LOG.info("\n");
			LOG.info(fieldName);
			if (Objects.equals(fieldValueBefore, fieldValueAfter)
					&& changeType.equals(ChangeType.UPDATE)) {
				//when one object is created and it contains some null field
				//the data change record for this null field will have null for both old value and new value
				LOG.info(" was not changed");
				continue;
			}
			LOG.info(" was changed! Try to find the data change record.");

			verifyUnEmbeddedChanges(object, fieldName, fieldValueBefore, fieldValueAfter, dataChanges, changeType);
		}
	}



	/**
	 * Find object in the collection by primary key.
	 * @param member the object to be found
	 * @param collection the collection
	 * @return the object found
	 */
	protected Object foundObjectInCollection(final Object member, final Collection<?> collection) {
		ClassMetaData metaData = JPAFacadeHelper.getMetaData(entityManager, member.getClass());

		if (collection != null) {
			for (Object object : collection) {
				for (FieldMetaData fieldMetaData : metaData.getFields()) {
					if (fieldMetaData.isPrimaryKey()) {
						String fieldName = fieldMetaData.getName();
						Method method = getMethod(object, fieldName);
						Object pk1 = getField(object, method);
						Object pk2 = getField(member, method);
						if (Objects.equals(pk1, pk2)) {
							return object;
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * Verify data changes of embedded fields.
	 *
	 * @param object the object
	 * @param fieldName the field name
	 * @param fieldValueBefore the field value before database operation
	 * @param fieldValueAfter the field value after database operation
	 * @param dataChanges the list of data changed
	 * @param changeType the change type
	 */
	protected void verifyUnEmbeddedChanges(final Object object, final String fieldName, String fieldValueBefore, final String fieldValueAfter,
										   final List<DataChanged> dataChanges, final ChangeType changeType) {

		dataChangedRecordCount++;

		List<DataChanged> filteredDataChangedList = findDataChangedByChangeType(object, fieldName, dataChanges, changeType);
		assertFalse(filteredDataChangedList.isEmpty());

		StringBuilder debugInfo = new StringBuilder();
		debugInfo.append("\nObject: \n");
		debugInfo.append(object.getClass().getSimpleName());
		debugInfo.append('.');
		debugInfo.append(fieldName);
		debugInfo.append(" changed from ");
		debugInfo.append(fieldValueBefore);
		debugInfo.append(" to ");
		debugInfo.append(fieldValueAfter);

		boolean verifyResult = false;
		for (DataChanged dataChanged : filteredDataChangedList) {
			debugInfo.append("\nData Change record: ");
			debugInfo.append(dataChanged.getFieldOldValue());
			debugInfo.append(" changed to ");
			debugInfo.append(dataChanged.getFieldNewValue());

			if (auditEquals(dataChanged.getFieldOldValue(), fieldValueBefore)
					&& auditEquals(dataChanged.getFieldNewValue(), fieldValueAfter)) {
				verifyResult = true;
				break;
			}
		}

		String debugMessage = debugInfo.toString();
		LOG.debug(debugMessage);

		if (!verifyResult) {
			LOG.error("No audit records match.  Audit Records:");
			LOG.error(filteredDataChangedList);
		}
		assertTrue(debugMessage, verifyResult);
	}

	private boolean auditEquals(final String dataChangedValue, final String fieldValue) {
		if (dataChangedValue != null && fieldValue != null) {
			try {
				return Math.abs(dateFormat.parse(fieldValue).getTime() - dateFormat.parse(dataChangedValue).getTime()) <= ONE_MINUTE;
			} catch (ParseException e) {
				// not a date so just use Objects.equals
			}
		}
		return Objects.equals(dataChangedValue, fieldValue);
	}

	/**
	 * Get the getter method.
	 *
	 * @param object the owner object of the method
	 * @param fieldName the field name
	 * @return the get method of the field on the object
	 */
	protected Method getMethod(final Object object, final String fieldName) {
		return Reflection.findGetter(object.getClass(), fieldName, true);
	}

	/**
	 * Get the object of the field.
	 *
	 * @param object the owner object of the field
	 * @param method the method to get the field
	 * @return the object of the field
	 */
	protected Object getField(final Object object, final Method method) {
		if (object != null) {
			return Reflection.get(object, method);
		}
		return null;
	}

	/**
	 * Get the string value of the field.
	 *
	 * @param field the filed
	 * @return the string value of the field
	 */
	protected String getFieldValue(final Object field) {
		if (field == null) {
			return null;
		}
		if (field instanceof Entity) {
			return ((Entity) field).getGuid();
		}
		if (field instanceof Persistable) {
			return String.valueOf(((Persistable) field).getUidPk());
		}
		return field.toString();
	}

	/**
	 * find data changed record in the given data changed list by object class name, field name and change type.
	 *
	 * @param object the object
	 * @param filedName the field name
	 * @param dataChanges the list of data changes
	 * @param changeType the change type
	 * @return a list of data changed records
	 */
	protected List<DataChanged> findDataChangedByChangeType(final Object object, final String filedName, final List<DataChanged> dataChanges,
															final ChangeType changeType) {
		List<DataChanged> dataChangedList = new ArrayList<>();
		for (DataChanged dataChanged : dataChanges) {
			if (dataChanged.getFieldName().equals(filedName) && dataChanged.getObjectName().equals(object.getClass().getName())
					&& dataChanged.getChangeType().equals(changeType)) {
				dataChangedList.add(dataChanged);
			}
		}
		return dataChangedList;
	}

}
