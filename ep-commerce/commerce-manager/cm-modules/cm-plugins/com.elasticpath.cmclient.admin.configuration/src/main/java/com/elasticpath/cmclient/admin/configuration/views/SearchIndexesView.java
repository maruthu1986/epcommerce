/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin.configuration.views;

import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.elasticpath.cmclient.core.util.DateTimeUtilFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.rap.rwt.service.ServerPushSession;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableItem;

import com.elasticpath.cmclient.admin.configuration.AdminConfigurationMessages;
import com.elasticpath.cmclient.admin.configuration.AdminConfigurationPlugin;
import com.elasticpath.cmclient.admin.configuration.actions.RebuildIndexAction;
import com.elasticpath.cmclient.admin.configuration.models.IndexBuildStatusSelector;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.views.AbstractListView;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.search.IndexBuildStatus;
import com.elasticpath.domain.search.IndexNotification;
import com.elasticpath.domain.search.IndexStatus;
import com.elasticpath.domain.search.UpdateType;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.search.IndexBuildStatusService;
import com.elasticpath.service.search.IndexNotificationService;
import com.elasticpath.service.search.IndexType;

/**
 * View to show and allow the manipulation of the available SearchIndexes.
 */
public class SearchIndexesView extends AbstractListView implements IndexBuildStatusSelector {

	/**
	 * The amount of time in milliseconds that is used to wait between two refresh actions.
	 */
	private static final int PERIOD = 10000;

	/** The view ID. */
	public static final String VIEW_ID = "com.elasticpath.cmclient.admin.configuration.views.SearchIndexesView"; //$NON-NLS-1$

	private static final String SEARCH_INDEX_TABLE = "Search Index"; //$NON-NLS-1$

	private static final int NAME_COLUMN_WIDTH = 200;

	private static final int LAST_BUILD_TIME_COLUMN_WIDTH = 200;

	private static final int STATUS_COLUMN_WIDTH = 200;

	private static final int PROGRESS_COLUMN_WIDTH = 75;

	private static final int REMAINING_TIME_COLUMN_WIDTH = 145;

	private static final int PERCENTAGE_DONE_COLUMN = 3;

	private static final int COMPLETION_TIME_COLUMN = 4;


	private Action rebuildIndexAction;

	private final IndexBuildStatusService indexBuildStatusService;

	private final IndexNotificationService indexNotificationService;

	private final Timer timer;

	private final TimeService timeService;

	private final ServerPushSession pushSession = new ServerPushSession();

	/**
	 * The constructor.
	 */
	public SearchIndexesView() {
		super(false, SEARCH_INDEX_TABLE);
		this.indexBuildStatusService = ServiceLocator.getService("indexBuildStatusService"); //$NON-NLS-1$
		this.indexNotificationService = ServiceLocator.getService(ContextIdNames.INDEX_NOTIFICATION_SERVICE);
		this.timer = new Timer();
		this.timeService = ServiceLocator.getService(ContextIdNames.TIME_SERVICE);
	}

	@Override
	protected IndexBuildStatus[] getViewInput() {
		final List<IndexBuildStatus> indexBuildStatusList = indexBuildStatusService.getIndexBuildStatuses();

		return indexBuildStatusList.toArray(new IndexBuildStatus[indexBuildStatusList.size()]);
	}


	@Override
	protected void initializeTable(final IEpTableViewer epTableViewer) {
		epTableViewer.addTableColumn(AdminConfigurationMessages.get().IndexName, NAME_COLUMN_WIDTH);
		epTableViewer.addTableColumn(AdminConfigurationMessages.get().LastBuildTime, LAST_BUILD_TIME_COLUMN_WIDTH);
		epTableViewer.addTableColumn(AdminConfigurationMessages.get().Status, STATUS_COLUMN_WIDTH);
		epTableViewer.addTableColumn(AdminConfigurationMessages.get().progressColumn, PROGRESS_COLUMN_WIDTH, PERCENTAGE_DONE_COLUMN);
		epTableViewer.addTableColumn(AdminConfigurationMessages.get().remainingTimeColumn, REMAINING_TIME_COLUMN_WIDTH, COMPLETION_TIME_COLUMN);

		epTableViewer.getSwtTableViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				final IndexBuildStatus selectedIndexBuildStatus = getSelectedIndexBuildStatus();
				final boolean enabled = selectedIndexBuildStatus != null
					&& hasBuildBeenNotScheduled(selectedIndexBuildStatus.getIndexType());
				rebuildIndexAction.setEnabled(enabled);
			}

