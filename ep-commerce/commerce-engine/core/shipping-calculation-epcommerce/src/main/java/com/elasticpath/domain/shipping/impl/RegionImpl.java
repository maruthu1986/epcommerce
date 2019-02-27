/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.domain.shipping.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.impl.AbstractEpDomainImpl;
import com.elasticpath.domain.shipping.Region;

/**
 * A Region represents a geography definition of an area. For now, it is composed of country and a subcountry, i.e. CA(country) and BC(subcountry).
 */
public class RegionImpl extends AbstractEpDomainImpl implements Region {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private static final Pattern REGION_PATTERN = Pattern.compile("^(,\\s*)*\\[*([A-Z]{2})\\(([^\\)]+)*\\)\\]$");

	private static final int COUNTRY_CODE_GROUP_NO = 2;

	private static final int SUBCOUNTRY_CODE_GROUP_NO = 3;

	private String countryCode;

	private List<String> subCountryCodeList;

	/**
	 * Default constructor.
	 */
	public RegionImpl() {
		super();
		subCountryCodeList = new ArrayList<>();
	}

	/**
	 * Constructor.
	 *
	 * @param countryCode - the ISO country code for this region.
	 */
	public RegionImpl(final String countryCode) {
		this();
		this.countryCode = countryCode;
	}

	/**
	 * Constructor.
	 *
	 * @param countryCode - the ISO country code for this region.
	 * @param subCountryCodeList - the list of ISO subCountry code for this region.
	 */
	public RegionImpl(final String countryCode, final List<String> subCountryCodeList) {
		this();
		this.countryCode = countryCode;
		this.subCountryCodeList = subCountryCodeList;
	}

	/**
	 * Get the region's country code (ISO country code).
	 *
	 * @return the region's country code.
	 */
	@Override
	public String getCountryCode() {
		return countryCode;
	}

	/**
	 * Set the region's country code (ISO country code).
	 *
	 * @param countryCode the region's country code.
	 */
	@Override
	public void setCountryCode(final String countryCode) {
		this.countryCode = countryCode;
	}

	/**
	 * Get the region's subcountry code list.
	 *
	 * @return the region's subcountry code list.
	 */
	@Override
	public List<String> getSubCountryCodeList() {
		return subCountryCodeList;
	}

	/**
	 * Set the region's subcountry code list.
	 *
	 * @param subCountryCodeList the region's subcountry code.
	 */
	@Override
	public void setSubCountryCodeList(final List<String> subCountryCodeList) {
		if (subCountryCodeList == null) {
			throw new EpDomainException("Null subCountryCodeList cannot be set. Set it to an empty list instead.");
		}
		this.subCountryCodeList = subCountryCodeList;
	}

	/**
	 * Merge the given additionalSubCountryCodeList into the existing subCountryCodeList of this <code>Region</code> instance.
	 *
	 * @param additionalSubCountryCodeList - the additional subCountryCodeList to be merged in.
	 */
	@Override
	public void mergeSubCountryCodeList(final List<String> additionalSubCountryCodeList) {
		if (subCountryCodeList == null || subCountryCodeList.isEmpty()) {
			subCountryCodeList = additionalSubCountryCodeList;
		} else {
			for (final String newSubCountryCode : additionalSubCountryCodeList) {
				if (!subCountryCodeList.contains(newSubCountryCode)) {
					subCountryCodeList.add(newSubCountryCode);
				}
			}

		}
	}

	/**
	 * Return the String representation of this <code>Region</code>.
	 *
	 * @return string representation of the region.
	 */
	public String toString() {
		if (StringUtils.isBlank(countryCode)) {
			throw new EpDomainException("Failed to get the string representation of an empty region.");
		}

		final StringBuilder regionStr = new StringBuilder("[");
		regionStr.append(countryCode);
		regionStr.append('(');
		if (!getSubCountryCodeList().isEmpty()) {
			for (final String code : getSubCountryCodeList()) {
				regionStr.append(code);
				regionStr.append(',');
			}
			regionStr.deleteCharAt(regionStr.lastIndexOf(","));
		}
		regionStr.append(")]");
		return regionStr.toString();
	}

	/**
	 * Return the <code>Region</code> from parsing the given string representation.
	 *
	 * @param regionStr - the String representation of the <code>Region</code>
	 * @return Region - the instance of <code>Region</code>
	 */
	@Override
	public Region fromString(final String regionStr) {
		final Matcher regionMatch = REGION_PATTERN.matcher(regionStr);
		if (regionMatch.matches()) {
			final String countryCode = regionMatch.group(COUNTRY_CODE_GROUP_NO);
			setCountryCode(countryCode);
			if (regionMatch.group(SUBCOUNTRY_CODE_GROUP_NO) != null) {
				String[] subCountryArray = regionMatch.group(SUBCOUNTRY_CODE_GROUP_NO).split(",");
				setSubCountryCodeList(Arrays.asList(subCountryArray));
			}
		} else {
			throw new EpDomainException("Invalid region string representation: " + regionStr);
		}
		return this;
	}
}
