/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.cmclient.core.registry.ObjectRegistry;
import com.elasticpath.common.dto.ChangeSetDependencyDto;
import com.elasticpath.commons.pagination.DirectedSortingField;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.domain.changeset.ChangeSetMember;
import com.elasticpath.domain.changeset.ChangeSetObjectStatus;
import com.elasticpath.domain.changeset.ChangeSetUserView;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.persistence.api.LoadTuner;
import com.elasticpath.service.changeset.BusinessObjectResolver;
import com.elasticpath.service.changeset.ChangeSetPolicyException;
import com.elasticpath.service.changeset.ChangeSetService;

/**
 * Resolves Objects to their BusinessObjectDescriptors before delegating the
 * call to the underlying ChangeSetService.  This is intended to reduce
 * data passed over the wire thereby improving performance of frequently called
 * change set operations.
 */
public class LocalResolvingChangeSetServiceImpl implements ChangeSetService {

	private ChangeSetService changeSetServiceDelegate;

	private BusinessObjectResolver businessObjectResolver;


	@Override
	public void removeObjectFromChangeSet(final String changeSetGuid, final Object object) {
		BusinessObjectDescriptor objectDescriptor = businessObjectResolver.resolveObjectDescriptor(object);
		if (objectDescriptor == null) {
			throw new ChangeSetPolicyException("Object with class: "  //$NON-NLS-1$
					+ object.getClass().getName()
					+ " cannot be resolved to an object descriptor. It is not supported by the Change Set Policy."); //$NON-NLS-1$
		}
		getChangeSetServiceDelegate().removeObjectFromChangeSet(changeSetGuid, objectDescriptor);
	}

	@Override
	public void addObjectToChangeSet(final String changeSetGuid, final Object object, final Map<String, String> metadata) {
		if (changeSetGuid == null || object == null) {
			throw new IllegalArgumentException("Cannot add object: " + object  //$NON-NLS-1$
					+ " to change set (GUID): " + changeSetGuid); //$NON-NLS-1$
		}
		BusinessObjectDescriptor objectDescriptor = businessObjectResolver.resolveObjectDescriptor(object);
		if (objectDescriptor == null) {
			throw new ChangeSetPolicyException("Object with class: "  //$NON-NLS-1$
					+ object.getClass().getName()
					+ " cannot be resolved to an object descriptor. It is not supported by the Change Set Policy."); //$NON-NLS-1$
		}

		getChangeSetServiceDelegate().addObjectToChangeSet(changeSetGuid, objectDescriptor, metadata);
		ObjectRegistry.getInstance().fireEvent(ChangeSetHelper.OBJECT_REG_ACTIVE_CHANGE_SET);
	}

	@Override
	@SuppressWarnings("PMD.EmptyCatchBlock")
	public ChangeSet findChangeSet(final Object object) {
		if (object == null) {
			return null;
		}

		BusinessObjectDescriptor objectDescriptor = null;
		try {
			objectDescriptor = businessObjectResolver.resolveObjectDescriptor(object);
		} catch (ChangeSetPolicyException cspe) {
			// Interface says that we should return null if the object is not resolvable.
			// Simply fall through to the return.
		}

		if (objectDescriptor == null) {
			return null;
		}

		return getChangeSetServiceDelegate().findChangeSet(objectDescriptor);
	}

	@Override
	public Map<String, String> findChangeSetMemberMetadata(final String changeSetGuid, final Object object) {
		BusinessObjectDescriptor objectDescriptor = businessObjectResolver.resolveObjectDescriptor(object);
		return getChangeSetServiceDelegate().findChangeSetMemberMetadata(changeSetGuid, objectDescriptor);
	}

	@Override
	public ChangeSetObjectStatus getStatus(final Object object) {
		BusinessObjectDescriptor objectDescriptor = businessObjectResolver.resolveObjectDescriptor(object);
		return getChangeSetServiceDelegate().getStatus(objectDescriptor);
	}

	@Override
	public String resolveObjectGuid(final Object object) {
		return businessObjectResolver.resolveObjectGuid(object);
	}

