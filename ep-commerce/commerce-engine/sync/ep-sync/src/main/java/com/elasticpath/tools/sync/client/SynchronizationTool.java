/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.tools.sync.client;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.cli.AlreadySelectedException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import com.elasticpath.tools.sync.client.impl.CLISyncJobConfiguration;
import com.elasticpath.tools.sync.configuration.ConnectionConfiguration;
import com.elasticpath.tools.sync.configuration.dao.ConnectionConfigurationDao;
import com.elasticpath.tools.sync.configuration.dao.impl.ConnectionConfigurationDaoImpl;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;
import com.elasticpath.tools.sync.target.result.Summary;
import com.elasticpath.tools.sync.target.result.SyncErrorResultItem;
import com.elasticpath.tools.sync.target.result.SyncResultItem;

/**
 * This is the entry point for the synchronization tool.
 * The tool reads the input from the command line, resolves source and target environment which may be any
 * combination of local or remote location.<br>
 */
@SuppressWarnings({ "PMD.SystemPrintln", "PMD.GodClass" })
public final class SynchronizationTool {

	/**
	 * The location of the sync tool context Spring XML file.
	 */
	private static final String SPRING_SYNC_TOOL_XML = "classpath:spring/sync-tool-context.xml";

	private static final String DEFAULT_SOURCECONF_XML = "sourceconfig.xml";

	private static final String DEFAULT_TARGETCONF_XML = "targetconfig.xml";

	private static final String OPTION_ROOT_PATH = "r";

	private static final String OPTION_SUB_DIR = "d";

	private static final String OPTION_ADAPTER_PARAM = "p";

	private static final String OPTION_SOURCE = "s";

	private static final String OPTION_TARGET = "t";

	private static final String OPTION_HELP = "h";

	private static final String OPTION_PROCESS_LOAD = "l";

	private static final String OPTION_PROCESS_EXPORT = "e";

	private static final String OPTION_PROCESS_FULL = "f";

	private static final List<String> REQUIRED_OPTIONS = Arrays.asList(OPTION_PROCESS_FULL, OPTION_PROCESS_EXPORT, OPTION_PROCESS_LOAD,
			OPTION_HELP);

	private static final String MISSING_OPTION_MESSAGE = "At least one process option " + REQUIRED_OPTIONS + " should be selected.\n"
			+ " Option [" + OPTION_ADAPTER_PARAM + "] is required for " + Arrays.asList(OPTION_PROCESS_FULL, OPTION_PROCESS_EXPORT) + "\n"
			+ " Option [" + OPTION_ROOT_PATH + "] is required for " + Arrays.asList(OPTION_PROCESS_LOAD, OPTION_PROCESS_EXPORT) + "\n"
			+ " Option [" + OPTION_SUB_DIR + "] is required for [" + OPTION_PROCESS_LOAD + "]";

	private static final String HELP_STRING = "SynchronizationTool <options>";

	private static final String HELP_FOOTER = "\n\nsynctool.bat -f [-a <AdapterName>] -p <AdapterParameters> -r \"/root/directory\"\n"
			+ "synctool.bat -f [-a <AdapterName>] -p <AdapterParameters>\n"
			+ "synctool.bat -e [-a <AdapterName>] -p <AdapterParameters> -r \"/root/directory\"\n"
			+ "synctool.bat -l -r \"/root/directory/\" -d \"dataFileName\"\n\n";

	private ConfigurableApplicationContext applicationContext;

