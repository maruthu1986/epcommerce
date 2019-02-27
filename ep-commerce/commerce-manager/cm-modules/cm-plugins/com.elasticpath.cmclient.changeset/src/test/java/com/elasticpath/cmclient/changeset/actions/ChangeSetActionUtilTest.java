/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.changeset.actions;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Locale;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.domain.changeset.ChangeSetObjectStatus;
import com.elasticpath.service.changeset.ChangeSetService;

/**
 * Testing save behaviour of utility class.
 *
 */
public class ChangeSetActionUtilTest {

	@Rule
	public final MockitoRule rule = MockitoJUnit.rule();

	private static final String CHANGE_SET_GUID = "1234-4567-7890-4242"; //$NON-NLS-1$
	private static final String UNKNOWN_CHANGE_SET_GUID = "5555-4567-7890-4242"; //$NON-NLS-1$

	private static final String NULL_CHANGE_SET_MESSAGE = "Should return true if changeset is null"; //$NON-NLS-1$
	private static final String DIRTY_KNOWN_MESSAGE = "Should return true for dirty editor and known change set"; //$NON-NLS-1$
	private static final String DIRTY_UNKOWN_MESSAGE = "Should return true for unknown change set"; //$NON-NLS-1$
	private static final String USER_NO_KNOWN_DIRTY_MESSAGE = "Should return true for known change set, no"; //$NON-NLS-1$
	private static final String USER_CANCELLED_KNOWN_CHANGE_SET_DIRTY_MESSAGE = "Should return false for known change set, cancelled"; //$NON-NLS-1$

	@Mock
	private ChangeSet knownChangeSet;

	@Mock
	private ChangeSet unknownChangeSet;

	private ChangeSetActionUtil userYesUtil;
	private ChangeSetActionUtil userNoUtil;
	private ChangeSetActionUtil userCancelUtil;
	private AbstractCmClientFormEditor dirtyEditor;
	private AbstractCmClientFormEditor cleanEditor;

	@Mock
	private ChangeSetService changeSetService;

	@Mock
	private IWorkbenchWindow workbenchWindow;

	@Mock
	private IWorkbenchPage activePage;

	@Mock
	private IEditorReference editorReference;


	/**
	 * Initialise needed variables.
	 */
	@Before
	public void setUp() {
		final ChangeSetObjectStatus status = mock(ChangeSetObjectStatus.class);

		when(changeSetService.getStatus(nullable(Object.class))).thenReturn(status);
		when(knownChangeSet.getGuid()).thenReturn(CHANGE_SET_GUID);
		when(unknownChangeSet.getGuid()).thenReturn(UNKNOWN_CHANGE_SET_GUID);
		when(status.isMember(CHANGE_SET_GUID)).thenReturn(true);
		when(status.isMember(UNKNOWN_CHANGE_SET_GUID)).thenReturn(false);

		dirtyEditor = new TestEditor() {
			public boolean isDirty() {
				return true;
			}
			public boolean isSaveOnCloseNeeded() {
				return true;
			}
		};

		cleanEditor = new TestEditor() {
			public boolean isDirty() { 
				return false;
			}
			public boolean isSaveOnCloseNeeded() {
				return true;
			}
		};



		userYesUtil = new ChangeSetActionUtil() {
			@Override
			protected ChangeSetService getChangeSetService() {
				return changeSetService;
			}

			@Override
			protected int getUserResponse() {
				return ISaveablePart2.YES;
			}
		};

		userCancelUtil = new ChangeSetActionUtil() {
			@Override
			protected ChangeSetService getChangeSetService() {
				return changeSetService;
			}

			@Override
			protected int getUserResponse() {
				return ISaveablePart2.CANCEL;
			}
		};

		userNoUtil = new ChangeSetActionUtil() {
			@Override
			protected ChangeSetService getChangeSetService() {
				return changeSetService;
			}

			@Override
			protected int getUserResponse() {
				return ISaveablePart2.NO;
			}
		};


	}

	private void prepareSaveAndReloadTest() {

		final IEditorReference[] openEditorReferences = new IEditorReference[] { editorReference };

		when(workbenchWindow.getActivePage()).thenReturn(activePage);
		when(activePage.getEditorReferences()).thenReturn(openEditorReferences);
		when(editorReference.getEditor(false)).thenReturn(dirtyEditor);

	}

