/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.admin.users;

import com.elasticpath.cmclient.core.nls.LocalizedMessagePostProcessor;

/**
 * Messages class for the users plugin.
 */
@SuppressWarnings({ "PMD.VariableNamingConventions", "PMD.TooManyFields" })
public final class AdminUsersMessages {
	private static final String BUNDLE_NAME = "com.elasticpath.cmclient.admin.users.AdminUsersPluginResources"; //$NON-NLS-1$

	public static final String EMPTY_STRING = ""; //$NON-NLS-1$
	
	public static final String SPACE = " "; //$NON-NLS-1$

	private AdminUsersMessages() {
	}

	// ----------------------------------------------------
	// Default package
	// ----------------------------------------------------
	public String UserAdminSection_UserAdmin;

	public String UserAdminSection_RoleAdmin;

	// Global
	public String UserName;

	public String FirstName;

	public String LastName;

	public String Email;

	public String CmUser;

	public String ApiUser;

	public String Password;

	public String NewPassword;

	public String ConfirmPassword;
	
	public String SuperUser;

	public String CMUser;

	public String WSUser;

	public String Role;
	
	public String TitleStep;

	public String RoleDescription;
	
	// User Status
	public String Status;

	public String Active;

	public String Inactive;
	
	public String Locked;

	// User operations
	public String CreateUser;

	public String EditUser;

	public String DisableUser;
	
	public String ResetUserPassword;

	// Role operations
	public String CreateRole;

	public String EditRole;

	public String DeleteRole;

	//
	// Wizard page titles
	//
	// User Wizard
	public String UserDetails;

	public String RoleAssignments;

	public String StorePermissions;

	public String CatalogPermissions;

	public String WarehousePermissions;
	
	public String UserNameExists;
	
	public String EmailExists;

	// Role Wizard
	public String RoleDetails;

	public String RolePermissions;

	public String UserAssignments;
	
	public String RoleNameExists;

	// Wizard page section titles
	public String UserDetails_UserProfile;

	public String UserDetails_UserType;

	public String UserDetails_ConfigurableSettings;

	// Wizard Labels

	public String RoleAssignment_AvailableRoles;

	public String RoleAssignment_AssignedRoles;

	public String CatalogPermissions_AvailableCatalogs;

	public String CatalogPermissions_AssignedCatalogs;

	public String CatalogPermissions_AssignAllCatalogs;

	public String CatalogPermissions_AssignSpecialCatalogs;

	public String StorePermissions_AvailableStores;

	public String StorePermissions_AssignedStores;

	public String StorePermissions_AssignAllStores;

	public String StorePermissions_AssignSpecialStores;
	
	public String PriceListPermissions_AvailablePL;

	public String PriceListPermissions_AssignedPL;

	public String PriceListPermissions_AssignAllPL;

	public String PriceListPermissions_AssignSpecialPL;	

	public String WarehousePermissions_AvailableWarehouses;

	public String WarehousePermissions_AssignedWarehouses;

	public String WarehousePermissions_AssignAllWarehouses;

	public String WarehousePermissions_AssignSpecialWarehouses;

	public String RoleDetails_RoleName;

	public String RoleDetails_Description;

	public String RolePermissions_PermissionFilter;

	public String RolePermissions_AvailablePermissions;

	public String RolePermissions_AssignedPermissions;

	public String UserAssignments_AddUser;

	public String UserAssignments_RemoveUser;

	// Dialog boxes
	public String ConfirmDisableUser;

	public String ConfirmDeleteRole;
	
	public String ConfirmLineSeparator;

	public String CreateUserErrorEmailDialogTitle;
	
	public String CreateUserErrorEmailDialogDescription;
	
	public String ChangePasswordDialogConfirmTitle;
	
	public String ChangePasswordDialogConfirm;

	// Validation
	public String Validate_ConfirmPassword;

	// Search Users
	public String SearchView_SearchTermsGroup;

	public String SearchView_Search_Label_UserName;

	public String SearchView_Search_Label_UserLastName;

	public String SearchView_Search_Label_UserFirstName;

	public String SearchView_Search_Label_UserEmail;

	public String SearchView_FiltersGroup;

	public String SearchView_Filter_Label_Status;

	public String SearchView_Filter_Label_User_Roles;

	public String SearchView_Filter_Label_AssignedCatalog;

	public String SearchView_Filter_Label_AssignedStore;

	public String SearchView_SearchButton;

	public String SearchView_ClearButton;

	public String SearchView_Filter_Item_Any;

	public String SearchView_Filter_Item_Active;

	public String SearchView_Filter_Item_Disabled;

	public String SearchView_Filter_Item_Locked;

	public String SearchView_Sorting_User_Name;

	public String SearchView_Sorting_First_Name;

	public String SearchView_Sorting_Last_Name;

	public String SearchView_Sorting_Email;

	public String SearchView_Sorting_Status;
	
	public String PriceListPermissions;

	/**
	 * Gets the NLS localize message class.
	 * @return the localized message class.
	 */
	public static AdminUsersMessages get() {
		return LocalizedMessagePostProcessor.getUTF8Encoded(BUNDLE_NAME, AdminUsersMessages.class);
	}

}