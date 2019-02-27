/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.catalog.impl;

import java.util.Comparator;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.FetchGroups;
import org.apache.openjpa.persistence.jdbc.ForeignKey;

import com.elasticpath.commons.constants.GlobalConstants;
import com.elasticpath.domain.DatabaseLastModifiedDate;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.impl.AbstractLegacyEntityImpl;
import com.elasticpath.persistence.api.AbstractEntityImpl;
import com.elasticpath.persistence.support.FetchGroupConstants;

/**
 * Abstract implementation of a <code>Category</code> object. Holds the fields common to categories that are linked and categories that are not
 * linked. Uses JPA's JOINED inheritance strategy.<br/>
 *
 * NOTE that the presence of the {@code DatabaseLastModifiedDate} means that whenever this object is saved or updated to the database
 * the lastModifiedDate will be set by the {@code LastModifiedPersistenceEngineImpl} if that class in configured in Spring.
 */
@Entity
@Table(name = AbstractCategoryImpl.TABLE_NAME)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "TYPE", discriminatorType = DiscriminatorType.STRING, length = GlobalConstants.SHORT_TEXT_MAX_LENGTH)
@FetchGroups({
	@FetchGroup(name = FetchGroupConstants.CATALOG, attributes = {
			@FetchAttribute(name = "catalog") }),
	@FetchGroup(name = FetchGroupConstants.CATEGORY_BASIC, attributes = {
			@FetchAttribute(name = "uidPk"),
			@FetchAttribute(name = "guid"),
			@FetchAttribute(name = "lastModifiedDate"),
			@FetchAttribute(name = "ordering"),
			@FetchAttribute(name = "parentGuid"),
			@FetchAttribute(name = "catalog") }),
	@FetchGroup(name = FetchGroupConstants.CATEGORY_INDEX, fetchGroups = { FetchGroupConstants.CATEGORY_BASIC }),
	@FetchGroup(name = FetchGroupConstants.CATEGORY_HASH_MINIMAL, attributes = {
			@FetchAttribute(name = "guid") }),
	@FetchGroup(name = FetchGroupConstants.LINK_PRODUCT_CATEGORY,
			fetchGroups = { FetchGroupConstants.CATEGORY_HASH_MINIMAL })
})
@SuppressWarnings({ "PMD.AvoidDuplicateLiterals", "PMD.GodClass" })
public abstract class AbstractCategoryImpl extends AbstractLegacyEntityImpl implements Category, DatabaseLastModifiedDate {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000003L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TCATEGORY";

	private String parentGuid;

	private int ordering;

	private Date lastModifiedDate;

	private Catalog catalog;

	private long uidPk;
	private String guid;

	/**
	 * Default constructor.
	 */
	public AbstractCategoryImpl() {
		super();
	}

	/**
	 * Return the guid.
	 *
	 * @return the guid.
	 */
	@Override
	@Basic(optional = false)
	@Column(name = "GUID", length = AbstractEntityImpl.GUID_LENGTH, nullable = false, unique = true)
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

	@Basic
	@Column(name = "PARENT_CATEGORY_GUID", nullable = true)
	@Override
	public String getParentGuid() {
		return parentGuid;
	}

	@Override
	public void setParentGuid(final String parentGuid) {
		this.parentGuid = parentGuid;
	}

	@Override
	public void setParent(final Category parent) {
		if (parent == null) {
			setParentGuid(null);
		} else {
			if (!parent.isPersisted()) {
				throw new EpDomainException("Parent category must be persisted before associating the child with the parent");
			}

			setParentGuid(parent.getGuid());
		}
	}

	@Override
	public boolean hasParent() {
		return getParentGuid() != null;
	}

	/**
	 * Get the ordering number.
	 *
	 * @return the ordering number
	 */
	@Override
	@Basic
	@Column(name = "ORDERING")
	public int getOrdering() {
		return this.ordering;
	}

	/**
	 * Set the ordering number.
	 *
	 * @param ordering the ordering number
	 */
	@Override
	public void setOrdering(final int ordering) {
		this.ordering = ordering;
	}

	/**
	 * Return the compound category guid based on category code and appropriate catalog code.
	 *
	 * @return the compound guid.
	 */
	@Override
	@Transient
	public String getCompoundGuid() {
		return this.getCode() + CATEGORY_LEGACY_GUID_DELIMITER + getCatalog().getCode();
	}

	/**
	 * Compares this category with the specified object for order.
	 *
	 * @param category the given object
	 * @return a negative integer, zero, or a positive integer if this object is less than, equal to, or greater than the specified object.
	 */
	@Override
	public int compareTo(final Category category) {
		return Comparator.nullsFirst(Comparator.comparing(Category::getOrdering)
			.thenComparing(Category::getGuid))
			.compare(this, category);
	}

	/**
	 * Returns the date when the category was last modified.
	 *
	 * @return the date when the category was last modified
	 */
	@Override
	@Basic(optional = true)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_MODIFIED_DATE", nullable = false)
	public Date getLastModifiedDate() {
		return this.lastModifiedDate;
	}

	/**
	 * Set the date when the category was last modified.
	 *
	 * @param lastModifiedDate the date when the category was last modified
	 */
	@Override
	public void setLastModifiedDate(final Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	/**
	 * Get the catalog this category belongs to.
	 *
	 * @return the catalog
	 */
	@Override
	@ManyToOne(optional = false, targetEntity = CatalogImpl.class, cascade = { CascadeType.REFRESH, CascadeType.MERGE })
	@JoinColumn(name = "CATALOG_UID", nullable = false)
	@ForeignKey
	public Catalog getCatalog() {
		return this.catalog;
	}

	/**
	 * Set the catalog this category belongs to.
	 *
	 * @param catalog the catalog to set
	 */
	@Override
	public void setCatalog(final Catalog catalog) {
		this.catalog = catalog;
	}

	/**
	 * Gets the unique identifier for this domain model object.
	 *
	 * @return the unique identifier.
	 */
	@Override
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = AbstractCategoryImpl.TABLE_NAME)
	@TableGenerator(name = AbstractCategoryImpl.TABLE_NAME, table = "JPA_GENERATED_KEYS",
			pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = AbstractCategoryImpl.TABLE_NAME)
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

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof AbstractCategoryImpl)) {
			return false;
		}

		return super.equals(other);
	}

}
