/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.wizard;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.domain.rules.RuleElement;
import com.elasticpath.domain.rules.RuleParameter;

/**
 * An abstract wizard page for all the input wizard pages.
 * 
 * @param <T> the class this wizard page is designed for
 */
public abstract class AbstractEPWizardPage<T> extends WizardPage {

	private static final int VERTICAL_MARGIN = 5;

	private static final int HORIZONTAL_MARGIN = 10;

	private final int numColumns;

	private final boolean equalWidthColumns;

	/** Data Binding Context for managing bindings. */
	private final DataBindingContext dataBindingContext;

	/**
	 * Constructs the wizard page.
	 * 
	 * @param numColumns columns count for the GridLayout.
	 * @param equalWidthColumns should the columns be with equal width.
	 * @param pageName name of the page.
	 * @param dataBindingContext Data Binding Context for managing bindings.
	 */
	protected AbstractEPWizardPage(final int numColumns, final boolean equalWidthColumns, final String pageName,
			final DataBindingContext dataBindingContext) {
		super(pageName);
		this.dataBindingContext = dataBindingContext;
		this.numColumns = numColumns;
		this.equalWidthColumns = equalWidthColumns;
	}

	/**
	 * Constructs the wizard page.
	 *  @param numColumns columns count for the GridLayout.
	 * @param equalWidthColumns should the columns be with equal width.
	 * @param pageName name of the page.
	 * @param titleName the titleName
	 * @param message the message
	 * @param dataBindingContext Data Binding Context for managing bindings.
	 */
	protected AbstractEPWizardPage(final int numColumns, final boolean equalWidthColumns, final String pageName, final String titleName,
								   final String message, final DataBindingContext dataBindingContext) {
		super(pageName);
		this.dataBindingContext = dataBindingContext;
		this.numColumns = numColumns;
		this.equalWidthColumns = equalWidthColumns;
		this.setTitle(titleName);
		this.setMessage(message);
	}

	/**
	 * Controls are populated and binded after all content is created.
	 * 
	 * @param parent the composite parent
	 */
	@Override
	public void createControl(final Composite parent) {
		this.createPageArea(parent);
		this.populateControls();
		this.bindControls();
	}

	/**
	 * Creates the page area.
	 * 
	 * @param parent the page parent
	 */
	protected void createPageArea(final Composite parent) {
		final IEpLayoutComposite pageComposite = CompositeFactory.createGridLayoutComposite(parent, this.numColumns, this.equalWidthColumns);
		pageComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		// alter the layout data margins for the wizard page
		final GridLayout gridLayout = (GridLayout) pageComposite.getSwtComposite().getLayout();
		gridLayout.marginLeft = HORIZONTAL_MARGIN;
		gridLayout.marginRight = HORIZONTAL_MARGIN;
		gridLayout.marginTop = VERTICAL_MARGIN;
		gridLayout.marginBottom = VERTICAL_MARGIN;

		createEpPageContent(pageComposite);

		if (getTitlePage() != null) {
			setTitle(getTitlePage());
		}
	}

	/**
	 * Creates controls on the wizard page.
	 * 
	 * @param pageComposite the EP layout composite to be used
	 */
	protected abstract void createEpPageContent(final IEpLayoutComposite pageComposite);

	/**
	 * Populates the controls for the contents.
	 */
	protected abstract void populateControls();

	/**
	 * Binds the controls for the contents.
	 */
	protected abstract void bindControls();

	/**
	 * Get the DataBindingContext.
	 * 
	 * @return the DataBindingContext.
	 */
	public DataBindingContext getDataBindingContext() {
		return dataBindingContext;
	}

	/**
	 * Descendants could override this method to change wizard page header image.
	 * 
	 * @return wizard page image.
	 */
	@Override
	public Image getImage() {
		return null;
	}

	/**
	 * Descendants could override this method to change wizard page's title.
	 * 
	 * @return wizard page title.
	 */
	protected String getTitlePage() {
		return null;
	}

	/**
	 * Is called before switching to the next page.
	 * 
	 * @param event page changing event parameters.
	 * @return if page switching is allowed.
	 */
	public boolean beforeNext(final PageChangingEvent event) {
		return true;
	}

	/**
	 * Is called before switching to the previous page.
	 * 
	 * @param event page changing event parameters.
	 * @return if page switching is allowed.
	 */
	public boolean beforePrev(final PageChangingEvent event) {
		return true;
	}

	/**
	 * Is called before switching from the next page.
	 * 
	 * @param event page changing event parameters.
	 * @return if page switching is allowed.
	 */
	public boolean beforeFromNext(final PageChangingEvent event) {
		return true;
	}

	/**
	 * Is called before switching from the previous page.
	 * 
	 * @param event page changing event parameters.
	 * @return if page switching is allowed.
	 */
	public boolean beforeFromPrev(final PageChangingEvent event) {
		return true;
	}

	/**
	 * A utility method for getting the domain object model.
	 * 
	 * @return T instance of the class
	 */
	public T getModel() {
		return ((AbstractEpWizard<T>) getWizard()).getModel();
	}

	/**
	 * Returns the RuleParamter with the given paramKey inside the given ruleElement.
	 * 
	 * @param ruleElement the RuleElement object to scan
	 * @param paramKey the String key of the RuleParameter object to retrieve
	 * @return the RuleParameter with the given paramKey
	 */
	protected RuleParameter getRuleParameterByKey(final RuleElement ruleElement, final String paramKey) {
		for (RuleParameter currRuleParameter : ruleElement.getParameters()) {
			if (paramKey.equals(currRuleParameter.getKey())) {
				return currRuleParameter;
			}
		}
		return null;
	}

}
