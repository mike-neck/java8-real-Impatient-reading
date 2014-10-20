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

import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MatrixTest {

    @Test
    public void calculateFibonacci() {
        final Matrix matrix = Matrix
                .leftTop(1).rightTop(1)
                .leftBottom(1).rightBottom(0).make();
        Matrix[] fibonacci = new Matrix[5];
        Arrays.parallelSetAll(fibonacci, i -> matrix);
        Arrays.parallelPrefix(fibonacci, (left, right) -> left.multiply(right));
        Matrix fib6 = fibonacci[5 - 1];
        System.out.println(fib6);
        assertThat(fib6.getLeftTop(), is(8));
    }
}
