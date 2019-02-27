/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.changeset;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.cmclient.changeset.support.SupportedComponent;
import com.elasticpath.cmclient.changeset.support.SupportedComponentsExtPoint;
import com.elasticpath.cmclient.core.CmSingletonUtil;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;

/**
 * Class that contains editors that are supported by the change set.
 * All the editors are passed to this class at early startup.
 * <p>
 *
 * @see ChangeSetEarlyStartup
 * @since 7.0
 */
public class ChangeSetSupportedEditors {

	private SupportedComponentsExtPoint supportedEditorsExtPoint;

	/**
	 * Getter for the Supported Editors session instance.
	 *
	 * @return instance of this class
	 */
	public static ChangeSetSupportedEditors getDefault() {
		return CmSingletonUtil.getSessionInstance(ChangeSetSupportedEditors.class);
	}

	/**
	 * Setter for the supported Editors ext point.
	 *
	 * @param supportedEditorsExtPoint supported editors
	 */
	public void setSupportedEditorsExtPoint(final SupportedComponentsExtPoint supportedEditorsExtPoint) {
		this.supportedEditorsExtPoint = supportedEditorsExtPoint;
	}

	/**
	 * Finds a supported editor for the given object descriptor.
	 *
	 * @param descriptor the descriptor
	 * @return the supported editor for this descriptor or <code>null</code> if none was found
	 */
	public SupportedComponent findSupportedComponent(final BusinessObjectDescriptor descriptor) {
		for (SupportedComponent supportedComponent : supportedEditorsExtPoint.getSupportedComponents()) {
			if (StringUtils.equals(supportedComponent.getObjectType(), descriptor.getObjectType())) {
				return supportedComponent;
			}
		}
		return null;
	}
}
