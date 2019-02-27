/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.helpers.extenders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.forms.editor.FormPage;

import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.EpUiException;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPage;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.helpers.TestIdUtil;
import com.elasticpath.cmclient.core.ui.framework.AbstractCmClientFormSectionPart;
import com.elasticpath.commons.util.extenum.ExtensibleEnum;

/**
 * Helper class for extending commerce manager.
 */
public final class PluginHelper {
    private static final String CORE_PLUGIN_ID = "com.elasticpath.cmclient.core";

	private static final Logger LOG = Logger.getLogger(PluginHelper.class);

    /**
     * Plugin Extension point ids (referenced in Plugin.xml).
     */
    private static final String EDITOR_TABLE_EXTENSION_ID = "Extender";
    private static final String STATE_POLICY_EXTENSION_ID = "StatePolicyExtender";
    private static final String PROMOTION_EXTENDER = "PromotionExtender";
    private static final String TEST_EXTENSION = "TestExtension";
    private static final String MODEL_EXTENDER = "ModelExtender";
    
    /**
     * Element names.
     */
    private static final String TABLE_ELEMENT_NAME = "TableColumn";
    private static final String PAGE_ELEMENT_NAME = "Page";
    private static final String MESSAGING_ELEMENT_NAME = "Messaging";
    private static final String DETERMINERS_ELEMENT_NAME = "Determiners";
    private static final String WIDGET_ELEMENT_NAME = "Widget";
    private static final String DIALOG_ELEMENT_ID = "Dialog";
    private static final String TEST_ELEMENT_NAME = "EpWidgetUtil";
    private static final String SECTION_ELEMENT_ID = "Section";

    /**
     * Attribute ids.
     */
    private static final String CLASS_NAME_ATTRIBUTE = "className";
    private static final String POSITION_ATTRIBUTE = "position";
    private static final String PARENT_TABLE_ATTRIBUTE = "table";
    private static final String PARENT_EDITOR_NAME_ATTRIBUTE = "editorName";
    private static final String PARENT_PAGE_ID_ATTRIBUTE = "pageId";
    private static final String DIALOG_NAME_ATTRIBUTE = "dialogName";


    private PluginHelper() {
        // Empty constructor.
    }


    /**
     * Gets a specific attribute for a given extension.
     * @param pluginId The plugin containing the extension point.
     * @param extensionId The extension plugin.
     * @param type The element type.
     * @param attributeId the attribute.
     * @return the attribute value, or null.
     */
    public static String getPluginAttribute(final String pluginId, final String extensionId, final String type, final String attributeId) {
        IExtension[] extensions = Platform.getExtensionRegistry().getExtensionPoint(pluginId, type).getExtensions();
        Optional<String> first = Arrays.stream(extensions).filter(extension -> extension.getNamespaceIdentifier().equalsIgnoreCase(extensionId))
                .flatMap(extension -> Arrays.stream(extension.getConfigurationElements()))
                .map(configurationElement -> configurationElement.getAttribute(attributeId))
                .findFirst();

        return first.orElse(null);
    }
    /**
     * Find table extenders.
     *
     * @param tableName the table name.
     * @param pluginId  the plugin id.
     * @return list of table extenders.
     */
    public static List<EPTableColumnCreator> findTables(final String tableName, final String pluginId) {
        List<EPTableColumnCreator> tableExtenders = new ArrayList<>();

        List<IConfigurationElement> configElements = getConfigElements(pluginId, EDITOR_TABLE_EXTENSION_ID);
        configElements.stream()
                .filter(configurationElement -> configurationElement.getName().equals(TABLE_ELEMENT_NAME)
                        && configurationElement.getAttribute(PARENT_TABLE_ATTRIBUTE).equals(tableName))
                .forEach(configElement -> instantiateTable(tableExtenders, configElement));

        return tableExtenders;
    }

