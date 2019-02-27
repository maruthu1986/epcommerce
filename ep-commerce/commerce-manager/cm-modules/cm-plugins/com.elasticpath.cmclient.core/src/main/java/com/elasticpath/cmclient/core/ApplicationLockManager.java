/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.eclipse.jface.window.Window;
import org.eclipse.rap.rwt.internal.lifecycle.LifeCycleUtil;
import org.eclipse.rap.rwt.service.ServerPushSession;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.core.ui.UnlockDialog;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Manages last access to ui threads, for locking purposes.
 */
public class ApplicationLockManager {
	private static final Logger LOG = Logger.getLogger(ApplicationLockManager.class);

	private static final long SECONDS_IN_MS = 1000L;
	private static final long MINUTES_IN_SECONDS = 60L;
	private static final int DEFAULT_IDLE_IN_MINUTES = 15;
	private static final long DAEMON_WAIT_TIME = 5 * SECONDS_IN_MS;
	private static final String IDLE_SETTING_DEFINITION = "COMMERCE/APPSPECIFIC/RCP/idleTimeForLock";


	private Map<String, SessionHolder> sessions = new ConcurrentHashMap<>();
	private SettingsReader settingsReader;
	private Timer timer;
	private boolean isStarted;

	/**
	 * constructor.
	 */
	protected ApplicationLockManager() {
		initializeSettingsReader();
	}


	/**
	 * constructor.
	 * @param shouldInitSettingsReader whether to initialize the settings reader.
	 */
	protected  ApplicationLockManager(final Boolean shouldInitSettingsReader) {
		if (shouldInitSettingsReader) {
			initializeSettingsReader();
		}

	}


	private void initializeSettingsReader() {
		settingsReader = ServiceLocator.getService(ContextIdNames.CACHED_SETTINGS_READER);
	}

	/**
	 * Starts the session Expiry Daemon.
	 */
	public void start() {
		if (!isStarted) {
			doStart();
		}


	}

	private void doStart() {
		LOG.info("Starting ApplicationLockManager Daemon TimerTask, idleTimeout = " + getIdleTimeout() + "ms");
		TimerTask daemon = new TimerTask() {

			public void run() {
				List<String> sessionIdsToRemove = new ArrayList<>();
				// For all of the registered sessionHolders find any that are expired.
				for (Map.Entry<String, SessionHolder> sessionEntry : sessions.entrySet()) {
					UISession session = sessionEntry.getValue().getUiSession();
					long idleTime = System.currentTimeMillis() - sessionEntry.getValue().getTimeStamp();
					LOG.trace(session.getId() + " inactive for: " + (idleTime / SECONDS_IN_MS) + "s ");
					//expired timeout found, lock that session.
					if (idleTime > getIdleTimeout()) {
						LOG.debug("Timed out for session " + session.getId());
						final Display display = LifeCycleUtil.getSessionDisplay(session);
						sessionIdsToRemove.add(sessionEntry.getKey());
						display.asyncExec(() -> lockSession(sessionEntry, display));
					}
				}
				removeSessionsFromSessionMap(sessionIdsToRemove);
			}
		};

		timer = new Timer(true);
		timer.scheduleAtFixedRate(daemon, 0, getTimerPeriod());
		isStarted = true;
	}

	/**
	 * Gets the timer period for the daemon.
	 * @return the timer period.
	 */
	protected long getTimerPeriod() {
		return DAEMON_WAIT_TIME;
	}


	/**
	 * Locks the session, and remove it from the map of sessions.
	 * @param sessionEntry the Map Entry.
	 * @param display the display thread to run on.
	 */
	private void lockSession(final Map.Entry<String, SessionHolder> sessionEntry, final Display display) {
		SessionHolder value = sessionEntry.getValue();

		UISession session = value.getUiSession();
		Shell[] shells = display.getShells();
		List<Shell> shellsToReOpen = new ArrayList<>();

		for (Shell shell : shells) {
			if (shell.isVisible() && !shell.isDisposed()) {
				shellsToReOpen.add(shell);
				shell.setVisible(false);
			}

		}

		//push the locking of the display, instead of waiting for async communication from browser.
		ServerPushSession serverPushSession = value.getPushSession();
		if (serverPushSession != null) {
			serverPushSession.stop();
		}
		
		//loop the unlock dialog, 're-opening' each time a login fails.
		UnlockDialog loginDialog;
		do {
			loginDialog = new UnlockDialog(display.getActiveShell());

			if (loginDialog.open() == Window.OK && loginDialog.isAuthenticatedAndAuthorized()) {
				LOG.debug("Logged back in for session " + session.getId());
				registerSession(session);
				for (Shell shell : shellsToReOpen) {
					if (!shell.isDisposed()) {
						shell.setVisible(true);
					}
				}
				break;
			}
		} while (!loginDialog.isAuthenticatedAndAuthorized());

	}

	/**
	 * Gets the Timer.
	 * @return the timer.
	 */
	protected Timer getTimer() {
		return timer;
	}


	/**
	 * Gets the idle timeout from the Setting Service.
	 *
	 * @return the idle timeout
	 */
	protected long getIdleTimeout() {

		SettingValue settingValue = settingsReader.getSettingValue(IDLE_SETTING_DEFINITION);
		int idleMinutes;
		if (settingValue == null) {
			idleMinutes = DEFAULT_IDLE_IN_MINUTES;
			LOG.warn("Could not find idle time, defaulting to 15 minutes");
		} else {
			idleMinutes = settingValue.getIntegerValue();
		}
		return idleMinutes * MINUTES_IN_SECONDS * SECONDS_IN_MS;

	}

