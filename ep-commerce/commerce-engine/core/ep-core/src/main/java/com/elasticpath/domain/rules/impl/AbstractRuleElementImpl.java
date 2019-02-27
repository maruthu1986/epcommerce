/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.rules.impl;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.jdbc.ElementForeignKey;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;

import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.impl.AbstractLegacyPersistenceImpl;
import com.elasticpath.domain.rules.RuleElement;
import com.elasticpath.domain.rules.RuleException;
import com.elasticpath.domain.rules.RuleExceptionType;
import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.service.rules.PromotionRuleExceptions;

/**
 * Abstract class with behaviour common to all rule elements.
 */
@Entity
@Table(name = AbstractRuleElementImpl.TABLE_NAME)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE", discriminatorType = DiscriminatorType.STRING)
@DataCache(enabled = false)
public abstract class AbstractRuleElementImpl extends AbstractLegacyPersistenceImpl implements RuleElement {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TRULEELEMENT";

	private String kind;

	private String type;

	private Set<RuleParameter> parameters = new HashSet<>();

	private Set<RuleException> exceptions = new HashSet<>();

	private long ruleId;

	private long uidPk;

	/**
	 * Must be implemented by subclasses to return their kind. (E.g. eligibility, condition, action)
	 *
	 * @return the kind of the element subclass.
	 */
	protected abstract String getElementKind();

	/**
	 * Get the kind of this <code>RuleElement</code> (e.g. eligibility, condition, action).
	 *
	 * @return the kind
	 */
	@Override
	@Basic
	@Column(name = "KIND")
	public String getKind() {
		if (this.kind == null) {
			this.kind = getElementKind();
		}
		return this.kind;
	}

	/**
	 * Set the kind of this <code>RuleElement</code> (e.g. eligibility, condition, action)
	 *
	 * @param kind the kind of the rule element
	 */
	@Override
	public void setKind(final String kind) {
		this.kind = kind;
	}

	/**
	 * Get the type of this rule element. (e.g. cartCategoryPercentDiscountAction. Should match spring bean factory bean id.)
	 *
	 * @return the type
	 */
	@Override
	@Basic
	@Column(name = "TYPE")
	public String getType() {
		if (this.type == null) {
			this.type = getElementType().getPropertyKey();
		}
		return this.type;
	}

	/**
	 * Set the type of element. (e.g. cartCategoryPercentDiscountAction. Should match bean name)
	 *
	 * @param type the type of element
	 */
	@Override
	public void setType(final String type) {
		this.type = type;
	}

	/**
	 * Returns the value of a parameter with the specified key.
	 *
	 * @param key The key of the parameter to be returned
	 * @return the value of the parameter with the specified key or "" if no matching parameter was found.
	 */
	@Override
	public String getParamValue(final String key) {
		if (getParameters() != null) {
			for (RuleParameter currParam : getParameters()) {
				if (currParam.getKey().equals(key)) {
					return currParam.getValue();
				}
			}
		}
		return "";
	}

	/**
	 * Checks that the rule set domain model is well formed. For example, rule conditions must have all required parameters specified. Note: the
	 * parameter value of categoryId, productId and skuCode are allowed to be empty, which mean "ANY".
	 *
	 * @throws EpDomainException if the structure is not correct.
	 */
	@Override
	public void validate() throws EpDomainException {
		String[] keys = getParameterKeys();
		if (keys != null) {
			for (int i = 0; i < keys.length; i++) {
				String paramValue = getParamValue(keys[i]);
				if (StringUtils.isBlank(paramValue)) {
					throw new EpDomainException("Rule element " + this.getType() + " must have parameter with key " + keys[i]);
				}
			}
		}
	}

