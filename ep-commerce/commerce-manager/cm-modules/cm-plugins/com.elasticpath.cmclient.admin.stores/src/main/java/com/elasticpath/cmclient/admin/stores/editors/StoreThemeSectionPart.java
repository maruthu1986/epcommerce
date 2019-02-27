/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin.stores.editors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.elasticpath.cmclient.admin.stores.AdminStoresMessages;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPageSectionPart;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.helpers.store.StoreEditorModel;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;

/**
 * Represents the store theme section part.
 */
final class StoreThemeSectionPart extends AbstractCmClientEditorPageSectionPart {

	private IEpLayoutComposite controlPane;

	private final Map<String, Button> themeButtons;

	private final List<String> themeNames;
	
	/**
	 * Constructs store theme section part.
	 * 
	 * @param formPage the form page.
	 * @param editor the editor
	 * @param style the style
	 * @param themeNames the list of theme names
	 * @param authorized whether the page is editable by the current user
	 */
	StoreThemeSectionPart(final FormPage formPage, final AbstractCmClientFormEditor editor, 
			final int style, final List<String> themeNames, final boolean authorized) {
		super(formPage, editor, style);
		this.themeButtons = new HashMap<>();
		this.themeNames = themeNames;
		this.getSection().setEnabled(authorized);
	}

	@Override
	public void initialize(final IManagedForm form) {
		super.initialize(form);
		controlPane.setControlModificationListener(getEditor());
	}

	@Override
	protected String getSectionTitle() {
		return AdminStoresMessages.get().StoreAssignedTheme;
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		final EpControlBindingProvider binder = EpControlBindingProvider.getInstance();

		for (final Button entry : themeButtons.values()) {
			binder.bind(bindingContext, entry, null, null, createUpdateStrategyFor(entry), false);
		}
	}

	private ObservableUpdateValueStrategy createUpdateStrategyFor(final Button button) {
		return new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				if (newValue.equals(Boolean.TRUE)) {
					getStoreEditorModel().setStoreThemeSetting((String) button.getData());
				}
				return Status.OK_STATUS;
			}
		};
	}

	@Override
	protected void createControls(final Composite client, final FormToolkit toolkit) {
		controlPane = CompositeFactory.createGridLayoutComposite(client, 1, false);
		final IEpLayoutData layoutData = controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true);

		Button noThemeButton = controlPane.addRadioButton(AdminStoresMessages.get().NoneSelection, null, EpState.EDITABLE, layoutData);
		noThemeButton.setData(StringUtils.EMPTY);
		noThemeButton.setEnabled(getStoreEditorModel().getStoreState().isIncomplete());
		themeButtons.put(StringUtils.EMPTY, noThemeButton);
		
		for (final String themeName : themeNames) {
			final Button themeButton = controlPane.addRadioButton(themeName, EpState.EDITABLE, layoutData);
			themeButton.setData(themeName);
			themeButtons.put(themeName, themeButton);
		}
	}

	@Override
	protected void populateControls() {
		String themeName = (getStoreEditorModel()).getStoreThemeSetting();
		if (themeButtons.containsKey(themeName)) {
			themeButtons.get(themeName).setSelection(true);
		}
	}

	private StoreEditorModel getStoreEditorModel() {
		return (StoreEditorModel) getModel();
	}
}