/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.changeset.editors;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.changeset.ChangeSetMessages;
import com.elasticpath.common.dto.ChangeSetDependencyDto;

/**
 * A label provider for the change set members table.
 */
public class ConflictsChangeSetLabelProvider extends LabelProvider implements ITableLabelProvider {
	
	private static final int SOURCE_OBJECT_NAME = 0;
	private static final int SOURCE_OBJECT_TYPE = 1;
	private static final int DEPENDENCY_OBJECT_NAME = 2;
	private static final int DEPENDENCY_OBJECT_TYPE = 3;
	private static final int CHANGE_SET_OF_DEPENDENCY = 4;

	/**
	 * Get column image.
	 * @param element the object element
	 * @param columnIndex the column index
	 * @return an image or null
	 */
	@Override
	public Image getColumnImage(final Object element, final int columnIndex) {
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
		ChangeSetDependencyDto changeSetDependencyDto = (ChangeSetDependencyDto) element;			
		switch (columnIndex) {		
			case SOURCE_OBJECT_NAME:
				return changeSetDependencyDto.getSourceObjectName(); 
			case SOURCE_OBJECT_TYPE:
				return changeSetDependencyDto.getSourceObjectType();
			case DEPENDENCY_OBJECT_NAME:
				return changeSetDependencyDto.getDependencyObjectName(); 
			case DEPENDENCY_OBJECT_TYPE:
				return changeSetDependencyDto.getDependencyObjectType();
			case CHANGE_SET_OF_DEPENDENCY:
				return changeSetDependencyDto.getDependencyChangeSetName();
			default: 
				return ChangeSetMessages.EMPTY_STRING;
		}
	}

}