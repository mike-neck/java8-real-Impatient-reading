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

import org.junit.AfterClass;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import static ch3.ExInterfaces.unchecked;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(Enclosed.class)
public class LambdaExceptionHandlingTest {

    public static class RunnableTest {

        private static final ExecutorService EXEC = Executors.newFixedThreadPool(2);

        @AfterClass
        public static void shutDownService() {
            EXEC.shutdown();
        }

        @Test
        public void uncheckedException1WithoutException() throws InterruptedException {
            final BlockingQueue<String> queue = new LinkedBlockingQueue<>();
            final String name = "Runnable";
            EXEC.submit(unchecked(() -> {
                Thread.sleep(100L);
                queue.put(name.toLowerCase());
            }, e -> fail(e.getMessage())));
            assertThat(queue.take(), is("runnable"));
        }

        @Test
        public void uncheckedException1WithException() {
            EXEC.submit(unchecked(() -> new FileReader("notExistingFile"),
                    (e) -> assertThat(e, instanceOf(FileNotFoundException.class))));
        }
    }
}