	/**
	 * Test the save/reload process, with user interaction dialog removed.
	 */
	@Test
	public void testSaveAndReloadEditors() {
		prepareSaveAndReloadTest();

		assertTrue(NULL_CHANGE_SET_MESSAGE, userCancelUtil.saveAndReloadEditors(null, null));
		assertTrue(NULL_CHANGE_SET_MESSAGE, userYesUtil.saveAndReloadEditors(null, null));
		assertTrue(NULL_CHANGE_SET_MESSAGE, userNoUtil.saveAndReloadEditors(null, null));
		assertTrue(DIRTY_KNOWN_MESSAGE, userYesUtil.saveAndReloadEditors(knownChangeSet, workbenchWindow));
		assertTrue(DIRTY_UNKOWN_MESSAGE, userYesUtil.saveAndReloadEditors(unknownChangeSet, workbenchWindow));
		assertTrue(DIRTY_UNKOWN_MESSAGE, userNoUtil.saveAndReloadEditors(unknownChangeSet, workbenchWindow));
		assertTrue(DIRTY_UNKOWN_MESSAGE, userCancelUtil.saveAndReloadEditors(unknownChangeSet, workbenchWindow));
		assertTrue(USER_NO_KNOWN_DIRTY_MESSAGE, userNoUtil.saveAndReloadEditors(knownChangeSet, workbenchWindow));
		assertFalse(USER_CANCELLED_KNOWN_CHANGE_SET_DIRTY_MESSAGE, userCancelUtil.saveAndReloadEditors(knownChangeSet, workbenchWindow));

		verify(activePage).saveEditor(any(IEditorPart.class), eq(false));

	}

	/**
	 * Should throw a null pointer exception. can't give null workbenchWindow
	 */
	@Test(expected = NullPointerException.class)
	public void testSaveAndReloadWithBadInput() {
		prepareSaveAndReloadTest();
		userYesUtil.saveAndReloadEditors(knownChangeSet, null);
	}


	/**
	 * Verify that is save needed returns the appropriate values.
	 */
	@Test
	public void testIsSaveNeeded() {
		assertTrue(userYesUtil.isSaveNeeded(dirtyEditor, knownChangeSet));
		assertFalse(userYesUtil.isSaveNeeded(dirtyEditor, unknownChangeSet));
		assertFalse(userYesUtil.isSaveNeeded(cleanEditor, knownChangeSet));
		assertFalse(userYesUtil.isSaveNeeded(cleanEditor, unknownChangeSet));
	}

	/**
	 * Check basic functionality of testIsObjectPartOfChangeSet.
	 */
	@Test
	public void testIsObjectPartOfChangeSet() {
		assertFalse("Will return false for a null changeset", userYesUtil.isObjectPartOfChangeSet(null, null)); //$NON-NLS-1$
		//Normally null wouldn't be a valid first parameter here, but we aren't using the real
		//change set service, we're mocking it up to just return a status
		//object of our own.
		assertTrue(userYesUtil.isObjectPartOfChangeSet(null, knownChangeSet));
		assertFalse(userYesUtil.isObjectPartOfChangeSet(null, unknownChangeSet));
	}

	/**
	 * Mocked up editor for testing. Just needs to implement getDependentObject, then tests
	 * can override isDirty, and isSaveOnCloseNeeded
	 */
	private class TestEditor extends AbstractCmClientFormEditor {

		@Override
		public void reload() {
			//no-op
		}

		@Override
		public Object getDependentObject() {
			return "Hi"; //$NON-NLS-1$
		}
		@Override
		protected void addPages() {
			//no-op - not using in test
		}

		@Override
		public Locale getDefaultLocale() {
			return null;
		}

		@Override
		public Collection<Locale> getSupportedLocales() {
			return null;
		}

		@Override
		protected void initEditor(final IEditorSite site, final IEditorInput input) {
			//no-op - not using in test
		}

		@Override
		public void reloadModel() {
			//no-op - not using in test
		}

		@Override
		protected void saveModel(final IProgressMonitor monitor) {
			//no-op - not using in test
		}

		@Override
		protected String getEditorName() {
			return StringUtils.EMPTY;
		}

		@Override
		public Object getAdapter(final Class clazz) {
			return null;
		}


	}
}
