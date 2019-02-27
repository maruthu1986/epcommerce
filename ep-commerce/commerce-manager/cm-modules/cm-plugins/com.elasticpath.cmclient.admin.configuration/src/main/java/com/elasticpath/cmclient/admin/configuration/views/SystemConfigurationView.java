/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.admin.configuration.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import com.elasticpath.cmclient.admin.configuration.models.SettingsModel;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.views.AbstractCmClientView;

/**
 * View to show and allow the manipulation of the available SettingValues.
 */
public class SystemConfigurationView extends AbstractCmClientView {
	/** The view ID. */
	public static final String VIEW_ID = "com.elasticpath.cmclient.admin.configuration.views.SystemConfigurationView"; //$NON-NLS-1$

	private SettingsModel definitionModel;

	/**
	 * The constructor.
	 */
	public SystemConfigurationView() {
		super();
	}

	@Override
	protected void createViewPartControl(final Composite parentComposite) {

		definitionModel = new SettingsModel();

		//create a containing composite with a grid layout with a 40/60 left/right split
		IEpLayoutComposite parentEpComposite = createSystemConfigurationComposite(parentComposite);
		//create a new list view on the left for setting definition paths

		SettingDefinitionComposite settingDefinitionComposite = createSettingDefinitionComposite(parentEpComposite);

		//create a new composite on the right to hold setting value stuff
		SettingValueInformationComposite settingValueInformationComposite = createSettingValueComposite(parentEpComposite);

		settingDefinitionComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		settingValueInformationComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		settingDefinitionComposite.addSelectionChangedListener(settingValueInformationComposite);

		definitionModel.registerSettingDefinitionUpdateListener(settingDefinitionComposite);
		definitionModel.registerSettingDefinitionUpdateListener(settingValueInformationComposite);
	}

	/**
	 * Create the composite within which all the system configuration composites should be rendered.
	 * @param parentComposite the composite within which the created composite should be rendered
	 * @return the system configuration view's composite
	 */
	protected IEpLayoutComposite createSystemConfigurationComposite(final Composite parentComposite) {

//		final IEpLayoutData layoutData = parentEpComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL);
//		parentEpComposite.setLayoutData(layoutData);

		return CompositeFactory.createGridLayoutComposite(parentComposite, 2, false);
	}

	/**
	 * Create the composite within which the setting definition table should be rendered (Left-hand-side).
	 * @param parentComposite the composite within which the created composite should be rendered
	 * @return the created composite
	 */
	protected SettingDefinitionComposite createSettingDefinitionComposite(final IEpLayoutComposite parentComposite) {
		return new SettingDefinitionComposite(parentComposite.getSwtComposite(), SWT.NONE, definitionModel);
	}

	/**
	 * Create the composite within which the setting value table should be rendered (Right-hand-side).
	 * It is scrollable area.
	 *
	 * @param parentComposite the composite within which the created scrolling composite should be rendered
	 * @return the created scrolling composite
	 */
	protected SettingValueInformationComposite createSettingValueComposite(final IEpLayoutComposite parentComposite) {
		IEpLayoutComposite scrollingComposite = parentComposite.addScrolledGridLayoutComposite(1, true, false);

		return new SettingValueInformationComposite(scrollingComposite.getSwtComposite(), SWT.NONE, definitionModel);
	}

	@Override
	protected Object getModel() {
		return definitionModel;
	}

	@Override
	public void setFocus() {
		//do nothing.		
	}

	@Override
	protected String getPartId() {
		return VIEW_ID;
	}
}
