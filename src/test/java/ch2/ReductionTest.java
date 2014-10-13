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

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import static ch2.Fraction.divisor;
import static ch2.Fraction.positive;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(Enclosed.class)
public class ReductionTest {

    public static class DivisorTest {

        @Test
        public void divisorOf12And18is6() {
            int actual = divisor(12, 18);
            assertThat(actual, is(6));
        }

        @Test
        public void ifOneOfArgumentIs1Then1() {
            assertThat(divisor(1, 238), is(1));
        }

        @Test
        public void argumentsArePrimeThen1() {
            assertThat(divisor(7, 3), is(1));
        }

        @Test
        public void argumentsAreSameThenDivisorIsSame() {
            assertThat(divisor(21, 21), is(21));
        }

        @Test
        public void divisorShouldReturnPositive() {
            assertThat(divisor(-1, 1) > 0, is(true));
        }

        @Test
        public void divisorOf175And35is35() {
            assertThat(divisor(175, 35), is(35));
        }

        @Test
        public void divisorOf144And84is12() {
            assertThat(divisor(144, 84), is(12));
        }

        @Test
        public void resultShouldBeTheSameWhenOrderOfArgumentDiffers() {
            assertThat(divisor(84, 144) == divisor(144, 84), is(true));
        }
    }

    public static class PositiveTest {
        @Test
        public void positiveShouldBeTheSameValue () {
            assertThat(positive(1), is(1));
        }

        @Test
        public void negativeBecomesPositiveValue () {
            assertThat(positive(-1), is(1));
        }
    }

    public static class EqualityTest {
        @Test
        public void simpleEquality() {
            assertThat(new Fraction(1,2), is(new Fraction(1,2)));
        }

        @Test
        public void negativeEquality() {
            assertThat(new Fraction(-1, 2), is(new Fraction(-1, 2)));
        }

        @Test
        public void transNegativeEquality() {
            assertThat(new Fraction(1, -2), is(new Fraction(-1, 2)));
        }

        @Test
        public void divisorEquality() {
            assertThat(new Fraction(4, 8), is(new Fraction(1, 2)));
        }

        @Test
        public void transPositiveEquality() {
            assertThat(new Fraction(-1, -2), is(new Fraction(1, 2)));
        }
    }

    public static class PlusTest {
        @Test
        public void fraction1_2PlusFraction1_4Becomes3_4() {
            Fraction left = new Fraction(1, 2);
            Fraction right = new Fraction(1, 4);
            assertThat(left.plus(right), is(new Fraction(3, 4)));
        }

        @Test
        public void fraction3_4PlusFractionMinus1_2Becomes1_4() {
            Fraction left = new Fraction(3, 4);
            Fraction right = new Fraction(1, -2);
            assertThat(left.plus(right), is(new Fraction(1, 4)));
        }

        @Test
        public void fractionMinus3_4PlusFraction1_2BecomesMinus1_4() {
            Fraction left = new Fraction(-3, 4);
            Fraction right = new Fraction(1, 2);
            assertThat(left.plus(right), is(new Fraction(-1, 4)));
        }

        @Test
        public void commutativity() {
            Fraction a = new Fraction(1, 4);
            Fraction b = new Fraction(2, 3);
            assertThat(a.plus(b), is(b.plus(a)));
        }

        @Test
        public void fractionPlusInt() {
            Fraction fraction = new Fraction(1, 2);
            assertThat(fraction.plus(1), is(new Fraction(3, 2)));
        }
    }

    public static class MultiplyTest {
        @Test
        public void fractionMultipliesPositiveInt() {
            Fraction fraction = new Fraction(1, 2);
            assertThat(fraction.multiply(3), is(new Fraction(3, 2)));
        }

        @Test
        public void fractionMultipliesNegativeInt() {
            Fraction fraction = new Fraction(1, 2);
            assertThat(fraction.multiply(-5), is(new Fraction(-5, 2)));
        }

        @Test
        public void fraction1_2MultipliedByFraction3_4BecomesFraction3_8() {
            Fraction left = new Fraction(1, 2);
            Fraction right = new Fraction(3, 4);
            assertThat(left.multiply(right), is(new Fraction(3, 8)));
        }

        @Test
        public void fraction3_4MultipliedByFraction2_9BecomesFraction1_6() {
            Fraction left = new Fraction(3, 4);
            Fraction right = new Fraction(2, 9);
            assertThat(left.multiply(right), is(new Fraction(1, 6)));
        }

        @Test
        public void commutativity() {
            Fraction a = new Fraction(4, 3);
            Fraction b = new Fraction(9, 18);
            assertThat(b.multiply(a), is(a.multiply(b)));
        }
    }
}
