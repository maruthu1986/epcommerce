/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.helpers.extenders;

import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.ui.framework.AbstractCmClientFormSectionPart;
import org.eclipse.ui.forms.editor.FormPage;

/**
 * EP section creator.
 */
public interface EpSectionCreator {

    /**
     * Instantiate a section.
     *
     * @param formPage the form page.
     * @param editor the editor.
     * @return the AbstractCmClientFormSectionPart.
     */
    AbstractCmClientFormSectionPart instantiateSection(FormPage formPage, AbstractCmClientFormEditor editor);
}
