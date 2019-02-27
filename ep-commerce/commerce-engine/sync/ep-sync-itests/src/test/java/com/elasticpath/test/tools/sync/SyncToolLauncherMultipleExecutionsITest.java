/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.test.tools.sync;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Paths;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.domain.changeset.ChangeSetStateCode;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.service.changeset.ChangeSetManagementService;
import com.elasticpath.service.changeset.ChangeSetService;
import com.elasticpath.service.cmuser.CmUserService;
import com.elasticpath.test.integration.BasicSpringContextTest;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.test.persister.CatalogTestPersister;
import com.elasticpath.tools.sync.client.SyncToolControllerType;
import com.elasticpath.tools.sync.client.SyncToolLauncher;
import com.elasticpath.tools.sync.client.impl.SyncJobConfigurationImpl;
import com.elasticpath.tools.sync.client.impl.SyncToolConfigurationImpl;
import com.elasticpath.tools.sync.target.result.Summary;

public class SyncToolLauncherMultipleExecutionsITest extends BasicSpringContextTest {

	private static final String CM_USER_NAME = "admin";
	private static final String CATALOG_CODE = "SYNCTESTCATALOG";
	private static final String BRAND_CODE_1 = "SYNCTESTBRAND1";
	private static final String BRAND_CODE_2 = "SYNCTESTBRAND2";

	@Autowired
	private SyncToolConfigurationImpl syncToolConfiguration;

	@Autowired
	private ChangeSetManagementService changeSetManagementService;

	@Autowired
	private ChangeSetService changeSetService;

	@Autowired
	private CmUserService cmUserService;

	@Autowired
	private SyncToolLauncher syncToolLauncher;

	@Value("${sync.export.target.directory.root}")
	private String exportParentDirectory;

	private CmUser createdByCmUser;

	private Brand brandToSync1;
	private Brand brandToSync2;

	@Before
	public void setUp() {
		assertThat(syncToolConfiguration.getControllerType())
				.as("This test depends on a configuration using a controller type of EXPORT.  If this is not the case, please either correct the "
							+ "test data set, or refactor this test accordingly.")
				.isEqualTo(SyncToolControllerType.EXPORT_CONTROLLER);

		createdByCmUser = cmUserService.findByUserName(CM_USER_NAME);

		assertThat(createdByCmUser)
				.as("CM User required for test conditioning not found in the database.  "
							+ "Please ensure that the appropriate test data set has been loaded.")
				.isNotNull();

		final CatalogTestPersister catalogTestPersister = getTac().getPersistersFactory().getCatalogTestPersister();

		final Catalog catalog = catalogTestPersister.persistCatalog(CATALOG_CODE, true);
		brandToSync1 = catalogTestPersister.persistBrand(BRAND_CODE_1, catalog);
		brandToSync2 = catalogTestPersister.persistBrand(BRAND_CODE_2, catalog);
	}

	@DirtiesDatabase
	@Test
	public void testSameSyncToolLauncherCanExecuteMultipleDifferentSyncJobs() {
		final ChangeSet changeSet1 = createReadyToPublishChangeSetWithMembers(brandToSync1);
		final ChangeSet changeSet2 = createReadyToPublishChangeSetWithMembers(brandToSync2);

		final String exportPath1 = Paths.get(exportParentDirectory, "export1").toString();
		final String exportPath2 = Paths.get(exportParentDirectory, "export2").toString();

		assertThatExportCreated(executeSyncTool(changeSet1.getObjectGroupId(), exportPath1), exportPath1);
		assertThatExportCreated(executeSyncTool(changeSet2.getObjectGroupId(), exportPath2), exportPath2);
	}

	private ChangeSet createReadyToPublishChangeSetWithMembers(final Object... members) {
		final String name = "Test change set for " + SyncToolLauncherMultipleExecutionsITest.class + "." + System.currentTimeMillis();

		ChangeSet changeSet = getBeanFactory().getBean(ContextIdNames.CHANGE_SET);
		changeSet.setName(name);
		changeSet.setDescription(name);
		changeSet.setCreatedDate(new Date());
		changeSet.setCreatedByUserGuid(createdByCmUser.getGuid());

		changeSet = changeSetManagementService.add(changeSet);

		for (final Object member : members) {
			changeSetService.addObjectToChangeSet(changeSet.getGuid(), member, null);
		}

		changeSet.setStateCode(ChangeSetStateCode.LOCKED);

		return changeSetManagementService.update(changeSet, null);
	}

	private Summary executeSyncTool(final String changeSetGuid, final String exportPath) {
		final SyncJobConfigurationImpl jobConfiguration1 = new SyncJobConfigurationImpl();
		jobConfiguration1.setAdapterParameter(changeSetGuid);
		jobConfiguration1.setRootPath(exportPath);

		return syncToolLauncher.processJob(jobConfiguration1);
	}

	private void assertThatExportCreated(final Summary summary, final String exportPath) {
		assertThat(summary.getSyncErrors())
				.as("Change Set publish failed")
				.isEmpty();

		assertThat(Paths.get(exportPath))
				.isDirectory();
	}

}
