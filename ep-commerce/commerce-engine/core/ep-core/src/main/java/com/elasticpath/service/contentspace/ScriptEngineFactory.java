/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.contentspace;

import java.text.MessageFormat;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 *	ScriptEngineFactory create instance of ScriptEngine depends 
 *  from specified in content wrapper <code>script-language</code> value.
 */
public class ScriptEngineFactory {
	
	private static final Logger LOG = Logger.getLogger(ScriptEngineFactory.class);	
	
	private Map<String, ? extends ScriptEngine> values;

	/**
	 * Returns an instance of ScriptEngine, injected via Spring, for given <code>scriptLanguageEnum</code> script language.
	 * @param scriptLanguage Script language for content wrapper
	 * @return concrete instance of ScriptEngine
	 */
	public ScriptEngine getInstance(final String scriptLanguage) {
		ScriptEngine scriptEngineImpl = values.get(scriptLanguage);
		if (scriptEngineImpl != null) {
			return scriptEngineImpl;
		}

		LOG.error(MessageFormat.format("Can not find script engine implementation in Spring context for {0}", scriptLanguage));
		return null;
	}
	
	/**
	 * Set the supported script engines. 
	 * @param values Map of String and Class 
	 */
	public void setValues(final Map<String, ? extends ScriptEngine> values) {
		this.values = values;
	}
}
