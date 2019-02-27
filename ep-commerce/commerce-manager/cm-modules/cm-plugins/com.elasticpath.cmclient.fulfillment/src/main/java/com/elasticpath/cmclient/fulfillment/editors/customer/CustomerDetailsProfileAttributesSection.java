/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.fulfillment.editors.customer;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.conversion.StringToNumberConverter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.util.DateTimeUtilFactory;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.conversion.EpStringToBigDecimalConverter;
import com.elasticpath.cmclient.core.conversion.EpStringToDateConverter;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPageSectionPart;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.ControlModificationListener;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpDateTimePicker;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.FulfillmentPermissions;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.service.attribute.AttributeService;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

/**
 * UI representation of the customer details profile basic section.
 */
@SuppressWarnings({ "PMD.GodClass" })
public class CustomerDetailsProfileAttributesSection extends AbstractCmClientEditorPageSectionPart {

	private static final int LAYOUT_COLUMNS = 2;
	private static final int MAIN_PANEL_CONTROL_INDENT = 60;

	private final Customer customer;

	private IEpLayoutComposite mainPane;

	private final ControlModificationListener listener;

	private boolean authorized;

	private Map<Attribute, Control> attributeWidgetMap;

	private static final  int MAIN_PANEL_RIGHT_MARGIN = 30;

	/**
	 * Constructor.
	 * 
	 * @param editor the editor containing this Section Constructor to create a new Section in an editor's FormPage.
	 * @param formPage the formpage
	 */
	public CustomerDetailsProfileAttributesSection(final FormPage formPage, final AbstractCmClientFormEditor editor) {
		super(formPage, editor, ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		this.customer = (Customer) editor.getModel();
		this.listener = editor;
	}

	@Override
	protected void createControls(final Composite parentComposite, final FormToolkit toolkit) {
		final EpState authorization;

		AuthorizationService authorizationService = AuthorizationService.getInstance();
		authorized = authorizationService.isAuthorizedWithPermission(FulfillmentPermissions.CUSTOMER_EDIT)
						&& authorizationService.isAuthorizedForStore(customer.getStoreCode());

		if (authorized) {
			authorization = EpState.EDITABLE;
		} else {
			authorization = EpState.READ_ONLY;
		}

		this.mainPane = CompositeFactory.createTableWrapLayoutComposite(parentComposite, LAYOUT_COLUMNS, false);
		final TableWrapData mainPanelWrapData = new TableWrapData(TableWrapData.FILL, TableWrapData.FILL);
		mainPanelWrapData.grabHorizontal = true;
		mainPanelWrapData.indent = MAIN_PANEL_CONTROL_INDENT;
		this.mainPane.setLayoutData(mainPanelWrapData);

		TableWrapLayout tableWrapLayout = (TableWrapLayout) mainPane.getSwtComposite().getLayout();
		tableWrapLayout.rightMargin = MAIN_PANEL_RIGHT_MARGIN;

		final AttributeService attributeService = ServiceLocator.getService(ContextIdNames.ATTRIBUTE_SERVICE);
		final List<Attribute> attributes = attributeService.getCustomerProfileAttributes();
		attributeWidgetMap = new HashMap<>();
		for (final Attribute attribute : attributes) {
			final Control control = createAttributeControl(authorization, attribute);
			if (control != null) {
				attributeWidgetMap.put(attribute, control);
			}
		}
	}

	@SuppressWarnings("PMD.MissingBreakInSwitch")
	private Control createAttributeControl(final EpState epState, final Attribute attribute) {
		Control control = null;
		final IEpLayoutData labelData = this.mainPane.createLayoutData(IEpLayoutData.END, IEpLayoutData.CENTER);
		final IEpLayoutData fieldData = this.mainPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false);

		if (!attribute.isSystem()) {
			// add label
			if (attribute.isRequired()) {
				mainPane.addLabelBoldRequired(attribute.getName(), epState, labelData);
			} else {
				mainPane.addLabelBold(attribute.getName(), labelData);
			}
			// add text, button or date/time component
			switch (attribute.getAttributeType().getTypeId()) {
			case AttributeType.BOOLEAN_TYPE_ID:
				control = mainPane.addCheckBoxButton("", epState, fieldData); //$NON-NLS-1$
				break;
			case AttributeType.DECIMAL_TYPE_ID:
			case AttributeType.SHORT_TEXT_TYPE_ID:
			case AttributeType.INTEGER_TYPE_ID:
			case AttributeType.IMAGE_TYPE_ID:
			case AttributeType.FILE_TYPE_ID:
				control = mainPane.addTextField(epState, fieldData);
				break;
			case AttributeType.DATE_TYPE_ID:
				final IEpDateTimePicker dateComp = mainPane.addDateTimeComponent(IEpDateTimePicker.STYLE_DATE, epState, fieldData);
				control = dateComp.getSwtText();
				break;
			case AttributeType.DATETIME_TYPE_ID:
				final IEpDateTimePicker dateTimeComp = mainPane.addDateTimeComponent(IEpDateTimePicker.STYLE_DATE_AND_TIME, epState, fieldData);
				control = dateTimeComp.getSwtText();
				break;
			default:
				// do nothing
			}
		}
		return control;
	}