	@Override
	public void addObjectToChangeSet(final String changeSetGuid, final BusinessObjectDescriptor objectDescriptor,
			final Map<String, String> metadata) {
		getChangeSetServiceDelegate().addObjectToChangeSet(changeSetGuid, objectDescriptor, metadata);
		ObjectRegistry.getInstance().fireEvent(ChangeSetHelper.OBJECT_REG_ACTIVE_CHANGE_SET);
	}

	/**
	 * Adds a new member object to a change set using the object descriptor.
	 *
	 * @param changeSetGuid the change set code, it cannot be null.
	 * @param objectDescriptor the descriptor, it cannot be null.
	 * @param objectMetadata the meta data map, it could be null if no meta data is applied for the business object
	 * @param resolveMetadata whether to resolve metadata
	 */
	public void addObjectToChangeSet(final String changeSetGuid, final BusinessObjectDescriptor objectDescriptor,
			final Map<String, String> objectMetadata, final boolean resolveMetadata) {
		getChangeSetServiceDelegate().addObjectToChangeSet(changeSetGuid, objectDescriptor, objectMetadata, resolveMetadata);
		ObjectRegistry.getInstance().fireEvent(ChangeSetHelper.OBJECT_REG_ACTIVE_CHANGE_SET);
	}

	/**
	 * Finds change set member by business object.
	 *
	 * @param objectDescriptor the business object
	 * @return the change member
	 */
	public ChangeSet findChangeSet(final BusinessObjectDescriptor objectDescriptor) {
		return getChangeSetServiceDelegate().findChangeSet(objectDescriptor);
	}


	/**
	 * Finds change set member by object descriptor.
	 *
	 * @param changeSetGuid the change set GUID the object belongs to
	 * @param objectDescriptor the object descriptor
	 * @return the change member
	 */
	public Map<String, String> findChangeSetMemberMetadata(final String changeSetGuid, final BusinessObjectDescriptor objectDescriptor) {
		return getChangeSetServiceDelegate().findChangeSetMemberMetadata(changeSetGuid, objectDescriptor);
	}

	/**
	 * Removes a member object from the change set specified by its code.
	 *
	 * @param changeSetGuid the change set code
	 * @param objectDescriptor the descriptor to use
	 */
	public void removeObjectFromChangeSet(final String changeSetGuid, final BusinessObjectDescriptor objectDescriptor) {
		getChangeSetServiceDelegate().removeObjectFromChangeSet(changeSetGuid, objectDescriptor);
	}


	/**
	 * Looks for change set members in the specified range.
	 *
	 * @param groupId the change set GUID
	 * @param startIndex the starting index
	 * @param maxResults maximum results to be returned
	 * @param sortingFields the fields to sort by
	 * @param loadTuner the load tuner
	 * @return a collection of change set members
	 */
	public List<ChangeSetMember> findMembersByChangeSetGuid(final String groupId,
			final int startIndex, final int maxResults,
			final DirectedSortingField[] sortingFields, final LoadTuner loadTuner) {
		return getChangeSetServiceDelegate().findMembersByChangeSetGuid(groupId, startIndex, maxResults,
				sortingFields, loadTuner);
	}


	/**
	 * Get available users. (This needs to move to another service).
	 * @param permissions the permissions the user must have assigned
	 * @return a list of change set users
	 */
	public Collection<ChangeSetUserView> getAvailableUsers(final String... permissions) {
		return getChangeSetServiceDelegate().getAvailableUsers(permissions);
	}


	/**
	 * Finds the count of all the members of a change set.
	 *
	 * @param groupId the change set GUID
	 * @return number of members
	 */
	public long getChangeSetMemberCount(final String groupId) {
		return getChangeSetServiceDelegate().getChangeSetMemberCount(groupId);
	}


