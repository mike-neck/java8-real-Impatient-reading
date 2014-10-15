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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static ch2.Fraction.divisor;
import static ch2.Fraction.positive;
import static ch2.Fraction.power;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;

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

    public static class MinusTest {
        @Test
        public void fraction1_2MinusFraction5_6IsNegative1_3() {
            Fraction left = new Fraction(1, 2);
            Fraction right = new Fraction(5, 6);
            assertThat(left.minus(right), is(new Fraction(-1, 3)));
        }

        @Test
        public void fraction1_2MinusInt1IsNegative1_2() {
            Fraction fraction = new Fraction(1, 2);
            assertThat(fraction.minus(1), is(new Fraction(-1, 2)));
        }
    }

    public static class ToNegativeTest {
        @Test
        public void positiveToNegative() {
            Fraction fraction = new Fraction(1, 2);
            assertThat(fraction.toNegative(), is(new Fraction(-1, 2)));
        }

        @Test
        public void negativeToPositive() {
            Fraction fraction = new Fraction(-1, 2);
            assertThat(fraction.toNegative(), is(new Fraction(1, 2)));
        }
    }

    public static class TransposeTest {
        @Test
        public void transposeOfFraction2_3IsFraction3_2() {
            Fraction fraction = new Fraction(2, 3);
            assertThat(fraction.transpose(), is(new Fraction(3, 2)));
        }

        @Test
        public void transposeOfNegativeFraction2_3IsNegativeFraction3_2() {
            Fraction fraction = new Fraction(-2, 3);
            assertThat(fraction.transpose(), is(new Fraction(-3, 2)));
        }
    }

    public static class DividingTest {
        @Test
        public void fraction2_3DividedByFraction4_3IsFraction1_2() {
            Fraction left = new Fraction(2, 3);
            Fraction right = new Fraction(4, 3);
            assertThat(left.divide(right), is(new Fraction(1, 2)));
        }

        @Test
        public void fraction2_3DividedByNegativeFraction4_3IsNegativeFraction1_2() {
            Fraction left = new Fraction(2, 3);
            Fraction right = new Fraction(-4, 3);
            assertThat(left.divide(right), is(new Fraction(-1, 2)));
        }

        @Test
        public void fraction2_3DividedByInt2IsFraction1_3() {
            Fraction fraction = new Fraction(2, 3);
            assertThat(fraction.divide(2), is(new Fraction(1, 3)));
        }
    }

    public static class IntConstructorTest {
        @Test
        public void fromInt1() {
            Fraction fraction = new Fraction(1);
            assertThat(fraction, is(new Fraction(1, 1)));
        }

        @Test
        public void fromIntNegative3() {
            Fraction fraction = new Fraction(-3);
            assertThat(fraction, is(new Fraction(-3, 1)));
        }
    }

    public static class ComparisonTest {
        @Test
        public void fraction1_2IsBiggerThan1_4() {
            Fraction big = new Fraction(1, 2);
            Fraction small = new Fraction(1, 4);
            assertThat(big.compareTo(small) > 0, is(true));
        }

        @Test
        public void fraction1_2IsSmallerThanInt1() {
            Fraction small = new Fraction(1, 2);
            assertThat(small.compareTo(1) < 0, is(true));
        }
    }

    public static class PowerTest {
        @Test
        public void power1IsTheSameValue() {
            Fraction fraction = new Fraction(1, 2);
            assertThat(fraction.power(1), is(new Fraction(1, 2)));
        }

        @Test
        public void innerPower() {
            assertThat(power(2, 2), is(4));
        }

        @Test
        public void power2() {
            Fraction fraction = new Fraction(1, 2);
            assertThat(fraction.power(2), is(new Fraction(1, 4)));
        }

        @Test
        public void negativeValuePoweredEvenTimesBecomesPositive() {
            Fraction fraction = new Fraction(-1, 4);
            assertThat(fraction.power(6).compareTo(0) > 0, is(true));
        }

        @Test
        public void negativeValuePoweredOddTimesRemainsNegative() {
            Fraction fraction = new Fraction(-1, 4);
            Fraction powered = fraction.power(5);
            assertThat(powered.compareTo(0) < 0, is(true));
        }
    }

    public static class IntOperationTest {
        @Test
        public void isInt() {
            Fraction fraction = new Fraction(1);
            assertThat(fraction.isInt(), is(true));
        }

        @Test
        public void isNotInt() {
            Fraction fraction = new Fraction(1, 2);
            assertThat(fraction.isInt(), is(false));
        }

        @Test
        public void asInt() {
            Fraction fraction = new Fraction(2);
            assumeThat(fraction.isInt(), is(true));
            assertThat(fraction.asInt(), is(2));
        }
    }

    public static class StreamReductionTest {
        @Test
        public void plus() {
            Optional<Fraction> sum = Stream
                    .iterate(new Fraction(1), f -> f.multiply(new Fraction(1, 2)))
                    .limit(10)
                    .reduce((l, r) -> l.plus(r));
            assertThat(sum, is(Optional.of(new Fraction(2).minus(new Fraction(1, 2).power(9)))));
        }

        @Test
        public void toList() {
            Fraction reduce = Stream
                    .iterate(new Fraction(1), f -> f.multiply(new Fraction(1, 2)))
                    .limit(10)
                    .reduce(new Fraction(0), (s, f) -> s.plus(f));
            assertThat(reduce, is(new Fraction(2).minus(new Fraction(1, 2).power(9))));
        }

        @Test
        public void filterIntCalculation() {
            int sum = Stream
                    .iterate(new Fraction(1, 3), f -> f.plus(new Fraction(2, 3)))
                    .filter(Fraction::isInt)
                    .limit(10)
                    .mapToInt(Fraction::asInt)
                    .reduce(0, (s, n) -> s + n);
            assertThat(sum, is(100));
        }

        @Test
        public void reduceToList() {
            List<Fraction> list = Stream
                    .iterate(new Fraction(1, 3), f -> f.plus(new Fraction(1, 6)))
                    .limit(10)
                    .reduce(new ArrayList<>(), (l, f) -> {
                        l.add(f);
                        return l;
                    }, (left, right) -> {
                        left.addAll(right);
                        return left;
                    });
            assertThat(list.size(), is(10));
        }
    }
}