			private boolean hasBuildBeenNotScheduled(final IndexType indexType) {
				return indexNotificationService.findByIndexAndUpdateType(indexType, UpdateType.REBUILD).isEmpty();
			}
		});

		//Start the automatic refreshing of the table with a 0 second delay, and
		//refresh the table every 10 seconds.
		startTimedTableRefresh();
	}

	/**
	 * Create a timer and a timed task, which will refresh the
	 * search index build table and update the results of the build
	 * status'.
	 */
	private void startTimedTableRefresh() {
		final Display disp = Display.getCurrent();
		final TimerTask task = new TimerTask() {
			@Override
			public void run() {
				disp.syncExec(() -> refreshViewerInput());
			}
		};
		timer.scheduleAtFixedRate(task, 0, SearchIndexesView.PERIOD);
		pushSession.start();
	}

	/**
	 * Updates the selection after refresh so that it does not get away.
	 */
	private void updateSelection(final IndexBuildStatus currentStatus) {
		if (currentStatus != null) {
			final TableViewer viewer = getViewer();
			final TableItem[] items = viewer.getTable().getItems();
				for (TableItem item : items) {
					final IndexBuildStatus itemData = (IndexBuildStatus) item.getData();
					if (currentStatus.getIndexType().equals(itemData.getIndexType())) {
						viewer.getTable().setSelection(item);
						break;
					}
				}
		}
	}

	@Override
	public void dispose() {
		timer.cancel();
		pushSession.stop();
		super.dispose();
	}

	/**
	 * Gets the currently-selected IndexBuildStatus.
	 *
	 * @return the currently-selected IndexBuildStatus
	 */
	@Override
	public IndexBuildStatus getSelectedIndexBuildStatus() {
		final IStructuredSelection selection = (IStructuredSelection) getViewer().getSelection();
		if (selection.isEmpty()) {
			return null;
		}
		return (IndexBuildStatus) selection.getFirstElement();
	}

	@Override
	public void refresh() {
		this.refreshViewerInput();
	}

	/**
	 * Overrides the refresh function because the selection should be preserved.
	 */
	@Override
	public void refreshViewerInput() {
		if (!getViewer().getControl().isDisposed()) {
			final IndexBuildStatus currentStatus = (IndexBuildStatus) ((IStructuredSelection) getViewer().getSelection()).getFirstElement();
			final IndexBuildStatus[] input = getViewInput();
			getViewer().setInput(input);
			updateSelection(currentStatus);
		}
	}

	@Override
	protected String getPluginId() {
		return AdminConfigurationPlugin.PLUGIN_ID;
	}

	@Override
	protected void initializeViewToolbar() {
		rebuildIndexAction = new RebuildIndexAction(getSite().getShell(), this);
		rebuildIndexAction.setEnabled(false);

		addDoubleClickAction(rebuildIndexAction);

		final Separator searchIndexesActionGroup = new Separator("searchIndexesActionGroup"); //$NON-NLS-1$
		getToolbarManager().add(searchIndexesActionGroup);
		getToolbarManager().appendToGroup(searchIndexesActionGroup.getGroupName(), createActionContributionItem(rebuildIndexAction));
	}

	/*
	 * Creates an instance of ActionContributionItem
	 */
	private static ActionContributionItem createActionContributionItem(final Action action) {
		final ActionContributionItem actionContributionItem = new ActionContributionItem(action);
		actionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		return actionContributionItem;
	}

	@Override
	protected ITableLabelProvider getViewLabelProvider() {
		return new SearchIndexesViewLabelProvider();
	}

	/**
	 * A label provider for current view.
	 */
	private class SearchIndexesViewLabelProvider extends LabelProvider implements ITableLabelProvider {

		private static final float MAX_PERCENT = 100f;

		private static final long MILLISECONDS_IN_DAY = 86400000L;

		private static final long MILLISECONDS_IN_HOUR = 3600000L;

		private static final long MILLISECONDS_IN_MINUTE = 60000L;

		private static final String DATE_FORMAT_DAYS = "d' days 'H' hours 'm' min'"; //$NON-NLS-1$

		private static final String DATE_FORMAT_HOURS = "H' hours 'm' min'"; //$NON-NLS-1$

		private static final String DATE_FORMAT_MINUTES = "m' min 's' seconds'"; //$NON-NLS-1$

		private static final String DATE_FORMAT_SECONDS = "s' seconds'"; //$NON-NLS-1$

		private static final String NA_STRING = "---"; //$NON-NLS-1$

		public static final int INDEX_NAME = 0;

		private static final int INDEX_LAST_BUILD_TIME = 1;

		private static final int INDEX_STATUS = 2;

		/**
		 * Get the image to put in each column.
		 *
		 * @param element the row object
		 * @param columnIndex the column index
		 * @return the Image to put in the column
		 */
		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			return null;
		}

		/**
		 * Get the text to put in each column.
		 *
		 * @param element the row object
		 * @param columnIndex the column index
		 * @return the String to put in the column
		 */
		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			final IndexBuildStatus indexBuildStatus = (IndexBuildStatus) element;
			final IndexStatus indexStatus = indexBuildStatus.getIndexStatus();
			switch (columnIndex) {
			case INDEX_NAME:
				return AdminConfigurationMessages.get().getMessage(indexBuildStatus.getIndexType().getIndexName());
			case INDEX_LAST_BUILD_TIME:
				return DateTimeUtilFactory.getDateUtil().formatAsDateTime(indexBuildStatus.getLastBuildDate(), NA_STRING);
			case INDEX_STATUS:
				final Collection<IndexNotification> notifications = indexNotificationService.
					findByIndexAndUpdateType(indexBuildStatus.getIndexType(), UpdateType.REBUILD);

				if (IndexStatus.COMPLETE.equals(indexStatus) && !notifications.isEmpty()) {
					return AdminConfigurationMessages.get().REBUILD_SCHEDULED;
				}

				return AdminConfigurationMessages.get().getMessage(indexStatus.toString());
			case PERCENTAGE_DONE_COLUMN :
					if (indexBuildStatus.getTotalRecords() <= 0 || indexBuildStatus.getProcessedRecords() <= 0
							|| IndexStatus.COMPLETE.equals(indexStatus)) {
						return NA_STRING;
					}
					final float processed = (float) indexBuildStatus.getProcessedRecords();
					final float total = (float) indexBuildStatus.getTotalRecords();
					return Math.round((processed / total) * MAX_PERCENT) + "%"; //$NON-NLS-1$
				case COMPLETION_TIME_COLUMN :
					return formatMillisToString(calculateTimeRemaining(indexBuildStatus));
			default:
				return StringUtils.EMPTY;
			}
		}

		private String formatMillisToString(final long timeInMillis) {
			if (timeInMillis <= 0) {
				return NA_STRING;
			}

			String dateFormat = DATE_FORMAT_SECONDS;
			if (timeInMillis > MILLISECONDS_IN_DAY) {
				dateFormat = DATE_FORMAT_DAYS;
			} else if (timeInMillis > MILLISECONDS_IN_HOUR) {
				dateFormat = DATE_FORMAT_HOURS;
			} else if (timeInMillis > MILLISECONDS_IN_MINUTE) {
				dateFormat = DATE_FORMAT_MINUTES;
			}

			return DurationFormatUtils.formatDuration(timeInMillis, dateFormat);
		}

		private long calculateTimeRemaining(final IndexBuildStatus indexStatus) {
			if (indexStatus.getOperationStartDate() == null
					|| indexStatus.getTotalRecords() <= 0
					|| indexStatus.getProcessedRecords() <= 0) {
				return 0;
			}

			final long elapsedTimeMillis = timeService.getCurrentTime().getTime() - indexStatus.getOperationStartDate().getTime();
			final long timePerRecord = (elapsedTimeMillis / indexStatus.getProcessedRecords());
			final long recordsRemaining = (indexStatus.getTotalRecords() - indexStatus.getProcessedRecords());

			return (timePerRecord * recordsRemaining);
		}
	}

	@Override
	protected String getPartId() {
		return VIEW_ID;
	}
}
