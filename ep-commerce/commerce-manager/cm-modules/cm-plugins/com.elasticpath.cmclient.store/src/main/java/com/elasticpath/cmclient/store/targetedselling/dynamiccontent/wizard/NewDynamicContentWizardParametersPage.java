/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.targetedselling.dynamiccontent.wizard;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.dialogs.PageChangingEvent;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpWizardPageSupport;
import com.elasticpath.cmclient.core.dialog.value.support.IValueChangedListener;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.wizard.AbstractEpWizard;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareWizardPage;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingMessages;
import com.elasticpath.cmclient.store.targetedselling.dynamiccontent.wizard.parameters.ParametersTableSection;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.contentspace.ContentWrapper;
import com.elasticpath.domain.contentspace.DynamicContent;
import com.elasticpath.domain.contentspace.Parameter;
import com.elasticpath.domain.contentspace.ParameterValue;

/**
 * The new promotions details parameters wizard page.
 */
public class NewDynamicContentWizardParametersPage extends AbstractPolicyAwareWizardPage<DynamicContent> 
	implements IValueChangedListener<ParameterValue> {

	private static final Logger LOG = Logger.getLogger(NewDynamicContentWizardParametersPage.class);

	private ParametersTableSection parametersViewPart;

	private DynamicContent model;

	private IEpLayoutComposite parent;

	/**
	 * Default constructor.
	 * 
	 * @param pageName the name of the page
	 * @param title the page title
	 */
	protected NewDynamicContentWizardParametersPage(final String pageName, final String title) {
		super(2, false, pageName, title, TargetedSellingMessages.get().DynamicContentWizardParametersPage_Description, new DataBindingContext());
	}

	@Override
	protected void bindControls() {
		// Create the new content wizard details page
		EpWizardPageSupport.create(NewDynamicContentWizardParametersPage.this, getDataBindingContext());
	}

	/**
	 * Handles table parameters population from the model.
	 * 
	 * @param model - the <code>DynamicContent</code> model.
	 * @param wrapper - the <code>ContentWrapper</code> model.
	 */
	public void initParametersListFromModel(final DynamicContent model, final ContentWrapper wrapper) {
		this.model = model;

		List<ParameterValue> userInputSettings = getParameterValueListFrom(wrapper.getUserInputSettings());
		// NOTE: template parameters are not to be used in the UI.
		// List<ParameterValue> templateParameters = getParameterValueListFrom(wrapper.getTemplateParameters());
		// userInputSettings.addAll(templateParameters);
		if (CollectionUtils.isEmpty(model.getParameterValues())) {
			model.setParameterValues(userInputSettings);
		} else {
			// for each parameter in wrapper
			for (ParameterValue inputParam : userInputSettings) {
				// find such parameter in database
				boolean updated = false;
				for (ParameterValue value : model.getParameterValues()) {
					if (inputParam.getParameterName().equals(value.getParameterName())) {
						// update parameter info
						value.setParameter(inputParam.getParameter());
						updated = true;
						break;
					}
				}
				// if value not synchronised need to add this parameter
				if (!updated) {
					model.getParameterValues().add(inputParam);
				}
			}
		}

		parametersViewPart.setModel(model);
		parametersViewPart.refreshTableModelForNewLocale();
	}

	/**
	 * Handles table parameters population from the selected <code>ContentWrapper</code>.
	 * 
	 * @param model - the <code>DynamicContent</code> model.
	 * @param wrapper - the <code>ContentWrapper</code> model.
	 */
	public void initParametersListFromWrapper(final DynamicContent model, final ContentWrapper wrapper) {
		List<ParameterValue> userInputSettings = getParameterValueListFrom(wrapper.getUserInputSettings());
		// NOTE: template parameters are not to be used in the UI.
		// List<ParameterValue> templateParameters = getParameterValueListFrom(wrapper.getTemplateParameters());
		// userInputSettings.addAll(templateParameters);
		model.setParameterValues(userInputSettings);
		this.model = model;

		parametersViewPart.setModel(model);
		parametersViewPart.refreshTableModelForNewLocale();
	}

	private List<ParameterValue> getParameterValueListFrom(final List<Parameter> parameters) {
		List<ParameterValue> parameterValues = new LinkedList<>();
		for (Parameter parameter : parameters) {
			if (parameter.getScriptExpression() == null) {
				ParameterValue parameterValue = ServiceLocator.getService(ContextIdNames.DYNAMIC_CONTENT_WRAPPER_PARAMETER_VALUE);
				parameterValue.setLocalizable(parameter.isLocalizable());
				parameterValue.setParameter(parameter);
				parameterValue.setParameterName(parameter.getParameterId());
				parameterValue.setDescription(parameter.getDescription());
				parameterValues.add(parameterValue);
			}
		}
		return parameterValues;
	}

	@Override
	protected void populateControls() {
		//nothing to do...
	}

	@Override
	public DynamicContent getModel() {
		return this.model;
	}

	@Override
	public boolean beforeNext(final PageChangingEvent event) {
		LOG.debug("before next"); //$NON-NLS-1$
		return isValidParametersSection();
	}

	/**
	 * Checks parameter section to validate that all mandatory parameters are entered. Scans all available languages and all
	 * required parameters.
	 * 
	 * @return true if all required parameters for all available locales are filled in, false otherwise
	 */
	private boolean isValidParametersSection() {
		// data to validate
		final DynamicContent model = parametersViewPart.getModel();
		// locales to check
		final SortedSet<Locale> availableLocales = parametersViewPart.getAvailableLocales();


		for (Locale locale : availableLocales) {
			boolean isValid = true;
			if (CollectionUtils.isNotEmpty(model.getParameterValues())) {
				for (ParameterValue parameterValue : model.getParameterValues()) {
					isValid = checkParameterValue(parameterValue, locale);
					if (!isValid) {
						break;
					}
				}

			}
			if (isValid) {
				return true; // if successfully verified at least one locale - return success
			}
		}
		return false;
	}

	/**
	 * check a single parameter value.
	 * 
	 * @param parameterValue the value to check
	 * @param locale of the parameter to check
	 * @return OK status if parameter is not required or all values for availabeLocales are present
	 */
	private boolean checkParameterValue(final ParameterValue parameterValue, final Locale locale) {
		// only required are checked
		if (parameterValue != null && parameterValue.getParameter() != null && parameterValue.getParameter().isRequired()) {
			final String value = parameterValue.getValue(locale.toString());
			if (StringUtils.isEmpty(value)) {
				return false;
			}

		}
		return true;
	}

	@Override
	public boolean isPageComplete() {
		final boolean isCompleteSuper = super.isPageComplete();
		final boolean isWrapperInitialized = ((NewDynamicContentWizardWrapperPage) getWizard().getStartingPage()).isParametersInitialised();
		final boolean isComplete = isWrapperInitialized && isValidParametersSection();
		LOG.debug("Param Page is complete? ..." + isCompleteSuper + " (super), " + isComplete + " (this)"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		return isComplete;
	}

	@Override
	public void valueChanged(final ParameterValue attributeValue) {
		LOG.debug("attribute changed"); //$NON-NLS-1$
		((AbstractEpWizard<?>) getWizard()).getWizardDialog().updateButtons();
	}
	
	@Override
	protected void createPageContents(final IPolicyTargetLayoutComposite policyComposite) {
		this.parent = policyComposite.getLayoutComposite();
		
		PolicyActionContainer policyActionContainer = addPolicyActionContainer("dynamicContentWizardParametersPage");  //$NON-NLS-1$

		parametersViewPart = new ParametersTableSection(model, this);
		parametersViewPart.createControls(policyComposite);
		
		policyActionContainer.addDelegate(parametersViewPart);

		/* MUST be called */
		setControl(this.parent.getSwtComposite());
	}

}
