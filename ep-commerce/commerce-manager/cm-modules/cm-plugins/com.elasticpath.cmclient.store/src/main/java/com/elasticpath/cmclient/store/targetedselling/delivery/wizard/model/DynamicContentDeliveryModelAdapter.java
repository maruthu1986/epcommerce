/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.targetedselling.delivery.wizard.model;

import java.util.Set;

import com.elasticpath.cmclient.conditionbuilder.wizard.pages.AbstractSellingContextAdapter;
import com.elasticpath.domain.contentspace.ContentSpace;
import com.elasticpath.domain.contentspace.DynamicContent;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.domain.targetedselling.DynamicContentDelivery;

/**
 * Class wraps DynamicContentDelivery entity - adding TAG framework support.
 */
public class DynamicContentDeliveryModelAdapter extends AbstractSellingContextAdapter {

	private final DynamicContentDelivery dcd;

	/**
	 * Constructor.
	 * 
	 * @param dcd
	 *            DynamicContentDelivery to be wrapped.
	 */
	public DynamicContentDeliveryModelAdapter(final DynamicContentDelivery dcd) {
		this.dcd = dcd;
		super.populateSellingContext(dcd.getSellingContextGuid(), dcd.getName());
		this.dcd.setSellingContext(getSellingContext());
	}

	@Override
	public int hashCode() {
		return dcd.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof DynamicContentDeliveryModelAdapter)) {
			return false;
		}
		return dcd.equals(((DynamicContentDeliveryModelAdapter) obj).dcd);
	}

	/**
	 * Returns DynamicContentDelivery priority.
	 * 
	 * @return DynamicContentDelivery priority
	 */
	public int getPriority() {
		return dcd.getPriority();
	}

	/**
	 * Sets {@link DynamicContentDelivery} priority.
	 * 
	 * @param priority -
	 *            {@link DynamicContentDelivery} priority
	 */
	public void setPriority(final int priority) {
		dcd.setPriority(priority);
	}

	/**
	 * Returns set of {@link ContentSpace} from wrapped
	 * {@link DynamicContentDelivery}.
	 * 
	 * @return - set of {@link ContentSpace}
	 */
	public Set<ContentSpace> getContentspaces() {
		return dcd.getContentspaces();
	}

	/**
	 * Sets set of {@link ContentSpace} for wrapped
	 * {@link DynamicContentDelivery}.
	 * 
	 * @param contenspaces -
	 *            set of {@link ContentSpace}
	 */
	public void setContentspaces(final Set<ContentSpace> contenspaces) {
		dcd.setContentspaces(contenspaces);
	}

	/**
	 * Returns name of wrapped {@link DynamicContentDelivery}.
	 * 
	 * @return name
	 */
	public String getName() {
		return dcd.getName();
	}

	/**
	 * Returns description of wrapped {@link DynamicContentDelivery}.
	 * 
	 * @return description
	 */
	public String getDescription() {
		return dcd.getDescription();
	}

	/**
	 * Returns DynamicContent of wrapped {@link DynamicContentDelivery}.
	 * 
	 * @return DynamicContent
	 */
	public DynamicContent getDynamicContent() {
		return dcd.getDynamicContent();
	}

	/**
	 * Returns GUID of wrapped {@link DynamicContentDelivery}.
	 * 
	 * @return GUID
	 */
	public final String getGuid() {
		return dcd.getGuid();
	}

	/**
	 * Sets {@link DynamicContent} for wrapped {@link DynamicContentDelivery}.
	 * 
	 * @param dynamicContent -
	 *            {@link DynamicContent}
	 */
	public void setDynamicContent(final DynamicContent dynamicContent) {
		dcd.setDynamicContent(dynamicContent);
	}

	/**
	 * Returns wrapped {@link DynamicContentDelivery}.
	 * 
	 * @return {@link DynamicContentDelivery}
	 */
	public final DynamicContentDelivery getDynamicContentDelivery() {
		return dcd;
	}

	@Override
	public void clearSellingContext() {
		this.dcd.setSellingContext(null);
	}

	@Override
	public void setSellingContext(final SellingContext sellingContext) {
		super.setSellingContext(sellingContext);
		dcd.setSellingContext(sellingContext);
	}
}
