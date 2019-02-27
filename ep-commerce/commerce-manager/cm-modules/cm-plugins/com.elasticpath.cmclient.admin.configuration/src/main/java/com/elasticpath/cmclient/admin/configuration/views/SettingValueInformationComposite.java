/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.admin.configuration.views;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.elasticpath.cmclient.admin.configuration.AdminConfigurationMessages;
import com.elasticpath.cmclient.admin.configuration.listener.SettingDefinitionUpdateListener;
import com.elasticpath.cmclient.admin.configuration.models.SettingsModel;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.settings.domain.SettingDefinition;

/**
 * A composite to display information about SettingValues and the SettingDefinition to which
 * they correspond.
 */
public class SettingValueInformationComposite extends Composite implements ISelectionChangedListener, SettingDefinitionUpdateListener {

	private SettingDefinitionDisplayComposite displayComposite;
	private SettingValueComposite valueComposite;
	private SettingMetadataComposite metadataComposite;
	private Section metadataExpandableSection;

	private final FormToolkit formToolkit;
	private SettingDefinition selectedDefinition;
	
	private final SettingsModel definitionModel;
	

	/**
	 * Constructor.
	 * @param parent the parent composite
	 * @param style the style bits
	 * @param definitionModel the service that will be used to get information about settings
	 */
	public SettingValueInformationComposite(final Composite parent, final int style, final SettingsModel definitionModel) {
		super(parent, style);
		formToolkit = EpControlFactory.getInstance().createFormToolkit();
		formToolkit.adapt(this);
		this.definitionModel = definitionModel;
		createControls();
	}

	private void createControls() {
		setupLayout();
		
		displayComposite = new SettingDefinitionDisplayComposite(this, SWT.NONE, false);
		displayComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));

		metadataExpandableSection = formToolkit.createSection(this,
				ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);  // Start 'closed'
		metadataExpandableSection.setText(AdminConfigurationMessages.get().settingDefMetadata);
		metadataExpandableSection.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		
		metadataComposite = new SettingMetadataComposite(metadataExpandableSection, SWT.NONE, definitionModel);
		metadataComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		metadataExpandableSection.setEnabled(false);
		metadataExpandableSection.setClient(metadataComposite);

		Section valueCompositeSection = formToolkit.createSection(this, ExpandableComposite.TITLE_BAR
				| ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED);
		valueCompositeSection.setText(AdminConfigurationMessages.get().definedValues);
		valueCompositeSection.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		valueComposite = new SettingValueComposite(valueCompositeSection, SWT.NONE, definitionModel);
		valueComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		valueCompositeSection.setClient(valueComposite);

		definitionModel.registerSettingValueUpdateListener(valueComposite);
		definitionModel.registerSettingDefinitionUpdateListener(metadataComposite);
	}
	

	private void setupLayout() {
		this.setLayout(new GridLayout(1, true));
	}
	
	/**
	 * If the setting definition that's being displayed should change, this implementation
	 * will ensure that this composite's display changes appropriately by showing
	 * details of the setting definition and its SettingValues.
	 * @param selectionChangedEvent the event containing the new SettingDefinition
	 */
	@Override
	public void selectionChanged(final SelectionChangedEvent selectionChangedEvent) {
		if ((selectionChangedEvent.getSelection() instanceof IStructuredSelection) 
				&& ((IStructuredSelection) selectionChangedEvent.getSelection()).getFirstElement() instanceof SettingDefinition) {
			
			this.selectedDefinition = (SettingDefinition) ((IStructuredSelection) selectionChangedEvent.getSelection()).getFirstElement();
			this.displayComposite.setSettingDefinition(selectedDefinition);
			this.valueComposite.setSettingDefinition(selectedDefinition);
			this.metadataComposite.setSettingDefinition(selectedDefinition);
			this.metadataExpandableSection.setEnabled(true);
		}
	}
	
	
	

	/**
	 * {@inheritDoc}
	 * 
	 * Listens on any updates to the definition we are operating on.
	 */
	@Override
	public void settingDefinitionUpdated(final SettingDefinition event) {
		if (this.selectedDefinition.getPath().equals(event.getPath())) {
			displayComposite.setSettingDefinition(event);
			valueComposite.setSettingDefinition(event);
		}
	}
	
}
