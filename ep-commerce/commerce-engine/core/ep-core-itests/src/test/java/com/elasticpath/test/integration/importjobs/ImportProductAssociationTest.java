/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.test.integration.importjobs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.domain.dataimport.ImportDataType;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.domain.dataimport.impl.AbstractImportTypeImpl;
import com.elasticpath.domain.dataimport.impl.ImportDataTypeProductAssociationImpl;
import com.elasticpath.service.catalog.ProductAssociationService;
import com.elasticpath.service.catalog.ProductLookup;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.test.util.Utils;

/**
 * Test import job for ProductAssociation.
 */
public class ImportProductAssociationTest extends ImportJobTestCase {

    @Autowired
    private ProductLookup productLookup;

    @Autowired
    private ProductAssociationService productAssociationService;

    /**
     * Test import ProductAssociation insert does not delete existing ProductAssociations.
     */
    @DirtiesDatabase
    @Test
    public void testImportProductAssociationInsert() throws Exception {
        setUpProducts();
        Product product = productLookup.findByGuid("102");
        assertNotNull("Product should have been found", product);

        executeImportJob(createClearThenInsertProductAssociationImportJob());
        executeImportJob(createInsertProductAssociationImportJob());

        Set<ProductAssociation> productAssociations = getProductAssociations(product);
        assertEquals("The product should have two product associations.", 2, productAssociations.size());

        ProductAssociation productAssociation = findProductAssociationByTargetProductAndAssociationType("101", 2, productAssociations);
        assertProductAssociation("101", 2, 3, 0, false, productAssociation);
        productAssociation = findProductAssociationByTargetProductAndAssociationType("101", 3, productAssociations);
        assertProductAssociation("101", 3, 3, 0, false, productAssociation);
    }

    /**
     * Test import ProductAssociation insert/update inserts new ProductAssociation.
     */
    public void testImportProductAssociationInsertUpdateForNewProductAssociation() throws Exception {
        setUpProducts();
        Product product = productLookup.findByGuid("101");
        assertNotNull("Product should be found", product);

        executeImportJob(createInsertUpdateProductAssociationImportJob());
        Set<ProductAssociation> productAssociations = getProductAssociations(product);
        assertEquals("The product should have one product association.", 1, productAssociations.size());
        ProductAssociation productAssociation = findProductAssociationByTargetProductAndAssociationType("102", 3, productAssociations);
        assertProductAssociation("102", 3, 1, 0, true, productAssociation);
    }

    /**
     * Test import ProductAssociation insert/update updates existing ProductAssociation.
     */
    public void testImportProductAssociationInsertUpdateForExistingProductAssociation() throws Exception {
        setUpProducts();
        Product product = productLookup.findByGuid("103");
        assertNotNull("Product should be found", product);

        executeImportJob(createInsertProductAssociationImportJob());
        Set<ProductAssociation> productAssociations = getProductAssociations(product);
        assertEquals("The product should have one product association.", 1, productAssociations.size());
        ProductAssociation productAssociation = findProductAssociationByTargetProductAndAssociationType("101", 3, productAssociations);
        assertProductAssociation("101", 3, 1, 0, true, productAssociation);

        executeImportJob(createInsertUpdateProductAssociationImportJob());
        productAssociations = getProductAssociations(product);
        assertEquals("The product should have one product association.", 1, productAssociations.size());
        productAssociation = findProductAssociationByTargetProductAndAssociationType("101", 3, productAssociations);
        assertProductAssociation("101", 3, 4, 0, true, productAssociation);
    }

    /**
     * Test import ProductAssociation clear then insert deletes existing ProductAssocations before insert.
     */
    @DirtiesDatabase
    @Test
    public void testImportProductAssociationClearThenInsert() throws Exception {
        setUpProducts();
        Product product = productLookup.findByGuid("102");
        assertNotNull("Product should have been found", product);

        executeImportJob(createInsertProductAssociationImportJob());
        Set<ProductAssociation> productAssociations = getProductAssociations(product);
        assertEquals("The product should have one product association.", 1, productAssociations.size());
        ProductAssociation productAssociation = findProductAssociationByTargetProductAndAssociationType("101", 2, productAssociations);
        assertProductAssociation("101", 2, 3, 0, false, productAssociation);

        executeImportJob(createClearThenInsertProductAssociationImportJob());
        productAssociations = getProductAssociations(product);
        assertEquals("The product should have one product association.", 1, productAssociations.size());
        productAssociation = findProductAssociationByTargetProductAndAssociationType("101", 3, productAssociations);
        assertProductAssociation("101", 3, 3, 0, false, productAssociation);
    }

    private void setUpProducts() throws Exception {
        executeImportJob(createInsertCategoriesImportJob());
        executeImportJob(createInsertProductImportJob());
    }

    @Override
	protected ImportJob createClearThenInsertProductAssociationImportJob() {
        List<ImportDataType> importDataTypes = importService.getCatalogImportDataTypes(scenario.getCatalog().getUidPk());
        String importDataTypeName = findByType(importDataTypes, ImportDataTypeProductAssociationImpl.class).getName();
        Map<String, Integer> mappings = new HashMap<>();

        mappings.put("sourceProductCode", 1);
        mappings.put("targetProductCode", 2);
        mappings.put("associationType", 3);
        mappings.put("sourceProductDependant", 4);
        mappings.put("defaultQuantity", 5);
        mappings.put("ordering", 6);

        ImportJob importJob = createSimpleImportJob(scenario.getCatalog(), Utils.uniqueCode("Clear then Insert Product Associations"),
                "productassociation_clear_then_insert.csv", AbstractImportTypeImpl.CLEAR_INSERT_TYPE, importDataTypeName,
                mappings);
        return importJob;
    }

    private Set<ProductAssociation> getProductAssociations(Product product) {
        return productAssociationService.getAssociations(product.getCode(),
                product.getMasterCatalog().getCode(), true);
    }

    private ProductAssociation findProductAssociationByTargetProductAndAssociationType(final String expectedTargetProductCode,
                                                                                       final int expectedAssociationType,
                                                                                       final Set<ProductAssociation> productAssociations) {
        for (ProductAssociation productAssociation : productAssociations) {
            if (productAssociation.getTargetProduct().getCode().equals(expectedTargetProductCode)
                    && productAssociation.getAssociationType().getOrdinal() == expectedAssociationType) {
                return productAssociation;
            }

        }
        return null;
    }

    private void assertProductAssociation(final String expectedTargetProductCode,
                                          final int expectedAssociationType,
                                          final int expectedQuantity,
                                          final int expectedOrdering,
                                          final boolean isSourceProductDependent,
                                          final ProductAssociation productAssociation) {
        assertEquals("The target products should match.", expectedTargetProductCode, productAssociation.getTargetProduct().getCode());
        assertEquals("The association type should match.", expectedAssociationType, productAssociation.getAssociationType().getOrdinal());
        assertEquals("The ordering should match.", expectedOrdering, productAssociation.getOrdering());
        assertEquals("The quantity should match.", expectedQuantity, productAssociation.getDefaultQuantity());
        assertTrue("The start date should be set to before now.", new Date().after(productAssociation.getStartDate()));
        assertNull("End date should be null.", productAssociation.getEndDate());

        if (isSourceProductDependent) {
            assertTrue("The association should be source product dependent.", productAssociation.isSourceProductDependent());
        } else {
            assertFalse("The association should not be source product dependent.", productAssociation.isSourceProductDependent());
        }
    }
}