	/**
	 * Get change set user view objects from a list of change set user guids.
	 * @param changeSetUsersGuids a collection of change set user guids
	 * @return collection of change set user view objects
	 */
	public Collection<ChangeSetUserView> getChangeSetUserViews(final Collection<String> changeSetUsersGuids) {
		return getChangeSetServiceDelegate().getChangeSetUserViews(changeSetUsersGuids);
	}


	/**
	 * Get the real change set service.
	 *
	 * @return the real change set service
	 */
	public ChangeSetService getChangeSetServiceDelegate() {
		return changeSetServiceDelegate;
	}

	/**
	 * Set the real change set service.
	 *
	 * @param changeSetServiceDelegate the real service
	 */
	public void setChangeSetServiceDelegate(final ChangeSetService changeSetServiceDelegate) {
		this.changeSetServiceDelegate = changeSetServiceDelegate;
	}

	/**
	 * @return the businessObjectResolver
	 */
	public BusinessObjectResolver getBusinessObjectResolver() {
		return this.businessObjectResolver;
	}

	/**
	 * @param businessObjectResolver the businessObjectResolver
	 */
	public void setBusinessObjectResolver(final BusinessObjectResolver businessObjectResolver) {
		this.businessObjectResolver = businessObjectResolver;
	}

	@Override
	public String findChangeSetGuid(final BusinessObjectDescriptor objectDescriptor) {
		return getChangeSetServiceDelegate().findChangeSetGuid(objectDescriptor);
	}

	@Override
	public String findChangeSetGuid(final Object object) {
		return getChangeSetServiceDelegate().findChangeSetGuid(object);
	}

	@Override
	public Class< ? > findObjectClass(final BusinessObjectDescriptor objectDescriptor) {
		return getChangeSetServiceDelegate().findObjectClass(objectDescriptor);
	}

	@Override
	public Map<Object, String> getObjectsLocked(final Object[] objects) {
		return getChangeSetServiceDelegate().getObjectsLocked(objects);
	}

	@Override
	public ChangeSetObjectStatus getStatus(final BusinessObjectDescriptor objectDescriptor) {
		return getChangeSetServiceDelegate().getStatus(objectDescriptor);
	}

	@Override
	public boolean isChangeSetEnabled() {
		return getChangeSetServiceDelegate().isChangeSetEnabled();
	}

	@Override
	public boolean objectExists(final String groupId, final BusinessObjectDescriptor objectDescriptor) {
		return getChangeSetServiceDelegate().objectExists(groupId, objectDescriptor);
	}

	@Override
	public Set<BusinessObjectDescriptor> findDependentObjects(final BusinessObjectDescriptor object) {
		return getChangeSetServiceDelegate().findDependentObjects(object);
	}

	@Override
	public Map<BusinessObjectDescriptor, ChangeSet> findChangeSet(
			final Set<BusinessObjectDescriptor> objects, final ChangeSet excludedChangeSet) {
		return getChangeSetServiceDelegate().findChangeSet(objects, excludedChangeSet);
	}

	@Override
	public void updateResolvedMetadata(final String changeSetGuid) {
		getChangeSetServiceDelegate().updateResolvedMetadata(changeSetGuid);
	}

	@Override
	public List<ChangeSetDependencyDto> getChangeSetDependencies(
			final ChangeSet changeSet, final DirectedSortingField sortingField) {
		return getChangeSetServiceDelegate().getChangeSetDependencies(changeSet, sortingField);
	}

	@Override
	public List<ChangeSetMember> findFilteredMembersByChangeSetGuid(final String changeSetGuid, final int startIndex, final int maxResults,
			final DirectedSortingField[] sortingFields, final LoadTuner loadTuner, final List<String> objectTypeFilter) {
		return getChangeSetServiceDelegate().findFilteredMembersByChangeSetGuid(changeSetGuid, startIndex, maxResults,
				sortingFields, loadTuner, objectTypeFilter);
	}

	@Override
	public long getFilteredChangeSetMemberCount(final String groupId, final List<String> objectTypeFilter) {
		return getChangeSetServiceDelegate().getFilteredChangeSetMemberCount(groupId, objectTypeFilter);
	}

}
