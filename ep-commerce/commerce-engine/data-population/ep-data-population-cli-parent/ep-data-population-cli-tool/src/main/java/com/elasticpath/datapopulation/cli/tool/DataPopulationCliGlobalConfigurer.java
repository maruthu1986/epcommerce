/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.cli.tool;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.google.common.base.Predicate;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.reflections.ReflectionUtils;
import org.springframework.beans.factory.ListableBeanFactory;

import com.elasticpath.datapopulation.cli.tool.annotations.AfterAllGlobalCliOptions;
import com.elasticpath.datapopulation.cli.tool.annotations.BeforeAllGlobalCliOptions;
import com.elasticpath.datapopulation.cli.tool.annotations.DpCliComponent;
import com.elasticpath.datapopulation.cli.tool.annotations.DpCliOption;
import com.elasticpath.datapopulation.cli.tool.validators.BeforeOrAfterGlobalConfigurationMethodValidator;
import com.elasticpath.datapopulation.cli.tool.validators.GlobalConfigurationMethodValidator;
import com.elasticpath.datapopulation.core.utils.DpUtils;
import com.elasticpath.datapopulation.core.utils.OrderAnnotatedMethodComparator;

/**
 * A class which is responsible for finding the classes configured in the provided bean factory which are annotated
 * with the {@link DpCliComponent}, and invoking any annotated as global configuration methods, along with any methods annotated
 * to be run either before or after those global configuraiton methods have been run.
 * <p>
 * This class was created because the latest version of Spring-Shell at the time (1.0) did not support arguments which applied to
 * the application itself, instead it only supported arguments which applied to particular commands registered with the system.
 */
@SuppressWarnings("PMD.GodClass")
public class DataPopulationCliGlobalConfigurer {
	private static final Logger LOG = Logger.getLogger(DataPopulationCliGlobalConfigurer.class);

	private final ListableBeanFactory beanFactory;

