/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.policy.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.core.binding.EPControlUnbinder;
import com.elasticpath.cmclient.core.registry.ObjectRegistry;
import com.elasticpath.cmclient.core.ui.ICompositeBlock;
import com.elasticpath.cmclient.core.ui.ICompositeBlockSupport;
import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.policy.PolicyPlugin;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.StatePolicyDelegate;
import com.elasticpath.cmclient.policy.StatePolicyTarget;
import com.elasticpath.cmclient.policy.StatePolicyTargetListener;
import com.elasticpath.cmclient.policy.common.DefaultStatePolicyDelegateImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.domain.cmuser.CmUser;

/**
 * This abstract implementation adds basic support for state policy.
 */
public abstract class AbstractPolicyAwareDialog extends AbstractEpDialog implements StatePolicyTarget, ICompositeBlockSupport {

	private final StatePolicyDelegate statePolicyDelegate = new DefaultStatePolicyDelegateImpl();

	private StatePolicy statePolicy;

	private IPolicyTargetLayoutComposite wrappedPolicyComposite;

	private final ListenerList listenerList = new ListenerList(ListenerList.IDENTITY);

	private final List<ICompositeBlock> blocks = new ArrayList<>();

	/**
	 * Constructs the dialog.
	 * 
	 * @param parentShell the parent Eclipse shell
	 * @param numColumns columns count for the GridLayout
	 * @param equalWidthColumns should the columns be with equal width
	 */
	public AbstractPolicyAwareDialog(final Shell parentShell, final int numColumns, final boolean equalWidthColumns) {
		super(parentShell, numColumns, equalWidthColumns);
		PolicyPlugin.getDefault().registerStatePolicyTarget(this);
		ObjectRegistry.getInstance().putObject("activeEditor", this); //$NON-NLS-1$
	}

	@Override
	protected final void createEpDialogContent(final IEpLayoutComposite dialogComposite) {
		// Wrap our dialog composite in a policy composite
		wrappedPolicyComposite = PolicyTargetCompositeFactory.wrapLayoutComposite(dialogComposite);
		this.createDialogContent(wrappedPolicyComposite);

		for (ICompositeBlock block : blocks) {
			block.init(wrappedPolicyComposite.getLayoutComposite(), getDependentObject());
		}
		
		fireStatePolicyTargetActivated();

		if (statePolicy == null) {
			throw new IllegalStateException("Attempting to apply a null policy"); //$NON-NLS-1$			
		}

		initializeStatePolicy();
		statePolicyDelegate.applyStatePolicy(statePolicy);
	}
	
	/**
	 * Implement this method to return the binding context that you have used in your sub-class.
	 * This method is used to unbind all binders on the Dialog.close().
	 * 
	 * See MOJITO-529
	 * 
	 * @return the sub-class binding context. 
	 */
	protected abstract DataBindingContext getDataBindingContext();

	/**
	 * Unbinds all controls.
	 * @return true if successful.
	 */
	@Override
	public boolean close() {
		EPControlUnbinder.getInstance().unbindAll(getDataBindingContext());
		return super.close();
	}
	
	/**
	 * Initializes the state policy. A subclass can override this to initialize the state policy with a different object.
	 */
	protected void initializeStatePolicy() {
		statePolicy.init(getDependentObject());
	}

	/**
	 * Creates the dialog content. Clients should implement this method and add all the required controls to the composite.
	 * 
	 * @param dialogComposite the dialog composite
	 */
	protected abstract void createDialogContent(IPolicyTargetLayoutComposite dialogComposite);

	@Override
	public PolicyActionContainer addPolicyActionContainer(final String name) {
		return statePolicyDelegate.addPolicyActionContainer(name);
	}

	@Override
	public void applyStatePolicy(final StatePolicy statePolicy) {
		this.statePolicy = statePolicy;
		initializeStatePolicy();
		statePolicyDelegate.applyStatePolicy(statePolicy);
		refreshLayout();
	}

	/**
	 * Gets the dependent object.
	 * 
	 * @return the dependent object or null
	 */
	protected abstract Object getDependentObject();

	/**
	 * Refreshes the layout of the dialog.
	 */
	protected abstract void refreshLayout();

	@Override
	public Map<String, PolicyActionContainer> getPolicyActionContainers() {
		return statePolicyDelegate.getPolicyActionContainers();
	}

	/**
	 * Should be implemented by clients and should return the container to determine the state of the OK/Save button.
	 * 
	 * @return a policy action container
	 */
	protected abstract PolicyActionContainer getOkButtonPolicyActionContainer();

	@Override
	protected Button createEpOkButton(final Composite parent, final String buttonLabel, final Image image) {
		Button okButton = super.createEpOkButton(parent, buttonLabel, image);
		((PolicyTargetLayoutComposite) wrappedPolicyComposite).addControlToContainer(okButton,
				getOkButtonPolicyActionContainer());
		return okButton;
	}

	@Override
	public void updateButtons() {
		this.applyStatePolicy(statePolicy);
		if (getOkButton().isEnabled()) {
			super.updateButtons();
		}
	}


	/**
	 * Returns the given CM user correctly formatted.
	 * 
	 * @param cmUser The CM user name to format.
	 * @return [last name], [first name]
	 */
	protected StringBuilder getFormattedCmUserName(final CmUser cmUser) {
		final StringBuilder formattedName = new StringBuilder();
		formattedName.append(cmUser.getLastName());
		formattedName.append(", "); //$NON-NLS-1$
		formattedName.append(cmUser.getFirstName());
		return formattedName;
	}

	/**
	 * Fire the activation event to all listeners.
	 */
	private void fireStatePolicyTargetActivated() {
		for (Object listener : listenerList.getListeners()) {
			((StatePolicyTargetListener) listener).statePolicyTargetActivated(this);
		}
	}

	@Override
	public void addGovernableListener(final StatePolicyTargetListener governableListener) {
		listenerList.add(governableListener);
	}

	@Override
	public void removeGovernableListener(final StatePolicyTargetListener listener) {
		listenerList.remove(listener);
	}

	@Override
	public void addCompositeBlock(final ICompositeBlock block) {
		blocks.add(block);
	}

	/**
	 * @return the statePolicy
	 */
	public StatePolicy getStatePolicy() {
		return statePolicy;
	}   
}
