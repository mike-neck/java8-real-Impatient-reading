/*
 * Copyright 2014Shinya Mochida
 * <p>
 * Licensed under the Apache License,Version2.0(the"License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,software
 * Distributed under the License is distributed on an"AS IS"BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch3;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(Enclosed.class)
public class LazyLoggingTest {

    private final static class Logging {

        private static Map<Class<?>, Logging> LOGGERS = new HashMap<>();

        static Logging getLogger(Class<?> klass) {
            return LOGGERS.computeIfAbsent(klass, Logging::new);
        }

        private final Logger logger;

        private final AtomicInteger count = new AtomicInteger(0);

        private final Object lock = new Object();

        private Logging(Class<?> klass) {
            this.logger = Logger.getLogger(klass.getName());
        }

        private void log(Level level, Supplier<String> msg) {
            synchronized (lock) {
                if (logger.isLoggable(level)) {
                    logger.log(level, msg.get());
                    count.incrementAndGet();
                }
            }
        }

        private void log(Supplier<Boolean> condition, Level level, Supplier<String> msg) {
            if (condition.get()) {
                log(level, msg);
            }
        }

        public void severe(Supplier<String> msg) {
            log(Level.SEVERE, msg);
        }

        public void severe(Supplier<Boolean> condition, Supplier<String> msg) {
            log(condition, Level.SEVERE, msg);
        }

        public void info(Supplier<String> msg) {
            log(Level.INFO, msg);
        }

        public void info(Supplier<Boolean> condition, Supplier<String> msg) {
            log(condition, Level.INFO, msg);
        }

        public void config(Supplier<String> msg) {
            log(Level.CONFIG, msg);
        }

        public void config(Supplier<Boolean> condition, Supplier<String> msg) {
            log(condition, Level.CONFIG, msg);
        }

        public void fine(Supplier<String> msg) {
            log(Level.FINE, msg);
        }

        public void fine(Supplier<Boolean> condition, Supplier<String> msg) {
            log(condition, Level.FINE, msg);
        }

        public int getCount() {
            return count.get();
        }
    }

    public static class LoggingTest {

        private Logging logging;

        @Before
        public void setUpLogging() {
            logging = Logging.getLogger(getClass());
        }

        @Test
        public void lazyLogging() {
            logging.severe(() -> "severe");
            logging.config(() -> "config");
            logging.fine(() -> "fine");
            logging.info(() -> "info");
            assertThat(logging.getCount(), is(2));
        }
    }

    public static class LoggingTestWithCondition {

        private Logging logging;

        @Before
        public void setUpLogging() {
            logging = Logging.getLogger(getClass());
        }

        private Supplier<Boolean> getCondition(int num) {
            return () -> num % 2 == 0;
        }

        @Test
        public void lazyLoggingWithCondition() {
            int count = IntStream.iterate(0, pre -> pre + 1)
                    .limit(10)
                    .peek(n -> logging.severe(getCondition(n), () -> "severe[" + n + "]"))
                    .peek(n -> logging.info(getCondition(n), () -> "info[" + n + "]"))
                    .peek(n -> logging.config(getCondition(n), () -> "config[" + n + "]"))
                    .peek(n -> logging.fine(getCondition(n), () -> "fine[" + n + "]"))
                    .map(n -> n % 2 == 0 ? 2 : 0)
                    .sum();
            assertThat(logging.getCount(), is(count));
        }
    }
}
