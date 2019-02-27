/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.changeset.impl;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleElement;
import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.service.catalog.BrandService;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.catalog.ProductLookup;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.changeset.ChangeSetDependencyResolver;
import com.elasticpath.service.rules.RuleService;

/**
 *		Resolver for Catalog Promotions. 
 */
public class PromotionChangeSetDependencyResolverImpl implements ChangeSetDependencyResolver {

	private RuleService ruleService;
	
	private CategoryService categoryService;
	private ProductSkuLookup productSkuLookup;
	private BrandService brandService;
	private ProductLookup productLookup;

	@Override
	public Set<?> getChangeSetDependency(final Object object) {
		
		if (object instanceof Rule) {
			Set<Object> depends = new LinkedHashSet<>();
			Rule rule = (Rule) object;
			depends.addAll(getDependentsForRuleSet(rule.getRuleElements()));
			depends.addAll(getDependentsForRuleSet(rule.getConditions()));
			
			return depends;
		}
		return Collections.emptySet();
	}

	

	private Set<Object> getDependentsForRuleSet(final Set<? extends RuleElement> ruleElements) {
		Set<Object> depends = new LinkedHashSet<>();
		for (RuleElement condition : ruleElements) {
			Set<RuleParameter> parameters = condition.getParameters();
			for (RuleParameter parameter : parameters) {
				depends.addAll(getDependentObjects(parameter));
			}
		}
		return depends;
	}

	private Set<Object> getDependentObjects(final RuleParameter parameter) {
		Set<Object> objects = new LinkedHashSet<>();
		if (parameter.getKey().equals(RuleParameter.PRODUCT_CODE_KEY)) {
			Product prod = getProductLookup().findByGuid(parameter.getValue());
			objects.add(prod);
		} else if (parameter.getKey().equals(RuleParameter.BRAND_CODE_KEY)) {
			Brand brand = getBrandService().findByCode(parameter.getValue());
			objects.add(brand);
		} else if (parameter.getKey().equals(RuleParameter.CATEGORY_CODE_KEY)) {
			String value = getCategoryCodeFromParameter(parameter);
			Category category = getCategoryService().findByCode(value);
			objects.add(category);
		} else if (parameter.getKey().equals(RuleParameter.SKU_CODE_KEY)) {
			ProductSku productSku = getProductSkuLookup().findBySkuCode(parameter.getValue());
			Product product = productSku.getProduct();
			if (product.hasMultipleSkus()) {
				objects.add(productSku);
			} else {
				objects.add(product);
			}
		}

		return objects;
	}
	

	
	private String getCategoryCodeFromParameter(final RuleParameter parameter) {
		String value = parameter.getValue();
		String[] result = value.split("\\|");
		return result[0];
	}

	
	@Override
	public Object getObject(final BusinessObjectDescriptor object, final  Class<?> objectClass) {
		if (Rule.class.isAssignableFrom(objectClass)) {
			return getRuleService().findByRuleCode(object.getObjectIdentifier());
		
		}
		return null;
	}

	private RuleService getRuleService() {
		return ruleService;
	}
	public void setRuleService(final RuleService ruleService) {
		this.ruleService = ruleService;
	}

	private CategoryService getCategoryService() {
		return categoryService;
	}

	public void setCategoryService(final CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	private ProductSkuLookup getProductSkuLookup() {
		return productSkuLookup;
	}

	public void setProductSkuLookup(final ProductSkuLookup productSkuLookup) {
		this.productSkuLookup = productSkuLookup;
	}

	public BrandService getBrandService() {
		return brandService;
	}

	public void setBrandService(final BrandService brandService) {
		this.brandService = brandService;
	}

	protected ProductLookup getProductLookup() {
		return productLookup;
	}

	public void setProductLookup(final ProductLookup productLookup) {
		this.productLookup = productLookup;
	}
}
