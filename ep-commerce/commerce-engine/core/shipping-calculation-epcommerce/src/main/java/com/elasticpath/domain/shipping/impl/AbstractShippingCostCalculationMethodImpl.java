/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */

package com.elasticpath.domain.shipping.impl;

import static com.elasticpath.commons.constants.EpShippingContextIdNames.SHIPPING_COST_CALCULATION_PARAMETER;
import static com.google.common.base.Preconditions.checkArgument;
import static java.util.stream.Stream.of;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.jdbc.ElementForeignKey;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;

import com.elasticpath.commons.exception.SCCMCurrencyMissingException;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.impl.AbstractLegacyPersistenceImpl;
import com.elasticpath.domain.shipping.ShippingCostCalculationMethod;
import com.elasticpath.domain.shipping.ShippingCostCalculationParameter;
import com.elasticpath.shipping.connectivity.dto.ShippableItem;

/**
 * A shippingCostCalculationMethod represents a method to be used for shipping cost calculation. It is a component of a shipping service level.
 */
@Entity
@Table(name = AbstractShippingCostCalculationMethodImpl.TABLE_NAME)
@DiscriminatorColumn(name = "TYPE", discriminatorType = DiscriminatorType.STRING)
@DataCache(enabled = true)
public abstract class AbstractShippingCostCalculationMethodImpl extends AbstractLegacyPersistenceImpl implements ShippingCostCalculationMethod {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TSHIPPINGCOSTCALCULATIONMETHOD";

	private String type;

	private Set<ShippingCostCalculationParameter> parameters;

	private long uidPk;

	/**
	 * Must be implemented by subclasses to return their type. (e.g. fixedBaseAndOrderTotalPercentageMethod. Should match bean name)
	 *
	 * @return the kind of the action subclass.
	 */
	@Transient
	protected abstract String getMethodType();

	/**
	 * Get the type of shippingCostCalculationMethod, i.e. fixBase, fixBaseAndPercentageOfOrderTotal and etc.
	 *
	 * @return the shippingCostCalculationMethod type.
	 */
	@Override
	@Transient
	public String getType() {
		if (this.type == null) {
			this.type = getMethodType();
		} else if (!this.type.equals(this.getMethodType())) {
			throw new EpDomainException("Invalid shipping cost calculation type");
		}
		return this.type;
	}

	/**
	 * Set the type of shippingCostCalculationMethod.
	 *
	 * @param type the type of shippingCostCalculationMethod.
	 */
	@Override
	public void setType(final String type) {
		this.type = type;
	}

