/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 *
 */
package com.elasticpath.cmclient.conditionbuilder.component;

import java.beans.PropertyChangeListener;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

import com.elasticpath.cmclient.conditionbuilder.adapter.DataAdapter;
import com.elasticpath.cmclient.conditionbuilder.adapter.LogicalOperatorModelAdapter;
import com.elasticpath.cmclient.conditionbuilder.adapter.ResourceAdapter;
import com.elasticpath.cmclient.conditionbuilder.adapter.ResourceAdapterFactory;
import com.elasticpath.cmclient.conditionbuilder.valueeditor.ConditionRowValueFactory;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.helpers.CompositeLayoutUtility;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.tags.domain.TagDefinition;
import com.elasticpath.tags.domain.TagGroup;

/**
 * ConditionBlockComposite.
 *
 * @param <M>     model type
 * @param <A>     adapter type
 * @param <O>     operator type
 * @param <GROUP> group type
 */
public class ConditionBlockComposite<M, A, O, GROUP>
	extends ContainerComposite<LogicalOperatorModelAdapter<M, O>, A> {

	private static final int WIDTH = 3;
	private static final int MAX_ARRAY_SIZE = 18;
	private static final int ARRAY_SIZE_WITHOUT_DELETE_LINK = MAX_ARRAY_SIZE - 4;
	private static final int HORIZONTAL_INDENT = 10;

	private static final int MARGIN_HEIGHT = 5;
	private static final int MARGIN_WIDTH = 5;

	private static final String REMOVE_RULE_LABEL = "ConditionBuilder_Remove_Rule_label";
	private Rectangle rectContainer;
	private Rectangle rectDeleteLink;
	private Rectangle rectLeftControl;
	private Rectangle rectThis;
	private Rectangle rectAddRule;

	private Composite leftComposite;
	private Control leftControl;
	private final Control deleteLink;
	private final ImageHyperlink addRuleLink;

	private final ResourceAdapter<String> resourceAdapterForUiElements;

	private final LogicalOperatorModelAdapter<M, O> parentModelAdapter;
	private PropertyChangeListener parentPropertyChangeListener;
	private final String titleResourceKey;
	private final EpControlFactory controlFactory = EpControlFactory.getInstance();

	/**
	 * Default constructor.
	 *
	 * @param parent                 parent composite
	 * @param model                  model
	 * @param parentModelAdapter     parent model adapter
	 * @param resourceAdapterFactory resource adapter factory
	 * @param dataAdapterForGroup    data adapter, group -> tag definition list
	 * @param groupsList             tag groups list
	 * @param titleResourceKey       title resource key
	 */
	public ConditionBlockComposite(final Composite parent,
		final LogicalOperatorModelAdapter<M, O> model,
		final LogicalOperatorModelAdapter<M, O> parentModelAdapter,
		final ResourceAdapterFactory resourceAdapterFactory,
		final DataAdapter<GROUP, A> dataAdapterForGroup,
		final Set<GROUP> groupsList,
		final String titleResourceKey) {
		super(parent, parent.getStyle(), model, 2);

		this.parentModelAdapter = parentModelAdapter;
		ResourceAdapter<A> resourceAdapterForTagDefinitions = (ResourceAdapter<A>) resourceAdapterFactory.getResourceAdapter(TagDefinition.class);
		ResourceAdapter<GROUP> resourceAdapterForGroup = (ResourceAdapter<GROUP>) resourceAdapterFactory.getResourceAdapter(TagGroup.class);
		this.resourceAdapterForUiElements = resourceAdapterFactory.getResourceAdapter(String.class);

		this.titleResourceKey = titleResourceKey;

		int swtStyle = parent.getStyle();

		this.deleteLink = this.createDeleteLink(this, swtStyle);

		this.createLeftColumn(parent, swtStyle);

		this.createContainerComposite(this, swtStyle);

		// add button + menu
		this.addRuleLink = this.createImageHyperlinkForAddRule(this, swtStyle, null);
		final Menu menu = this.createPopupMenuWithSubmenu(this.addRuleLink,
			resourceAdapterForGroup,
			resourceAdapterForTagDefinitions,
			groupsList,
			dataAdapterForGroup,
			null,
			new SelectionListener() {
				public void widgetDefaultSelected(final SelectionEvent event) {
					//
				}

				public void widgetSelected(final SelectionEvent event) {
					MenuItem menuItem = (MenuItem) event.getSource();
					A tag = (A) menuItem.getData();
					ConditionBlockComposite.this.fireEventForAdd(tag);
				}
			});

		this.addRuleLink.setMenu(menu);
		this.addRuleLink.addHyperlinkListener(new IHyperlinkListener() {
			public void linkActivated(final HyperlinkEvent event) {
				menu.setLocation(CompositeLayoutUtility.getAbsoluteLocation(addRuleLink));
				menu.setVisible(true);

			}

			public void linkEntered(final HyperlinkEvent event) {
				//
			}

			public void linkExited(final HyperlinkEvent event) {
				//
			}
		});

		this.addPaintListener((PaintListener) paintEvent -> changeBorder(paintEvent.gc));

		this.addListenerForDelete(
			object -> {
				for (Control control : getContainerComposite().getChildren()) {
					control.notifyListeners(ConditionRowValueFactory.EVENT_FOR_UNBIND, new Event());
				}
			});
	}

	private void displayLogicalOperator(final Composite parent, final int swtStyle, final GridData layoutData) {
		boolean isFirstRow = this.equals(parent.getChildren()[0]);
		boolean isSecondRow = false;
		if (parent.getChildren().length >= 2) {
			isSecondRow = this.equals(parent.getChildren()[1]);
		}
		if (this.leftControl != null) {
			this.leftControl.dispose();
			this.leftControl = null;
		}
		if (isFirstRow) {
			this.leftControl = createLabelForTitle(this.leftComposite, swtStyle);
			this.leftControl.setBackground(this.leftComposite.getBackground());
		} else if (isSecondRow) {
			this.leftControl = createComboForLogicalOperator(this.leftComposite, swtStyle, this.parentModelAdapter);
		} else {
			this.leftControl = createLabelForLogicalOperator(this.leftComposite, swtStyle);
			this.leftControl.setBackground(this.leftComposite.getBackground());
		}
		this.leftControl.setLayoutData(layoutData);
	}

	private Control createLabelForLogicalOperator(final Composite parent, final int swtStyle) {
		return controlFactory.createLabel(parent,
			this.parentModelAdapter.getResourceAdapterForOperator().getLocalizedResource(this.parentModelAdapter.getLogicalOperator()),
			swtStyle | SWT.RIGHT, null);
	}

	private Control createLabelForTitle(final Composite parent, final int swtStyle) {
		return controlFactory.createLabel(parent,
			this.resourceAdapterForUiElements.getLocalizedResource(this.titleResourceKey),
			swtStyle | SWT.RIGHT, null);
	}

	private void createLeftColumn(final Composite parent, final int swtStyle) {

		GridData layoutData = new GridData(GridData.FILL, GridData.BEGINNING, false, false);
		layoutData.verticalSpan = 2;
		layoutData.horizontalIndent = 1;
		layoutData.verticalIndent = 1;


		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginWidth = MARGIN_WIDTH;
		layout.marginHeight = MARGIN_HEIGHT;

		this.leftComposite = new Composite(this, swtStyle);
		this.leftComposite.setLayoutData(layoutData);
		this.leftComposite.setLayout(layout);
		this.leftComposite.setBackground(parent.getBackground());

		// left
		final GridData gridLayoutData = new GridData(GridData.BEGINNING, GridData.CENTER, false, false);

		this.displayLogicalOperator(parent, swtStyle, gridLayoutData);

		this.parentPropertyChangeListener = event -> {
			displayLogicalOperator(parent, swtStyle, gridLayoutData);
			leftComposite.layout();
		};
		// listener for changes on parent model
		parentModelAdapter.addPropertyChangeListener(this.parentPropertyChangeListener);
	}

	private Control createDeleteLink(final Composite parent, final int swtStyle) {
		String text = resourceAdapterForUiElements.getLocalizedResource(REMOVE_RULE_LABEL);
		IHyperlinkListener listener = new IHyperlinkListener() {
			public void linkActivated(final HyperlinkEvent event) {
				ConditionBlockComposite.this.fireEventForDelete(ConditionBlockComposite.this.getModel());
			}
			public void linkEntered(final HyperlinkEvent event) {
				((ImageHyperlink) event.getSource()).setUnderlined(true);
			}
			public void linkExited(final HyperlinkEvent event) {
				((ImageHyperlink) event.getSource()).setUnderlined(false);
			}
		};

		GridData layoutData = new GridData(GridData.END, GridData.BEGINNING, false, false);
		layoutData.horizontalIndent = HORIZONTAL_INDENT;
		layoutData.horizontalSpan = 2;
		layoutData.verticalIndent = 2;

		final ImageHyperlink deleteLink = controlFactory.createImageHyperlink(parent, swtStyle,
			CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_REMOVE), text, text, listener);
		deleteLink.setLayoutData(layoutData);

		return deleteLink;
	}

	private void changeBorder(final GC graphicContext) {

		if (this.getColorGrey().isDisposed()) {
			return;
		}

		Composite composite = this.getContainerComposite();
		try {
			if (this.rectContainer != null) {
				// clear previous border
				graphicContext.setForeground(composite.getBackground());
				this.drawBorder(graphicContext);
			}
			// make copy for clear
			this.rectContainer = this.getContainerComposite().getBounds();
			this.rectLeftControl = this.leftComposite.getBounds();
			this.rectThis = this.getBounds();
			this.rectAddRule = this.addRuleLink.getBounds();
			if (this.deleteLink != null) {
				this.rectDeleteLink = this.deleteLink.getBounds();
			}
			// draw
			graphicContext.setForeground(this.getColorGrey());
			this.drawBorder(graphicContext);
		} finally {
			graphicContext.dispose();
		}
	}

	private void drawBorder(final GC graphicContext) {

		Control control = this.leftComposite;
		int size = MAX_ARRAY_SIZE;
		if (this.deleteLink == null) {
			size = ARRAY_SIZE_WITHOUT_DELETE_LINK;
		}
		int[] points = new int[size];
		int index = 0;
		int maxY = this.rectAddRule.y + this.rectAddRule.height + ((GridData) this.addRuleLink.getLayoutData()).verticalIndent + 2;

		points[index++] = this.rectThis.x;
		points[index++] = this.rectLeftControl.y - ((GridData) control.getLayoutData()).verticalIndent;

		if (this.deleteLink == null) {
			points[index++] = this.rectThis.x + this.rectThis.width - WIDTH;
			points[index++] = this.rectLeftControl.y - ((GridData) control.getLayoutData()).verticalIndent;
		} else {
			points[index++] = this.rectDeleteLink.x - ((GridData) this.deleteLink.getLayoutData()).horizontalIndent;
			points[index++] = this.rectLeftControl.y - ((GridData) control.getLayoutData()).verticalIndent;

			points[index++] = this.rectDeleteLink.x - ((GridData) this.deleteLink.getLayoutData()).horizontalIndent;
			points[index++] = this.rectDeleteLink.y - 1;

			points[index++] = this.rectContainer.x + this.rectContainer.width
				+ ((GridData) this.getContainerComposite().getLayoutData()).horizontalIndent + 2;
			points[index++] = this.rectDeleteLink.y - 1;
		}

		points[index++] = this.rectContainer.x + this.rectContainer.width
			+ ((GridData) this.getContainerComposite().getLayoutData()).horizontalIndent + 2;
		points[index++] = maxY;

		points[index++] = this.rectAddRule.x - ((GridData) this.addRuleLink.getLayoutData()).horizontalIndent;
		points[index++] = maxY;

		points[index++] = this.rectAddRule.x - ((GridData) this.addRuleLink.getLayoutData()).horizontalIndent;
		points[index++] = this.rectLeftControl.y + this.rectLeftControl.height + ((GridData) control.getLayoutData()).verticalIndent;

		points[index++] = this.rectThis.x;
		points[index++] = this.rectLeftControl.y + this.rectLeftControl.height + ((GridData) control.getLayoutData()).verticalIndent;

		points[index++] = this.rectThis.x;
		points[index] = this.rectLeftControl.y - ((GridData) control.getLayoutData()).verticalIndent;

		graphicContext.drawPolyline(points);
	}

	@Override
	protected void widgetDisposed(final DisposeEvent event) {

		if (this.parentPropertyChangeListener != null) {
			this.parentModelAdapter.removePropertyChangeListener(parentPropertyChangeListener);
		}
		super.widgetDisposed(event);
	}
}