    /**
     * Find extended pages.
     *
     * @param editorName the editor name.
     * @param pluginId   the plugin id.
     * @param editor     the editor.
     * @return list of pages.
     */
    public static List<AbstractCmClientEditorPage> findPages(final String editorName, final String pluginId,
                                                             final AbstractCmClientFormEditor editor) {
        String parentEditorName = PARENT_EDITOR_NAME_ATTRIBUTE;
        List<AbstractCmClientEditorPage> extendedPages = new ArrayList<>();
        List<IConfigurationElement> allConfigElements = getConfigElements(pluginId, EDITOR_TABLE_EXTENSION_ID);
        allConfigElements.stream()
                .filter(configurationElement -> configurationElement.getName().equals(PAGE_ELEMENT_NAME)
                        && configurationElement.getAttribute(parentEditorName).equals(editorName))
                .sorted(elementComparator())
                .forEach(configElement -> instantiatePage(editor, extendedPages, configElement));

        return extendedPages;
    }

    /**
     * Find extended sections.
     *
     * @param pluginId the plugin id.
     * @param pageId   the form id.
     * @param formPage the form page.
     * @param editor   the editor.
     * @return list of sections.
     */
    public static List<AbstractCmClientFormSectionPart> findSections(final String pluginId, final String pageId,
                                                                     final FormPage formPage, final AbstractCmClientFormEditor editor) {
        List<AbstractCmClientFormSectionPart> extendedParts = new ArrayList<>();
        List<IConfigurationElement> allConfigElements = getConfigElements(pluginId, EDITOR_TABLE_EXTENSION_ID);
        allConfigElements.stream()
                .filter(configurationElement -> SECTION_ELEMENT_ID.equals(configurationElement.getName())
                        && configurationElement.getAttribute(PARENT_PAGE_ID_ATTRIBUTE).equals(pageId))
                .sorted(elementComparator())
                .forEach(configElement -> instantiateSection(formPage, editor, extendedParts, configElement));
        return extendedParts;
    }

    /**
     * Finds dialog extensions.
     * @param dialogName the name of the dialog to extend.
     * @param pluginId the plugin id.
     * @return A list of DialogExtensions
     */
    public static List<DialogExtension> findDialogExtensions(final String dialogName, final String pluginId) {

        List<DialogExtension> dialogExtensions = new ArrayList<>();
        List<IConfigurationElement> allConfigElements = getConfigElements(pluginId, EDITOR_TABLE_EXTENSION_ID);
        allConfigElements.stream()
                    .filter(configurationElement -> DIALOG_ELEMENT_ID.equals(configurationElement.getName())
                            && configurationElement.getAttribute(DIALOG_NAME_ATTRIBUTE).equals(dialogName))
                    .forEach(configElement-> dialogExtensions.add(instantiateDialogElement(configElement)));

        return dialogExtensions;
    }

    /**
     * Find extended localized string.
     *
     * @param pluginId the plugin id.
     * @param anEnum   the key
     * @return the localized string.
     */
    public static String getExtendedLocalizedString(final String pluginId, final ExtensibleEnum anEnum) {

        List<MessageReader> messageReaders = new ArrayList<>();
        List<IConfigurationElement> allConfigElements = getConfigElements(pluginId, PROMOTION_EXTENDER);
        allConfigElements.stream()
                .filter(configurationElement -> MESSAGING_ELEMENT_NAME.equals(configurationElement.getName()))
                .forEach(configElement -> messageReaders.add(instantiateMessaging(configElement)));

        for (MessageReader messageReader : messageReaders) {
            String localizedName = messageReader.getLocalizedName(anEnum);
            if (localizedName != null) {
                return localizedName;
            }
        }

        return null;
    }

    /**
     * Finds the first test util that have been registered as extensions.
     * @return The test util. Null otherwise.
     */
    public static TestIdUtil findTestIdUtil() {
        List<IConfigurationElement> testExtension = getConfigElements(CorePlugin.PLUGIN_ID, TEST_EXTENSION);
        return testExtension.stream()
                .filter(configurationElement -> TEST_ELEMENT_NAME.equals(configurationElement.getName()))
                .findFirst().map(PluginHelper::instantiateWigetTestUtil).orElse(null);
    }

