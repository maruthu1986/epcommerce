/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.importexport.common.adapters.promotion;

import static com.elasticpath.importexport.common.comparators.ExportComparators.EXCEPTION_DTO;
import static com.elasticpath.importexport.common.comparators.ExportComparators.PARAMETER_DTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.elasticpath.common.dto.Dto;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.rules.RuleElement;
import com.elasticpath.domain.rules.RuleException;
import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;
import com.elasticpath.importexport.common.dto.promotion.ExceptionDTO;
import com.elasticpath.importexport.common.dto.promotion.ParameterDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRollbackException;

/**
 * @param <DTO> the data transfer interface that extends <code>Dto</code> interface
 */
public abstract class AbstractElementAdapter<DTO extends Dto> extends AbstractDomainAdapterImpl<RuleElement, DTO> { // NOPMD

	/**
	 * Creates RuleElementParameterDTO List for a Set of RuleParameters.
	 * 
	 * @param ruleParameterSet a Set of RuleParameters
	 * @return RuleElementParameterDTO List
	 */	
	List<ParameterDTO> createElementParameterDTOList(final Set<RuleParameter> ruleParameterSet) {
		final List<ParameterDTO> ruleElementParameters = new ArrayList<>();

		for (final RuleParameter ruleParameter : ruleParameterSet) {
			ruleElementParameters.add(createElementParameterDTO(ruleParameter));
		}
		Collections.sort(ruleElementParameters, PARAMETER_DTO);
		
		return ruleElementParameters;
	}
	
	/**
	 * Creates ParameterDTO for given RuleParameter domain.
	 * 
	 * @param ruleParameter the RuleParameter domain object 
	 * @return populated ParameterDTO
	 */
	ParameterDTO createElementParameterDTO(final RuleParameter ruleParameter) {
		ParameterDTO ruleElementParameterDTO = new ParameterDTO();
		
		ruleElementParameterDTO.setKey(ruleParameter.getKey());
		ruleElementParameterDTO.setValue(ruleParameter.getValue());
		return ruleElementParameterDTO;
	}
	
	/**
	 * Creates RuleElementExceptionDTO List for a Set of RuleException.
	 * 
	 * @param ruleExceptionSet a Set of RuleException
	 * @return RuleElementExceptionDTO List
	 */	
	List<ExceptionDTO> createElementExceptionDTOList(final Set<RuleException> ruleExceptionSet) {
		final List<ExceptionDTO> ruleElementExceptionDTOList = new ArrayList<>();
		
		for (final RuleException ruleException : ruleExceptionSet) {
			ruleElementExceptionDTOList.add(createElementExceptionDTO(ruleException));
		}
		Collections.sort(ruleElementExceptionDTOList, EXCEPTION_DTO);

		return ruleElementExceptionDTOList;
	}

	/**
	 * Creates ExceptionDTO for given RuleException domain.
	 * 
	 * @param ruleException the RuleException domain object 
	 * @return populated ExceptionDTO
	 */
	ExceptionDTO createElementExceptionDTO(final RuleException ruleException) {
		final ExceptionDTO ruleElementExceptionDTO = new ExceptionDTO();
		
		ruleElementExceptionDTO.setExceptionType(ruleException.getType());
		ruleElementExceptionDTO.setExceptionParameters(createElementParameterDTOList(ruleException.getParameters()));
		
		return ruleElementExceptionDTO;
	}
	
	/**
	 * Creates populated Rule Exception Set using a List of ExceptionDTO.
	 * 
	 * @param elementExceptionDTOList a List of ExceptionDTO
	 * @return a Set of RuleException 
	 */
	Set<RuleException> createRuleExceptionSet(final List<ExceptionDTO> elementExceptionDTOList) {
		final Set<RuleException> ruleExceptions = new HashSet<>();
		
		for (ExceptionDTO exceptionDTO : elementExceptionDTOList) {
			final RuleException ruleException = createRuleException(exceptionDTO.getExceptionType());

			ruleException.setParameters(createRuleParameterSet(exceptionDTO.getExceptionParameters()));
			
			ruleExceptions.add(ruleException);
		}		
		
		return ruleExceptions;
	}

	/**
	 * Creates RuleException using type.
	 * 
	 * @param exceptionType the type
	 * @return RuleException of given type
	 * @throws PopulationRollbackException if given exceptionType is not valid
	 */
	RuleException createRuleException(final String exceptionType) {
		RuleException ruleException = getBeanFactory().getBean(exceptionType);
		
		if (ruleException == null) {
			throw new PopulationRollbackException("IE-10700", exceptionType);
		}
		
		return ruleException;
	}

	/**
	 * Creates populated Set of RuleParameter using a List of ParameterDTO.
	 * 
	 * @param elementParameterDTOList a List of ParameterDTO
	 * @return a Set of RuleParameter
	 */
	Set<RuleParameter> createRuleParameterSet(final List<ParameterDTO> elementParameterDTOList) {
		final Set<RuleParameter> parameters = new HashSet<>();
		for (final ParameterDTO parameterDTO : elementParameterDTOList) {
			final RuleParameter ruleParameter = getBeanFactory().getBean(ContextIdNames.RULE_PARAMETER);
			
			ruleParameter.setKey(parameterDTO.getKey());
			ruleParameter.setValue(parameterDTO.getValue());
			
			parameters.add(ruleParameter);
		}
		return parameters;
	}
}
