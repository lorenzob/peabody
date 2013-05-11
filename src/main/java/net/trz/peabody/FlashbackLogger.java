package net.trz.peabody;

import static net.trz.peabody.FlashbackLoggerFactory.*;

import org.apache.log4j.*;
import org.apache.log4j.spi.LoggingEvent;

public class FlashbackLogger extends Logger {

	private final LoggingEvent START_MARKER = newMarker(">>>>>>>>> FLASHBACK START >>>>>>>>>");
	private final LoggingEvent END_MARKER = newMarker("<<<<<<<<<  FLASHBACK END  <<<<<<<<<");

	private static final String FQCN = FlashbackLogger.class.getName();

	private static final ThreadLocal<LogEventsBuffer> bufferTL = new ThreadLocal<LogEventsBuffer>();

	private final boolean flashbackIsEnabled;

	public FlashbackLogger(String logName) {
		super(logName);
		flashbackIsEnabled = isFlashBackEnabledForThisLogger();
	}

	/**
	 * @return true if message still needs to be logged
	 */
	private boolean enqueueMessage(Object message, Throwable t, Level eventLevel) {

		if (eventLevel.isGreaterOrEqual(triggerLevel)) {
			flushBuffer();
			return true;
		}

		boolean msgIsBelowLoggerLevel = !eventLevel.isGreaterOrEqual(this.getEffectiveLevel());
		if (keepLoggedEvents || msgIsBelowLoggerLevel) {
			if (flashbackIsEnabled) {
				LoggingEvent loggingEvent = new LoggingEvent(FQCN, this, eventLevel, message, t);
				if (collectLocationInformation) {
					loggingEvent.getLocationInformation();
				}
				getBuffer().add(loggingEvent);

				// log here to avoid an extra LoggingEvent creation and LocationInformation collection
				if (!msgIsBelowLoggerLevel) {
					log(message, t, eventLevel);
					return false;
				}
			}
		}
		return !msgIsBelowLoggerLevel;
	}

	private void flushBuffer() {

		LogEventsBuffer buffer = getBuffer();
		if (buffer.isEmpty()) {
			// logFlashbackMarker(">>>>>>>>> FLASHBACK EMPTY <<<<<<<<<");
			return;
		}

		callAppenders(START_MARKER);
		for (LoggingEvent event : buffer.eventList) {
			callAppenders(event);
		}
		callAppenders(END_MARKER);
		buffer.clear();
	}

	private void processLogEvent(Object message, Throwable t, Level eventLevel) {
		if (enqueueMessage(message, t, eventLevel)) {
			log(message, t, eventLevel);
		}
	}

	private void log(Object message, Throwable t, Level eventLevel) {
		if (repository.isDisabled(eventLevel.toInt()))
			return;
		if (eventLevel.isGreaterOrEqual(this.getEffectiveLevel()))
			forcedLog(FQCN, eventLevel, message, t);
	}

	private LogEventsBuffer getBuffer() {
		LogEventsBuffer list = bufferTL.get();
		if (list == null) {
			bufferTL.set(list = new LogEventsBuffer());
		}
		return list;
	}

	private boolean isFlashBackEnabledForThisLogger() {
		if (loggerList == null) {
			return true;
		}
		for (int i = 0; i < loggerList.length; i++) {
			String loggerNamePrefix = loggerList[i];
			if (name.startsWith(loggerNamePrefix)) {
				return true;
			}
		}
		return false;
	}

	// TODO: forse non serve
	// overridden to use our FQCN instead of the fqcn argument
//	@Override
//	protected void forcedLog(String fqcn, Priority eventLevel, Object message, Throwable t) {
//		callAppenders(new LoggingEvent(FQCN, this, eventLevel, message, t));
//	}

	private LoggingEvent newMarker(String msg) {
		return new LoggingEvent(FQCN, this, triggerLevel, msg, null);
	}

	public void trace(Object message) {
		processLogEvent(message, null, Level.TRACE);
	}

	public void trace(Object message, Throwable t) {
		processLogEvent(message, t, Level.TRACE);
	}

	public void debug(Object message) {
		processLogEvent(message, null, Level.DEBUG);
	}

	public void debug(Object message, Throwable t) {
		processLogEvent(message, t, Level.DEBUG);
	}

	public void info(Object message) {
		processLogEvent(message, null, Level.INFO);
	}

	public void info(Object message, Throwable t) {
		processLogEvent(message, t, Level.INFO);
	}

	public void warn(Object message) {
		processLogEvent(message, null, Level.WARN);
	}

	public void warn(Object message, Throwable t) {
		processLogEvent(message, t, Level.WARN);
	}

	public void error(Object message) {
		processLogEvent(message, null, Level.ERROR);
	}

	public void error(Object message, Throwable t) {
		processLogEvent(message, t, Level.ERROR);
	}

	public void fatal(Object message) {
		processLogEvent(message, null, Level.FATAL);
	}

	public void fatal(Object message, Throwable t) {
		processLogEvent(message, t, Level.FATAL);
	}

}