	/**
	 * Get the parameters associated with this shippingCostCalculationMethod.
	 *
	 * @return the parameters
	 */
	@Override
	@OneToMany(targetEntity = ShippingCostCalculationParameterImpl.class, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH,
			CascadeType.REMOVE}, fetch = FetchType.EAGER)
	@ElementJoinColumn(name = "SCCM_UID")
	@ElementDependent
	@ElementForeignKey(name = "TSHIPPINGCOSTCALCULATIONPARAM_IBFK_1")
	public Set<ShippingCostCalculationParameter> getParameters() {
		return this.parameters;
	}

	/**
	 * Set the parameters of this shippingCostCalculationMethod.
	 *
	 * @param shippingCostCalculationParameters a set of <code>ShippingCostCalculationParamater</code> objects
	 */
	@Override
	public void setParameters(final Set<ShippingCostCalculationParameter> shippingCostCalculationParameters) {
		this.parameters = shippingCostCalculationParameters;
	}

	@Override
	@Transient
	public boolean hasParameter(final String key, final Currency... currencies) {
		checkArgument(currencies.length > 1 || key == null, "Illegal argument: key should not be null and only one currency can be matched.");

		if (getParameters() == null) {
			return false;
		}

		return getParameters().stream()
				.filter(currParam -> currParam.getKey().equals(key))
				.anyMatch(currParam -> isValidParameter(currParam, key, of(currencies).findFirst().orElse(null)));
	}

	/**
	 * Checks the parameter of shpping cost calculation is valid or not.
	 *
	 * @param param    the parameter.
	 * @param key      the key of parameter.
	 * @param currency the currency
	 * @return true if satisfied by the criteria. false if not.
	 */
	private boolean isValidParameter(final ShippingCostCalculationParameter param, final String key, final Currency currency) {
		if (!param.getKey().equals(key)) {
			return false;
		}

		if (param.isCurrencyAware()) {
			return currency != null && currency.equals(param.getCurrency());
		}
		return currency == null;
	}

	/**
	 * Returns the value of a parameter with the specified key if the parameter is not currencyAware.
	 *
	 * @param key The key of the parameter to be returned
	 * @return the value of the parameter with the specified key or "" if no matching parameter was found.
	 */
	protected String getParamValue(final String key) {
		String value = "";
		if (getParameters() != null) {
			for (ShippingCostCalculationParameter currParam : getParameters()) {
				if (currParam.getKey().equals(key) && !currParam.isCurrencyAware()) {
					value = currParam.getValue();
					break;
				}
			}
		}
		return value;
	}

	/**
	 * Returns the value of a parameter with the specified key for the specified currency.
	 *
	 * @param key      The key of the parameter to be returned
	 * @param currency The currency, parameter should match to.
	 * @return the value of the parameter with the specified key and currency.
	 * @throws SCCMCurrencyMissingException if matching param value for the passed currency is not found or if currency is null.
	 */
	protected String getParamValue(final String key, final Currency currency) {
		if (currency == null) {
			throw new SCCMCurrencyMissingException("Currency was null for key: " + key);
		}

		String value = null;
		if (this.parameters != null) {
			for (ShippingCostCalculationParameter currParam : this.parameters) {
				// if currency parameter provided - then take it into consideration when getting appropriate parameter.
				if (currParam.isCurrencyAware() && currParam.getKey().equals(key)
						&& currParam.getCurrency() != null && currency.equals(currParam.getCurrency())) {
					value = currParam.getValue();
					break;
				}
			}
		}

		if (StringUtils.isEmpty(value)) {
			throw new SCCMCurrencyMissingException("No value for " + key + " matching currency: "
					+ currency.getCurrencyCode());
		}

		return value;
	}

	@Override
	public List<ShippingCostCalculationParameter> getDefaultParameters(final List<Currency> currencyList) {
		List<ShippingCostCalculationParameter> paramsList = new ArrayList<>();
		for (String paramKey : getParameterKeys()) {
			ShippingCostCalculationParameter parameter = getShippingCostCalculationParameterBean();
			parameter.setKey(paramKey);
			/** now we have a parameter, currency-awareness flag was already set. */

			/** if this parameter is currency aware, then clone it for each currency. */
			if (parameter.isCurrencyAware()) {
				ShippingCostCalculationParameter parameterToSet = parameter;
				for (Currency currency : currencyList) {
					parameterToSet.setCurrency(currency);
					paramsList.add(parameterToSet);
					parameterToSet = getShippingCostCalculationParameterBean();
					parameterToSet.setKey(paramKey);
				}
			} else {
				/** otherwise-set non-currency aware parameter. */
				paramsList.add(parameter);
			}
		}
		return paramsList;
	}

	/**
	 * @return a new {@code ShippingCostCalculationParameter} bean.
	 */
	@Transient
	ShippingCostCalculationParameter getShippingCostCalculationParameterBean() {
		return getBean(SHIPPING_COST_CALCULATION_PARAMETER);
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
	 * Calculate the total weight of the items.
	 *
	 * @param shippableItems the items to calculate the weight of
	 * @return totalWeight
	 */
	protected BigDecimal calculateTotalWeight(final Collection<? extends ShippableItem> shippableItems) {
		BigDecimal totWeight = BigDecimal.ZERO;

		for (ShippableItem shippableItem : shippableItems) {
			BigDecimal currItemWeight = shippableItem.getWeight();
			if (currItemWeight != null && currItemWeight.compareTo(BigDecimal.ZERO) > 0) {
				totWeight = totWeight.add(currItemWeight.multiply(BigDecimal.valueOf(shippableItem.getQuantity())));
			}
		}

		return totWeight;
	}

}
