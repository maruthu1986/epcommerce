/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.event;

import java.util.EventObject;

import org.eclipse.jface.resource.ImageDescriptor;

import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.CoreMessages;

/**
 * Represents a navigation event when a user is navigating through a virtual result set. 
 */
public class NavigationEvent extends EventObject {
	private static final long serialVersionUID = 1L;
//	private static final Logger LOG = Logger.getLogger(NavigationEvent.class);

	/** Defines a type of navigation action. */
	public enum NavigationType {
		/** Go to first record. */
		FIRST(CoreImageRegistry.IMAGE_RESULTSET_FIRST, CoreMessages.get().navigation_FirstPage, "Navigating to the first page."), //$NON-NLS-1$
		/** Go to previous record. */
		PREVIOUS(CoreImageRegistry.IMAGE_RESULTSET_PREVIOUS,
				CoreMessages.get().navigation_PreviousPage, "Navigating to the previous page."), //$NON-NLS-1$
		/** Go to next record. */
		NEXT(CoreImageRegistry.IMAGE_RESULTSET_NEXT, CoreMessages.get().navigation_NextPage, "Navigating to the next page."), //$NON-NLS-1$
		/** Go to last record. */
		LAST(CoreImageRegistry.IMAGE_RESULTSET_LAST, CoreMessages.get().navigation_LastPage, "Navigating to the last page."), //$NON-NLS-1$
		/** Go to specific record. */
		TO(null, null, "Navigating to the specific page.");  //$NON-NLS-1$
		
		private ImageDescriptor imageDescriptor;
		private String toolTipText;
		private String debugMessage;
		
		/**
		 * @param imageDescriptor image descriptor
		 * @param toolTipText tooltip 
		 * @param debugMessage debug message
		 */
		NavigationType(final ImageDescriptor imageDescriptor, final String toolTipText, final String debugMessage) {
			this.imageDescriptor = imageDescriptor;
			this.toolTipText = toolTipText;
			this.debugMessage = debugMessage;
		}
		/**
		 * @return the imageDescriptor
		 */
		public ImageDescriptor getImageDescriptor() {
			return imageDescriptor;
		}
		/**
		 * @return the toolTipText
		 */
		public String getToolTipText() {
			return toolTipText;
		}
		/**
		 * @return the debugMessage
		 */
		public String getDebugMessage() {
			return debugMessage;
		}
	}
		
	
	private final Enum<NavigationType> navigationType;
	
	private Object[] args;
	
	/**
	 * Constructor.
	 *
	 * @param source the event's source
	 * @param navigationType the type of navigation (first, previous, next, last)
	 * @param args the arguments
	 */
	public NavigationEvent(final Object source, final Enum<NavigationType> navigationType, final Object[] args) {
		super(source);
		this.navigationType = navigationType;
		
		if (args != null) {
			this.args = args.clone();
		}
	}
	
	/**
	 * Get the type of navigation associated with this event.
	 *
	 * @return the navigation type
	 */
	public Enum<NavigationType> getType() {
		return this.navigationType;
	}

	/**
	 * Get the arguments.
	 * 
	 * @return the arguments
	 */
	public Object[] getArgs() {
		if (args != null) {
			return args.clone();
		}
		return null;
	}
}
