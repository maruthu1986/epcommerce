/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.service.contentspace.impl;

import java.util.Map;

import groovy.lang.Binding;

import com.elasticpath.service.contentspace.ScriptEngine;

/**
 * An abstract Groovy based script engine for evaluating expressions.  Subclasses must provide
 * the caching mechanism.
 */
public abstract class AbstractGroovyScriptEngineImpl implements ScriptEngine {

	/**
	 * The state of this particular engine instance, must be plugged into the cached
	 * script before it is run for this engine instance.
	 */
	private Binding binding;

	/**
	 * Evaluates an expression.  Will use a pre-compiled version of the
	 * <code>expression</code>.
	 *
	 * @param expression the expression
	 * @return the result of the expression. Could be null.
	 */
	@Override
	public Object evaluateExpression(final String expression) {
		if (expression != null) {
			RunnableScript script = getCompiledScript(expression);
			if (script != null) {
				return script.run(binding);
			}
		}
		return null;
	}

	/**
	 * Initializes the engine with a context of parameters and their values.
	 *
	 * @param dynamicContext the dynamic context
	 * @param scriptInitSection multiline string with initial instructions. Can be null
	 */
	@Override
	public void initialize(final Map<String, Object> dynamicContext, final String scriptInitSection) {
		binding = new Binding(dynamicContext);
		evaluateExpression(scriptInitSection);
	}


	/**
	 * An abstraction around the running of a Groovy <code>Script</code>.
	 */
	protected interface RunnableScript {

		/**
		 * Runs the script in the context of the supplied binding.
		 * @param binding the binding to run the script in the context of.
		 * @return the result of running the script.
		 */
		Object run(Binding binding);
	}


	/**
	 * Returns the compiled version of textScript.
	 * @param textScript the script to get a compiled version of.
	 * @return the compiled version of the script.
	 */
	protected abstract RunnableScript getCompiledScript(String textScript);

}
