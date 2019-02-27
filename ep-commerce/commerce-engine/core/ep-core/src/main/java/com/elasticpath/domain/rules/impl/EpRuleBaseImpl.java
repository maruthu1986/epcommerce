/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.domain.rules.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.util.Date;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.Externalizer;
import org.apache.openjpa.persistence.Factory;
import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.Type;
import org.apache.openjpa.persistence.jdbc.ForeignKey;
import org.drools.core.common.DroolsObjectInputStream;
import org.drools.core.common.DroolsObjectOutputStream;
import org.kie.api.KieBase;

import com.elasticpath.domain.DatabaseLastModifiedDate;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.rules.EpRuleBase;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.persistence.api.AbstractPersistableImpl;

/**
 * Default implementation of {@link EpRuleBase}.
 */
@Entity
@Table(name = EpRuleBaseImpl.TABLE_NAME)
@DataCache(enabled = false)
public class EpRuleBaseImpl extends AbstractPersistableImpl implements EpRuleBase, DatabaseLastModifiedDate {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	private static final String RULEBASE_ZIPENTRY_NAME = "compressed-rules";

	/** The name of the table & generator to use for persistence. */
	public static final String TABLE_NAME = "TRULESTORAGE";

	private long uidPk;

	private KieBase ruleBase;

	private Store store;

	private Catalog catalog;

	private int scenarioId;

	private Date lastModifiedDate;

	/**
	 * Gets the compiled rule base.
	 *
	 * @return the compiled rule base
	 */
	@Override
	@Lob
	@Persistent(optional = false)
	@Column(name = "RULEBASE")
	@Externalizer("com.elasticpath.domain.rules.impl.EpRuleBaseImpl.externalizeRuleBase")
	@Factory("com.elasticpath.domain.rules.impl.EpRuleBaseImpl.ruleBaseFactory")
	@Type(byte[].class)
	public KieBase getRuleBase() {
		return ruleBase;
	}

	/**
	 * Sets the compiled rule base.
	 *
	 * @param ruleBase the compiled rule base
	 */
	@Override
	public void setRuleBase(final KieBase ruleBase) {
		this.ruleBase = ruleBase;
	}

	/**
	 * Reads the rule base from a string. The string is assumed to be a zipped object stream.
	 * Processing of the stream occurs in the following order:
	 * <p>
	 * {@code read zip entry -> read object}
	 * </p>
	 *
	 * @param ruleBaseStream the rule base as a string
	 * @return the rule base represented by the stream
	 * @throws EpDomainException if there are any errors reading from the string
	 */
	public static KieBase ruleBaseFactory(final byte[] ruleBaseStream) {
		try (final ZipInputStream zippedFile = getZipInputStream(ruleBaseStream);
			 final ObjectInputStream input = new DroolsObjectInputStream(zippedFile, EpRuleBaseImpl.class.getClassLoader())
		) {
			return (KieBase) input.readObject();
		} catch (IOException | ClassNotFoundException e) {
			throw new EpDomainException("Fatal error reading rule base", e);
		}
	}

	/**
	 * Gets a valid ZipInputStream for the given byte array.
	 *
	 * @param ruleBaseStream the rule base array stream
	 * @return a ZipInputStream
	 * @throws IOException if the stream does not contain a zip entry
	 */
	protected static ZipInputStream getZipInputStream(final byte[] ruleBaseStream) throws IOException {
		final ZipInputStream zippedFile = new ZipInputStream(new ByteArrayInputStream(ruleBaseStream));

		ZipEntry entry = zippedFile.getNextEntry();
		while (entry != null && !entry.getName().equals(RULEBASE_ZIPENTRY_NAME)) {
			entry = zippedFile.getNextEntry();
		}

		if (entry == null) {
			throw new EpDomainException(String.format("Invalid rule base stream! Unable to find zip entry %1$s",
					RULEBASE_ZIPENTRY_NAME));
		}
		return zippedFile;
	}

