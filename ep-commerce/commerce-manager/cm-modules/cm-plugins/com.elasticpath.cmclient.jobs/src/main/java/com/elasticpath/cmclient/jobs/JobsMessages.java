/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.jobs;

import java.lang.reflect.Field;


import com.elasticpath.cmclient.core.MessageException;
import com.elasticpath.cmclient.core.nls.LocalizedMessagePostProcessor;
import com.elasticpath.domain.dataimport.ImportDataType;


/**
 * Messages class for the catalog plugin.
 */
@SuppressWarnings({ "PMD.TooManyFields", "PMD.VariableNamingConventions", "PMD.ExcessivePublicCount" })
public final class JobsMessages {
	private static final String BUNDLE_NAME = "com.elasticpath.cmclient.jobs.JobsPluginResources"; //$NON-NLS-1$

	private JobsMessages() {
	}

	// ----------------------------------------------------
	// Import Manger text and tipText
	// ----------------------------------------------------
	public String JobListView_TableColumnTitle_Catalog;
	
	public String JobListView_TableColumnTitle_Store;

	public String JobListView_TableColumnTitle_DataType;

	public String JobListView_TableColumnTitle_ImportType;

	public String JobListView_TableColumnTitle_JobName;

	public String JobListView_TableColumnTitle_PriceList;

	public String CreateJobAction;

	public String CreateJobActionToolTip;

	public String EditJobAction;

	public String EditJobActionToolTip;

	public String DeleteJobAction;

	public String DeleteJobActionToolTip;

	public String RunJobAction;

	public String RunJobActionToolTip;

	public String ImportJobWizard_CreateNewImportJob;

	public String ImportJobWizard_EditImportJob;

	public String ImportJobWizard_ConfigureImportJob;

	public String ImportJobWizard_ConfigureImportJobDescription;

	public String ImportJobWizard_DataFieldMapping;

	public String ImportJobWizard_DataFieldMappingDescription;

	public String ImportJobWizard_DataFields;

	public String ImportJobWizard_DataFieldsColumnDataField;

	public String ImportJobWizard_DataFieldsColumnCsvColumn;

	public String ImportJobWizard_CsvColumns;

	public String ImportJobWizard_MapButton;

	public String ImportJobWizard_MapButtonHelp;

	public String ImportJobWizard_UnmapButton;

	public String ImportJobWizard_UnmapButtonHelp;

	public String ImportJobWizard_ClearButton;

	public String ImportJobWizard_ClearButtonHelp;

	public String ImportJobWizard_ImportJobNameExists;
	
	public String ImportJobWizard_TooManyAllowedErrors;
	
	public String ImportJobWizard_SelectStore;
	
	public String ImportJobWizard_SelectCatalog;
	
	public String ImportJobWizard_SelectWarehouse;

	public String ImportJobWizard_SelectPriceList;

	public String JobDetailsPage_Catalog;
	
	public String JobDetailsPage_Warehouse;
	
	public String JobDetailsPage_PriceList;
	
	public String JobDetailsPage_Store;

	public String JobDetailsPage_DataType;

	public String JobDetailsPage_ImportType;

	public String JobDetailsPage_ImportName;

	public String JobDetailsPage_TemplateFile;

	public String JobDetailsPage_ImportFile;

	public String JobDetailsPage_ColumnDelimeter;

	public String JobDetailsPage_TextDelimeter;

	public String JobDetailsPage_Browse;

	public String DeleteJobTitle;

	public String DeleteJobText;

	public String JobDetailsPage_DefaultMaximumErrors;

	public String JobDetailsPage_MaximumErrors;

	public String JobDetailsPage_PreviewData;

	public String JobDetailsPage_SelectImportFile;

	public String JobDetailsPage_StartImportFileValidationInfo;

	public String ColumnDelimiter_Tab;

	public String ColumnDelimiter_Comma;

	public String ColumnDelimiter_Semicolon;

	public String ColumnDelimiter_Space;

	public String ColumnDelimiter_Other;

	public String TextDelimiter_DoubleQuote;

	public String TextDelimiter_SingleQuote;

	public String TextDelimiter_Other;

	// Run wizard
	public String RunWizard_WrongColumnsError;

	public String RunWizard_WrongGuidError;

	public String RunWizard_NotNullError;

	public String RunWizard_TooLongError;

	public String RunWizard_BindError;

	public String RunWizard_UnexpectedError;

	public String RunWizard_EmptyTitleNameNotAllowError;

	public String RunWizard_MultipleLinesError;

	public String RunWizard_BadTitleNameError;

	public String RunWizard_BrowseButtonLabel;

	public String RunWizard_CsvImportDialogTitle;

	public String RunWizard_RunningJobProgress_Title;

	public String RunWizard_RunningJobProgress_CurrentRow;

	public String RunWizard_ImportComplete_Title;

	public String RunWizard_ImportComplete_Details;

	public String RunWizard_ImportComplete_Message;

	public String RunWizard_RunningJob;

	public String RunWizard_RequiredColumnError;

	public String RunWizard_WrongColumnNumber;

	public String RunWizard_ErrorPageTitle;

	public String RunWizard_ErrorPageDetails;

	public String RunWizard_JobInitError;

	public String RunWizard_CompletionDialog_ImportJobName;

	public String RunWizard_CompletionDialog_StartTime;

	public String RunWizard_CompletionDialog_EndTime;

	public String RunWizard_CompletionDialog_TotalRows;

	public String RunWizard_CompletionDialog_SucceededRows;

	public String RunWizard_CompletionDialog_FailedRows;

	public String RunWizard_CompletionDialog_ErrorsMessage;