	/**
	 * Entry point to the sync tool.
	 * @param args command line args:
	 * <table>
	 *  <tr>
	 *  	<td>Command:</td>
	 *  	<td>Parameter:</td>
	 *  	<td>Description:</td>
	 *  </tr>
	 * 	<tr>
	 * 		<td>(-h, --help)</td>
	 * 		<td>(none)</td>
	 * 		<td>prints Help</td>
	 * 	</tr>
	 *  <tr>
	 *  	<td>(-f, --full)</td>
	 *  	<td>(none)</td>
	 *  	<td>means "full" process including loading data from staging and merging it onto production</td>
	 *  </tr>
	 *  <tr>
	 *  	<td>(-e, --export)</td>
	 *  	<td>(none)</td>
	 *  	<td>means "export" data to synchronize into files</td>
	 *  </tr>
	 *  <tr>
	 *  	<td>(-l, --load)</td>
	 *  	<td>(none)</td>
	 *  	<td>means "load" data from files prepared before into target </td>
	 *  </tr>
	 *  <tr>
	 *  	<td>(-r, --root)</td>
	 *  	<td>&lt;dir name&gt;</td>
	 *  	<td>means "root" directory</td></tr>
	 *  <tr>
	 *  	<td>(-d, --subdir)</td>
	 *  	<td>&lt;dir name&gt;</td>
	 *  	<td>means "sub directory" to load all files from</td>
	 *  </tr>
	 *  <tr>
	 *  	<td>(-p, --param)</td>
	 *  	<td>&lt;adapter parameter string&gt;</td>
	 *  	<td>the adapter parameter string can define the path and adapter config file name or key value parameters
	 *  		for a specify adapter. It is Change Set name for ChangeSetAdapter</td>
	 *  </tr>
	 *  <tr>
	 *  	<td>(-s, --source)</td>
	 *  	<td>&lt;sourceconfig.xml&gt;</td>
	 *  	<td>the file contains the parameters to get the access to the source</td>
	 *  </tr>
	 *  <tr>
	 *  	<td>(-t, --target)</td>
	 *  	<td>&lt;targetconfig.xml&gt;</td>
	 *  	<td>the file contains the parameters to get the access to the source</td>
	 *  </tr>
	 * </table>
	 */
	public static void main(final String... args) {
		final SynchronizationTool syncTool = new SynchronizationTool();

		final Options options = createOptions();

		final CommandLineParser parser = new GnuParser();

		try {
			final CommandLine commandLine = parser.parse(options, args);
			if (commandLine.hasOption(OPTION_HELP)) {
				printHelp(options);
				return;
			}
			syncTool.processCommandLine(commandLine);

		} catch (AlreadySelectedException e) {
			printMessage("Only one option of " + REQUIRED_OPTIONS + " should be selected at once");
			printHelp(options);
		} catch (MissingSyncToolOptionException e) {
			printMessage(MISSING_OPTION_MESSAGE);
			printHelp(options);
		} catch (ParseException e) {
			printMessage("Error : " + e.getMessage());
			printHelp(options);
		} catch (Exception e) {
			printMessage("Error : " + e.getMessage());
		}
	}

	/**
	 * Verifies whether all the required tool configuration is in place.
	 */
	private void verifyToolConfiguration(final SyncToolConfiguration toolConfiguration) {
		final SyncToolControllerType controllerType = toolConfiguration.getControllerType();
		requireNonNull(controllerType, "Controller type must be specified on tool configuration");

		switch (controllerType.getOrdinal()) {
			case SyncToolControllerType.EXPORT_CONTROLLER_ORDINAL:
				requireNonNull(toolConfiguration.getSourceConfig(), "Source config name must be specified on tool configuration for exports");
				break;
			case SyncToolControllerType.LOAD_CONTROLLER_ORDINAL:
				requireNonNull(toolConfiguration.getTargetConfig(), "Target config name must be specified on tool configuration for exports");
				break;
			case SyncToolControllerType.FULL_CONTROLLER_ORDINAL:
			case SyncToolControllerType.FULL_AND_SAVE_CONTROLLER_ORDINAL:
				requireNonNull(toolConfiguration.getSourceConfig(),
						"Source config name must be specified on tool configuration for full publishing");
				requireNonNull(toolConfiguration.getTargetConfig(),
						"Target config name must be specified on tool configuration for full publishing");
				break;
			default:
		}
	}

	/**
	 * Verifies whether all the required job configuration is in place.
	 */
	private void verifyJobConfiguration(final SyncToolControllerType controllerType, final SyncJobConfiguration jobConfiguration) {
		if (Objects.equals(controllerType, SyncToolControllerType.EXPORT_CONTROLLER)) {
			assertValuesNotNull(jobConfiguration.getAdapterParameter(), jobConfiguration.getRootPath());
		} else if (Objects.equals(controllerType, SyncToolControllerType.FULL_CONTROLLER)) {
			assertValuesNotNull(jobConfiguration.getAdapterParameter());
		} else if (Objects.equals(controllerType, SyncToolControllerType.LOAD_CONTROLLER)) {
			assertValuesNotNull(jobConfiguration.getRootPath(), jobConfiguration.getSubDir());
		}
	}

