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
import org.junit.BeforeClass;
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
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CollectTest {

    private static final Logger L = Logger.getLogger(CollectTest.class.getName());

    private static final Pattern PATTERN = Pattern.compile("[\\P{L}]+");

    private ClassLoader loader = getClass().getClassLoader();

    private String text;

    @BeforeClass
    public static void setLogger() {
        L.setLevel(Level.SEVERE);
    }

    @Before
    public void loadText() throws URISyntaxException, IOException {
        URL resource = Objects.requireNonNull(loader.getResource("APACHE_LICENSE.txt"));
        Path path = Paths.get(resource.toURI());
        text = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }

    @Test
    public void count() {
        long count = PATTERN.splitAsStream(text)
                .map(String::toLowerCase)
                .distinct()
                .count();
        assertThat(count, is(60L));
    }

    @Test
    public void countBySet() {
        Set<String> set = PATTERN.splitAsStream(text)
                .map(String::toLowerCase)
                .collect(toSet());
        assertThat(set.size(), is(60));
    }

    @Test
    public void countByList() {
        List<String> list = PATTERN.splitAsStream(text)
                .map(String::toLowerCase)
                .distinct()
                .collect(Collectors.toList());
        assertThat(list.size(), is(60));
    }

    @Test
    public void countBySummarizingInt() {
        IntSummaryStatistics statistics = PATTERN.splitAsStream(text)
                .map(String::toLowerCase)
                .distinct()
                .collect(summarizingInt(s -> 1));
        assertThat(statistics.getCount(), is(60L));
    }

    @Test
    public void countByMap() {
        AtomicInteger index = new AtomicInteger(0);
        Map<String, StringWithIndex> map = PATTERN
                .splitAsStream(text)
                .map(String::toLowerCase)
                .map(s -> new StringWithIndex(index.incrementAndGet(), s))
                .collect(toMap(StringWithIndex::getWord,
                        Function.identity(),
                        (old, renew) -> {
                            L.info("old [" + old + "]");
                            L.info("new [" + renew + "]");
                            return renew;
                        }));
        assertThat(index.get() > map.size(), is(true));
        assertThat(index.get(), is(85));
        assertThat(map.size(), is(60));
    }

    @Test
    public void countByMapWhoseValueTypeToBeSet() {
        AtomicInteger index = new AtomicInteger(0);
        Map<String, Set<StringWithIndex>> map = PATTERN
                .splitAsStream(text)
                .map(String::toLowerCase)
                .map(s -> new StringWithIndex(index.incrementAndGet(), s))
                .collect(toMap(StringWithIndex::getWord, Collections::singleton, (l, r) -> {
                    Set<StringWithIndex> set = new HashSet<>(l);
                    set.addAll(r);
                    return set;
                }));
        assertThat(index.get() > map.size(), is(true));
        assertThat(index.get(), is(85));
        assertThat(map.size(), is(60));
    }

    @Test
    public void countByMapUsingGroupBy() {
        AtomicInteger index = new AtomicInteger(0);
        Map<String, List<StringWithIndex>> map = PATTERN
                .splitAsStream(text)
                .map(String::toLowerCase)
                .map(s -> new StringWithIndex(index.incrementAndGet(), s))
                .collect(groupingBy(StringWithIndex::getWord));
        assertThat(index.get() > map.size(), is(true));
        assertThat(index.get(), is(85));
        assertThat(map.size(), is(60));
    }

    @Test
    public void countByMapUsingGroupByWithSetType() {
        AtomicInteger index = new AtomicInteger(0);
        Map<String, Set<StringWithIndex>> map = PATTERN
                .splitAsStream(text)
                .map(String::toLowerCase)
                .map(s -> new StringWithIndex(index.incrementAndGet(), s))
                .collect(groupingBy(StringWithIndex::getWord, toSet()));
        assertThat(index.get() > map.size(), is(true));
        assertThat(index.get(), is(85));
        assertThat(map.size(), is(60));
        assertThat(map.get("license").size(), is(8));
    }

    @Test
    public void countByMapUsingGroupByWithMinimumFunction() {
        AtomicInteger index = new AtomicInteger(0);
        Map<String, Optional<StringWithIndex>> map = PATTERN
                .splitAsStream(text)
                .map(String::toLowerCase)
                .map(s -> new StringWithIndex(index.incrementAndGet(), s))
                .collect(groupingBy(StringWithIndex::getWord,
                        minBy(Comparator.comparing(StringWithIndex::getIndex))));
        assertThat(index.get() > map.size(), is(true));
        assertThat(index.get(), is(85));
        assertThat(map.size(), is(60));
    }

    @Test
    public void countByMapUsingGroupByWithMappingFunction() {
        AtomicInteger index = new AtomicInteger(0);
        Map<String, Optional<Integer>> map = PATTERN
                .splitAsStream(text)
                .map(String::toLowerCase)
                .map(s -> new StringWithIndex(index.incrementAndGet(), s))
                .collect(groupingBy(StringWithIndex::getWord,
                        mapping(StringWithIndex::getIndex,
                                minBy(Comparator.naturalOrder()))));
        assertThat(index.get() > map.size(), is(true));
        assertThat(index.get(), is(85));
        assertThat(map.size(), is(60));
    }

    @Test
    public void countWords() {
        Map<String, Integer> map = PATTERN.splitAsStream(text)
                .map(String::toLowerCase)
                .collect(groupingBy(Function.<String>identity(), summingInt(s -> 1)));
        System.out.println(map.get("license"));
    }

    private class StringWithIndex {
        private final int index;
        private final String word;

        private StringWithIndex(int index, String word) {
            this.index = index;
            this.word = Objects.requireNonNull(word);
        }

        public int getIndex() {
            return index;
        }

        public String getWord() {
            return word;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof StringWithIndex)) return false;

            StringWithIndex that = (StringWithIndex) o;

            return index == that.index && word.equals(that.word);
        }

        @Override
        public int hashCode() {
            return Objects.hash(index, word);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("StringWithIndex{");
            sb.append("index=").append(index);
            sb.append(", word='").append(word).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }
}
