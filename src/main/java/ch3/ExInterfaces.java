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

    public static Runnable unchecked(ExRunnable1<?> runnable, Consumer<Exception> handler) {
        return () -> {
            try {
                runnable.run();
            } catch (Exception e) {
                handler.accept(e);
            }
        };
    }

    @FunctionalInterface
    public interface ExRunnable1<E extends Exception> {
        public void run() throws E;
    }

}