	/**
	 *
	 */
	private void assertValuesNotNull(final String... values) {
		for (String value : values) {
			if (value == null) {
				throw new MissingSyncToolOptionException("Missing a required argument");
			}
		}
	}

	/**
	 * Create the command line options.
	 *
	 * @return the command line options
	 */
	public static Options createOptions() {
		Options options = new Options();

		OptionGroup group = new OptionGroup();

		group.addOption(
				new Option(OPTION_PROCESS_FULL, "full", false, "full process including loading data from staging and merging it onto production"));
		group.addOption(new Option(OPTION_PROCESS_EXPORT, "export", false, "export data to synchronize into files"));
		group.addOption(new Option(OPTION_PROCESS_LOAD, "load", false, "load data from files prepared before into target"));
		group.addOption(new Option(OPTION_HELP, "help", false, "prints help"));
		group.setRequired(true);

		options.addOptionGroup(group);

		options.addOption(OPTION_ROOT_PATH, "root", true, "root directory");
		options.addOption(OPTION_SUB_DIR, "subdir", true, "sub directory to load all files from");

		final Option adapterParamOption = new Option(OPTION_ADAPTER_PARAM, "param", true,
				"adapter parameter string can define the path and adapter config file name or key value parameters for a specify adapter.");
		adapterParamOption.setArgs(Option.UNLIMITED_VALUES);
		options.addOption(adapterParamOption);

		options.addOption(OPTION_SOURCE, "source", true,
				"specifies the file with connection configuration for source system. sourceconfig.xml is default");
		options.addOption(OPTION_TARGET, "target", true,
				"specifies the file with connection configuration for target system. targetconfig.xml is default");

		return options;
	}

	/**
	 * Creates a sync tool configuration, verifies the configuration and launches the sync tool.
	 *
	 * @param commandLine the command line
	 * @return the result summary of the sync process
	 * @throws SyncToolConfigurationException on error
	 */
	public Summary processCommandLine(final CommandLine commandLine) throws SyncToolConfigurationException {
		final CommandLineConfiguration toolConfiguration = new CommandLineConfiguration(commandLine);

		// Verify the tool configuration, we will validate each job configuration later below.
		verifyToolConfiguration(toolConfiguration);

		try {
			initializeApplicationContext(toolConfiguration);

			System.out.println(format("%n%tc Starting up...", getCurrentTime()));

			final SyncToolLauncher launcher = applicationContext.getBean("syncToolLauncher", SyncToolLauncher.class);

			final SyncJobConfiguration jobConfiguration = toolConfiguration.createJobConfiguration(toolConfiguration);

			System.out.println(
					format("%n%tc Attempting to process ChangeSet GUID: %s", getCurrentTime(), jobConfiguration.getAdapterParameter()));

			verifyJobConfiguration(toolConfiguration.getControllerType(), jobConfiguration);

			final Summary resultSummary = launcher.processJob(jobConfiguration);
			processSummary(resultSummary);
			return resultSummary;
		} finally {
			processShutDown();
		}
	}

	/**
	 * Creates an application context (bean factory) from the XML file that holds all the
	 * bean definitions for the sync tool that are only related to the sync tool itself.
	 *
	 * @param syncToolConfiguration the configuration for the application
	 * @return the application context
	 */
	private ConfigurableApplicationContext initializeApplicationContext(final SyncToolConfiguration syncToolConfiguration) {
		final GenericApplicationContext applicationContext = new GenericApplicationContext();
		applicationContext.getBeanFactory().registerSingleton("syncToolConfiguration", syncToolConfiguration);

		XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(applicationContext);
		xmlBeanDefinitionReader.loadBeanDefinitions(SPRING_SYNC_TOOL_XML);

		applicationContext.refresh();

		System.out.println("Local context has been read and initialized");

		this.applicationContext = applicationContext;

		return this.applicationContext;
	}

	private void processSummary(final Summary summary) {
		System.out.println(format("%n%tc Summary:", getCurrentTime()));
		for (SyncResultItem resultItem : summary.getSuccessResults()) {
			System.out.println(resultItem);
		}
		if (summary.hasErrors()) {
			System.err.println(summary.getNumberOfErrors() + " errors found");
			for (SyncErrorResultItem errorItem : summary.getSyncErrors()) {
				System.err.println(errorItem);
			}
		}
	}

