/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.contentspace.impl;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.jdbc.ElementForeignKey;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;
import org.apache.openjpa.persistence.jdbc.Unique;

import com.elasticpath.domain.contentspace.DynamicContent;
import com.elasticpath.domain.contentspace.ParameterValue;
import com.elasticpath.persistence.api.AbstractEntityImpl;

/**
 * <p>Defines everything needed to describe a piece of content that may be delivered conditionally.</p>
 * <p>This implementation is annotated for JPA persistence.</p>
 */
@Entity
@Table(name = DynamicContentImpl.TABLE_NAME)
@DataCache(enabled = false)
public class DynamicContentImpl extends AbstractEntityImpl implements DynamicContent {
	
	
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 20090112L;
	
	
	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TCSDYNAMICCONTENT";
	
	private static final String CSDYNAMICCONTENT_UID = "CSDYNAMICCONTENT_UID";
	
	private long uidPk;	
	private String name;
	private String description;
	
	private List<ParameterValue> parameterValues; 

	/** The Id of the associated content wrapper for this content wrapper action. **/
	private String contentWrapperId;


	private String guid;
	

	/**
	 * Gets the unique identifier for this domain model object.
	 * 
	 * @return the unique identifier.
	 */
	@Override
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
	public long getUidPk() {
		return this.uidPk;
	}
	
	/**
	 * Gets the content wrapper Id for this content wrapper action.
	 * 
	 * @return content wrapper Id
	 */
	@Override
	@Basic
	@Column(name = "CONTENT_WRAPPER_ID")	
	public String getContentWrapperId() {
		return contentWrapperId;
	}
	
	
	
	/**
	 * Sets the unique identifier for this domain model object.
	 * 
	 * @param uidPk the new unique identifier.
	 */
	@Override
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}


	
	/**
	 * Sets the content wrapper Id for this content wrapper action.
	 * 
	 * @param contentWrapperId content wrapper Id
	 */
	@Override
	public void setContentWrapperId(final String contentWrapperId) {
		this.contentWrapperId = contentWrapperId;
	}
	
	/**
	 * Sets the parameter values for the content wrapper action.
	 * @param parameterValues the parameters that will be set
	 */
	@Override
	public void setParameterValues(final List<ParameterValue> parameterValues) {
		this.parameterValues = parameterValues;
	}

	/**
	 * Gets the parameter values.
	 * @return parameter values
	 */
	@Override
	@OneToMany(targetEntity = ParameterValueImpl.class, cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@ElementJoinColumn(name = CSDYNAMICCONTENT_UID, nullable = false)
	@ElementForeignKey(name = "TCSDYNAMICCONTENT_FK")
	@ElementDependent
	public List<ParameterValue> getParameterValues() {
		if (parameterValues == null) {
			parameterValues = new ArrayList<>();
		}
		return parameterValues;
	}


	/**
	 * Returns the type id of the action and what content space it is responsible for.
	 * 
	 * @return the id of the content wrapper action
	 */
	@Override
	@Transient
	public String getIdentity() {
		return getType() + getName();
	}

	@Override
	@Transient
	public String getType() {
		return "DynamicContent";
	}
	
	/**
	 * @return the name
	 */
	@Override
	@Basic
	@Column(name = "NAME", unique = true, nullable = false)
	@Unique(name = "TCSDYNAMICCONTENT_UNIQUE_NAME")
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	@Override
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	@Override
	@Basic
	@Column(name = "DESCRIPTION")
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	@Override
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * @return the guid.
	 */
	@Override
	@Basic
	@Column(name = "GUID")
	@Unique(name = "TCSDYNAMICCONTENT_UNIQUE")
	public String getGuid() {
		return guid;
	}

	/**
	 * Set the guid.
	 *
	 * @param guid the guid to set.
	 */
	@Override
	public void setGuid(final String guid) {
		this.guid = guid;
	}


	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("DynamicContent class:");
		stringBuilder.append(this.getClass().getName());
		stringBuilder.append("\ncontentWrapperId = [");
		stringBuilder.append(contentWrapperId);
		stringBuilder.append("]\nname = [");
		stringBuilder.append(name);
		stringBuilder.append("]\ndescription = [");
		stringBuilder.append(description);
		stringBuilder.append("]\nparameter values = [");
		stringBuilder.append(getParameterValues());
		stringBuilder.append("]\nguid = [");
		stringBuilder.append(getGuid());
		stringBuilder.append(']');
		
		return stringBuilder.toString();
		
	}
	
	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof DynamicContentImpl)) {
			return false;
		}
		return super.equals(other);
	}

	@Override
	@SuppressWarnings("PMD.UselessOverridingMethod")
	public int hashCode() {
		return super.hashCode();
	}
}
