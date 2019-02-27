/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 *
 */
package com.elasticpath.cmclient.conditionbuilder;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.junit.Rule;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.elasticpath.cmclient.conditionbuilder.impl.tag.ConditionBuilderFactoryImpl;
import com.elasticpath.tags.domain.Condition;
import com.elasticpath.tags.domain.LogicalOperator;
import com.elasticpath.tags.domain.LogicalOperatorType;
import com.elasticpath.tags.domain.TagDefinition;
import com.elasticpath.tags.domain.TagGroup;
import com.elasticpath.tags.domain.TagOperator;
import com.elasticpath.tags.domain.TagValueType;

/**
 * TestConditionBuilder.
 */
@SuppressWarnings("PMD")
public class TestConditionBuilder {

	private static final int NUM_4 = 4;
	private static final int NUM_3 = 3;
	private Display display = new Display(); //NOPMD
	private Shell shell = new Shell(display); //NOPMD

	@Rule
	public final MockitoRule rule = MockitoJUnit.rule();

	/**
	 * Main method.
	 *
	 * @param args command line args.
	 */
	public static void main(final String[] args) {
		new TestConditionBuilder();
	}

	/**
	 * Default constructor.
	 */
	public TestConditionBuilder() {
		super();
		shell.setLayout(new GridLayout());

		(new Label(shell, SWT.NULL)).setText("[This is a test screen of condition Builder]"); //$NON-NLS-1$

		this.initConditionBuilder(shell);

		shell.pack();
		shell.open();

		// Set up the event loop.
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				// If no more entries in event queue
				display.sleep();
			}
		}

		display.dispose();
	}

	private void initConditionBuilder(final Shell shell) {

		final Set<TagOperator> tagOperators = this.createTagOperatorList();
		List<TagDefinition> tagDefinitions = new LinkedList<>();

		List<TagGroup> tagGroups = new LinkedList<>();

		Set<TagDefinition> tagDefinitionSet = new HashSet<>();
		Set<TagDefinition> tagDefinitionSet2 = new HashSet<>();
		Set<TagDefinition> tagDefinitionSet3 = new HashSet<>();

		TagGroup group = this.createTagGroup("GUID_GROUP1", "GROUP 1 localized", tagDefinitionSet);  //$NON-NLS-1$//$NON-NLS-2$
		TagGroup group2 = this.createTagGroup("GUID_GROUP2", "GROUP 2 localized", tagDefinitionSet2);  //$NON-NLS-1$//$NON-NLS-2$
		TagGroup group3 = this.createTagGroup("GUID_GROUP3", "GROUP 3 localized", tagDefinitionSet3);  //$NON-NLS-1$//$NON-NLS-2$

		tagGroups.add(group);
		tagGroups.add(group2);
		tagGroups.add(group3);

		this.createTagDefinition(group, "GUID1", "Tag #1 name.", "Tag #1 name, localized!", tagOperators); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

		this.createTagDefinition(group, "CATEGORIES_VISITED", //$NON-NLS-1$
				"CATEGORIES_VISITED", "CATEGORIES_VISITED", tagOperators);   //$NON-NLS-1$//$NON-NLS-2$		
		this.createTagDefinition(group2, "GEOIP_GMT_TIME_ZONE", //$NON-NLS-1$
				"TGEOIP_GMT_TIME_ZONE", "GEOIP_GMT_TIME_ZONE", tagOperators);   //$NON-NLS-1$//$NON-NLS-2$
		this.createTagDefinition(group2, "GUID2", "Tag #2 name.", "Tag #2 name, localized!", tagOperators); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
		this.createTagDefinition(group2, "GUID3", "Tag #3 name.", "Tag #3 name, localized!", tagOperators); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

		this.createTagDefinition(group3, "GUID4", "Tag #4 name.", "Tag #4 name, localized!", tagOperators); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
		this.createTagDefinition(group3, "GUID5", "Tag #5 name.", "Tag #5 name, localized!", tagOperators); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

		DataBindingContext dataBindingContext = new DataBindingContext(
				SWTObservables.getRealm(Display.getDefault())
		);

		Set<TagDefinition> tagDefinitionAll = new HashSet<>();
		tagDefinitionAll.addAll(tagDefinitionSet);
		tagDefinitionAll.addAll(tagDefinitionSet2);
		tagDefinitionAll.addAll(tagDefinitionSet3);
		ConditionBuilderFactoryImpl factory = new ConditionBuilderFactoryImpl();
		factory.setDataBindingContext(dataBindingContext);
		factory.setLocale(Locale.getDefault());
		factory.setTagDictionary("DICTIOANRY_GUID"); //$NON-NLS-1$
		factory.setTagOperatorService(guid -> {
			TagOperator result = null;
			for (TagOperator operator : tagOperators) {
				if (!operator.getGuid().equals(guid)) {
					continue;
				}
				result = operator;
				break;
			}
			return result;
		});
		factory.setAddButtonText("ConditionBuilder_AddConditionButton"); //$NON-NLS-1$
		factory.setConditionBuilderTitle("ConditionBuilder_Title"); //$NON-NLS-1$
		factory.getResourceAdapterFactory().setResourceAdapterForLogicalOperator(LogicalOperatorType::getMessageKey);
		factory.getResourceAdapterFactory().setResourceAdapterForUiElements(object -> object);

		factory.setListenerForRefreshParentComposite(
				object -> {
					shell.pack();
					shell.layout();
				});


		// model
		final LogicalOperator topModel = new LogicalOperator(LogicalOperatorType.AND);

		LogicalOperator block1 = new LogicalOperator(LogicalOperatorType.OR);
		LogicalOperator block2 = new LogicalOperator(LogicalOperatorType.AND);

		block1.setParentLogicalOperator(topModel);
		block2.setParentLogicalOperator(topModel);

		Condition condition01 = new Condition(tagDefinitions.get(1), "equalTo", "5.0"); //$NON-NLS-1$ //$NON-NLS-2$
		Condition condition02 = new Condition(tagDefinitions.get(NUM_3), "greaterThan", "value02"); //$NON-NLS-1$ //$NON-NLS-2$
		Condition condition11 = new Condition(tagDefinitions.get(NUM_4), "lessThan", "value11"); //$NON-NLS-1$ //$NON-NLS-2$ 
		Condition condition12 = new Condition(tagDefinitions.get(2), "greaterThanOrEqualTo", "value12"); //$NON-NLS-1$ //$NON-NLS-2$ 

		condition01.setParentLogicalOperator(block1);
		condition02.setParentLogicalOperator(block1);
		condition11.setParentLogicalOperator(block2);
		condition12.setParentLogicalOperator(block2);

		block1.addCondition(condition01);
		block1.addCondition(condition02);
		block2.addCondition(condition11);
		block2.addCondition(condition12);

		factory.createFullUiFromModel(shell, SWT.FLAT, topModel);

		Button button = new Button(shell, SWT.FLAT);
		button.setText("System.out.println(LogicalOperator.toString())"); //$NON-NLS-1$
		button.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(final SelectionEvent event) {
				// 
			}

			@Override
			public void widgetSelected(final SelectionEvent event) {
				System.out.println("Get it: [" + topModel.toString() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		});
	}

	private TagGroup createTagGroup(final String guid, final String nameLocalized, final Set<TagDefinition> tagDefinitions) {
		final TagGroup group = mock(TagGroup.class, guid);
		when(group.getGuid()).thenReturn(guid);
		when(group.getLocalizedGroupName(Locale.ENGLISH)).thenReturn(nameLocalized);
		when(group.getTagDefinitions()).thenReturn(tagDefinitions);
		return group;
	}

	private TagDefinition createTagDefinition(final TagGroup tagGroup,
											  final String guid, final String name, final String nameLocalized,
											  final Set<TagOperator> tagOperators) {

		final TagDefinition tag = mock(TagDefinition.class, guid);
		when(tag.getGuid()).thenReturn(guid);
		when(tag.getLocalizedName(Locale.ENGLISH)).thenReturn(nameLocalized);
		when(tag.getName()).thenReturn(name);
		when(tag.getValueType()).thenReturn(createTagValueType(guid, tagOperators));
		when(tag.getGroup()).thenReturn(tagGroup);

		tagGroup.getTagDefinitions().add(tag);

		return tag;
	}

	private TagValueType createTagValueType(final String guid, final Set<TagOperator> tagOperators) {

		final TagValueType tagValue = mock(TagValueType.class, guid + "tagValueType"); //$NON-NLS-1$
		when(tagValue.getGuid()).thenReturn(guid);
		when(tagValue.getOperators()).thenReturn(tagOperators);
		return tagValue;
	}

	private Set<TagOperator> createTagOperatorList() {
		String[] operators = new String[]
				{"equalTo", "lessThan", "greaterThan", "greaterThanOrEqualTo", "includes"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		// $NON-NLS-5$

		Set<TagOperator> result = new LinkedHashSet<>(operators.length);
		TagOperator tagOperator;
		for (String operator : operators) {
			tagOperator = this.createTagOperator(operator);
			result.add(tagOperator);
		}
		return result;
	}

	private TagOperator createTagOperator(final String guid) {
		final TagOperator tagOperator = mock(TagOperator.class, guid);
		when(tagOperator.getGuid()).thenReturn(guid);
		when(tagOperator.getName(Locale.getDefault())).thenReturn(guid);
		return tagOperator;
	}
}
