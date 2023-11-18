package kinoko.util;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;

import java.nio.charset.Charset;

@Plugin(name = "LoggerLayout", category = Node.CATEGORY)
public final class LoggerLayout extends AbstractStringLayout {
    private LoggerLayout(Charset charset) {
        super(charset);
    }

    @Override
    public String toSerializable(LogEvent event) {
        final String message = event.getMessage().getFormattedMessage();
        final StringBuilder sb = new StringBuilder()
                .append("[")
                .append(event.getLevel())
                .append("] ");
        if (!message.startsWith("[")) {
            sb.append("[")
                    .append(getClassName(event.getLoggerName()))
                    .append("] ");
        }
        return sb.append(message)
                .append(System.lineSeparator())
                .toString();
    }

    private String getClassName(String loggerName) {
        if (loggerName == null) {
            return "";
        }
        int lastDot = loggerName.lastIndexOf('.');
        if (lastDot != -1) {
            return loggerName.substring(lastDot + 1);
        }
        return loggerName;
    }

    @PluginFactory
    public static LoggerLayout createLayout(@PluginAttribute(value = "charset", defaultString = "UTF-8") Charset charset) {
        return new LoggerLayout(charset);
    }
}
