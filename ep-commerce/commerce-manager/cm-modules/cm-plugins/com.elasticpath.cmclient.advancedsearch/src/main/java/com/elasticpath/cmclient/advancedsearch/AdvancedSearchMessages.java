/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.advancedsearch;

import java.lang.reflect.Field;

import com.elasticpath.cmclient.core.MessageException;
import com.elasticpath.cmclient.core.nls.LocalizedMessagePostProcessor;

/**
 * Messages class for the advanced search plugin.
 */
@SuppressWarnings({ "PMD.TooManyFields", "PMD.VariableNamingConventions" })
public final class AdvancedSearchMessages {
	private static final String BUNDLE_NAME = "com.elasticpath.cmclient.advancedsearch.AdvancedSearchPluginResources"; //$NON-NLS-1$

	public String QueryOwner;
	
	public String QueryName;
	
	public String Catalog;
	
	public String Category;
	
	public String Product;
	
	public String Public;
	
	public String Private;
	
	public String Save;
	
	public String SaveQuery;
	
	public String SaveAs;
	
	public String SaveQueryAs;
	
	public String Name;
	
	public String Description;
	
	public String Visibility;
	
	public String PrivateForCurrentUser;
	
	public String PublicForAllUsers;
	
	public String CreateQuery;
	
	public String RunQuery;
	
	public String OpenQuery;
	
	public String EditQuery;
	
	public String DeleteQuery;
	
	public String QueryBuilder;	
	
	public String SavedQueries;
	
	public String QueryType;
	
	public String Query;
	
	public String Queries;
	
	public String QueryID;
	
	public String Owner;
	
	public String DeleteQuery_MsgBox_Title;

	public String DeleteQuery_MsgBox_Content;
	
	public String AdvancedSearchResults;
	
	public String Help; 
	
	public String UserName_Format;
	
	public String AdvancedSearchHelpBrowser_Title;
	
	public String CouldNotLocateHelpResource_Message;

	public String PathToResource;
	
	public String Name_Exists;

	private AdvancedSearchMessages() {
	}

	// ----------------------------------------------------
	// Global Keys
	// ----------------------------------------------------
	
	public String QueryBuilderTab_ValidateQuery;

	/**
	 * Return a message String given the message key.
	 * 
	 * @param messageKey the message key (static field) that holds the message String
	 * @return the message String
	 */
	public String getMessage(final String messageKey) {
		try {
			final Field field = AdvancedSearchMessages.class.getField(messageKey);
			return (String) field.get(this);
		} catch (final Exception e) {
			throw new MessageException(e);
		}
	}

	/**
	 * Gets the NLS localize message class.
	 * @return the localized message class.
	 */
	public static AdvancedSearchMessages get() {
		return LocalizedMessagePostProcessor.getUTF8Encoded(BUNDLE_NAME, AdvancedSearchMessages.class);
	}
}
