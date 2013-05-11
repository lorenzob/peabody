package net.trz.peabody;

import java.lang.reflect.Field;

import org.apache.log4j.*;
import org.apache.log4j.spi.*;

public class FlashbackLoggerFactory implements LoggerFactory {

	static Level triggerLevel = Level.ERROR;
	static boolean keepLoggedEvents = true;
	static int bufferSize = 100;
	static String[] loggerList;
	static int discardOldEventsThreshold = 10 * 1000;
	static boolean collectLocationInformation = true;

	static {
		try {
			init();
		} catch (Throwable e) {
			throw new RuntimeException("FlashbackLogger init failed", e);
		}
	}

	private static void init() throws NoSuchFieldException, IllegalAccessException {

		Field field = getPrivateField(LogManager.class, "repositorySelector");
		DefaultRepositorySelector repositorySelector = (DefaultRepositorySelector) field.get(null);
		
		LoggerRepository loggerRepository = repositorySelector.getLoggerRepository();
		Field field2 = getPrivateField(loggerRepository.getClass(), "defaultFactory");
		field2.set(loggerRepository, new FlashbackLoggerFactory());
	}

	@Override
	public Logger makeNewLoggerInstance(String name) {
		return new FlashbackLogger(name);
	}

	public void setTriggerLevel(String lvl) {
		triggerLevel = Level.toLevel(lvl);
	}

	public void setKeepLoggedEvents(boolean b) {
		keepLoggedEvents = b;
	}

	public static void setCollectLocationInformation(boolean b) {
		collectLocationInformation = b;
	}
	
	public void setBufferSize(int n) {
		bufferSize = n;
	}

	public void setLoggerList(String csvList) {
		loggerList = splitAndTrim(csvList, ",");
	}

	public void setDiscardOldEventsThreshold(int n) {
		discardOldEventsThreshold = n;
	}

	public static String[] splitAndTrim(String s, String c) {
		String regex = "\\s*" + c + "\\s*";
		String[] attributes = s.split(regex);
		return attributes;
	}

	private static Field getPrivateField(Class clazz, String name) throws NoSuchFieldException {
		Field field = clazz.getDeclaredField(name);
		field.setAccessible(true);
		return field;
	}

}
