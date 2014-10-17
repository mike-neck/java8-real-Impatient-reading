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
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        private final ClassLoader loader = getClass().getClassLoader();

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

        @Test
        public void uncheckedException2WithoutException() throws InterruptedException {
            final BlockingQueue<List<String>> queue = new LinkedBlockingQueue<>();
            EXEC.submit(unchecked(() -> {
                URL resource = Objects.requireNonNull(loader.getResource("APACHE_LICENSE.txt"));
                Path path = Paths.get(resource.toURI());
                final Pattern pattern = Pattern.compile("[\\P{L}]+");
                try (Stream<String> lines = Files.lines(path, StandardCharsets.UTF_8)) {
                    queue.put(lines.flatMap(pattern::splitAsStream)
                            .map(String::toLowerCase)
                            .filter(w -> w.length() > 5)
                            .collect(Collectors.toList()));
                }
            }, e -> fail(e.getMessage())));
            assertThat(queue.take().size(), is(38));
        }
    }
}