	/**
	 * Get the parameters associated with this rule element.
	 *
	 * @return the parameters
	 */
	@Override
	@OneToMany(targetEntity = RuleParameterImpl.class, fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@ElementJoinColumn(name = "RULE_ELEMENT_UID")
	@ElementDependent
	@ElementForeignKey(name = "TRULEPARAMETER_IBFK_1")
	public Set<RuleParameter> getParameters() {
		return this.parameters;
	}

	/**
	 * Set the parameters of this rule element.
	 *
	 * @param parameters a set of <code>RuleParameter</code> objects
	 */
	@Override
	public void setParameters(final Set<RuleParameter> parameters) {
		this.parameters = parameters;
	}

	/**
	 * Add a parameter to this rule element.
	 *
	 * @param ruleParameter a <code>RuleParameter</code> object
	 */
	@Override
	public void addParameter(final RuleParameter ruleParameter) {
		getParameters().add(ruleParameter);
	}

	/**
	 * Get the <code>RuleException</code> objects associated with this <code>RuleElement</code>.
	 *
	 * @return the set of ruleExceptions
	 */
	@Override
	@OneToMany(targetEntity = AbstractRuleExceptionImpl.class, fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@ElementJoinColumn(name = "RULE_ELEMENT_UID")
	@ElementDependent
	@ElementForeignKey(name = "TRULEEXCEPTION_IBFK_1")
	public Set<RuleException> getExceptions() {
		return this.exceptions;
	}

	/**
	 * Set the exceptions of this rule element.
	 *
	 * @param ruleExceptions a set of <code>RuleException</code> objects.
	 */
	@Override
	public void setExceptions(final Set<RuleException> ruleExceptions) {
		this.exceptions = ruleExceptions;
	}

	/**
	 * Add an exception to this rule element.
	 *
	 * @param ruleException the <code>RuleException</code> object to add
	 */
	@Override
	public void addException(final RuleException ruleException) {
		getExceptions().add(ruleException);
	}

	/**
	 * Get the string representation of the <code>RuleException</code>s associated with this <code>RuleElement</code>. This string will be used
	 * in the generated Drools code.
	 *
	 * @return the string representation of the <code>RuleException</code>s.
	 */
	@Transient
	protected String getExceptionStr() {
		final StringBuilder categoryCodes = new StringBuilder();
		final StringBuilder productCodes = new StringBuilder();
		final StringBuilder skuCodes = new StringBuilder();

		for (RuleException ruleException : getExceptions()) {
			if (RuleExceptionType.CATEGORY_EXCEPTION.equals(ruleException.getExceptionType())) {
				categoryCodes.append(ruleException.getParamValue(RuleParameter.CATEGORY_CODE_KEY)).append(
						PromotionRuleExceptions.EXCEPTION_STRING_SEPARATOR);
			} else if (RuleExceptionType.PRODUCT_EXCEPTION.equals(ruleException.getExceptionType())) {
				productCodes.append(ruleException.getParamValue(RuleParameter.PRODUCT_CODE_KEY)).append(
						PromotionRuleExceptions.EXCEPTION_STRING_SEPARATOR);
			} else if (RuleExceptionType.SKU_EXCEPTION.equals(ruleException.getExceptionType())) {
				skuCodes.append(ruleException.getParamValue(RuleParameter.SKU_CODE_KEY)).append(
						PromotionRuleExceptions.EXCEPTION_STRING_SEPARATOR);
			}
		}

		StringBuilder sbf = new StringBuilder();
		sbf.append(PromotionRuleExceptions.CATEGORY_CODES).append(categoryCodes);
		sbf.append(PromotionRuleExceptions.PRODUCT_CODES).append(productCodes);
		sbf.append(PromotionRuleExceptions.PRODUCTSKU_CODES).append(skuCodes);
		return sbf.toString();
	}

	/**
	 * Set the identifier for the rule that contains this action. (For traceablility)
	 *
	 * @param ruleId the id of the rule containing this action.
	 */
	@Override
	public void setRuleId(final long ruleId) {
		this.ruleId = ruleId;
	}

	/**
	 * Get the id of the rule containing this element (for traceability).
	 *
	 * @return the rule id.
	 */
	@Transient
	protected long getRuleId() {
		return this.ruleId;
	}

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
	 * Sets the unique identifier for this domain model object.
	 *
	 * @param uidPk the new unique identifier.
	 */
	@Override
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}

	/**
	 * Sets default values.
	 */
	@Override
	public void initialize() {
		// initialize rule parameters
		final String[] paramKeys = this.getParameterKeys();
		if (paramKeys != null) {
			for (final String currParamKey : paramKeys) {
				final RuleParameter currRuleParameter = new RuleParameterImpl();
				currRuleParameter.setKey(currParamKey);
				getParameters().add(currRuleParameter);
			}
		}
	}
}
