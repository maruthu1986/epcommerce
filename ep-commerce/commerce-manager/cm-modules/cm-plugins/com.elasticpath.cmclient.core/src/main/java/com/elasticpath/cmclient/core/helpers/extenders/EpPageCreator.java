/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.helpers.extenders;

import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPage;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;

/**
 * Interface used for instantiating a page.
 */
public interface EpPageCreator {

    /**
     * Instantiate a page.
     *
     * @param editor the editor.
     * @return the AbstractCmClientEditorPage.
     */
    AbstractCmClientEditorPage instantiatePage(AbstractCmClientFormEditor editor);

}