	/**
	 * Externalizes the rule base. The externalized rule base as a zipped object output stream.
	 * The algorithm used for externalization is the following
	 * <p>
	 * {@code object output stream -> standard high compression zipped entry}
	 * </p>
	 *
	 * @param ruleBase the rule base to externalize
	 * @return the externalized zipped array
	 */
	public static byte[] externalizeRuleBase(final KieBase ruleBase) {
		try (final ByteArrayOutputStream result = new ByteArrayOutputStream();
			 final ZipOutputStream zippedFile = createZipOutputStream(result);
			 final ObjectOutput output = new DroolsObjectOutputStream(zippedFile)
		) {
			output.writeObject(ruleBase);

			zippedFile.closeEntry();

			// we just need a consistent way to read and write data to the database
			return result.toByteArray();
		} catch (IOException e) {
			throw new EpDomainException("Fatal error writing rule base", e);
		}
	}

	/**
	 * Create a ZipOutputStream for the rulebase.
	 *
	 * @param bytestream the stream to output to
	 * @return a ZipOutputStream
	 * @throws IOException if there are errors creating the stream
	 */
	protected static ZipOutputStream createZipOutputStream(final ByteArrayOutputStream bytestream) throws IOException {
		final ZipOutputStream zippedFile = new ZipOutputStream(bytestream);

		zippedFile.putNextEntry(new ZipEntry(RULEBASE_ZIPENTRY_NAME));
		zippedFile.setLevel(Deflater.BEST_COMPRESSION);
		zippedFile.setMethod(ZipEntry.DEFLATED);
		return zippedFile;
	}

	/**
	 * Gets the scenario ID this rule base applies in.
	 *
	 * @return the scenario ID this rule base applies in
	 */
	@Override
	@Basic(optional = false)
	@Column(name = "SCENARIO")
	public int getScenarioId() {
		return scenarioId;
	}

	/**
	 * Sets the scenario ID this rule base applies in.
	 *
	 * @param ruleScenarioId the scenario ID this rule base applies in
	 */
	@Override
	public void setScenarioId(final int ruleScenarioId) {
		this.scenarioId = ruleScenarioId;
	}

	/**
	 * Gets the store this rule base belongs to.
	 *
	 * @return the store this rule base belongs to
	 */
	@Override
	@ManyToOne(targetEntity = StoreImpl.class, cascade = { CascadeType.MERGE })
	@JoinColumn(name = "STORE_UID")
	@ForeignKey
	public Store getStore() {
		return store;
	}

	/**
	 * Sets the store this rule base belongs to.
	 *
	 * @param store the store this rule base belongs to
	 */
	@Override
	public void setStore(final Store store) {
		this.store = store;
	}

	/**
	 * Gets the catalog this rule base belongs to.
	 *
	 * @return the catalog this rule base belongs to
	 */
	@Override
	@ManyToOne(targetEntity = CatalogImpl.class, cascade = { CascadeType.MERGE })
	@JoinColumn(name = "CATALOG_UID")
	@ForeignKey
	public Catalog getCatalog() {
		return catalog;
	}

	/**
	 * Sets the catalog this rule base belongs to.
	 *
	 * @param catalog  the catalog this rule base belongs to
	 */
	@Override
	public void setCatalog(final Catalog catalog) {
		this.catalog = catalog;
	}

	/**
	 * Gets the unique identifier for this domain model object.
	 *
	 * @return the unique identifier
	 */
	@Override
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS",
					pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
	public long getUidPk() {
		return uidPk;
	}

	/**
	 * Sets the unique identifier for this domain model object.
	 *
	 * @param uidPk the new unique identifier
	 */
	@Override
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}


	/**
	 * Returns the date when the rule base was last modified.
	 *
	 * @return the date when the rule base was last modified
	 */
	@Override
	@Basic(optional = true)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_MODIFIED_DATE", nullable = false)
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	/**
	 * Set the date when the rule base was last modified.
	 *
	 * @param lastModifiedDate the date when the rule base was last modified
	 */
	@Override
	public void setLastModifiedDate(final Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
}
