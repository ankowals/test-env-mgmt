package org.example.environment.framework.utils;

import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.stream.Collectors;

public class LoggerUtils {

    public static String getLoggerSettings(List<String> loggerPrefixesToExclude) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        return context.getLoggerList().stream()
                .filter(logger -> loggerPrefixesToExclude.stream().noneMatch(logger.getName()::startsWith))
                .filter(logger -> logger.getName().startsWith("org.example"))
                .filter(logger -> logger.getLevel() != null)
                .map(logger -> String.format("-Dlogging.level.%s=%s", logger.getName(), logger.getLevel()))
                .collect(Collectors.joining(" "));
    }
}
