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
package ch6;

import org.junit.AfterClass;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(Enclosed.class)
public class AtomicReferenceTest {

    public static class AtomicIntegerTest {

        private final static ExecutorService EXE = Executors.newFixedThreadPool(16);

        private final static Logger LOG = Logger.getLogger(AtomicIntegerTest.class.getName());

        @AfterClass
        public static void shutdown() {
            EXE.shutdown();
        }

        @Test
        public void updateConcurrently() throws ExecutionException, InterruptedException {
            final AtomicInteger largest = new AtomicInteger();
            final List<CompletableFuture<Void>> futures = new CopyOnWriteArrayList<>();
            OptionalInt max = IntStream.generate(() -> new Random().nextInt(64))
                    .limit(64)
                    .parallel()
                    .mapToObj(v -> new FutureHolder(v, largest))
                    .peek(h -> futures.add(h.getFuture()))
                    .peek(h -> LOG.info(h.toString()))
                    .mapToInt(FutureHolder::getValue)
                    .max();
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).get();
            assertThat(max.isPresent(), is(true));
            max.ifPresent(m -> assertThat(largest.get(), is(m)));
        }

        class FutureHolder {
            final int value;
            final CompletableFuture<Void> future;

            FutureHolder(final int value, final AtomicInteger largest) {
                this.value = value;
                future = CompletableFuture.runAsync(() -> largest.accumulateAndGet(value, Math::max), EXE);
            }

            public int getValue() {
                return value;
            }

            public CompletableFuture<Void> getFuture() {
                return future;
            }

            @Override
            public String toString() {
                return "Value[" + value + "]";
            }
        }
    }
}
