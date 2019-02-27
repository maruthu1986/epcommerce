/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.changeset.editors;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.changeset.ChangeSetMessages;
import com.elasticpath.cmclient.changeset.helpers.UserViewFormatter;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.util.DateTimeUtilFactory;
import com.elasticpath.cmclient.core.formatting.MetadataDateFormat;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.changeset.ChangeSetMember;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.service.changeset.ChangeSetMemberAction;
import com.elasticpath.service.cmuser.CmUserService;

/**
 * A label provider for the change set members table.
 */
public class ObjectsChangeSetLabelProvider extends LabelProvider implements ITableLabelProvider {
	private static final Logger LOG = Logger.getLogger(ObjectsChangeSetLabelProvider.class);
	
	private static final int OBJECT_SELECTED_CHECKBOX_COLUMN = 0;
	private static final int ACTION_TYPE_COLUMN = 1;
	private static final int OBJECT_ID_COLUMN = 2;
	private static final int OBJECT_NAME_COLUMN = 3;
	private static final int OBJECT_TYPE_COLUMN = 4;
	private static final int DATE_ADDED_COLUMN = 5;
	private static final int ADDED_BY_COLUMN = 6;

	/**
	 * Get column image.
	 * @param element the object element
	 * @param columnIndex the column index
	 * @return an image or null
	 */
	@Override
	public Image getColumnImage(final Object element, final int columnIndex) {
		if (columnIndex == ACTION_TYPE_COLUMN) {
			ChangeSetMember member = (ChangeSetMember) element;
			Map<String, String> metadata = member.getMetadata();			
			String actionStr = metadata.get("action"); //$NON-NLS-1$
			ChangeSetMemberAction type = ChangeSetMemberAction.getChangeSetMemberAction(actionStr);
			if (type == null) {
				return CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_STATE_CHANGED_SMALL);
			}
			if (type == ChangeSetMemberAction.ADD) {
				return CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_STATE_ADDED_SMALL);
			} else if (type == ChangeSetMemberAction.EDIT) {
				return CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_STATE_CHANGED_SMALL);
			} else if (type == ChangeSetMemberAction.DELETE) {
				return CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_STATE_DELETED_SMALL);
			}
		}
		return null;
	}
	
	/**
	 * Gets the text for the given column with index columnIndex.
	 * 
	 * @param element the object element
	 * @param columnIndex the column index
	 * @return the text to use
	 */
	@Override
	public String getColumnText(final Object element, final int columnIndex) {
		ChangeSetMember member = (ChangeSetMember) element;
		Map<String, String> metadata = member.getMetadata();			
		switch (columnIndex) {		
			case OBJECT_SELECTED_CHECKBOX_COLUMN:
				return ""; //$NON-NLS-1$
			case OBJECT_ID_COLUMN:
				return getObjectId(member);
			case OBJECT_NAME_COLUMN:
				return member.getMetadata().get("objectName"); //$NON-NLS-1$
			case OBJECT_TYPE_COLUMN:
				return member.getBusinessObjectDescriptor().getObjectType();
			case DATE_ADDED_COLUMN:
				return getDateAdded(metadata);
			case ADDED_BY_COLUMN:
				if (metadata == null) {
					return ChangeSetMessages.EMPTY_STRING;
				}
				return findUser(metadata.get("addedByUserGuid")); //$NON-NLS-1$
			default: 
				return ChangeSetMessages.EMPTY_STRING;
		}
	}

	private String getObjectId(final ChangeSetMember member) {
		return member.getBusinessObjectDescriptor().getObjectIdentifier();
	}
	
	private String getDateAdded(final Map<String, String> metadata) {
		String dateAdded = metadata.get("dateAdded"); //$NON-NLS-1$
		if (dateAdded != null) {
			try {
				Date date = new MetadataDateFormat().parse(dateAdded);
				return DateTimeUtilFactory.getDateUtil().formatAsDateTime(date);
			} catch (ParseException e) {
				LOG.error("Could not parse date " + dateAdded, e); //$NON-NLS-1$
			}
		}
		return ChangeSetMessages.EMPTY_STRING;
	}

	/**
	 * Find user helper method.
	 */
	private String findUser(final String userGuid) {
		if (userGuid == null) {
			return ChangeSetMessages.EMPTY_STRING;
		}
		CmUserService cmUserService = ServiceLocator.getService(ContextIdNames.CMUSER_SERVICE);
		CmUser user = cmUserService.findByGuid(userGuid);
		if (user != null) {
			return UserViewFormatter.formatWithName(user);
		}
		return ChangeSetMessages.EMPTY_STRING;
	}

}