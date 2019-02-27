/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.messaging.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.factory.FactoryBean;

/**
 * CamelAnnotationsCache.
 */
public class CamelAnnotatedClasses implements FactoryBean<Set<Class<?>>> {

	private List<Class<? extends Annotation>> annotations;

	@Override
	public Set<Class<?>> getObject() throws Exception {
		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder()
				.setUrls(filterClasspathURLs(ClasspathHelper.forManifest()))
				.setScanners(new SubTypesScanner(),
						new TypeAnnotationsScanner(),
						new MethodAnnotationsScanner(),
						new FieldAnnotationsScanner());
		Reflections reflections = new Reflections(configurationBuilder);

		Set<Class<?>> classes = new HashSet<>();
		for (Class<? extends Annotation> annotation : getAnnotations()) {
			addClassesWithFieldAnnotation(classes, annotation, reflections);
			addClassesWithMethodAnnotation(classes, annotation, reflections);
		}

		return classes;
	}

	private Set<URL> filterClasspathURLs(final Collection<URL> classpathUrls) {
		return classpathUrls.stream().filter(url -> {
			String fileName = StringUtils.substringAfterLast(url.toExternalForm(), "/");
			return StringUtils.isEmpty(fileName) || fileName.endsWith(".jar");
		}).collect(Collectors.toSet());
	}

	private void addClassesWithFieldAnnotation(final Set<Class<?>> classes,
												final Class<? extends Annotation> annotation,
												final Reflections reflections) {
		Set<Field> fields = reflections.getFieldsAnnotatedWith(annotation);
		for (Field field : fields) {
			classes.add(field.getDeclaringClass());
		}
	}

	private void addClassesWithMethodAnnotation(final Set<Class<?>> classes,
												final Class<? extends Annotation> annotation,
												final Reflections reflections) {
		Set<Method> methods = reflections.getMethodsAnnotatedWith(annotation);
		for (Method method : methods) {
			classes.add(method.getDeclaringClass());
		}
	}

	@Override
	public Class<?> getObjectType() {
		return Set.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public List<Class<? extends Annotation>> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(final List<Class<? extends Annotation>> annotations) {
		this.annotations = annotations;
	}

}
