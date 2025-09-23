package com.pht.utils;

import org.slf4j.Logger;

import java.time.LocalDateTime;

public class LogUtils {

    public static final int LOG_START = 0;
    public static final int LOG_FINISH = 1;

    /**
     * Show log info
     *
     * @param type         (0: log start, 1: log finish)
     * @param method       method's name
     * @param startTime    time in miliseconds
     * @param logClassName true: log with class name, false: log without class name
     */
    public static void logInfo(Logger logger, int type, String method, long startTime, boolean logClassName) {
        String className = logger.getName().substring(logger.getName().lastIndexOf(".") + 1);
        try {
            logger.info((type == LOG_START ? "Starting " : "Finished ")
                    + "execute -------------------- "
                    + (logClassName ? className + "." : "") + method
                    + " ---------------------- "
                    + (type == LOG_FINISH ? " in " + (System.currentTimeMillis() - startTime) + " ms" : ""));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static long logBegin(Logger logger) {
        return logBegin(logger, getExecuteMethodName(2), true);
    }

    public static long logEnd(Logger logger, long startTime) {
        return logEnd(logger, getExecuteMethodName(2), startTime, true);
    }

    public static long logBegin(Logger logger, String method, boolean logClassName) {
        long logTime = System.currentTimeMillis();
        logInfo(logger, LOG_START, method, logTime, logClassName);

        return logTime;
    }

    public static long logEnd(Logger logger, String method, long startTime, boolean logClassName) {
        long logTime = System.currentTimeMillis();
        logInfo(logger, LOG_FINISH, method, startTime, logClassName);

        return logTime;
    }

    /**
     * Get method name
     *
     * @param index getExecuteMethodName has index = 0, each method outside increase 1
     * @return method's name
     */
    public static String getExecuteMethodName(int index) {
        return new Throwable().getStackTrace()[index].getMethodName();
    }

    public static long getExecutionTime(long startTime, long endTime) {
        return endTime - startTime;
    }

    public static long getExecutionTime(long startTime) {
        return getExecutionTime(startTime, System.currentTimeMillis());
    }

    public static long getExecutionTime(LocalDateTime startTime, LocalDateTime endTime) {
        return DateTimeUtils.localDateTimeToMilli(endTime) - DateTimeUtils.localDateTimeToMilli(startTime);
    }

    public static long getExecutionTime(LocalDateTime startTime) {
        return getExecutionTime(startTime, LocalDateTime.now());
    }
}