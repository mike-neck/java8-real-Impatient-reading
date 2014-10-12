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
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class IterationToStream {

    private static final String REGEX = "[\\P{L}]+";

    private final ClassLoader loader = getClass().getClassLoader();

    private String text;

    private List<String> words;

    private long expected; // 38

    @Before
    public void loadWords() throws URISyntaxException, IOException {
        URL resource = Objects.requireNonNull(loader.getResource("APACHE_LICENSE.txt"));
        Path path = Paths.get(resource.toURI());
        text = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        words = Arrays.asList(text.split(REGEX));
        expected = 0L;
        for (String word : words) {
            if (word.length() > 5) {
                expected++;
            }
        }
    }

    @Test
    public void countWordsWithSingleStream() {
        long count = words.stream()
                .map(String::toLowerCase)
                .filter(w -> w.length() > 5)
                .count();
        assertThat(count, is(expected));
    }

    @Test
    public void countWordsWithParallelStream() {
        long count = words.parallelStream()
                .map(String::toLowerCase)
                .filter(w -> w.length() > 5)
                .count();
        assertThat(count, is(expected));
    }

    @Test
    public void generatingStreamWithMethodOfFromStringSplit() {
        long count = Stream.of(text.split(REGEX))
                .map(String::toLowerCase)
                .filter(w -> w.length() > 5)
                .count();
        assertThat(count, is(expected));
    }

    @Test
    public void generatingStreamWithMethodOfFromArguments() {
        long count = Stream.of("gradle", "wrapper", "gradle", "wrapper", "gradlew", "gradlew", "bat")
                .filter(w -> w.length() > 6)
                .count();
        assertThat(count, is(4L));
    }

    @Test
    public void emptyStream() {
        long count = Stream.empty()
                .count();
        assertThat(count, is(0L));
    }

    @Test
    public void generatingStreamWithMethodGenerate() {
        long count = Stream.generate(() -> "hoge")
                .limit(5L)
                .count();
        assertThat(count, is(5L));
    }

    @Test
    public void generatingStreamSequence() {
        Optional<Double> limit = Stream.iterate(3.0d, p -> p * 0.5d + 1)
                .parallel()
                .limit(1000L)
                .min(Comparator.naturalOrder());
        assertThat(limit.get(), is(2.0d));
    }

    @Test
    public void generatingStreamWithPattern() {
        long count = Pattern.compile(REGEX).splitAsStream(text)
                .map(String::toLowerCase)
                .filter(w -> w.length() > 5)
                .count();
        assertThat(count, is(expected));
    }

    @Test
    public void streamIsAutoClosable() {
        final AtomicInteger countDown = new AtomicInteger(1);
        Stream<String> stream = Pattern.compile(REGEX).splitAsStream(text);
        stream.onClose(countDown::decrementAndGet);
        stream.close();
        assertThat(countDown.get(), is(0));
    }
}