    /**
     * Find extension promotion widget creators.
     *
     * @param pluginId the plugin id.
     * @param paramKey the param key.
     * @return list of promotion widget creators.
     */
    public static List<PromotionWidgetCreator> findPromotionWidgetCreators(final String pluginId, final String paramKey) {

        List<PromotionWidgetCreator> promoVisuals = new ArrayList<>();
        List<IConfigurationElement> allConfigElements = getConfigElements(pluginId, PROMOTION_EXTENDER);
        allConfigElements.stream()
                .filter(configurationElement -> WIDGET_ELEMENT_NAME.equals(configurationElement.getName()))
                .forEach(configElement -> {
                    PromotionWidgetCreator promoVisualStuff = instantiatePromotionWidgetCreator(configElement);
                    if (promoVisualStuff != null && promoVisualStuff.isValid(paramKey)) {
                        promoVisuals.add(promoVisualStuff);
                    }
                });


        return promoVisuals;
    }

    private static PromotionWidgetCreator instantiatePromotionWidgetCreator(final IConfigurationElement configElement) {
        try {
            return (PromotionWidgetCreator) configElement.createExecutableExtension(CLASS_NAME_ATTRIBUTE);
        } catch (Exception e) {
            LOG.error("Error creating executable extension.", e);
        }
        return null;

    }

    private static MessageReader instantiateMessaging(final IConfigurationElement configElement) {
        try {
            return (MessageReader) configElement.createExecutableExtension(CLASS_NAME_ATTRIBUTE);
        } catch (Exception e) {
            LOG.error("Error instantiating messaging.", e);
        }
        return null;
    }
    private static TestIdUtil instantiateWigetTestUtil(final IConfigurationElement configElement) {
        try {
            return (TestIdUtil) configElement.createExecutableExtension(CLASS_NAME_ATTRIBUTE);
        } catch (Exception e) {
            LOG.error("Error instantiating WidgetTestUtil.", e);
        }
        return null;
    }


    private static List<IConfigurationElement> getConfigElements(final String pluginId, final String type) {
        List<IConfigurationElement> allConfigElements = new ArrayList<>();
        IExtensionPoint extensionPoint = Platform.getExtensionRegistry().
                getExtensionPoint(pluginId, type);
        if (extensionPoint != null) {
            IExtension[] extensions = extensionPoint.getExtensions();
            for (IExtension extension : extensions) {
                allConfigElements.addAll(Arrays.asList(extension.getConfigurationElements()));
            }
        }
        return allConfigElements;
    }

    private static Comparator<IConfigurationElement> elementComparator() {
        return (section1, section2) -> {
            String pos1String = section1.getAttribute(POSITION_ATTRIBUTE);
            String pos2String = section2.getAttribute(POSITION_ATTRIBUTE);

            if (pos1String == null || pos2String == null) {
                LOG.info("Could not compare ");
                return 0;
            } else {
                Double section1Position = Double.valueOf(pos1String);
                Double section2Position = Double.valueOf(pos2String);
                return section1Position.compareTo(section2Position);
            }
        };
    }

    private static DialogExtension instantiateDialogElement(final IConfigurationElement configElement) {
        DialogExtension dialogExtension = null;

        try {
         dialogExtension = (DialogExtension) configElement.createExecutableExtension(CLASS_NAME_ATTRIBUTE);
        } catch (Exception e) {
            LOG.error("Error instantiating section.", e);
        }
        return dialogExtension;
    }

    private static void instantiateSection(final FormPage formPage, final AbstractCmClientFormEditor editor,
                                           final List<AbstractCmClientFormSectionPart> extendedParts, final IConfigurationElement configElement) {
        try {
            EpSectionCreator sectionExtender =
                    (EpSectionCreator) configElement.createExecutableExtension(CLASS_NAME_ATTRIBUTE);
            AbstractCmClientFormSectionPart part =
                    sectionExtender.instantiateSection(formPage, editor);
            extendedParts.add(part);
        } catch (Exception e) {
            LOG.error("Error instantiating section.", e);
        }
    }

