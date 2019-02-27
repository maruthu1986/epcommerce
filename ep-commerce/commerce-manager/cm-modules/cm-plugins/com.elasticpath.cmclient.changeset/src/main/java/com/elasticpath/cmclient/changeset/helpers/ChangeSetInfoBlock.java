/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.changeset.helpers;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.Hyperlink;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.changeset.ChangeSetImageRegistry;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.adapters.EmailHyperlinkAdapter;
import com.elasticpath.cmclient.core.formatting.MetadataDateFormat;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.cmclient.core.ui.ICompositeBlock;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.impl.GridLayoutComposite;
import com.elasticpath.cmclient.core.util.DateTimeUtilFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.service.changeset.ChangeSetService;
import com.elasticpath.service.cmuser.CmUserService;

/**
 * A helper for showing the change set info block for a particular object.
 */
public class ChangeSetInfoBlock implements ICompositeBlock {

	private static final int MARGIN_VALUE = 0;
	private static final int EMPTY_COMPOSITE_HORIZONTAL_SPAN = 4;
	private static final int HYPERLINK_VERTICAL_IDENT = -2;
	private final ChangeSetService changeSetService = ServiceLocator.getService(ContextIdNames.CHANGESET_SERVICE);
	private final CmUserService cmUserService = ServiceLocator.getService(ContextIdNames.CMUSER_SERVICE);
	private final ChangeSetHelper changeSetHelper = ServiceLocator.getService(ChangeSetHelper.BEAN_ID);
	
	private final Image changeSetImage = ChangeSetImageRegistry.CHANGESET.createImage();

	/**
	 * Displays the change set info for the given object.
	 *
	 * @param composite the composite
	 * @param object    the object to display info for
	 */
	protected void addChangeSetInformation(final IEpLayoutComposite composite, final Object object) {
		final ChangeSet changeSet = findChangeSet(object);

		if (changeSet == null) {
			return;
		}

		final GridLayoutComposite mainComposite = (GridLayoutComposite) composite.addGridLayoutComposite(4, false,
				composite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.END, false, false));
		mainComposite.getGridLayout().marginWidth = MARGIN_VALUE;
		mainComposite.getGridLayout().marginHeight = MARGIN_VALUE;
		mainComposite.getGridLayout().verticalSpacing = MARGIN_VALUE;
		mainComposite.getGridLayout().verticalSpacing = MARGIN_VALUE;

		final IEpLayoutData imageLayoutData = mainComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.BEGINNING, false, false);
		final IEpLayoutData emptyLayoutData = mainComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.BEGINNING, false, false);
		final IEpLayoutData nameEmailLabelValueLayoutData = mainComposite.createLayoutData(IEpLayoutData.BEGINNING,
				IEpLayoutData.BEGINNING, false, false);

		Map<String, String> metadata = changeSetService.findChangeSetMemberMetadata(changeSet.getGuid(), object);
		final CmUser cmUser = cmUserService.findByGuid(metadata.get("addedByUserGuid"));

		//zero row
		GridLayoutComposite emptyComposite = (GridLayoutComposite) mainComposite.addGridLayoutComposite(1, false, null);
		emptyComposite.getGridData().horizontalSpan = EMPTY_COMPOSITE_HORIZONTAL_SPAN;

		//first row
		mainComposite.addImage(changeSetImage, imageLayoutData);
		createSkinnyLabel(mainComposite, CatalogMessages.get().VirtualCatalogDialog_ChangeSet, 1);
		createSkinnyLabel(mainComposite, changeSet.getName(), 2);

		//second row
		mainComposite.addEmptyComponent(emptyLayoutData);

		createSkinnyLabel(mainComposite, CatalogMessages.get().VirtualCatalogDialog_AddedBy, 1);
		createSkinnyLabel(mainComposite, findAddedByValue(cmUser), 1);
		final Hyperlink emailHyperlink = mainComposite.addHyperLinkText(StringUtils.EMPTY, EpControlFactory.EpState.EDITABLE,
				nameEmailLabelValueLayoutData);
		emailHyperlink.setText(cmUser.getEmail());
		emailHyperlink.addHyperlinkListener(new EmailHyperlinkAdapter());
		((GridData) emailHyperlink.getLayoutData()).verticalIndent = HYPERLINK_VERTICAL_IDENT;

		//third row
		mainComposite.addEmptyComponent(emptyLayoutData);
		createSkinnyLabel(mainComposite, CatalogMessages.get().VirtualCatalogDialog_DateAdded, 1);
		createSkinnyLabel(mainComposite, findAddedDate(metadata), 2);
	}

	private Label createSkinnyLabel(final IEpLayoutComposite mainComposite, final String text, final int horizontalSpan) {
		Label label = mainComposite.addSkinnyLabel(text,
				mainComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.BEGINNING, false, false));
		GridData gridData = new GridData();
		gridData.horizontalSpan = horizontalSpan;
		label.setLayoutData(gridData);
		return label;
	}

	private String findAddedByValue(final CmUser cmUser) {
		if (cmUser != null) {
			return getFormattedName(cmUser);
		}

		return StringUtils.EMPTY;
	}

	private String findAddedDate(final Map<String, String> metadata) {
		String dateAdded = metadata.get("dateAdded");
		try {
			Date date = new MetadataDateFormat().parse(dateAdded);
			return DateTimeUtilFactory.getDateUtil().formatAsDateTime(date);
		} catch (ParseException e) {
			return StringUtils.EMPTY;
		}
	}

	private ChangeSet findChangeSet(final Object object) {

		if (!changeSetHelper.isChangeSetsEnabled()) {
			return null;
		}

		return changeSetService.findChangeSet(object);
	}

	private String getFormattedName(final CmUser cmUser) {
		final StringBuilder formattedName = new StringBuilder();
		formattedName.append(cmUser.getLastName());
		formattedName.append(", "); //$NON-NLS-1$
		formattedName.append(cmUser.getFirstName());
		return formattedName.toString();
	}

	/**
	 * Adds the change set info widgets to the given composite.
	 *
	 * @param composite       the composite
	 * @param dependentObject the object
	 */
	public void init(final IEpLayoutComposite composite, final Object dependentObject) {
		addChangeSetInformation(composite, dependentObject);
	}
}

