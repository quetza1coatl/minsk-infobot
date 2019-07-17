package com.quetzalcoatl.minskinfobot.handlers;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.ExternalResource;
import org.junit.rules.Stopwatch;
import org.junit.runner.Description;
import org.slf4j.Logger;
import java.util.concurrent.TimeUnit;
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

}
