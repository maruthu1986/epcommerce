/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin.stores.editors;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
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
import com.elasticpath.persistence.api.Persistable;

/**
 * Abstract UI representation of the Store Assigned Section.
 * 
 * @param <Domain> the domain interface TODO : This Class must be refactored.
 */
public abstract class AbstractStoreAssignedSectionPart<Domain extends Persistable> extends AbstractCmClientEditorPageSectionPart {

	/**
	 * Internal DomainButton.
	 */
	private class DomainButton {
		private final Domain domain;

		private final Button button;

		DomainButton(final Domain domain, final Button button) {
			this.domain = domain;
			this.button = button;
		}

		DomainButton(final Button button) {
			this(null, button);
		}

		public Domain getDomain() {
			return domain;
		}

		public Button getButton() {
			return button;
		}
	}

	private final Map<Long, DomainButton> domainButtons = new HashMap<>();

	private IEpLayoutComposite controlPane;

	/**
	 * Constructor.
	 * 
	 * @param editor the editor containing this Section Constructor to create a new Section in an editor's FormPage.
	 * @param formPage the form page
	 */
	public AbstractStoreAssignedSectionPart(final FormPage formPage, final AbstractCmClientFormEditor editor) {
		super(formPage, editor, ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
	}

	/**
	 * Gets Current StoreEditorModel.
	 * 
	 * @return Store Instance
	 */
	protected StoreEditorModel getStoreEditorModel() {
		return (StoreEditorModel) getEditor().getModel();
	}

	/**
	 * Sets the Selected Domain Object to Store.
	 * 
	 * @param domain selected Domain
	 */
	protected abstract void setSelectedDomain(final Domain domain);

	/**
	 * Gets the Selected Domain Object from Store.
	 * 
	 * @return Domain instance
	 */
	protected abstract Domain getSelectedDomain();

	/**
	 * Lists all Domain objects of the Store.
	 * 
	 * @return List of Domains
	 */
	protected abstract Collection<Domain> listAllDomainObjects();

	/**
	 * Gets the name identifier of the Domain object.
	 * 
	 * @param domain the Domain
	 * @return String Identifier
	 */
	protected abstract String getDomainName(final Domain domain);

	/**
	 * Gets Image of the Domain Object.
	 * 
	 * @param domain the Domain.
	 * @return Image that corresponds with domain
	 */
	protected abstract Image getDomainImage(final Domain domain);

	@Override
	protected void createControls(final Composite client, final FormToolkit toolkit) {
		controlPane = CompositeFactory.createGridLayoutComposite(client, 1, false);
		final IEpLayoutData layoutData = controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true);

		domainButtons.put(null, new DomainButton(controlPane.addRadioButton(AdminStoresMessages.get().NoneSelection,
				null, EpState.EDITABLE, layoutData)));
		for (final Domain domain : listAllDomainObjects()) {
			final Button catalogButton = controlPane.addRadioButton(getDomainName(domain), getDomainImage(domain), EpState.EDITABLE, layoutData);
			domainButtons.put(domain.getUidPk(), new DomainButton(domain, catalogButton));
		}
	}

	@Override
	protected void populateControls() {
		setEnabled(getStoreEditorModel().getStoreState().isIncomplete());
		setEnabled(true, getSelectedDomain());
		domainButtons.get(getSelectedUidPk()).getButton().setSelection(true);
		controlPane.setControlModificationListener(getEditor());
	}

	/**
	 * Sets the status enabled or disabled for all radio buttons. 
	 * 
	 * @param status true if radio buttons should be enabled and false otherwise.
	 */
	protected void setEnabled(final boolean status) {
		for (Entry<Long, DomainButton> entry : domainButtons.entrySet()) {
			entry.getValue().getButton().setEnabled(status);
		}
	}

	/**
	 * Sets the status for radio button that binds with given domain object.
	 * 
	 * @param status true if radio button should be enabled and false otherwise.
	 * @param domain the domain object
	 */
	protected void setEnabled(final boolean status, final Domain domain) {
		Long uidPk = null;
		if (domain != null) {
			uidPk = domain.getUidPk();
		}
		domainButtons.get(uidPk).getButton().setEnabled(status);
	}

	/**
	 * Gets the UID for selected domain object.
	 * 
	 * @return the UID for selected domain object
	 */
	protected Long getSelectedUidPk() {
		final Domain selectedDomain = getSelectedDomain();
		if (selectedDomain == null) {
			return null;
		}
		return selectedDomain.getUidPk();
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		final EpControlBindingProvider binder = EpControlBindingProvider.getInstance();

		for (final DomainButton entry : domainButtons.values()) {
			binder.bind(bindingContext, entry.getButton(), null, null, createUpdateStrategyFor(entry.getDomain()), false);
		}
	}

	private ObservableUpdateValueStrategy createUpdateStrategyFor(final Domain domain) {
		return new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				if (newValue.equals(Boolean.TRUE)) {
					setSelectedDomain(domain);
				}
				return Status.OK_STATUS;
			}
		};
	}

	/**
	 * Gets the store status.
	 * 
	 * @return true if it is existent store and false otherwise
	 */
	protected boolean isEditStore() {
		return getStoreEditorModel().isPersistent();
	}
}
