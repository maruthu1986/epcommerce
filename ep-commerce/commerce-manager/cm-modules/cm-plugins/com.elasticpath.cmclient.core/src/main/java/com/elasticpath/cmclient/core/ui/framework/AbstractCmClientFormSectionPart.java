/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.ui.framework;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

/**
 * Abstract implementation of a section on a page of a Multi-page editor. Sections typically have a heading and may have a "twistie" to hide content
 * in the section. Classes that extend this class are required to implement: createControls() -- Hook to create custom controls populateControls() --
 * Set the initial values of the controls in this method bindControls() -- Perform data binding. The methods will be called in the above order.
 * <p><b>Subclasses are encouraged to override getSectionDescription() and getSectionTitle()</b></p>
 */
public abstract class AbstractCmClientFormSectionPart extends SectionPart {
	/**
	 * Right layout margin. 
	 */
	protected static final int RIGHT_MARGIN = 10;

	/**
	 * Left layout margin.
	 */
	protected static final int LEFT_MARGIN = 10;
	
	private DataBindingContext dataBindingContext;

	private Composite controlPane;

	/**
	 * Constructor for a CmClient Form Section.
	 *
	 * @param parent the section parent.
	 * @param toolkit the formToolkit
	 * @param dataBindingContext the databinding context to use. If null, a new one will be created.
	 * @param style the style bits applicable to a <code>Section</code>
	 */
	public AbstractCmClientFormSectionPart(
			final Composite parent, final FormToolkit toolkit, final DataBindingContext dataBindingContext, final int style) {
		super(parent, toolkit, style);
		if (dataBindingContext == null) {
			throw new IllegalArgumentException("Data binding context can not be null"); //$NON-NLS-1$
		}
		this.dataBindingContext = dataBindingContext;
	}

	@Override
	public void initialize(final IManagedForm form) {
		super.initialize(form);

		final Section section = this.getSection();
		//Make colors of this section consistent by using EpFormToolkit
		if (form.getToolkit() instanceof EpFormToolkit) {
			EpFormToolkit toolkit = (EpFormToolkit) form.getToolkit();
			toolkit.assignSectionColors(section);
		}

		section.setText(this.getSectionTitle());
		section.setDescription(this.getSectionDescription());

		controlPane = form.getToolkit().createComposite(section, SWT.WRAP);
		section.setClient(controlPane);
		
		section.clientVerticalSpacing = 0;
		section.descriptionVerticalSpacing = 0;
		
		this.createControlPane(controlPane);
		this.populateControls();
		this.bindControls(getBindingContext());

		section.addExpansionListener(new ExpansionAdapter() {
			@Override
			public void expansionStateChanged(final ExpansionEvent expansionEvent) {
				form.reflow(false);
			}
		});

		section.setLayoutData(getLayoutData());
	}

	/**
	 * Returns the layout to be set to the section.
	 * 
	 * @return Object (TableWrapLayout or GridLayout)
	 */
	protected Object getLayoutData() {
		final TableWrapData tableWrapData = new TableWrapData();
		tableWrapData.align = TableWrapData.FILL;
		tableWrapData.valign = TableWrapData.TOP;
		tableWrapData.grabHorizontal = true;
		tableWrapData.grabVertical = false;
		return tableWrapData;
	}

	/**
	 * Get the editor-wide binding context for binding and validation.
	 *
	 * @return the DataBindingContext
	 */
	public DataBindingContext getBindingContext() {
		return this.dataBindingContext;
	}
	
	/**
	 * Set the editor-wide binding context for binding and validation.
	 *
	 * @param newContext data binding context
	 */
	protected void setBindingContext(final DataBindingContext newContext) {
		this.dataBindingContext = newContext;
	}
	

	/**
	 * Create the pane inside the section that will contain the section's controls.
	 *
	 * @param controlPane the control pane
	 */
	private void createControlPane(final Composite controlPane) {
		controlPane.setLayout(getLayout());

		controlPane.setLayoutData(getLayoutData());

		this.createControls(controlPane, this.getManagedForm().getToolkit());
	}

	/**
	 * Returns the layout for this section.
	 * 
	 * @return layout instance
	 */
	protected Layout getLayout() {
		final TableWrapLayout layout = new TableWrapLayout();
		layout.leftMargin = LEFT_MARGIN;
		layout.rightMargin = RIGHT_MARGIN;
		layout.numColumns = 2;
		layout.topMargin = 0;
		layout.bottomMargin = 0;
		layout.verticalSpacing = 0;
		return layout;
	}

	/**
	 * Override to create the controls to be displayed on the page section.
	 *
	 * @param client the parent composite to add controls to
	 * @param toolkit the FormToolKit
	 */
	protected abstract void createControls(final Composite client, final FormToolkit toolkit);

	/**
	 * Override to populate the controls with their initial values.
	 */
	protected abstract void populateControls();

	/**
	 * Override to perform the bindings between the controls and the domain model.
	 * 
	 * @param bindingContext the data binding context to use for binding the controls
	 */
	protected abstract void bindControls(DataBindingContext bindingContext);

	/**
	 * Override to set the section title, which is blank by default.
	 *
	 * @return the section title
	 */
	protected String getSectionTitle() {
		return ""; //$NON-NLS-1$
	}

	/**
	 * Override to set the section description, which is blank by default.
	 * This will not get displayed unless this section part has style bit {@link Section#DESCRIPTION}.
	 *
	 * @return the section description
	 */
	protected String getSectionDescription() {
		return ""; //$NON-NLS-1$
	}
	
	@Override
	public void dispose() {
		if (!controlPane.isDisposed()) {
			controlPane.dispose();
		}
		if (!getSection().isDisposed()) {
			getSection().dispose();
		}
	}
	
	/**
	 *  Called when com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor.refreshEditorPages releases control.
	 *  Can be overridden to expose some clean-up activities.  
	 */
	public void sectionDisposed() {
		// nothing by default.
	}
}