	/**
	 * Constructor which takes in the bean factory to use to search for global configuration components.
	 *
	 * @param beanFactory the bean factory to use.
	 */
	public DataPopulationCliGlobalConfigurer(final ListableBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	// Entry method

	/**
	 * Processes the command line arguments searching for any command line arguments that match any methods annotated with {@link DpCliOption} in
	 * classes that are annotated with {@link DpCliComponent}. The search is made left to right, and is stopped when the first command line argument
	 * specified does not match one of the available {@link DpCliOption}-annotated methods. That unmatched command line argument and remainder of the
	 * command line arguments are returned for further processing by {@link DataPopulationCliApplication} since they are assumed to be commands and
	 * their options which are handled by Spring-Shell.
	 * <p>
	 * Note: The methods processed are ordered depending on if the methods found are annotated with the
	 * {@link org.springframework.core.annotation.Order} annotation. This allows one method to be reliably invoked before or after another one.
	 * This is particularly useful for setting precedence of different command line arguments, so that if more than one argument is
	 * missing/incorrect,
	 * the user is informed about the most important one, rather than a lesser important one.
	 * </p>
	 *
	 * @param commandLineArgs the command line arguments to process.
	 * @return the remaining command line arguments unprocessed as global configuration arguments, and are still to be processed by Spring-Shell.
	 */
	public String[] processGlobalConfiguration(final String[] commandLineArgs) {
		// Search for all cli component classes
		final Map<String, Object> cliComponents = findCliComponents(getBeanFactory());

		// Out of those classes search for the global configuration methods, as well as any methods annotated to be run either before or after them.
		final Multimap<Method, String> globalConfigurationMethods = findAllGlobalConfigurationMethods(cliComponents);
		final Multimap<Method, String> beforeGlobalConfigurationMethodMap = findAllBeforeGlobalConfigurationMethods(cliComponents);
		final Multimap<Method, String> afterGlobalConfigurationMethodMap = findAllAfterGlobalConfigurationMethods(cliComponents);

		// Given the global configuration methods found, generate a map of available command-line Options which will be used to parse the command
		// line
		// A map is returned so that for each option found on the command line, the associated methods (mapped to their respective bean ids) can
		// passed below to the process method for validation and invocation.
		final Map<Option, Multimap<Method, String>> globalConfigurationOptionMap = createGlobalConfigurationOptionMap(globalConfigurationMethods);

		// Process the command line args using the maps created above, return the unprocessed command line arguments for separate processing by
		// Spring-Shell.
		return processGlobalConfiguration(commandLineArgs, globalConfigurationOptionMap, beforeGlobalConfigurationMethodMap,
				afterGlobalConfigurationMethodMap);
	}

	// Implementation methods

	/**
	 * Processes the command line arguments using the given meta-data maps. The remaining command line arguments from the first non-matched
	 * command line argument is returned for separate processing by Spring-Shell.
	 * <p>
	 * The command line arguments are processed by calling {@link #processGlobalConfiguration(org.apache.commons.cli.CommandLine, java.util.Map)},
	 * so see the documentation on that class for more information.
	 * </p>
	 *
	 * @param commandLineArgs                    the command line arguments to process
	 * @param globalConfigurationOptionMap       a map of valid command line {@link Option}s to their respective methods and bean ids.
	 * @param beforeGlobalConfigurationMethodMap a map of methods and bean ids that should be invoked before any global configuration methods have
	 *                                           been invoked.
	 * @param afterGlobalConfigurationMethodMap  a map of methods and bean ids that should be invoked after any global configuration methods have
	 *                                           been invoked.
	 * @return the remaining unprocessed command line arguments.
	 */
	protected String[] processGlobalConfiguration(final String[] commandLineArgs,
												  final Map<Option, Multimap<Method, String>> globalConfigurationOptionMap,
												  final Multimap<Method, String> beforeGlobalConfigurationMethodMap,
												  final Multimap<Method, String> afterGlobalConfigurationMethodMap) {
		final Options options = createOptions(globalConfigurationOptionMap);
		final CommandLine commandLine = parseCommandLine(options, commandLineArgs);

		processMethods(beforeGlobalConfigurationMethodMap);
		processGlobalConfiguration(commandLine, globalConfigurationOptionMap);
		processMethods(afterGlobalConfigurationMethodMap);

		// Return the unprocessed arguments since they are the command arguments, if any.
		return commandLine.getArgs();
	}

	/**
	 * Processes the methods by invoking {@link #invokeNoArgMethod(String, java.lang.reflect.Method)} on each entry in the {@link Multimap}.
	 *
	 * @param methodMap the map to process.
	 */
	protected void processMethods(final Multimap<Method, String> methodMap) {
		for (Map.Entry<Method, String> methodEntry : methodMap.entries()) {
			final Method method = methodEntry.getKey();
			final String beanId = methodEntry.getValue();

			invokeNoArgMethod(beanId, method);
		}
	}

	/**
	 * Processes the global configuration command line arguments by iterating through the map passed in for each {@link Option} key and checking
	 * the given {@link CommandLine} whether it has been specified. If so {@link #invokeGlobalConfigurationMethods(org.apache.commons.cli
	 * .CommandLine,
	 * org.apache.commons.cli.Option, com.google.common.collect.Multimap)} is called to invoke the associated methods for that command line argument.
	 *
	 * @param commandLine                  the command line to process.
	 * @param globalConfigurationOptionMap the meta-data containing the available {@link Option}s mapped to the {@link Method}s to invoke for each
	 *                                     option, and the associated bean ids to invoke that {@link Method} on.
	 */
	protected void processGlobalConfiguration(final CommandLine commandLine,
											  final Map<Option, Multimap<Method, String>> globalConfigurationOptionMap) {
		// Iterate through every Option, and if the option was specified on the command line
		// invoke each of the associated bean methods with the specified or default value

		for (Map.Entry<Option, Multimap<Method, String>> gcoEntry : globalConfigurationOptionMap.entrySet()) {
			final Option option = gcoEntry.getKey();
			final Multimap<Method, String> optionMap = gcoEntry.getValue();

			final boolean optionSupplied = commandLine.hasOption(option.getOpt());
			if (optionSupplied) {
				invokeGlobalConfigurationMethods(commandLine, option, optionMap);
			}
		}
	}

	/**
	 * Retrieves the specified command line argument values from the given {@link CommandLine} and {@link Option} arguments, and calls
	 * {@link #invokeGlobalConfigurationMethod(org.apache.commons.cli.Option, String, java.lang.reflect.Method, String[])} for each mapping contained
	 * in the optionMap passed in.
	 *
	 * @param commandLine the command line argument to inspect.
	 * @param option      the option to process
	 * @param optionMap   the given methods and bean ids to invoke once the associated command line arguments have been retrieved.
	 */
	protected void invokeGlobalConfigurationMethods(final CommandLine commandLine, final Option option, final Multimap<Method, String> optionMap) {
		// Iterate through each method in the map and invoke the associated bean id.
		// It's a Multimap so there can be multiple bean ids (Multimap values) associated with an individual Method since there could be multiple
		// beans of the class instantiated, or more likely, multiple sub-class instances registered which all share a common annotated method
		// see FileConfiguration and its sub-classes as an example.

		for (Map.Entry<Method, String> methodEntry : optionMap.entries()) {
			final Method gcMethod = methodEntry.getKey();
			final String beanId = methodEntry.getValue();

			final String[] optionValues = commandLine.getOptionValues(option.getOpt());

			invokeGlobalConfigurationMethod(option, beanId, gcMethod, optionValues);
		}
	}

	/**
	 * Invokes the given method with the given arguments, on the given bean by calling
	 * {@link #invokeGlobalConfigurationMethod(org.apache.commons.cli.Option, String, java.lang.reflect.Method, Object)}.
	 * If no values are given, and the method takes arguments, then the Method's {@link DpCliOption} is inspected to see if its
	 * {@link DpCliOption#specifiedDefaultValue()} is set, and if so that value is used instead.
	 *
	 * @param option         the command line option being processed.
	 * @param beanId         the id of the bean to invoke.
	 * @param method         the method to invoke on the bean.
	 * @param explicitValues the explicit values passed on the command line.
	 */
	protected void invokeGlobalConfigurationMethod(final Option option, final String beanId, final Method method, final String[] explicitValues) {
		final Class<?>[] argumentTypes = method.getParameterTypes();

		Object methodParameter = null;
		// We only support either zero or single argument methods of either a String or String[] (the latter implicitly supports String varargs)
		// (See isValidGlobalConfigurationMethod())
		if (argumentTypes.length == 1) {
			final Class<?> singleArgumentType = argumentTypes[0];

			String[] values = explicitValues;
			if (ArrayUtils.isEmpty(values)) {
				final DpCliOption annotation = method.getAnnotation(DpCliOption.class);
				values = annotation.specifiedDefaultValue();
			}

			if (singleArgumentType == String.class) {
				if (ArrayUtils.isNotEmpty(values)) {
					methodParameter = values[0];
				}
			} else {
				methodParameter = values;
			}
		}

		invokeGlobalConfigurationMethod(option, beanId, method, methodParameter);
	}

	/**
	 * Invokes the global configuration {@link Method} for the associated {@link Option} given, on the given bean id, passing in the given method
	 * parameter. Calls {@link #invokeMethod(String, java.lang.reflect.Method, Object...)} but throws a {@link DataPopulationCliException} for
	 * the corresponding {@link Option} if there is a problem with the arguments passed.
	 *
	 * @param option          the associated {@link Option} for this global configuration method invocation.
	 * @param beanId          the bean id to invoke the method on.
	 * @param method          the method to invoke.
	 * @param methodParameter the method argument to pass.
	 */
	protected void invokeGlobalConfigurationMethod(final Option option, final String beanId, final Method method, final Object methodParameter) {
		try {
			invokeMethod(beanId, method, methodParameter);
		} catch (final IllegalArgumentException e) {
			throw new DataPopulationCliException("Error: Invalid global configuration argument(s) '" + methodParameter
					+ "' for option '--" + option.getLongOpt() + "' specified. " + DpUtils.getNestedExceptionMessage(e), e);
		}
	}

	/**
	 * Invokes the {@link Method} on the bean id given, passing in zero arguments. Calls
	 * {@link #invokeMethod(String, java.lang.reflect.Method, Object...)} but throws a {@link DataPopulationCliException} for the corresponding
	 * {@link Option} if the method is not a zero-arg method.
	 *
	 * @param beanId the id of the bean to invoke on.
	 * @param method the zero-arg method to invoke.
	 */
	protected void invokeNoArgMethod(final String beanId, final Method method) {
		try {
			invokeMethod(beanId, method);
		} catch (final IllegalArgumentException e) {
			throw new DataPopulationCliException("Error: Invalid method specified, expected a zero-arg method. ", e);
		}
	}

	// Helper methods

	/**
	 * Invokes the given {@link Method} on the given bean id, passing in the method arguments. Throws an {@link IllegalArgumentException} if there
	 * was a problem with the arguments passed, or a {@link DataPopulationCliException} if the method itself throws an exception or an
	 * {@link IllegalAccessException} occurred. As this method is a helper method, it is assumed the caller will catch the
	 * {@link IllegalArgumentException} and provide a context-sensitive error message to provide more information to the end user.
	 *
	 * @param beanId          the id of the bean to invoke.
	 * @param method          the method to invoke.
	 * @param methodArguments the arguments to pass to the method.
	 * @throws IllegalArgumentException if the arguments passed does not match the method's expected arguments.
	 */
	protected void invokeMethod(final String beanId, final Method method, final Object... methodArguments) {
		final Object bean = getBeanFactory().getBean(beanId);

		try {
			method.invoke(bean, methodArguments);
		} catch (final InvocationTargetException e) {
			final Throwable targetException = e.getTargetException();
			// If the target exception is a DataPopulationCliException the message is designed for end user consumption so just throw it as is
			if (targetException instanceof DataPopulationCliException) {
				throw (DataPopulationCliException) targetException;
			} else {
				// Otherwise wrap the exception with more context on where it occurred so the user has more information to the cause of the problem
				throw new DataPopulationCliException("An error occurred calling method '" + method + "' on bean id '" + beanId
						+ "'. " + DpUtils.getNestedExceptionMessage(e), e);
			}
		} catch (final IllegalAccessException e) {
			throw new DataPopulationCliException("An error occurred calling method '" + method + "' on bean id '" + beanId
					+ "'. " + DpUtils.getNestedExceptionMessage(e), e);
		}
	}

	/**
	 * With the given {@link Options} the given command-line arguments are parsed and a {@link CommandLine} object is returned for use.
	 *
	 * @param options the valid options that can be passed on the command line.
	 * @param args    the command-line arguments passed.
	 * @return a {@link CommandLine} object parsed from the given arguments and valid options.
	 * @throws DataPopulationCliException if there was a problem parsing the command line arguments.
	 */
	protected CommandLine parseCommandLine(final Options options, final String[] args) throws DataPopulationCliException {
		final CommandLine result;

		try {
			result = new PosixParser().parse(options, args, true);
		} catch (final ParseException e) {
			throw new DataPopulationCliException("Error: Unable to parse the global configuration options. "
					+ DpUtils.getNestedExceptionMessage(e), e);
		}

		return result;
	}

	// Find methods

	/**
	 * Returns a map of all classes in the given bean factory that are annotated with the {@link DpCliComponent} annotation.
	 *
	 * @param beanFactory the bean factory to search.
	 * @return a map of all classes in the given bean factory that are annotated with the {@link DpCliComponent} annotation.
	 */
	protected Map<String, Object> findCliComponents(final ListableBeanFactory beanFactory) {
		return beanFactory.getBeansWithAnnotation(DpCliComponent.class);
	}

	/**
	 * Returns a {@link Multimap} of {@link Method}s to bean ids to invoke for all methods found in the beans given that are annotated with the
	 * {@link DpCliOption} annotation. The Multimap allows a single {@link Method} can be mapped to multiple bean ids to invoke that method on.
	 * This is required because multiple instances of the same class may be passed in, also classes can be sub-classed and separately instantiated,
	 * and so they all share the same annotated {@link Method} via inheritance.
	 *
	 * @param cliComponents the beans to search for {@link DpCliOption} annotated methods
	 * @return a {@link Multimap} of {@link DpCliOption}-annotated {@link Method}s to the associated bean ids to invoke them on.
	 */
	protected Multimap<Method, String> findAllGlobalConfigurationMethods(final Map<String, Object> cliComponents) {
		return findAllAnnotatedMethods(cliComponents, DpCliOption.class, createGlobalConfigurationMethodValidator());
	}

	/**
	 * Returns a {@link Multimap} of {@link Method}s to bean ids to invoke for all methods found in the beans given that are annotated with the
	 * {@link BeforeAllGlobalCliOptions} annotation. The Multimap allows a single {@link Method} can be mapped to multiple bean ids to invoke that
	 * method on. This is required because multiple instances of the same class may be passed in, also classes can be sub-classed and separately
	 * instantiated, and so they all share the same annotated {@link Method} via inheritance.
	 *
	 * @param cliComponents the beans to search for {@link BeforeAllGlobalCliOptions} annotated methods
	 * @return a {@link Multimap} of {@link BeforeAllGlobalCliOptions}-annotated {@link Method}s to the associated bean ids to invoke them on.
	 */
	protected Multimap<Method, String> findAllBeforeGlobalConfigurationMethods(final Map<String, Object> cliComponents) {
		return findAllAnnotatedMethods(cliComponents, BeforeAllGlobalCliOptions.class, createBeforeAllGlobalConfigurationMethodsValidator());
	}

	/**
	 * Returns a {@link Multimap} of {@link Method}s to bean ids to invoke for all methods found in the beans given that are annotated with the
	 * {@link AfterAllGlobalCliOptions} annotation. The Multimap allows a single {@link Method} can be mapped to multiple bean ids to invoke that
	 * method on. This is required because multiple instances of the same class may be passed in, also classes can be sub-classed and separately
	 * instantiated, and so they all share the same annotated {@link Method} via inheritance.
	 *
	 * @param cliComponents the beans to search for {@link AfterAllGlobalCliOptions} annotated methods
	 * @return a {@link Multimap} of {@link AfterAllGlobalCliOptions}-annotated {@link Method}s to the associated bean ids to invoke them on.
	 */
	protected Multimap<Method, String> findAllAfterGlobalConfigurationMethods(final Map<String, Object> cliComponents) {
		return findAllAnnotatedMethods(cliComponents, AfterAllGlobalCliOptions.class, createAfterAllGlobalConfigurationMethodsValidator());
	}

	/**
	 * Finds all annotated methods in the given object map for the given annotation class that validates with the given {@link Method}
	 * {@link Predicate}. Returns the matched {@Method}s as a sorted {@link Multimap} (by calling {@link #createSortedSetMethodMultimap()})
	 * mapping a single {@link Method} to 1 or more bean ids to invoke that method on. The fact the map orders the {@link Method} allows the order
	 * of invocation by callers of this method to be deterministic and intentional. See {@link #createSortedSetMethodMultimap()} for how
	 * {@link Method}s are ordered.
	 *
	 * @param cliComponents   the beans to search for annotated methods.
	 * @param annotationClass the annotation class to search for.
	 * @param methodValidator the method validator (may be null to allow all annotated methods) to validate/filter the methods with.
	 * @return an ordered {@link Multimap} of validated, annotated {@link Method}s mapped to the bean id(s) to invoke on.
	 */
	protected Multimap<Method, String> findAllAnnotatedMethods(final Map<String, Object> cliComponents,
															   final Class<? extends Annotation> annotationClass,
															   final Predicate<Method> methodValidator) {
		// Create a SortedSetMultimap implementation to ensure that the map is iterated in order
		// as Methods can be annotated with @Order to dictate the order in which they are executed
		final SortedSetMultimap<Method, String> result = createSortedSetMethodMultimap();

		for (Map.Entry<String, Object> cliComponentEntry : cliComponents.entrySet()) {
			final String beanId = cliComponentEntry.getKey();
			final Object bean = cliComponentEntry.getValue();

			final Collection<Method> annotatedMethods = getMethodsAnnotatedWith(bean.getClass(), annotationClass);
			for (Method method : annotatedMethods) {
				if (methodValidator == null || methodValidator.apply(method)) {
					result.put(method, beanId);
				} else {
					LOG.warn("Invalid @" + annotationClass.getSimpleName() + " method found in class " + bean.getClass().getName() + "; method: "
							+ method + " therefore skipping.");
				}
			}
		}

		return result;
	}

	/**
	 * Returns a collection of {@link Method}s defined by the given class (or superclass) that are annotated with the given annotation.
	 *
	 * @param type       the type of class to inspect (also inspects all super classes)
	 * @param annotation the annotation to search for on methods.
	 * @return a collection of {@link Method}s defined by the given class (or superclass) that are annotated with the given annotation.
	 */
	// The warning is due to code in ReflectionUtils, there's nothing we can do from our side so we have to suppress the warning
	@SuppressWarnings("unchecked")
	protected Collection<Method> getMethodsAnnotatedWith(final Class<?> type, final Class<? extends Annotation> annotation) {
		return ReflectionUtils.getAllMethods(type, ReflectionUtils.withAnnotation(annotation));
	}

	/**
	 * For the given global configuration {@link Method}s a Map is constructed mapping a generated command-line {@link Option} to the associated
	 * {@link Method}s and bean ids. The generated map allows the {@link Option} keys to be searched for on the command line, and for the ones found,
	 * the associated {@link Method}s can be invoked on its associated bean ids.
	 *
	 * @param globalConfigurationMethods the method map, mapping from a {@link Method} to the bean id(s) to invoke that method on.
	 * @return a map mapping generated {@link Option}s to the associated {@link Method}s and bean ids.
	 */
	protected Map<Option, Multimap<Method, String>> createGlobalConfigurationOptionMap(final Multimap<Method, String> globalConfigurationMethods) {
		final Map<Option, Multimap<Method, String>> result = new HashMap<>();

		if (!globalConfigurationMethods.isEmpty()) {
			final Map<String, Option> optionMap = new TreeMap<>();

			for (Map.Entry<Method, String> entry : globalConfigurationMethods.entries()) {
				final Method gcMethod = entry.getKey();
				final String beanId = entry.getValue();

				final DpCliOption cliOptionAnnotation = gcMethod.getAnnotation(DpCliOption.class);
				final String cliOptionKey = cliOptionAnnotation.key();
				final boolean isPrimaryDefinition = cliOptionAnnotation.isPrimaryDefinition();

				Option cliOption = optionMap.get(cliOptionKey);

				if (cliOption == null || isPrimaryDefinition) {
					final boolean hasArgument = cliOptionAnnotation.hasArgument();
					final String help = cliOptionAnnotation.help();
					final boolean mandatory = cliOptionAnnotation.mandatory();

					if (cliOption == null) {
						cliOption = new Option(cliOptionKey, cliOptionKey, hasArgument, help);
					}

					cliOption.setRequired(hasArgument);
					cliOption.setDescription(help);
					cliOption.setRequired(mandatory);

					optionMap.put(cliOptionKey, cliOption);
				}

				Multimap<Method, String> methodMap = result.get(cliOption);
				if (methodMap == null) {
					methodMap = createSortedSetMethodMultimap();
					result.put(cliOption, methodMap);
				}

				methodMap.put(gcMethod, beanId);
			}
		}

		return result;
	}

	/**
	 * Helper method to create an {@link Options} object from the keys of the given map.
	 *
	 * @param optionMap the map to retrieve the {@link Option}s from.
	 * @param <V>       currently arbitrary since this implementation only processes the keys.
	 * @return a newly-constructed {@link Options} object containing the {@link Option}s in the given map.
	 */
	protected <V> Options createOptions(final Map<Option, V> optionMap) {
		final Options result = new Options();

		final Set<Option> optionsCollection = optionMap.keySet();
		for (Option option : optionsCollection) {
			result.addOption(option);
		}

		return result;
	}

	// Factory methods

	/**
	 * Factory method to create a {@link SortedSetMultimap}, this calls {@link #createAnnotatedMethodsMapComparator()} to sort the map's key,
	 * and {@link com.google.common.collect.Ordering#natural()} to sort the map's values.
	 *
	 * @return a {@link SortedSetMultimap} with ordered keys (ordering of values is incidental).
	 */
	protected SortedSetMultimap<Method, String> createSortedSetMethodMultimap() {
		return TreeMultimap.create(createAnnotatedMethodsMapComparator(), Ordering.natural());
	}

	/**
	 * Returns a {@link Comparator} which looks for the {@link org.springframework.core.annotation.Order}, and if so uses its value to sort
	 * {@link Method}s. This allows {@link Method} invocation to be intentionally sequenced. If the {@link Method} is not annotated with that
	 * annotation it uses the lowest precedence ordering value as it doesn't care which ordering to use, and the Comparator will order two
	 * {@link Method}s with the lowest precedence ordering value arbitrarily, ensuring that two {@link Method} objects with different declaring
	 * classes and signatures never return 0.
	 *
	 * @return a {@link Comparator} that uses the {@link org.springframework.core.annotation.Order} annotation if present to sort {@link Method}s.
	 */
	protected Comparator<Method> createAnnotatedMethodsMapComparator() {
		return new OrderAnnotatedMethodComparator();
	}

	/**
	 * Returns a {@link Predicate} for {@link Method} objects which validate if the {@link Method} is a valid {@link Method} to be executed as a
	 * global configuration {@link Method}. This is true if it has 0 or 1 arguments, and if 1, the argument type is either a String or String[].
	 *
	 * @return a {@link Predicate} validating global configuration {@link Method}s.
	 */
	protected Predicate<Method> createGlobalConfigurationMethodValidator() {
		return new GlobalConfigurationMethodValidator();
	}

	/**
	 * Returns a {@link Predicate} for {@link Method} objects which validate if the {@link Method} is a valid {@link Method} to be executed before
	 * all
	 * global configuration {@link Method}s. This is true if it has 0 arguments, false otherwise.
	 *
	 * @return a {@link Predicate} validating {@link Method}s to be run before all global configuration methods.
	 */
	protected Predicate<Method> createBeforeAllGlobalConfigurationMethodsValidator() {
		return new BeforeOrAfterGlobalConfigurationMethodValidator();
	}

	/**
	 * Returns a {@link Predicate} for {@link Method} objects which validate if the {@link Method} is a valid {@link Method} to be executed after all
	 * global configuration {@link Method}s. This is true if it has 0 arguments, false otherwise.
	 *
	 * @return a {@link Predicate} validating {@link Method}s to be run after all global configuration methods.
	 */
	protected Predicate<Method> createAfterAllGlobalConfigurationMethodsValidator() {
		return new BeforeOrAfterGlobalConfigurationMethodValidator();
	}

	// Getters and Setters

	/**
	 * Returns the bean factory in use by this configurer.
	 *
	 * @return the bean factory in use by this configurer.
	 */
	protected ListableBeanFactory getBeanFactory() {
		return this.beanFactory;
	}
}
