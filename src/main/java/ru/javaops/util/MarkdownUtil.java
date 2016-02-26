package ru.javaops.util;

import org.pegdown.PegDownProcessor;
import org.springframework.core.io.Resource;

/**
 * @author grigory.kislin
 *         Date: 04-Aug-15.
 */
public class MarkdownUtil {
    private final static ThreadLocal<PegDownProcessor> PROCESSOR = new ThreadLocal<PegDownProcessor>() {

        @Override
        protected PegDownProcessor initialValue() {
            return new PegDownProcessor();
        }
    };

    public static String toHtml(Resource resource) {
        return PROCESSOR.get().markdownToHtml(Util.toString(resource));
    }
}
