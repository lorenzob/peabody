package net.trz.peabody;

import static net.trz.peabody.FlashbackLoggerFactory.bufferSize;
import static net.trz.peabody.FlashbackLoggerFactory.discardOldEventsThreshold;

import java.util.Iterator;
import java.util.LinkedList;

import org.apache.log4j.spi.LoggingEvent;

class LogEventsBuffer {

	final LinkedList<LoggingEvent> eventList = new BoundedLinkedList<LoggingEvent>();

	private static class BoundedLinkedList<T> extends LinkedList<T> {

		public boolean add(T e) {
			if (size() >= bufferSize) {
				removeFirst();
			}
			return super.add(e);
		}
	}

	void clearOldEvents() {
		if (discardOldEventsThreshold > 0 && !eventList.isEmpty()) {
			long now = System.currentTimeMillis();
			for (Iterator iterator = eventList.iterator(); iterator.hasNext();) {
				LoggingEvent event = (LoggingEvent) iterator.next();
				long elapsed = now - event.getTimeStamp();
				if (elapsed > discardOldEventsThreshold) {
					iterator.remove();
				} else {
					break;
				}
			}
		}
	}

	public void add(LoggingEvent loggingEvent) {
		eventList.add(loggingEvent);
	}

	public boolean isEmpty() {
		clearOldEvents();
		return eventList.isEmpty();
	}

	public void clear() {
		eventList.clear();
	}

}
