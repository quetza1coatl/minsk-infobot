package com.quetzalcoatl.telebot.handlers;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.rules.Stopwatch;
import org.junit.runner.Description;
import org.slf4j.Logger;

import java.util.concurrent.TimeUnit;

import static com.quetzalcoatl.telebot.HandlersTestData.getAnotherMessages;
import static com.quetzalcoatl.telebot.HandlersTestData.getTargetMessages;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;

public abstract class AbstractHandlerTest {

    protected static Handler handler;
    protected static String key;
    private static final Logger log = getLogger("handler tests");
    private static StringBuilder results = new StringBuilder();

    @Rule
    public Stopwatch stopwatch = new Stopwatch() {
        @Override
        protected void finished(long nanos, Description description) {
            String result = String.format("\n%-35s %-15s %15d", description.getTestClass().getSimpleName(), description.getMethodName(), TimeUnit.NANOSECONDS.toMillis(nanos));
            results.append(result).append('\n');
        }
    };

    @ClassRule
    public static ExternalResource summary = new ExternalResource() {
        @Override
        protected void before() throws Throwable {
            results.setLength(0);
        }

        @Override
        protected void after() {
            log.info("\n-----------------------------------------------------------------------------" +
                    "\nClass                               Test                         Duration, ms" +
                    "\n-----------------------------------------------------------------------------\n" +
                    results +
                    "-----------------------------------------------------------------------------\n");
        }
    };

    @Test
    public void testIsSuitable() throws Exception {
        getTargetMessages(key).forEach(e -> assertTrue(
                "getTargetMessages:: invalid element: '" + e + "'",
                handler.isSuitable(e))
        );

        getAnotherMessages(key).forEach((s, strings) -> strings
                .forEach(e -> assertFalse(
                        "getAnotherMessages:: invalid element: '" + e + "'",
                        handler.isSuitable(e))));
    }

    @Test
    public void testGetText() {
        //TODO
    }

}
