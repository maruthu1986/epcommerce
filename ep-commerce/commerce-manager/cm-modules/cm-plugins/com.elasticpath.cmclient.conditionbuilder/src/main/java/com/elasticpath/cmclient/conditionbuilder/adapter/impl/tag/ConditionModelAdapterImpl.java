/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 * 
 */
package com.elasticpath.cmclient.conditionbuilder.adapter.impl.tag;

import java.math.BigDecimal;
import java.util.List;

import com.elasticpath.cmclient.conditionbuilder.adapter.ConditionModelAdapter;
import com.elasticpath.cmclient.conditionbuilder.adapter.ResourceAdapter;
import com.elasticpath.tags.domain.Condition;
import com.elasticpath.tags.domain.TagDefinition;
import com.elasticpath.tags.domain.TagOperator;
import com.elasticpath.tags.service.GenericService;

/**
 * ConditionModelAdapterImpl is a Condition adapter for UI.
 *
 */
public class ConditionModelAdapterImpl extends BaseModelAdapterImpl<Condition> implements ConditionModelAdapter<Condition, TagOperator> {

	private boolean dirty;
	
	private ResourceAdapter<TagOperator> resourceAdapterForOperator;
	private ResourceAdapter<TagDefinition> resourceAdapterForTagDefinition;

	private List<TagOperator> tagOperatorsList;
	private GenericService<TagOperator> tagOperatorService;
	
	/**
	 * Default constructor.
	 * @param condition model
	 */
	public ConditionModelAdapterImpl(final Condition condition) {
		super(condition);
		this.getPropertyChangeSupport().addPropertyChangeListener(event -> ConditionModelAdapterImpl.this.dirty = true);
	}

	@Override
	public void setTagDefinition(final TagDefinition leftOperand) {
		TagDefinition oldValue = this.getTagDefinition();
		this.getModel().setTagDefinition(leftOperand);
		this.getPropertyChangeSupport().firePropertyChange(ConditionModelAdapter.TAG_DEFINITION, oldValue, leftOperand);
	}
	
	@Override
	public TagDefinition getTagDefinition() {
		TagDefinition tagDefinition = null;
		if (this.getModel() != null) {
			tagDefinition = this.getModel().getTagDefinition();
		}
		return tagDefinition;
	}

	@Override
	public void setOperator(final TagOperator operator) {
		TagOperator oldValue = this.getOperator();
		this.getModel().setOperator(operator.getGuid());
		this.getPropertyChangeSupport().firePropertyChange(ConditionModelAdapter.OPERATOR, oldValue, operator);
	}
	
	@Override
	public TagOperator getOperator() {
		TagOperator operator = null;
		if (this.getModel() != null) {
			operator = this.tagOperatorService.findByGuid(this.getModel().getOperator());
		}
		if (operator == null) {
			operator = this.getOperatorsList().get(0);
			this.getModel().setOperator(operator.getGuid());
		}
		return operator;
	}

	@Override
	public void setTagValue(final Object rightOperand) {
		Object oldValue = this.getModel().getTagValue();
		this.getModel().setTagValue(rightOperand);
		this.getPropertyChangeSupport().firePropertyChange(ConditionModelAdapter.TAG_VALUE, oldValue, rightOperand);
	}
	
	@Override
	public Object getTagValue() {
		Object result = null;
		if (this.getModel() != null) {
			result = this.getModel().getTagValue();
		}
		return result;
	}

	/**
	 * Check if model is dirty.
	 * @return the dirty boolean
	 */
	public boolean isDirty() {
		return dirty;
	}

	/**
	 * Set dirty flag.
	 * @param dirty the dirty to set
	 */
	public void setDirty(final boolean dirty) {
		this.dirty = dirty;
	}

	@Override
	public List<TagOperator> getOperatorsList() {
		return this.tagOperatorsList;
	}

	/**
	 * Set the tags operators list.
	 * @param operatorsList operators list
	 */
	public void setOperatorsList(final List<TagOperator> operatorsList) {
		this.tagOperatorsList = operatorsList;
	}

	@Override
	public ResourceAdapter<TagOperator> getResourceAdapterForOperator() {
		return this.resourceAdapterForOperator;
	}

	/**
	 * Set the resource adapter.
	 * @param resourceAdapter resource adapter
	 */
	public void setResourceAdapterForOperator(final ResourceAdapter<TagOperator> resourceAdapter) {
		this.resourceAdapterForOperator = resourceAdapter;
	}

	@Override
	public ResourceAdapter<TagDefinition> getResourceAdapterForTagDefinition() {
		return this.resourceAdapterForTagDefinition;
	}

	/**
	 * Set the resource adapter.
	 * @param resourceAdapter resource adapter
	 */
	public void setResourceAdapterForTagDefinition(final ResourceAdapter<TagDefinition> resourceAdapter) {
		this.resourceAdapterForTagDefinition = resourceAdapter;
	}

	/**
	 * Set the service for search. 
	 * @param tagOperatorService the tagOperatorService to set
	 */
	public void setTagOperatorService(final GenericService<TagOperator> tagOperatorService) {
		this.tagOperatorService = tagOperatorService;
	}

	@Override
	public Object getTagValueFromString(final String stringTagValue) {
		
		final String dataType = getJavaType();
		
		try {
			if (BigDecimal.class.getCanonicalName().equals(dataType)) {
				return BigDecimal.valueOf(Double.valueOf(stringTagValue));
			} else if (Long.class.getCanonicalName().equals(dataType)) {
				return Long.valueOf(stringTagValue);
			} else if (Float.class.getCanonicalName().equals(dataType)) {
				return Float.valueOf(stringTagValue);
			} else if (Integer.class.getCanonicalName().equals(dataType)) {
				return Integer.valueOf(stringTagValue);
			}
		} catch (NumberFormatException nfe) { // NOPMD
			// a known problem but at the moment no better solution but simply
			// to return the value as string, since we need the error message
			// to return back from the validation engine and we need at least some 
			// sort of value for it to fail.
		}
		return stringTagValue;
	}
	
	private String getJavaType() {
		return this.getTagDefinition().getValueType().getJavaType();
	}
}
