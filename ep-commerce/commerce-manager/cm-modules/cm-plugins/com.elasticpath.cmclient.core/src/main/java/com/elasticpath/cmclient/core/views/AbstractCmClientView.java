/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.views;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import com.elasticpath.cmclient.core.helpers.EPTestUtilFactory;
import com.elasticpath.cmclient.core.pagination.PaginationInfo;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;

/**
 * Abstract class to be implemented by all views.
 */
public abstract class AbstractCmClientView extends ViewPart {

	private transient DataBindingContext dataBindingContext;

	/**
	 * Constructs the abstract class.
	 */
	protected AbstractCmClientView() {
		super();
	}

	/**
	 * Gets the singleton instance of the binding provider.
	 * 
	 * @return binding provider
	 */
	protected EpControlBindingProvider getBindingProvider() {
		return EpControlBindingProvider.getInstance();
	}

	@Override
	public void init(final IViewSite site) throws PartInitException {
		super.init(site);
		this.dataBindingContext = new DataBindingContext();
	}

	@Override
	public void createPartControl(final Composite parent) {
		EPTestUtilFactory.getInstance().getTestIdUtil().setAutomationId(parent, getPartId());

		this.createViewPartControl(parent);
	}

	/**
	 * Should return the model object.
	 * 
	 * @return model object
	 */
	protected abstract Object getModel();

	/**
	 * All the controls and composites should be created in this method.
	 * 
	 * @param parentComposite the parent EP composite
	 */
	protected abstract void createViewPartControl(Composite parentComposite);

	/**
	 * Returns the <code>DataBindingContext</code> associated with this view.
	 * 
	 * @return the dataBindingContext instance
	 */
	public DataBindingContext getDataBindingContext() {
		return this.dataBindingContext;
	}

	/**
	 * Binds the SWT control to a model object using a custom {@link ObservableUpdateValueStrategy}.
	 * This implementation does not hide the control decoration on the first validation pass 
	 * which is useful only when you don't want to validate the control at bind time.
	 * 
	 * @param control SWT control to be bound
	 * @param validator validator if needed
	 * @param converter converter if needed
	 * @param customUpdateStrategy <code>ObservableUpdateValueStrategy</code>
	 */
	protected void bind(final Control control, final IValidator validator, final Converter converter,
			final ObservableUpdateValueStrategy customUpdateStrategy) {
		this.getBindingProvider().bind(this.getDataBindingContext(), control, validator, converter, customUpdateStrategy, false);
	}

	/**
	 * Binds the SWT control to a model object field.
	 * This implementation does not hide the control decoration on the first validation pass,
	 * which is useful only when you don't want to validate the control at bind time.
	 * 
	 * @param control the SWT control
	 * @param target model's object
	 * @param fieldName model's object field name
	 * @param validator validator if needed
	 * @param converter converter if needed
	 */
	protected void bind(final Control control, final Object target, final String fieldName, final IValidator validator, final Converter converter) {
		this.getBindingProvider().bind(this.getDataBindingContext(), control, target, fieldName, validator, converter, false);
	}

	
	/**
	 * Get the pagination.
	 * 
	 * @return pagination settings for the application
	 */
	protected int getPagination() {
		return PaginationInfo.getInstance().getPagination();
	}

	/**
	 * Getter for the part id.
	 * @return part id
	 */
	protected abstract String getPartId();
}
