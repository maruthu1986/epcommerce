/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.domain.rules.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.StringReader;

import org.drools.core.WorkingMemory;
import org.drools.core.common.DroolsObjectInputStream;
import org.drools.core.common.DroolsObjectOutputStream;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

/**
 * Test case for {@link EpRuleBaseImpl}.
 */
public class EpRuleBaseImplTest {


	/**
	 * Test method for externalizers of {@link EpRuleBaseImpl} for the Reteoo {@link InternalKnowledgeBase}s
	 * it stores.
	 *
	 * @throws Exception in case of any errors
	 */
	@Test
	public void testRuleBaseExternalizersReteoo() throws Exception {
		final InternalKnowledgeBase ruleBase = KnowledgeBaseFactory.newKnowledgeBase(KnowledgeBaseFactory.newKnowledgeBaseConfiguration());

		addDefaultRuleToRuleBase(ruleBase);
		testDefaultRule(ruleBase);

		final KieBase serializedRuleBase = EpRuleBaseImpl.ruleBaseFactory(EpRuleBaseImpl.externalizeRuleBase(ruleBase));
		testDefaultRule(serializedRuleBase);
	}

	/**
	 * This test makes sure that an externally compiled rule base can be deserialized and
	 * reserialized whilst maintaining its ability to perform the rule.
	 *
	 * @throws Exception in case of errors
	 */
	@Test
	public void testWriteReadDifferentRuleBase() throws Exception {
		InternalKnowledgeBase reteooRuleBase1 = KnowledgeBaseFactory.newKnowledgeBase(KnowledgeBaseFactory.newKnowledgeBaseConfiguration());

		addDefaultRuleToRuleBase(reteooRuleBase1);

		// Test to make sure the rule works with the leaps type rule base
		testDefaultRule(reteooRuleBase1);

		Externalizable leapsExternalRuleBase = (Externalizable) reteooRuleBase1;
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		DroolsObjectOutputStream outputStream = new DroolsObjectOutputStream(output);
		leapsExternalRuleBase.writeExternal(outputStream);
		outputStream.close();

		InternalKnowledgeBase reteooRuleBase = KnowledgeBaseFactory.newKnowledgeBase(KnowledgeBaseFactory.newKnowledgeBaseConfiguration());
		Externalizable reteooExternalRuleBase = (Externalizable) reteooRuleBase;
		reteooExternalRuleBase.readExternal(new DroolsObjectInputStream(
				new ByteArrayInputStream(output.toByteArray()), EpRuleBaseImpl.class.getClassLoader()));

		testDefaultRule(reteooRuleBase);
	}

	private void testDefaultRule(final KieBase reteooRuleBase1) {
		final TestClass reteooTester1 = new TestClass();
		assertFalse(reteooTester1.isResult());

		reteooTester1.setResult(true);

		final WorkingMemory leapsMemory = (WorkingMemory) reteooRuleBase1.newKieSession();
		leapsMemory.insert(reteooTester1);
		leapsMemory.fireAllRules();

		assertTrue(reteooTester1.isResult());
	}

	@SuppressWarnings("PMD.ConsecutiveLiteralAppends")
	private void addDefaultRuleToRuleBase(final InternalKnowledgeBase reteooRuleBase1) throws Exception {
		final StringBuilder source = new StringBuilder("package test\n\n");
		source.append("import " + TestClass.class.getName().replace('$', '.') + ";\n");
		source.append("rule \"test rule\"\n");
		source.append("salience 0\n");
		source.append("when\n");
		source.append("tester : TestClass ( bool == true )\n");
		source.append("then\n");
		source.append("tester.setResult(true);\n");
		source.append("end");

		final KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		builder.add(ResourceFactory.newReaderResource(new StringReader(source.toString())), ResourceType.DRL);

		reteooRuleBase1.addPackages(builder.getKnowledgePackages());
	}

	/**
	 * Test class for testing.
	 */
	public class TestClass {
		private boolean bool;

		private boolean result;

		public boolean isBool() {
			return bool;
		}

		public void setBool(final boolean bool) {
			this.bool = bool;
		}

		public boolean isResult() {
			return result;
		}

		public void setResult(final boolean result) {
			this.result = result;
		}
	}
}
