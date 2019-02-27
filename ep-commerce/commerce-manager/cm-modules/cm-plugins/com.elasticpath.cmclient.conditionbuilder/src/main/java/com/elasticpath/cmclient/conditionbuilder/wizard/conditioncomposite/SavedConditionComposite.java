/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.conditionbuilder.wizard.conditioncomposite;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.elasticpath.cmclient.conditionbuilder.adapter.BaseModelAdapter;
import com.elasticpath.cmclient.conditionbuilder.adapter.impl.tag.BaseModelAdapterImpl;
import com.elasticpath.cmclient.conditionbuilder.wizard.pages.GridLayoutUtil;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpValueBinding;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.tags.domain.ConditionalExpression;

/**
 * Class contains composite that holds lines with named conditions.
 */
public class SavedConditionComposite extends Composite {

	private static final int VISIBLE_ENTRIES_5 = 5;

	private static final int LEFT_MARGIN_17 = 17;

	private final EpControlBindingProvider epControlBindingProvider;
	
	private final DataBindingContext dataBindingContext;
	
	private final List<ConditionalExpression> conditionsList;
	
	private EpValueBinding comboBinding;
	
	private BaseModelAdapter<ConditionalExpression> modelAdapter;
	
	private final ComboViewer conditionalExpressionCombo;
	
	/**
	 * Constructor.
	 * 
	 * @param composite parent composite
	 * @param dataBindingContext data binding context
	 * @param epControlBindingProvider control binding provider 
	 * @param modelAdapter model adapter
	 * @param conditionsList conditions list
	 * @param listener selection change listener
	 * @param pleaseSelectMessage label for select widget
	 */
	public SavedConditionComposite(final Composite composite, 
			final DataBindingContext dataBindingContext, 
			final EpControlBindingProvider epControlBindingProvider, 
			final BaseModelAdapter<ConditionalExpression> modelAdapter,
			final List<ConditionalExpression> conditionsList,
			final ISelectionChangedListener listener,
			final String pleaseSelectMessage
			) {
		super(composite, composite.getStyle());
		
		this.epControlBindingProvider = epControlBindingProvider;
		this.dataBindingContext = dataBindingContext;
		this.modelAdapter = modelAdapter;
		this.conditionsList = new ArrayList<>();
		if (conditionsList != null) {
			this.conditionsList.addAll(conditionsList);
		}
		(this.conditionsList).sort((cExp1, cExp2) -> cExp1.getName().compareToIgnoreCase(cExp2.getName()));
		// create dummy expression for "select..." prompt
		ConditionalExpression dummyExpression = ServiceLocator.getService(ContextIdNames.CONDITIONAL_EXPRESSION);
		dummyExpression.setName(pleaseSelectMessage);
		this.conditionsList.add(0, dummyExpression);
		
		// layout for this
		GridLayout gridLayout = new GridLayout(1, false);
		this.setLayout(gridLayout);
		
		// combo
		conditionalExpressionCombo = new ComboViewer(this, SWT.READ_ONLY);
		conditionalExpressionCombo.setContentProvider(new IStructuredContentProvider() {
			public void dispose() {
				//
			}
			public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
				//
			}
			public Object[] getElements(final Object inputElement) {
				return ((List<ConditionalExpression>) inputElement).toArray();
			} });
		
		conditionalExpressionCombo.setLabelProvider(new LabelProvider() {
			public String getText(final Object element) {
				return ((ConditionalExpression) element).getName();
			} });
		conditionalExpressionCombo.setInput(this.conditionsList);
		conditionalExpressionCombo.getCombo().setLayout(this.createGridLayout(1));
		conditionalExpressionCombo.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
		conditionalExpressionCombo.getCombo().setVisibleItemCount(VISIBLE_ENTRIES_5);
		ConditionalExpression model = modelAdapter.getModel();
		int index = this.conditionsList.indexOf(model);
		if (index == -1) {
			conditionalExpressionCombo.getCombo().setText(pleaseSelectMessage);
		} else {
			conditionalExpressionCombo.getCombo().select(index);
		}
		conditionalExpressionCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(final SelectionChangedEvent event) {
				ConditionalExpression expression = (ConditionalExpression) ((IStructuredSelection) event.getSelection()).getFirstElement();
				SavedConditionComposite.this.modelAdapter = new BaseModelAdapterImpl<>(expression);
			} });
		conditionalExpressionCombo.addSelectionChangedListener(listener);
	}

	/**
	 * Get the model adapter.
	 * @return the modelAdapter
	 */
	public BaseModelAdapter<ConditionalExpression> getModelAdapter() {
		return modelAdapter;
	}
	
	/**
	 * Binds combo to model.
	 */
	public void bindNamedConditionToModel() {
		if (null == comboBinding) {
			comboBinding = epControlBindingProvider.bind(dataBindingContext,
					conditionalExpressionCombo.getControl(), 
					EpValidatorFactory.REQUIRED_COMBO_FIRST_ELEMENT_NOT_VALID, 
					null,
					new ObservableUpdateValueStrategy() {
						@Override
						protected IStatus doSet(
								final IObservableValue observableValue,
								final Object value) {
//							Integer cIndex = (Integer) value;
//							selectedCondition = conditionsList.get(cIndex);
//							setSavedConditionToModel();
							return Status.OK_STATUS;
						}
					}, 
					true);
		}
	}
	
	/**
	 * Removes registered combo binding.
	 */
	public void unbindCondition() {
		if (null != comboBinding && null != dataBindingContext) {
			EpControlBindingProvider.removeEpValueBinding(dataBindingContext, comboBinding);
			comboBinding = null;
		}
	}
	
	private GridLayout createGridLayout(final int numberOfColumns) {
		GridLayout layout = GridLayoutUtil.getBorderlessLayout(numberOfColumns);
		layout.marginLeft = LEFT_MARGIN_17;
		return layout;
	}
	
}
