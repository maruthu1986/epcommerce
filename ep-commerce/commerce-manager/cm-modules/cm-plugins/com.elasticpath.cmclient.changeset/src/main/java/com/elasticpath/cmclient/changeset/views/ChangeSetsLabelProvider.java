/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.changeset.views;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.changeset.ChangeSetMessages;
import com.elasticpath.cmclient.changeset.helpers.UserViewFormatter;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.util.DateTimeUtilFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.service.cmuser.CmUserService;

/**
 * A label provider for change sets' table.
 */
public class ChangeSetsLabelProvider extends LabelProvider implements ITableLabelProvider {
	
	private final CmUserService cmUserService = ServiceLocator.getService(ContextIdNames.CMUSER_SERVICE);
	
	/**
	 * Column index of change set name.
	 */
	public static final int COLUMN_NAME = 0;
	
	/**
	 * Column index of change set creator (user) name.
	 */
	public static final int COLUMN_CREATOR = 1;
	
	/**
	 * Column index of change set date.
	 */
	public static final int COLUMN_CREATED_DATE = 2;
	
	/**
	 * Column index of change set description.
	 */
	public static final int COLUMN_DESCRIPTION = 4;

	/**
	 * Column index of change set state.
	 */
	public static final int COLUMN_STATE = 3;
	
	@Override
	public Image getColumnImage(final Object element, final int columnIndex) {
		return null;
	}

	/**
	 * Gets a cell string per columnIndex.
	 *  
	 * @param element the element to interrogate
	 * @param columnIndex the column index the text is relevant to
	 * @return a text representing name, description, etc.
	 */
	@Override
	public String getColumnText(final Object element, final int columnIndex) {
		ChangeSet changeSet = (ChangeSet) element;
		
		switch (columnIndex) {
		case COLUMN_NAME: 
			return changeSet.getName();
		case COLUMN_DESCRIPTION: 
			return changeSet.getDescription();
		case COLUMN_CREATED_DATE:
			return 	DateTimeUtilFactory.getDateUtil().formatAsDate(changeSet.getCreatedDate(), CoreMessages.get().NotAvailable);
		case COLUMN_CREATOR:
			return getColumnCreator(changeSet);
		case COLUMN_STATE:
			return ChangeSetMessages.get().getMessage(changeSet.getStateCode());
		default :
			break;
		}
		
		return ChangeSetMessages.EMPTY_STRING;
	}
	
	/**
	 * Generates the creator column value for the given {@link ChangeSet}.
	 * 
	 * @param changeSet The change set to get creator information for.
	 * @return  The change set creator name and last name.
	 */
	protected String getColumnCreator(final ChangeSet changeSet) {
		final CmUser creator = cmUserService.findByGuid(changeSet.getCreatedByUserGuid());
		if (creator == null) {
			return CoreMessages.get().NotAvailable;
		}
		
		return UserViewFormatter.formatWithName(creator);	
	}

}