	@Override
	protected void populateControls() {
		for (final Attribute attribute : attributeWidgetMap.keySet()) {
			final Object attributeValue = customer.getCustomerProfile().getProfileValue(attribute.getKey());
			if (attributeValue != null) {
				final Control control = attributeWidgetMap.get(attribute);
				switch (attribute.getAttributeType().getTypeId()) {
				case AttributeType.BOOLEAN_TYPE_ID:
					((Button) control).setSelection((Boolean) attributeValue);
					break;
				case AttributeType.IMAGE_TYPE_ID:
				case AttributeType.FILE_TYPE_ID:
				case AttributeType.DECIMAL_TYPE_ID:
				case AttributeType.SHORT_TEXT_TYPE_ID:
				case AttributeType.INTEGER_TYPE_ID:
					((Text) control).setText(attributeValue.toString());
					break;
				case AttributeType.DATE_TYPE_ID:
					((Text) control).setText(DateTimeUtilFactory.getDateUtil().formatAsDate((Date) attributeValue));
					break;
				case AttributeType.DATETIME_TYPE_ID:
					((Text) control).setText(DateTimeUtilFactory.getDateUtil().formatAsDateTime((Date) attributeValue));
					break;
				default:
					// do nothing
				}
			}
		}
		// Set the modification listener *after* setting the control values
		// so that controls aren't considered to be modified when the initial value is set
		this.mainPane.setControlModificationListener(this.listener);
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		final EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();
		for (final Attribute attribute : attributeWidgetMap.keySet()) {
			final Control control = attributeWidgetMap.get(attribute);
			bindingProvider.bind(bindingContext, control, getValidator(attribute), getConverter(attribute), new ObservableUpdateValueStrategy() {
				@Override
				protected IStatus doSet(final IObservableValue observableValue, final Object value) {
					customer.getCustomerProfile().setProfileValue(attribute.getKey(), value);
					return Status.OK_STATUS;
				}
			}, true);
		}
	}

	private IConverter getConverter(final Attribute attribute) {
		IConverter converter = null;
		switch (attribute.getAttributeType().getTypeId()) {
		case AttributeType.BOOLEAN_TYPE_ID:
			// not needed
			break;
		case AttributeType.DECIMAL_TYPE_ID:
			converter = new EpStringToBigDecimalConverter();
			break;
		case AttributeType.IMAGE_TYPE_ID:
		case AttributeType.FILE_TYPE_ID:
		case AttributeType.SHORT_TEXT_TYPE_ID:
			// no converter needed
			break;
		case AttributeType.INTEGER_TYPE_ID:
			converter = StringToNumberConverter.toInteger(false);
			break;
		case AttributeType.DATE_TYPE_ID:
		case AttributeType.DATETIME_TYPE_ID:
			converter = new EpStringToDateConverter();
			break;
		default:
			// do nothing
		}
		return converter;
	}

	private IValidator getValidator(final Attribute attribute) {
		IValidator validator = null;
		final boolean required = attribute.isRequired();
		switch (attribute.getAttributeType().getTypeId()) {
		case AttributeType.BOOLEAN_TYPE_ID:
			// no validator needed
			break;
		case AttributeType.DECIMAL_TYPE_ID:
			validator = getDecimalValidator(required);
			break;
		case AttributeType.IMAGE_TYPE_ID:
		case AttributeType.FILE_TYPE_ID:
		case AttributeType.SHORT_TEXT_TYPE_ID:
			validator = getShortTextValidator(required);
			break;
		case AttributeType.INTEGER_TYPE_ID:
			validator = getIntegerValidator(required);
			break;
		case AttributeType.DATE_TYPE_ID:
			validator = getDateValidator(required);
			break;
		case AttributeType.DATETIME_TYPE_ID:
			validator = getDateTimeValidator(required);
			break;
		default:
			// do nothing
		}
		return validator;
	}

	private IValidator getDateTimeValidator(final boolean required) {
		IValidator validator;
		if (required) {
			validator = EpValidatorFactory.DATE_TIME_REQUIRED;
		} else {
			validator = EpValidatorFactory.DATE_TIME;
		}
		return validator;
	}

	private IValidator getDateValidator(final boolean required) {
		IValidator validator;
		if (required) {
			validator = EpValidatorFactory.DATE_REQUIRED;
		} else {
			validator = EpValidatorFactory.DATE;
		}
		return validator;
	}

	private IValidator getIntegerValidator(final boolean required) {
		IValidator validator;
		if (required) {
			validator = EpValidatorFactory.POSITIVE_INTEGER_REQUIRED;
		} else {
			validator = EpValidatorFactory.POSITIVE_INTEGER;
		}
		return validator;
	}

	private IValidator getShortTextValidator(final boolean required) {
		IValidator validator;
		if (required) {
			validator = EpValidatorFactory.STRING_255_REQUIRED;
		} else {
			validator = EpValidatorFactory.MAX_LENGTH_255;
		}
		return validator;
	}

	private IValidator getDecimalValidator(final boolean required) {
		IValidator validator;
		if (required) {
			validator = EpValidatorFactory.BIG_DECIMAL_REQUIRED;
		} else {
			validator = EpValidatorFactory.BIG_DECIMAL;
		}
		return validator;
	}

	@Override
	protected String getSectionTitle() {
		return FulfillmentMessages.get().ProfileAttributesSection_Title;
	}
}
