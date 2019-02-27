/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.cucumber.steps;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.math.BigDecimal;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.util.TestDomainMarshaller;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.importexport.builder.ImportConfigurationBuilder;
import com.elasticpath.importexport.builder.ImportExportTestDirectoryBuilder;
import com.elasticpath.importexport.common.dto.inventory.InventorySkuDTO;
import com.elasticpath.importexport.common.dto.inventory.InventoryWarehouseDTO;
import com.elasticpath.importexport.common.manifest.Manifest;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.importer.configuration.ImportConfiguration;
import com.elasticpath.importexport.importer.controller.ImportController;
import com.elasticpath.importexport.importer.types.ImportStrategyType;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.inventory.InventoryFacade;
import com.elasticpath.service.store.WarehouseService;
import com.elasticpath.test.persister.TaxTestPersister;
import com.elasticpath.test.persister.TestApplicationContext;
import com.elasticpath.test.persister.TestDataPersisterFactory;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;

/**
 * Inventory Test Steps.
 */
public class InventorySteps {

	private static final String INVENTORY_IMPORT_FILE = "inventory.xml";
	private static final String MANIFEST_IMPORT_FILE = "manifest.xml";
	private static final int WAREHOUSE_PICK_DELAY = 10;
	private static final int QTY_10 = 10;

	private static int runNumber = 1;

	@Autowired
	private WarehouseService warehouseService;

	@Autowired
	private ImportController importController;

	@Autowired
	private InventoryFacade inventoryFacade;

	@Autowired
	private TestApplicationContext tac;

	private SimpleStoreScenario scenario;

	/* order=0 is set to ensure correct ordering of hooks on different OSs.
	  Cucumber's HookComparator class orders hooks (classes annotated with @Before or @After)
	  based on their order attribute in ascending order. Without setting order = 0,
	  alphabetic ordering would be used (default order is 10000), i.e. ImportCustomerWithPaymentMethodsSteps
	  will be first on Linux, while InventorySteps will be first on Windows,
	  but we want InventorySteps to be always the first one.
	 */
	/**
	 * Initialize the simple store scenario before the test.
	 */
	@Before(order = 0)
	public void initializeScenario() {
		scenario = tac.useScenario(SimpleStoreScenario.class);
	}

	/**
	 * Creates a SKU with the given SKU Code, a Warehouse with the given Warhouse code, and
	 * creates an empty inventory record for that sku in that warehouse.
	 *
	 * @param skuCode the code of the SKU to create
	 * @param warehouseCode the code of the warehouse to create
	 */
	@Given("^A SKU with code (\\w+) that exists in a warehouse with code (\\w+)$")
	public void createSkuInWarehouse(final String skuCode, final String warehouseCode) {
		Warehouse warehouse = createWarehouse(warehouseCode);
		tac.getPersistersFactory().getCatalogTestPersister().persistProductWithSku(
				scenario.getCatalog(),
				scenario.getCategory(),
				warehouse,
				BigDecimal.ONE,
				TestDataPersisterFactory.DEFAULT_CURRENCY,
				null,
				skuCode + "_product",
				"Product " + skuCode,
				skuCode,
				TaxTestPersister.TAX_CODE_GOODS,
				AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK,
				0);

		tac.getPersistersFactory().getCatalogTestPersister().persistInventory(
				skuCode,
				warehouse,
				0, 0, 0, "reason");

	}

	/**
	 * Creates Warehouse with the given code.
	 *
	 * @param warehouseCode the code of the warehouse to create
	 * @return the warehouse
	 */
	protected Warehouse createWarehouse(final String warehouseCode) {
		Warehouse warehouse = warehouseService.findByCode(warehouseCode);
		if (warehouse == null) {
			warehouse = tac.getPersistersFactory().getStoreTestPersister().persistWarehouse(
					warehouseCode, "Warehouse " + warehouseCode, WAREHOUSE_PICK_DELAY,
					"Vancouver", "CA", "Boulvard1", "BC", "123");
		}
		return warehouse;
	}

	/**
	 * Perform an import into the given warehouse and SKU with the given on-hand and allocated quantities.
	 *
	 * @param warehouseCode the code of the warehouse holding the inventory
	 * @param skuCode the code of the SKU whose inventory we are importing
	 * @param onHand the quantity on hand
	 * @param allocated the allocasted quantity
	 *
	 * @throws Exception in case of error
	 */
	@When("^I import inventory into (\\w+) for (\\w+) with on-hand quantity (\\d+) and allocated quantity (\\d+)$")
	public void importInventory(final String warehouseCode, final String skuCode, final int onHand, final int allocated)
			throws Exception {

		InventoryWarehouseDTO warehouseDTO = new InventoryWarehouseDTO();
		warehouseDTO.setCode(warehouseCode);
		warehouseDTO.setAvaliable(0);
		warehouseDTO.setReorderMin(QTY_10);
		warehouseDTO.setReorderQty(QTY_10);
		warehouseDTO.setReserved(0);
		warehouseDTO.setOnHand(onHand);
		warehouseDTO.setAllocated(allocated);

		InventorySkuDTO inventorySkuDto = new InventorySkuDTO();
		inventorySkuDto.setCode(skuCode);
		inventorySkuDto.setWarehouses(ImmutableList.of(warehouseDTO));

		final File importDirectory = ImportExportTestDirectoryBuilder.newInstance()
				.withTestName(this.getClass().getSimpleName())
				.withRunNumber(runNumber++)
				.build();

		if (!importDirectory.exists()) {
			importDirectory.mkdirs();
		}

		final Manifest manifest = new Manifest();
		manifest.addResource(INVENTORY_IMPORT_FILE);

		TestDomainMarshaller.marshalObject(Manifest.class, manifest, new File(importDirectory, MANIFEST_IMPORT_FILE));
		new TestDomainMarshaller().marshall(inventorySkuDto, JobType.INVENTORY, new File(importDirectory, INVENTORY_IMPORT_FILE).getAbsolutePath());

		final ImportConfiguration importConfiguration =
				ImportConfigurationBuilder.newInstance()
						.setRetrievalSource(importDirectory.getPath())
						.addImporterConfiguration(JobType.INVENTORY, ImportStrategyType.INSERT)
						.build();

		importController.loadConfiguration(importConfiguration);
		importController.executeImport();

	}

	/**
	 * Verify the system ended up with the expected inventory records.
	 *
	 * @param skuCode the code of the SKU shose inventory we wish to validate
	 * @param warehouseCode the code of the warehouse containing the inventory
	 * @param expectedOnHand the expected on-hand quantity
	 * @param expectedAllocated the expected allocated quantity
	 */
	@Then("^the inventory for (\\w+) in warehouse (\\w+) should show an on-hand quantity of (\\d+) and an allocated quantity of (\\d+)$")
	public void verifyInventory(final String skuCode, final String warehouseCode, final int expectedOnHand, final int expectedAllocated) {
		Warehouse warehouse = warehouseService.findByCode(warehouseCode);

		Map<String, InventoryDto> inventories = inventoryFacade.getInventoriesForSkusInWarehouse(ImmutableSet.of(skuCode), warehouse.getUidPk());
		InventoryDto inventoryDto = inventories.get(skuCode);

		assertEquals("The on-hand quantity should match", expectedOnHand, inventoryDto.getQuantityOnHand());
		assertEquals("The allocated quantity should match", expectedAllocated, inventoryDto.getAllocatedQuantity());
	}

}
