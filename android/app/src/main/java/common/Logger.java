package common;

import android.util.Log;

public class Logger {
	Class<?> clazz;
	public static Logger getLogger(Class<?> clazz) {
		Logger logger = new Logger();
		logger.clazz = clazz;
		return logger;
	}
	
	public void v(String msg) {
		Log.v(clazz.getName(), msg);
	}
	
	public void e(String msg) {
		Log.e(clazz.getName(), msg);
	}
	
	public void d(String msg) {
		Log.d(clazz.getName(), msg);
	}
}
