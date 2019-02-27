/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 *
 */
package com.elasticpath.cmclient.conditionbuilder.component;

import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

import com.elasticpath.cmclient.conditionbuilder.adapter.ConditionModelAdapter;
import com.elasticpath.cmclient.conditionbuilder.adapter.LogicalOperatorModelAdapter;
import com.elasticpath.cmclient.conditionbuilder.adapter.ResourceAdapter;
import com.elasticpath.cmclient.conditionbuilder.valueeditor.ConditionRowValueFactory;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.helpers.EPTestUtilFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;

/**
 * ConditionRowComposite.
 *
 * @param <M>  model type
 * @param <OP>  operator type
 * @param <M2> parent model adapter type
 * @param <O2> parent operator type
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class ConditionRowComposite<M, OP, M2, O2> extends BaseComposite<ConditionModelAdapter<M, OP>, OP> {

	/** VERTICAL_INDENT. */
	protected static final int VERTICAL_INDENT = 5;
	/** HORIZONTAL_SPAN. */
	protected static final int HORIZONTAL_SPAN = 4;
	/** HORIZONTAL_INDENT_IN_ROW. */
	protected static final int HORIZONTAL_INDENT_IN_ROW = 5;
	/** LAYOUT_COLUMNS. */
	protected static final int LAYOUT_COLUMNS = 4;

	/** USER_VALUE_WIDTH. */
	protected static final int USER_VALUE_WIDTH = 200;

	private final ResourceAdapter<OP> resourceAdapterForOperator;

	private Canvas secondRowCanvas;
	private Control controlForLogicalOperator;

	private PropertyChangeListener parentPropertyChangeListener;

	private Rectangle rectSecondRowComposite;
	private Rectangle rectControlForLogicalOperator;

	private final LogicalOperatorModelAdapter<M2, O2> parentModelAdapter;
	private final EpControlFactory controlFactory = EpControlFactory.getInstance();

	/**
	 * Used by extensions that will create their own controls.
	 *
	 * @param parent                   parent composite
	 * @param swtStyle                 SWT style
	 * @param modelAdapter             model adapter
	 * @param parentModelAdapter       parent model adapter
	 * @param layoutColumns the layout columns
	 */
	public ConditionRowComposite(
			final Composite parent, 
			final int swtStyle,
			final ConditionModelAdapter<M, OP> modelAdapter, 
			final LogicalOperatorModelAdapter<M2, O2> parentModelAdapter,
			final int layoutColumns
			) {
		super(parent, swtStyle, modelAdapter, layoutColumns);

		this.parentModelAdapter = parentModelAdapter;
		this.resourceAdapterForOperator = modelAdapter.getResourceAdapterForOperator();
	}

	/**
	 * Default constructor.
	 *
	 * @param parent                   parent composite
	 * @param swtStyle                 SWT style
	 * @param modelAdapter             model adapter
	 * @param parentModelAdapter       parent model adapter
	 * @param dataBindingContext       the DataBindingContext
	 * @param conditionRowValueFactory the {@link ConditionRowValueFactory}
	 */
	public ConditionRowComposite(final Composite parent, final int swtStyle,
		final ConditionModelAdapter<M, OP> modelAdapter, final LogicalOperatorModelAdapter<M2, O2> parentModelAdapter,
		final DataBindingContext dataBindingContext, final ConditionRowValueFactory conditionRowValueFactory) {

		super(parent, swtStyle, modelAdapter, LAYOUT_COLUMNS);

		this.parentModelAdapter = parentModelAdapter;
		this.resourceAdapterForOperator = modelAdapter.getResourceAdapterForOperator();

		GridData layoutData = new GridData(GridData.BEGINNING, GridData.CENTER, false, false);

		this.createDescriptionLabel(swtStyle, modelAdapter, layoutData);

		// combobox for operator
		layoutData = new GridData(GridData.CENTER, GridData.CENTER, false, false);
		layoutData.horizontalIndent = HORIZONTAL_INDENT_IN_ROW;

		this.createOperatorCombo(swtStyle, modelAdapter, layoutData);

		// value
		layoutData = new GridData(GridData.FILL, GridData.CENTER, false, false);
		layoutData.horizontalIndent = HORIZONTAL_INDENT_IN_ROW;
		layoutData.widthHint = USER_VALUE_WIDTH;

		this.createUserValueControl(swtStyle, modelAdapter, dataBindingContext, conditionRowValueFactory, layoutData);

		layoutData = new GridData(GridData.END, GridData.CENTER, true, false);
		layoutData.horizontalIndent = 1;

		this.createDeleteRowButton(swtStyle, layoutData);

		// parent Logical Operator
		layoutData = new GridData(GridData.FILL, GridData.CENTER, false, false);
		layoutData.horizontalSpan = HORIZONTAL_SPAN;
		layoutData.verticalIndent = VERTICAL_INDENT;

		this.createRowSeparator(parent, swtStyle, parentModelAdapter, layoutData);

	} //END of constructor ---------

	/**
	 * Creates the row separator.
	 *
	 * @param parent the parent
	 * @param swtStyle the style
	 * @param parentModelAdapter the model adapter
	 * @param layoutData the layout data
	 */
	protected void createRowSeparator(final Composite parent, final int swtStyle,
		final LogicalOperatorModelAdapter<M2, O2> parentModelAdapter, final GridData layoutData) {
		this.secondRowCanvas = new Canvas(this, swtStyle);
		this.secondRowCanvas.setLayoutData(layoutData);
		this.secondRowCanvas.setLayout(new GridLayout());
		this.secondRowCanvas.setBackground(this.getBackground());

		final GridData gridLayoutData = new GridData(GridData.BEGINNING, GridData.CENTER, false, false);
		gridLayoutData.horizontalIndent = HORIZONTAL_INDENT_IN_ROW;

		// listener for grey line
		PaintListener paintListener = (PaintListener) paintEvent -> drawLine(paintEvent.gc);
		this.secondRowCanvas.addPaintListener(paintListener);
		this.secondRowCanvas.addDisposeListener((DisposeListener) event -> secondRowCanvas.removePaintListener(paintListener));
		displayLogicalOperator(parent, swtStyle, gridLayoutData);

		this.parentPropertyChangeListener = event -> {
			displayLogicalOperator(parent, swtStyle, gridLayoutData);
			secondRowCanvas.layout();
		};
		// listener for changes on parent model
		parentModelAdapter.addPropertyChangeListener(this.parentPropertyChangeListener);
	}

	/**
	 * Creates the delete button for the row.
	 *
	 * @param swtStyle the style
	 * @param layoutData the layout data
	 */
	protected void createDeleteRowButton(final int swtStyle, final GridData layoutData) {
		ImageHyperlink deleteLink = this.createImageHyperlinkForDeleteRule(this, swtStyle,
			new HyperlinkAdapter() {
				@Override
				public void linkActivated(final HyperlinkEvent event) {
					ConditionRowComposite.this.fireEventForDelete(ConditionRowComposite.this.getModel());
				}
			});
		deleteLink.setLayoutData(layoutData);
	}

	/**
	 * Creates the value control.
	 *
	 * @param swtStyle the style
	 * @param modelAdapter the model adapter
	 * @param dataBindingContext the binding context
	 * @param conditionRowValueFactory the row factory
	 * @param layoutData the layout data
	 */
	protected void createUserValueControl(final int swtStyle, final ConditionModelAdapter<M, OP> modelAdapter,
		final DataBindingContext dataBindingContext, final ConditionRowValueFactory conditionRowValueFactory, final GridData layoutData) {

		final Control valueControl = conditionRowValueFactory.createControl(this, swtStyle | SWT.BORDER,
			modelAdapter, dataBindingContext, null);
		valueControl.setLayoutData(layoutData);

		this.addListenerForDelete(object -> valueControl.notifyListeners(ConditionRowValueFactory.EVENT_FOR_UNBIND, new Event()));
		this.addListener(ConditionRowValueFactory.EVENT_FOR_UNBIND,
			(Listener) event -> valueControl.notifyListeners(ConditionRowValueFactory.EVENT_FOR_UNBIND, new Event())
		);
	}

	/**
	 * Creates the operator combo.
	 *
	 * @param swtStyle the style
	 * @param modelAdapter the model adapter
	 * @param layoutData the layout data
	 */
	protected void createOperatorCombo(final int swtStyle, final ConditionModelAdapter<M, OP> modelAdapter, final GridData layoutData) {
		ComboViewer operatorCombo = new ComboViewer(new CCombo(this, swtStyle | SWT.READ_ONLY));
		operatorCombo.setContentProvider(new IStructuredContentProvider() {
			public void dispose() {
				//
			}

			public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
				//
			}

			public Object[] getElements(final Object inputElement) {
				return ((List<OP>) inputElement).toArray();
			}
		});

		operatorCombo.setLabelProvider(new LabelProvider() {
			public String getText(final Object element) {
				return resourceAdapterForOperator.getLocalizedResource((OP) element);
			}
		});
		operatorCombo.setInput(modelAdapter.getOperatorsList());
		operatorCombo.getCCombo().setLayoutData(layoutData);
		OP operator = modelAdapter.getOperator();
		operatorCombo.getCCombo().select(modelAdapter.getOperatorsList().indexOf(operator));
		operatorCombo.addSelectionChangedListener(event -> {
			OP selectedOperator = (OP) ((IStructuredSelection) event.getSelection()).getFirstElement();
			modelAdapter.setOperator(selectedOperator);
		});

		EPTestUtilFactory.getInstance().getTestIdUtil().setUniqueId(operatorCombo.getCCombo());
	}

	/**
	 * Creates the description label.
	 *
	 * @param swtStyle the style
	 * @param modelAdapter the model adapter
	 * @param layoutData the layout data
	 */
	protected void createDescriptionLabel(final int swtStyle, final ConditionModelAdapter<M, OP> modelAdapter, final GridData layoutData) {
		String labelText = modelAdapter.getTagDefinition().getLocalizedName(CorePlugin.getDefault().getDefaultLocale());
		createDescriptionLabel(swtStyle, layoutData, labelText);
	}

	/**
	 * Creates a description label for the text.
	 *
	 * @param swtStyle the style
	 * @param layoutData the layout data
	 * @param labelText the text for the label
	 */
	protected void createDescriptionLabel(final int swtStyle, final GridData layoutData, final String labelText) {
		Label tagLabel = controlFactory.createLabel(
			this,
			labelText,
			swtStyle, layoutData);
		tagLabel.setBackground(this.getBackground());
	}

	/**
	 * Draws a line.
	 *
	 * @param graphicContext the graphics context
	 */
	protected void drawLine(final GC graphicContext) {
		if (this.controlForLogicalOperator == null || this.getColorGrey().isDisposed()) {
			return;
		}

		try {
			if (this.rectSecondRowComposite != null) {
				// clear previous border
				graphicContext.setForeground(this.secondRowCanvas.getBackground());
				this.performLineDrawing(graphicContext);
			}
			// make copy for clear
			this.rectSecondRowComposite = this.secondRowCanvas.getBounds();
			this.rectControlForLogicalOperator = this.controlForLogicalOperator.getBounds();
			// draw
			graphicContext.setForeground(this.getColorLightGrey());
			this.performLineDrawing(graphicContext);
		} finally {
			graphicContext.dispose();
		}
	}

	private void performLineDrawing(final GC graphicContext) {
		int pointX1 = this.rectControlForLogicalOperator.x + this.rectControlForLogicalOperator.width
			+ ((GridData) this.controlForLogicalOperator.getLayoutData()).horizontalIndent;
		int pointY = this.rectControlForLogicalOperator.y + this.rectControlForLogicalOperator.height / 2;
		int pointX2 = this.rectSecondRowComposite.x + this.rectSecondRowComposite.width
			+ ((GridData) this.secondRowCanvas.getLayoutData()).horizontalIndent;
		graphicContext.drawLine(pointX1, pointY, pointX2, pointY);
	}

	/**
	 * Displays the operator.
	 *
	 * @param parent the parent
	 * @param swtStyle the style
	 * @param layoutData the layout data
	 */
	protected void displayLogicalOperator(final Composite parent, final int swtStyle, final GridData layoutData) {
		boolean isFirstRow = this.equals(parent.getChildren()[0]);
		boolean isLastRow = this.equals(parent.getChildren()[parent.getChildren().length - 1]);
		if (this.controlForLogicalOperator != null) {
			this.controlForLogicalOperator.dispose();
			this.controlForLogicalOperator = null;
		}
		if (!isLastRow) {
			if (isFirstRow) {
				this.controlForLogicalOperator = createComboForLogicalOperator(secondRowCanvas, swtStyle, this.parentModelAdapter);
			} else {
				this.controlForLogicalOperator = createLabelForLogicalOperator(secondRowCanvas, swtStyle);
			}
			this.controlForLogicalOperator.setLayoutData(layoutData);
		}
	}

	/**
	 * Creates the label control for the logical operator.
	 *
	 * @param parent the parent
	 * @param swtStyle the style
	 * @return the new control
	 */
	protected Control createLabelForLogicalOperator(final Composite parent, final int swtStyle) {
		return controlFactory.createLabel(parent,
			this.parentModelAdapter.getResourceAdapterForOperator().getLocalizedResource(this.parentModelAdapter.getLogicalOperator()),
			swtStyle, null);
	}

	@Override
	protected void widgetDisposed(final DisposeEvent event) {
		if (this.parentPropertyChangeListener != null) {
			this.parentModelAdapter.removePropertyChangeListener(parentPropertyChangeListener);
		}
		super.widgetDisposed(event);
	}
}
