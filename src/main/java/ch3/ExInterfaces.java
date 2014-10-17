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

import java.util.function.Consumer;

public final class ExInterfaces {

    private ExInterfaces() {}

    private static Runnable toRunnable(ExRunnable1<? extends Exception> runnable, Consumer<Exception> handler) {
        return () -> {
            try {
                runnable.run();
            } catch (Exception e) {
                handler.accept(e);
            }
        };
    }

    public static Runnable unchecked(ExRunnable1<? extends Exception> runnable, Consumer<Exception> handler) {
        return toRunnable(runnable, handler);
    }

    @FunctionalInterface
    public interface ExRunnable1<E extends Exception> {
        public void run() throws E;
    }

    public static <T> Consumer<T> unchecked(ExConsumer<T, ? extends Exception> consumer, Consumer<Exception> handler) {
        return (T t) -> {
            try {
                consumer.accept(t);
            } catch (Exception e) {
                handler.accept(e);
            }
        };
    }

    @FunctionalInterface
    public interface ExConsumer<T, E extends Exception> {
        public void accept(T t) throws E;

        default <F extends Exception> ExConsumer<T, Exception> andThen(ExConsumer<T, F> next) {
            return (T t) -> {
                accept(t);
                next.accept(t);
            };
        }
    }
}