    private static void instantiatePage(final AbstractCmClientFormEditor editor, final List<AbstractCmClientEditorPage> extendedPages,
                                        final IConfigurationElement configElement) {
        try {
            EpPageCreator pageExtender = (EpPageCreator) configElement.createExecutableExtension(CLASS_NAME_ATTRIBUTE);
            AbstractCmClientEditorPage page =
                    pageExtender.instantiatePage(editor);

            extendedPages.add(page);

        } catch (Exception e) {
            LOG.error("Error instantiating page.", e);
        }
    }

    private static void instantiateTable(final List<EPTableColumnCreator> tableExtenders, final IConfigurationElement configElement) {
        try {
            EPTableColumnCreator tableExtender = (EPTableColumnCreator) configElement.createExecutableExtension(CLASS_NAME_ATTRIBUTE);
            tableExtenders.add(tableExtender);
        } catch (Exception e) {
            LOG.error("Error instantiating table.", e);
        }
    }

    /**
     * Find determiners.
     *
     * @param pluginId the plugin id.
     * @param containerClassName the container id.
     * @return list of determiners.
     */
    public static Map<String, Object> findDeterminers(final String pluginId, final String containerClassName) {

        Map<String, Object> allDeterminers = new HashedMap();
        List<IConfigurationElement> allConfigElements = getConfigElements(pluginId, STATE_POLICY_EXTENSION_ID);
        allConfigElements.stream()
                .filter(configurationElement -> DETERMINERS_ELEMENT_NAME.equals(configurationElement.getName())
                        && configurationElement.getAttribute("policyToApplyTo").equals(containerClassName))
                .forEach(element -> allDeterminers.put(element.getAttribute("containerName"), instantiateDeterminers(element)));
        return allDeterminers;
    }

    private static Object instantiateDeterminers(final IConfigurationElement configElement) {
        try {
            return configElement.createExecutableExtension(CLASS_NAME_ATTRIBUTE);
        } catch (Exception e) {
            LOG.error("Error instantiating determiners.", e);
        }
        return null;
    }
    
    /**
     * Gets the EpModelCreator for any class, if any.  If there is more than one then the first one found is returned
     * and an error is logged and an exception thrown.
     *
     * @param <T> the type of class for the model creator
     * @param klass the class to get the creator for
     * @return EpModelCreator for any class
     */
    public static <T> EpModelCreator<T> getModelCreator(final Class<T> klass) {

        List<IConfigurationElement> allConfigElements = getConfigElements(CORE_PLUGIN_ID, MODEL_EXTENDER);

        List<EpModelCreator<T>> baDtoCreators = new ArrayList<>();
        String name = klass.getSimpleName();

        allConfigElements.stream()
                .filter(configurationElement -> name.equals(configurationElement.getName()))
                .forEach(configElement -> baDtoCreators.add(instantiateEpModelCreator(configElement)));

        if (baDtoCreators.size() > 1) {
            StringBuilder errorText = new StringBuilder("There are more than one implementation of EpModelCreator<BaseAmountDTO>:\n");
            baDtoCreators
                .forEach(creator -> errorText.append(creator.getClass().getName()).append('\n'));
            LOG.error(errorText);
            throw new EpUiException(errorText.toString(), null);
        }

        return baDtoCreators.isEmpty() ? null : baDtoCreators.iterator().next();

    }
    
    private static <T> EpModelCreator<T> instantiateEpModelCreator(final IConfigurationElement configElement) {
        EpModelCreator<T> baDtoCreator;
        try {
            baDtoCreator = (EpModelCreator<T>) configElement.createExecutableExtension(CLASS_NAME_ATTRIBUTE);
        } catch (Exception e) {
            String errorText = "Error creating EpModelCreator extension.";
            LOG.error(errorText, e);
            throw new EpUiException(errorText, e);
        }

        return baDtoCreator;
    }

}
