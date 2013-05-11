import org.apache.log4j.Logger;

public class Prova {

	public static void main(String[] args) throws Exception {

		Logger logger = Logger.getLogger("it.com.apress.Provaclass");
		logger.debug("DEBUG");

		logger.info("INFO 1");
		Logger logger3 = Logger.getLogger("it.zzz.kdv");
		logger3.debug("DEBUG 3");
		
		for (int i = 0; i < 1; i++) {
			logger3.debug("DEBUG 3 bis");
			logger3.info("INFO 2");
		}
		
		//Thread.sleep(500);
		
		Logger logger31 = Logger.getLogger("zzz");
		logger31.debug("DEBUG ZZZ");
		logger3.debug("DEBUG 3 tris");
		aaa(logger3);

		//Thread.sleep(100);
		
		logger3.debug("DEBUG 5");
		Logger logger2 = Logger.getLogger("err");
		logger2.error("ERROR");

	}

	private static void aaa(Logger logger3) {
		logger3.info("INFO 3");
	}


}
