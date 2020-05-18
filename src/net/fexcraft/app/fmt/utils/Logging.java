package net.fexcraft.app.fmt.utils;

import java.util.zip.Deflater;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.CompositeTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.SizeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.TimeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;

public class Logging {

	private static final Logger LOGGER_MAIN;
	static{
		String name = "FMT";
		LoggerContext context = (LoggerContext)LogManager.getContext(false);
		org.apache.logging.log4j.core.config.Configuration conf = context.getConfiguration();
		PatternLayout layout = PatternLayout.newBuilder().withPattern("%d{dd MMM yyyy HH:mm:ss(SSS)} [%t" + /*/%c*/ "]: %m%n").withConfiguration(conf).build();
		RollingFileAppender appender = RollingFileAppender.createAppender("./logs/fmt.log", "./logs/fmt-%d{yyyy-MM-dd}.%i.log.gz",
			"true", name, "true", "8192", "true",
			CompositeTriggeringPolicy.createPolicy(SizeBasedTriggeringPolicy.createPolicy("1 M"), TimeBasedTriggeringPolicy.createPolicy("1", null)),
			DefaultRolloverStrategy.createStrategy("1024", "1", "max", Deflater.NO_COMPRESSION + "", conf), layout, (Filter)null, "true", "true", "test", conf);
		appender.start();
		conf.addAppender(appender);
	    AppenderRef ref = AppenderRef.createAppenderRef(name, null, null);
	    LoggerConfig logcfg = LoggerConfig.createLogger("true", Level.INFO, name, "true", new AppenderRef[] { ref }, null, conf, null);
	    conf.addLogger(name, logcfg);
	    Logger logger = context.getLogger(name);
	    logger.addAppender(appender);
		context.updateLoggers();
		LOGGER_MAIN = context.getLogger(name);
	}
	
	public static void log(Object... log){
		for(Object obj : log) log(obj);
	}
	
	public static void log(Object obj){
		LOGGER_MAIN.log(Level.INFO, obj);
	}
	
	public static void log(Level lvl, Object... log){
		for(Object obj : log) log(lvl, obj);
	}
	
	public static void log(Level lvl, Object obj){
		LOGGER_MAIN.log(lvl, obj);
	}
	
	public static void log(Exception e){
		LOGGER_MAIN.log(Level.INFO, "ERROR: " + e.getLocalizedMessage());
		//LOGGER_MAIN.log(Level.INFO, "ERROR: " + e.getMessage());
		for(StackTraceElement trace : e.getStackTrace()){
			LOGGER_MAIN.log(Level.ERROR, "\t" + trace);
		}
	}

}