	private void removeSessionsFromSessionMap(final List<String> sessionIdsToRemove) {
		for (String sessionId : sessionIdsToRemove) {
			SessionHolder sessionHolder = sessions.get(sessionId);
			if (sessionHolder != null) {
				deregisterListeners(sessionHolder);
			}
			sessions.remove(sessionId);
		}
	}

	/**
	 * Gets the application instance of the Lock Manager.
	 *
	 * @return The Application Lock Manager.
	 */
	public static ApplicationLockManager getInstance() {
		return CmSingletonUtil.getApplicationInstance(ApplicationLockManager.class);
	}

	/**
	 * Registers the session with the Lock Manager.
	 *
	 * @param session The session.
	 */
	void registerSession(final UISession session) {
		SessionHolder sessionHolder;

		//only register listeners once.
		if (sessions.containsKey(session.getId())) {
			sessionHolder = sessions.get(session.getId());
		} else {
			sessionHolder = new SessionHolder(session);
			registerListeners(sessionHolder);

		}
		//add or update sessionHolder in map
		sessions.put(session.getId(), sessionHolder);
	}

	private void registerListeners(final SessionHolder sessionHolder) {
		Display display = LifeCycleUtil.getSessionDisplay(sessionHolder.getUiSession());

		Listener updateTimeListener = new LockManagerUpdateSessionTimestampListener(sessionHolder);

		display.addFilter(SWT.Activate, updateTimeListener);
		display.addFilter(SWT.KeyDown, updateTimeListener);
		display.addFilter(SWT.MouseDown, updateTimeListener);
		display.addFilter(SWT.MouseUp, updateTimeListener);
		display.addFilter(SWT.MouseDoubleClick, updateTimeListener);

		sessionHolder.addDisplayListener(SWT.Activate, updateTimeListener);
		sessionHolder.addDisplayListener(SWT.KeyDown, updateTimeListener);
		sessionHolder.addDisplayListener(SWT.MouseDown, updateTimeListener);
		sessionHolder.addDisplayListener(SWT.MouseUp, updateTimeListener);
		sessionHolder.addDisplayListener(SWT.MouseDoubleClick, updateTimeListener);

		//remove sessions upon logout.
		sessionHolder.getUiSession().addUISessionListener(uiSessionEvent ->
				sessions.remove(uiSessionEvent.getUISession().getId()));

	}

	private void deregisterListeners(final SessionHolder sessionHolder) {
		Map<Integer, Listener> displayListeners = sessionHolder.getDisplayListeners();
		Display display = LifeCycleUtil.getSessionDisplay(sessionHolder.getUiSession());

		for (Map.Entry<Integer, Listener> mapEntry : displayListeners.entrySet()) {
			display.asyncExec(() -> display.removeFilter(mapEntry.getKey(), mapEntry.getValue()));
		}

	}

	/**
	 * Gets the sessions map.
	 * @return the sessions map.
	 */
	protected Map<String, SessionHolder> getSessions() {
		return sessions;
	}

	/**
	 * Sets the sessions map.
	 * @param sessions the sessions map.
	 */
	protected void setSessions(final Map<String, SessionHolder> sessions) {
		this.sessions = sessions;
	}

	/**
	 * Holder class for session related data.
	 */
	protected class SessionHolder {
		private final UISession uiSession;
		private final Map<Integer, Listener> displayListeners = new HashMap<>();
		private ServerPushSession pushSession;
		private Long timeStamp;

		/**
		 * Constructor.
		 *
		 * @param session the UISession.
		 */
		SessionHolder(final UISession session) {
			timeStamp = System.currentTimeMillis();
			this.uiSession = session;
			pushSession = new ServerPushSession();
			pushSession.start();
		}

		/**
		 * Gets the UI session.
		 *
		 * @return the ui session.
		 */
		UISession getUiSession() {
			return uiSession;
		}

		/**
		 * Gets the push session.
		 *
		 * @return the push session.
		 */
		ServerPushSession getPushSession() {
			return pushSession;
		}

		/**
		 * Sets the ServerPushSession.
		 *
		 * @param pushSession the serverPushSession.
		 */
		void setPushSession(final ServerPushSession pushSession) {
			this.pushSession = pushSession;
		}

		/**
		 * Gets the timestamp.
		 *
		 * @return the timestamp.
		 */
		Long getTimeStamp() {
			return timeStamp;
		}

		/**
		 * Sets the timestamp.
		 *
		 * @param timeStamp the timestamp.
		 */
		void setTimeStamp(final Long timeStamp) {
			this.timeStamp = timeStamp;
		}

		/**
		 * Gets the display listeners.
		 *
		 * @return the listeners.
		 */
		Map<Integer, Listener> getDisplayListeners() {
			return displayListeners;
		}

		/**
		 * Adds a Listener.
		 *
		 * @param listenerId              the id.
		 * @param displayListener the Listener.
		 */
		void addDisplayListener(final Integer listenerId, final Listener displayListener) {
			this.displayListeners.put(listenerId, displayListener);
		}
	}

	/**
	 * Listener class to update the session timestamp.
	 */
	private class LockManagerUpdateSessionTimestampListener implements Listener {
		private final SessionHolder sessionHolder;

		/**
		 * constructor.
		 * @param sessionHolder the session holder.
		 */
		LockManagerUpdateSessionTimestampListener(final SessionHolder sessionHolder) {
			this.sessionHolder = sessionHolder;
		}

		@Override
		public void handleEvent(final Event event) {
			sessionHolder.setTimeStamp(System.currentTimeMillis());
		}
	}
}