	private void processShutDown() {
		System.out.println(format("%n%tc Shutting down...", getCurrentTime()));

		if (applicationContext != null) {
			final ConfigurableApplicationContext currentApplicationContext = this.applicationContext;
			this.applicationContext = null;

			currentApplicationContext.close();
		}

		System.out.println("Data Sync Tool completed shutdown.");
	}

	private Date getCurrentTime() {
		return new Date();
	}

	private static void printHelp(final Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(HELP_STRING, HELP_FOOTER, options, "");
	}

	/**
	 * Prints message to standard error output stream, for pure console output as a Tool.
	 *
	 * @param message the text message.
	 */
	private static void printMessage(final String message) {
		System.err.println(message);
	}

	/**
	 * A configuration based on the command line parameters.
	 */
	public static class CommandLineConfiguration implements SyncToolConfiguration {

		private final CommandLine commandLine;

		/**
		 * Constructor.
		 *
		 * @param commandLine the command line
		 */
		public CommandLineConfiguration(final CommandLine commandLine) {
			this.commandLine = commandLine;
		}

		private SyncJobConfiguration createJobConfiguration(final CommandLineConfiguration commandLineConfiguration) {
			String adapterParameter = null;

			if (commandLineConfiguration.commandLine.hasOption(OPTION_ADAPTER_PARAM)) {
				adapterParameter = commandLineConfiguration.getAdapterParameter();
			}

			return new CLISyncJobConfiguration(CommandLineConfiguration.this, adapterParameter);
		}

		String getAdapterParameter() {
			return getParameter(OPTION_ADAPTER_PARAM);
		}

		/**
		 * Determines the controller type out of the passed option.
		 *
		 * @return the controller type
		 */
		@Override
		public SyncToolControllerType getControllerType() {
			if (commandLine.hasOption(OPTION_PROCESS_LOAD)) {
				return SyncToolControllerType.LOAD_CONTROLLER;
			} else if (commandLine.hasOption(OPTION_PROCESS_FULL)) {
				if (getRootPath() == null) {
					return SyncToolControllerType.FULL_CONTROLLER;
				}
				return SyncToolControllerType.FULL_AND_SAVE_CONTROLLER;
			} else if (commandLine.hasOption(OPTION_PROCESS_EXPORT)) {
				return SyncToolControllerType.EXPORT_CONTROLLER;
			}

			throw new MissingSyncToolOptionException("No option is specified for the requested operation.");
		}

		public String getRootPath() {
			return getParameter(OPTION_ROOT_PATH, null);
		}

		@Override
		public ConnectionConfiguration getSourceConfig() {
			final String fileName = getParameter(OPTION_SOURCE, DEFAULT_SOURCECONF_XML);
			final ConnectionConfigurationDao configurationDao = new ConnectionConfigurationDaoImpl();
			try {
				return configurationDao.load(Paths.get(fileName).toUri().toURL());
			} catch (final MalformedURLException e) {
				throw new SyncToolConfigurationException("Unable to load source configuration", e);
			}
		}

		public String getSubDir() {
			return getParameter(OPTION_SUB_DIR);
		}

		@Override
		public ConnectionConfiguration getTargetConfig() {
			final String fileName = getParameter(OPTION_TARGET, DEFAULT_TARGETCONF_XML);
			final ConnectionConfigurationDao configurationDao = new ConnectionConfigurationDaoImpl();
			try {
				return configurationDao.load(Paths.get(fileName).toUri().toURL());
			} catch (final MalformedURLException e) {
				throw new SyncToolConfigurationException("Unable to load target configuration", e);
			}
		}

		private String getParameter(final String option) throws MissingSyncToolOptionException {
			return commandLine.getOptionValue(option);
		}

		private String getParameter(final String option, final String defaultValue) {
			return commandLine.getOptionValue(option, defaultValue); // if it is not found default value will be returned
		}

		private String[] getParameters(final String option) {
			return commandLine.getOptionValues(option);
		}

		@Override
		public String toString() {
			return new ToStringBuilder(this, SHORT_PREFIX_STYLE)
					.append("DST Tool Controller Type", getControllerType())
					.append("Source Configuration",
							getControllerType().equals(SyncToolControllerType.LOAD_CONTROLLER) ? "" : getSourceConfig().getUrl())
					.append("Target Configuration",
							getControllerType().equals(SyncToolControllerType.EXPORT_CONTROLLER) ? "" : getTargetConfig().getUrl())
					.build();
		}
	}

}
