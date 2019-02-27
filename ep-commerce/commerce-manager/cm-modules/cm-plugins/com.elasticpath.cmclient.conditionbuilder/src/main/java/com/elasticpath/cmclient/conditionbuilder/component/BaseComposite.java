/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.conditionbuilder.component;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

import com.elasticpath.cmclient.conditionbuilder.adapter.DataAdapter;
import com.elasticpath.cmclient.conditionbuilder.adapter.LogicalOperatorModelAdapter;
import com.elasticpath.cmclient.conditionbuilder.adapter.ResourceAdapter;
import com.elasticpath.cmclient.conditionbuilder.plugin.ConditionBuilderMessages;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.helpers.EPTestUtilFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;

/**
 * BaseComposite is base class for condition composite controls.
 *
 * @param <MA> model adapter
 * @param <A>  model adapter for children elements
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.GodClass"})
public class BaseComposite<MA, A> extends Canvas {

	private Color colorGrey;
	private Color colorLightGrey;
	private static final RGB RGB_LIGHT_GREY = new RGB(229, 229, 229);

	/**
	 * Model adapter.
	 */
	private MA model;

	private final EpControlFactory controlFactory = EpControlFactory.getInstance();

	/**
	 * Delete events list.
	 */
	private final List<ActionEventListener<MA>> listForDelete = new LinkedList<>();
	/**
	 * Add events list.
	 */
	private final List<ActionEventListener<A>> listForAdd = new LinkedList<>();

	/**
	 * Default constructor.
	 *
	 * @param parent parent composite
	 * @param style  SWT style
	 * @param model  model
	 */
	public BaseComposite(final Composite parent, final int style, final MA model) {
		super(parent, style);

		this.model = model;

		// set colors
		initColors(parent);
		this.setBackground(parent.getBackground());
		this.setForeground(parent.getForeground());

		// set top layout for this.
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 1;
		layout.marginWidth = 1;

		this.setLayout(layout);

		// dispose listeners
		this.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(final DisposeEvent event) {
				BaseComposite.this.widgetDisposed(event);
			}
		});
	}

	/**
	 * Default constructor.
	 *
	 * @param parent        parent composite
	 * @param style         SWT style
	 * @param model         model
	 * @param columnsNumber number columns in layout
	 */
	public BaseComposite(final Composite parent, final int style, final MA model, final int columnsNumber) {
		super(parent, style);

		this.model = model;

		// set colors
		initColors(parent);
		this.setBackground(parent.getBackground());
		this.setForeground(parent.getForeground());

		// set top layout for this.
		GridLayout layout = new GridLayout(columnsNumber, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;

		this.setLayout(layout);

		// dispose listeners
		this.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(final DisposeEvent event) {
				BaseComposite.this.widgetDisposed(event);
			}
		});
	}

	/**
	 * Dispose widget.
	 *
	 * @param event dispose event
	 */
	protected void widgetDisposed(final DisposeEvent event) {
		this.model = null;
	}

	/**
	 * Create popup menu.
	 *
	 * @param <V>                       object type for item
	 * @param <X>                       object type for subitem
	 * @param component                 component
	 * @param resourceAdapter           resource adapter
	 * @param resourceAdapterForSubItem resource adapter
	 * @param items                     items list
	 * @param dataAdapter               data adapter for submenu item
	 * @param currentItem               current item, not mandatory field
	 * @param listener                  listener
	 * @return Menu
	 */
	protected <X, V> Menu createPopupMenuWithSubmenu(final Control component,
		final ResourceAdapter<X> resourceAdapter,
		final ResourceAdapter<V> resourceAdapterForSubItem,
		final Set<X> items,
		final DataAdapter<X, V> dataAdapter,
		final V currentItem,
		final SelectionListener listener) {

		List<X> sortedItems = sortItems(new LinkedList<>(items), resourceAdapter);

		final Menu menu = new Menu(component);
		boolean selected = false;

		for (X item : sortedItems) {
			MenuItem menuItem = this.createMenuItem(menu, SWT.CASCADE, resourceAdapter, item);

			Menu subMenu = this.createSubMenu(menu,
				resourceAdapterForSubItem, dataAdapter.getChildren(item), listener);
			menuItem.setMenu(subMenu);

			if (item.equals(currentItem)) {
				menuItem.setSelection(true);
				menu.setDefaultItem(menuItem);
				selected = true;
			}
		}
		if (currentItem == null || !selected) {
			menu.setDefaultItem(null);
		}
		return menu;
	}

	/**
	 * Create submenu for objects array.
	 *
	 * @param <V>             menu item type
	 * @param component       component for menu
	 * @param resourceAdapter resource adapter
	 * @param items           items
	 * @param listener        listener
	 * @return Menu
	 */
	protected <V> Menu createSubMenu(final Menu component,
		final ResourceAdapter<V> resourceAdapter,
		final List<V> items, final SelectionListener listener) {

		List<V> sortedItems = sortItems(new LinkedList<>(items), resourceAdapter);
		final Menu menu = new Menu(component);

		for (V item : sortedItems) {
			MenuItem menuItem = this.createMenuItem(menu, SWT.PUSH, resourceAdapter, item);
			if (listener != null) {
				menuItem.addSelectionListener(listener);
			}
		}
		return menu;
	}

	private <V> List<V> sortItems(final List<V> items, final ResourceAdapter<V> resourceAdapter) {
		items.sort((object1, object2) -> {
			String string1 = resourceAdapter.getLocalizedResource(object1);
			String string2 = resourceAdapter.getLocalizedResource(object2);
			return string1.compareTo(string2);
		});
		return items;
	}

	/**
	 * Create image for add rule button.
	 *
	 * @param parent   parent
	 * @param style    SWT style
	 * @param listener listener
	 * @return ImageHyperlink
	 */
	protected ImageHyperlink createImageHyperlinkForAddRule(final Composite parent, final int style, final IHyperlinkListener listener) {
		ImageDescriptor imageDescriptor = CoreImageRegistry.IMAGE_ADD;
		return controlFactory.createImageHyperlink(parent, style,
			CoreImageRegistry.getImage(imageDescriptor),
			ConditionBuilderMessages.get().ConditionBuilder_Add_Rule_label,
			null,
			listener);
	}

	/**
	 * Create image hyperlink for delete rule.
	 *
	 * @param parent   parent composite
	 * @param style    SWT style
	 * @param listener listener
	 * @return ImageHyperlink
	 */
	protected ImageHyperlink createImageHyperlinkForDeleteRule(final Composite parent, final int style, final IHyperlinkListener listener) {
		ImageDescriptor imageDescriptor = CoreImageRegistry.IMAGE_REMOVE;
		return controlFactory.createImageHyperlink(parent, style,
			CoreImageRegistry.getImage(imageDescriptor),
			StringUtils.EMPTY,
			ConditionBuilderMessages.get().ConditionBuilder_Remove_Statement_label,
			listener);
	}

	/**
	 * Get Model.
	 *
	 * @return the model
	 */
	public MA getModel() {
		return model;
	}

	/**
	 * Fire event for delete operation.
	 *
	 * @param model model adapter object for delete
	 */
	protected void fireEventForDelete(final MA model) {
		for (ActionEventListener<MA> listener : this.listForDelete) {
			listener.onEvent(model);
		}
	}

	/**
	 * Add listener for add operation.
	 *
	 * @param listener listener
	 */
	public void addListenerForDelete(final ActionEventListener<MA> listener) {
		this.listForDelete.add(listener);
	}

	/**
	 * Fire event for delete operation.
	 *
	 * @param model model adapter for add
	 */
	protected void fireEventForAdd(final A model) {
		for (ActionEventListener<A> listener : this.listForAdd) {
			listener.onEvent(model);
		}
	}

	/**
	 * Add listener for add operation.
	 *
	 * @param listener listener
	 */
	public void addListenerForAdd(final ActionEventListener<A> listener) {
		this.listForAdd.add(listener);
	}

	/**
	 * Create combobox for parent logical operator.
	 *
	 * @param parent             parent composite
	 * @param swtStyle           SWT style
	 * @param parentModelAdapter parent model adapter
	 * @param <M2>               model adapter
	 * @param <O2>               operator object
	 * @return control
	 */
	protected <M2, O2> Control createComboForLogicalOperator(
		final Composite parent, final int swtStyle, final LogicalOperatorModelAdapter<M2, O2> parentModelAdapter) {

		final ResourceAdapter<O2> resourceAdapterForLogicalOperator = parentModelAdapter.getResourceAdapterForOperator();

		ComboViewer combo = new ComboViewer(parent, swtStyle | SWT.READ_ONLY);
		combo.getCombo().setVisibleItemCount(parentModelAdapter.getLogicalOperatorsList().size());
		combo.setContentProvider(new IStructuredContentProvider() {
			public void dispose() {
				//
			}

			public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
				//
			}

			public Object[] getElements(final Object inputElement) {
				return ((List<O2>) inputElement).toArray();
			}
		});
		combo.setLabelProvider(new LabelProvider() {
			public String getText(final Object element) {
				return resourceAdapterForLogicalOperator.getLocalizedResource((O2) element);
			}
		});
		combo.setInput(parentModelAdapter.getLogicalOperatorsList());
		combo.getCombo().select(parentModelAdapter.getLogicalOperatorsList().indexOf(parentModelAdapter.getLogicalOperator()));
		combo.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(final SelectionChangedEvent event) {
				O2 operator = (O2) ((IStructuredSelection) event.getSelection()).getFirstElement();
				parentModelAdapter.setLogicalOperator(operator);
			}
		});
		EPTestUtilFactory.getInstance().getTestIdUtil().setUniqueId(combo.getCombo());
		return combo.getCombo();
	}

	private void initColors(final Composite parent) {
		Display display = parent.getDisplay();
		colorGrey = display.getSystemColor(SWT.COLOR_DARK_GRAY);
		colorLightGrey = new Color(display, RGB_LIGHT_GREY);
	}

	@Override
	public void dispose() {
		super.dispose();
		if (colorLightGrey != null && !colorLightGrey.isDisposed()) {
			colorLightGrey.dispose();
		}
	}

	/**
	 * Get the grey color.
	 *
	 * @return the colorGrey
	 */
	protected Color getColorGrey() {
		return colorGrey;
	}

	/**
	 * Get the light grey color.
	 *
	 * @return the colorGrey
	 */
	public Color getColorLightGrey() {
		return colorLightGrey;
	}

	@Override
	public void setEnabled(final boolean enabled) {
		super.setEnabled(enabled);

		for (Control child : getChildren()) {
			if (!child.isDisposed()) {
				child.setEnabled(enabled);
			}
		}
	}

	/**
	 * Used for creating menus with test ids.
	 */
	private <V> MenuItem createMenuItem(final Menu menu, final int style, final ResourceAdapter<V> resourceAdapter, final V item) {
		MenuItem menuItem = controlFactory.createMenuItem(menu, style, resourceAdapter.getLocalizedResource(item));
		menuItem.setData(item);
		return menuItem;
	}
}
