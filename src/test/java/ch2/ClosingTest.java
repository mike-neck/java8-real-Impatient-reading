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
package ch2;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ClosingTest {

    private static final String REGEX = "[\\P{L}]+";

    private final ClassLoader loader = getClass().getClassLoader();

    private String text;

    @Before
    public void loadText() throws URISyntaxException, IOException {
        URL resource = Objects.requireNonNull(loader.getResource("APACHE_LICENSE.txt"));
        Path path = Paths.get(resource.toURI());
        text = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }

    @Test
    public void testStreamIsClosed() {
        Queue<String> queue = new LinkedList<>();
        Stream<String> stream = Pattern.compile(REGEX).splitAsStream(text);
        stream.onClose(() -> queue.offer("closed!"));
        stream.map(String::toLowerCase)
                .filter(w -> w.length() > 5)
                .findAny();
        assertThat(queue.isEmpty(), is(true));
    }

    private void doNothing(String arg) {}

    private Comparator<String> comparator = (s1, s2) -> s1.length() - s2.length();

    @Test
    public void testStreamIsClosedOnTerminalOperation() {
        final AtomicInteger count = new AtomicInteger(0);
        List<Consumer<Stream<String>>> list = Arrays.asList(
                st -> st.forEach(this::doNothing),
                st -> st.forEachOrdered(this::doNothing),
                st -> st.allMatch(String::isEmpty),
                st -> st.anyMatch(String::isEmpty),
                (Consumer<Stream<String>>) Stream::count,
                Stream::findAny,
                Stream::findFirst,
                st -> st.max(comparator),
                st -> st.min(comparator),
                st -> st.collect(Collectors.toList()),
                st -> st.reduce(0, (total, s) -> total + s.length(), (left, right) -> left + right)
        );
        Iterator<Consumer<Stream<String>>> iterator = list.iterator();
        final Pattern pattern = Pattern.compile(REGEX);
        Stream.generate(() -> pattern.splitAsStream(text).onClose(count::incrementAndGet))
                .limit(list.size())
                .forEach(stream -> iterator.next().accept(stream));
        assertThat(count.get(), is(0));
    }
}
