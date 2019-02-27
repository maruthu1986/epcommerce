/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.ui.framework;

/**
 * Country default implementation.
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.NPathComplexity" })
public class Country implements Comparable<Country> {

	private final String countryCode;

	private final String countryName;

	private String subCountryCode;

	private String subCountryName;

	/**
	 * Constructor.
	 * 
	 * @param country the Country entity
	 */
	public Country(final Country country) {
		this.countryCode = country.getCountryCode();
		this.countryName = country.getCountryName();
		this.subCountryCode = country.getSubCountryCode();
		this.subCountryName = country.getSubCountryName();
	}

	/**
	 * Constructor.
	 * 
	 * @param country the Country entity
	 * @param subCountryCode the subCountryCode
	 * @param subCountryName the subCountryName
	 */
	public Country(final Country country, final String subCountryCode, final String subCountryName) {
		this.countryCode = country.getCountryCode();
		this.countryName = country.getCountryName();
		this.subCountryCode = subCountryCode;
		this.subCountryName = subCountryName;
	}

	/**
	 * Constructor.
	 * 
	 * @param countryCode the countryCode
	 * @param countryName the countryName
	 */
	public Country(final String countryCode, final String countryName) {
		this.countryCode = countryCode;
		this.countryName = countryName;
	}

	/**
	 * Constructor.
	 * 
	 * @param countryCode the countryCode
	 * @param countryName the countryName
	 * @param subCountryCode the subCountryCode
	 * @param subCountryName the subCountryName
	 */
	public Country(final String countryCode, final String countryName, final String subCountryCode, final String subCountryName) {
		this.countryCode = countryCode;
		this.countryName = countryName;
		this.subCountryCode = subCountryCode;
		this.subCountryName = subCountryName;
	}

	/**
	 * @return the countryCode
	 */
	public String getCountryCode() {
		return countryCode;
	}

	/**
	 * @return the countryName
	 */
	public String getCountryName() {
		return countryName;
	}

	/**
	 * @return the subCountryCode
	 */
	public String getSubCountryCode() {
		return subCountryCode;
	}

	/**
	 * @return the subCountryName
	 */
	public String getSubCountryName() {
		return subCountryName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		if (countryCode == null) {
			result = prime * result;
		} else {
			result = prime * result + countryCode.hashCode();
		}

		if (countryName == null) {
			result = prime * result;
		} else {
			result = prime * result + countryName.hashCode();
		}

		if (subCountryCode == null) {
			result = prime * result;
		} else {
			result = prime * result + subCountryCode.hashCode();
		}

		if (subCountryName == null) {
			result = prime * result;
		} else {
			result = prime * result + subCountryName.hashCode();
		}

		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Country)) {
			return false;
		}

		final Country other = (Country) obj;
		if (countryCode == null) {
			if (other.countryCode != null) {
				return false;
			}
		} else if (!countryCode.equals(other.countryCode)) {
			return false;
		}
		if (countryName == null) {
			if (other.countryName != null) {
				return false;
			}
		} else if (!countryName.equals(other.countryName)) {
			return false;
		}
		if (subCountryCode == null) {
			if (other.subCountryCode != null) {
				return false;
			}
		} else if (!subCountryCode.equals(other.subCountryCode)) {
			return false;
		}
		if (subCountryName == null) {
			if (other.subCountryName != null) {
				return false;
			}
		} else if (!subCountryName.equals(other.subCountryName)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer(countryName);
		if (subCountryCode != null) {
			result.append(" (").append(subCountryName).append(')'); //$NON-NLS-1$
		}
		return result.toString();
	}

	/**
	 * Compares to countries lexicographicly.
	 * 
	 * @param other Other country this one to compare with.
	 * @return -1 if this country is higher in the list than the other one. 0 if both countries are the same. 1 if this
	 *         country is lower in the list than the other one.
	 */
	public int compareTo(final Country other) {
		if (this == other) {
			return 0;
		}

		if (other == null) {
			return 1;
		}

		return toString().compareTo(other.toString());
	}

}