	public String RunWizard_ImportFilePageTitle;

	public String RunWizard_ImportFilePageDetails;

	public String RunWizard_JobPreviewPageTitle;

	public String RunWizard_JobPreviewPageDetails;

	public String RunWizard_CsvValidationPageTitle;

	public String RunWizard_CsvValidationPageDetails;

	public String RunWizard_MappingsValidationPageTitle;

	public String RunWizard_MappingsValidationPageDetails;

	public String RunWizard_MappingsValidationErrors;

	public String RunWizard_MappingsTableColumnTitle_Row;

	public String RunWizard_MappingsTableColumnTitle_Name;

	public String RunWizard_MappingsTableColumnTitle_Data;

	public String RunWizard_MappingsTableColumnTitle_Error;

	public String RunWizard_MappingsValidationPageSubTitile;

	public String RunWizard_CsvValidationPageErrorsSubTitle;

	public String RunWizard_CsvValidationPageErrors;

	public String RunWizard_MappingsValidationSuccess;

	public String RunWizard_RequiredDepdendentFieldsMissing;
	
	public String RunWizard_UnexpectedErrors;

	public String RunWizard_ErrorAnotherJobRunning;
	
	public String RunWizard_FileCanNotBeTransferedMessage;
	
	public String RunWizard_ImportFolderNotFound;
	
	public String RunWizard_TransferFileError;
	
	public String RunWizard_VFSConnectionError; 

	public String RunWizard_UnexpectedError_title;

	public String RunWizard_Canceled_Title;

	public String RunWizard_Canceled_Message;

	public String RunWizard_ObjectUnavailableForChangeSet;
	
	public String RunWizard_PriceListUnavailableForChangeSet;


	// ----------------------------------------------------
	// Import data types
	// ----------------------------------------------------
	public String ImportDataType_Category;
	
	public String ImportDataType_Customer;
	
	public String ImportDataType_CustomerAddress;
	
	public String ImportDataType_ProductAssociation;
	
	public String ImportDataType_ProductCategoryAssociation;
	
	public String ImportDataType_Product;
	
	public String ImportDataType_ProductPrice;
	
	public String ImportDataType_ProductSku;
	
	public String ImportDataType_BaseAmount;
	
	public String ImportDataType_ProductSkuPrice;
	
	public String ImportDataType_Inventory;
	
	// ----------------------------------------------------
	// Import types
	// ----------------------------------------------------
	
	public String ImportType_Insert;

	public String ImportType_InsertAndUpdate;

	public String ImportType_ClearThenInsert;

	public String ImportType_Update;

	public String ImportType_Delete;

	// Promo Coupon Csv upload
	public String CouponCodesCsvImportWizard_Title;
	public String CouponCodesCsvImportWizard_ErrorTitle;
	public String CouponCodesCsvImportWizard_Description;

	public String CouponCodesCsvImportWizard_Empty;
	public String CouponCodesCsvImportWizard_WrongFormat;
	public String CouponCodesCsvImportWizard_NoCouponOnLine;
	public String CouponCodesCsvImportWizard_InvalidEmailOnLine;
	public String CouponCodesCsvImportWizard_ProgressValidating;
	public String CouponCodesCsvImportWizard_ProgressLoading;
	
	public String CouponCodesCsvImportWizard_DuplicateCouponCode;
	
	// ConfigurePriceListImportJobPage
	public String ConfigurePriceListImportJobPage_ErrorDialog_Title;
	public String ConfigurePriceListImportJobPage_ErrorDialog_Message;
	public String ConfigurePriceListImportJobPage_No_Permission_Message; 
	public String ConfigurePriceListImportJobPage_Msg_DifferentNames;
	public String ConfigurePriceListImportJobPage_Msg_WrongFormat;
	public String ConfigurePriceListImportJobPage_Msg_WrongCurrency;
	public String ConfigurePriceListImportJobPage_Msg_WrongCurrencyFormat;
	
	public String ConfigurePriceListImportJobPage_FileUpload_Title; 
	public String ConfigurePriceListImportJobPage_FileUpload_Message; 

	public String DirectCsvImportBaseAmountsWizard_Title;
	public String DirectCsvImportConfigurePriceListImportPage_Title;
	public String DirectCsvImportConfigurePriceListImportPage_Description;

	public String RunWizard_Error_SalePriceExceedListPrice;
	
	public String import_csvFile_badRow_productCannotBeDeleted;
	public String import_csvFile_badRow_databaseError;
	public String import_csvFile_badRow_unavailableForChangeSet;

	public String import_unexpected_error;	
	

	/**
	 * Return a message String given the message key.
	 * 
	 * @param messageKey the message key (static field) that holds the message String
	 * @return the message String
	 */
	public String getMessage(final String messageKey) {
		try {
			final Field field = JobsMessages.class.getField(messageKey);
			return (String) field.get(this);
		} catch (final Exception e) {
			throw new MessageException(e);
		}
	}
	
	
	/**
	 * Return a message String given the message key.
	 * 
	 * @param messageKey the message key (static field) that holds the message String
	 * @param typeName the name of an import type
	 * @return the message String
	 */	
	public String getMessage(final String messageKey, final String typeName) {
		if (typeName == null) {
			return getMessage(messageKey);
		}
		return getMessage(messageKey) + ImportDataType.SEPARATOR + typeName;
	}

	/**
	 * Gets the NLS localize message class.
	 * @return the localized message class.
	 */
	public static JobsMessages get() {
		return LocalizedMessagePostProcessor.getUTF8Encoded(BUNDLE_NAME, JobsMessages.class);
	}


}
