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

public class Fraction {

    static int divisor(int left, int right) throws ArithmeticException {
        if (right == 0) {
            throw new ArithmeticException("right should be non 0 number.");
        }
        final int large = positive(left);
        final int small = positive(right);
        if (large < small) {
            return divisor(small, large);
        } else if (small == 1) {
            return 1;
        }
        final int quotient = large / small;
        final int surplus = large - small * quotient;
        if (surplus == 0) {
            return small;
        } else {
            return divisor(small, surplus);
        }
    }

    static int positive(int value) {
        return value < 0? -value:value;
    }

    private static int arithmeticValue(int num, int den, boolean pos) {
        return (pos? 1:-1) * num * den;
    }

    private final boolean positive;
    private final int numerator;
    private final int denominator;

    public Fraction(int numerator, int denominator) {
        int divisor = divisor(numerator, denominator);
        this.numerator = positive(numerator) / divisor;
        this.denominator = positive(denominator) / divisor;
        this.positive =
                (numerator > 0 && denominator > 0) ||
                (numerator < 0 && denominator < 0);
    }

    private Fraction(int numerator, int denominator, boolean positive) {
        int divisor = divisor(numerator, denominator);
        this.numerator = positive(numerator) / divisor;
        this.denominator = positive(denominator) / divisor;
        this.positive = (positive && numerator > 0) || (!positive && numerator < 0);
    }

    public Fraction plus(int value) {
        return plus(new Fraction(denominator * value, denominator));
    }

    public Fraction plus(Fraction other) {
        final int newNumerator = arithmeticValue(this.numerator, other.denominator, this.positive)
                + arithmeticValue(other.numerator, this.denominator, other.positive);
        final int newDenominator = this.denominator * other.denominator;
        return new Fraction(newNumerator, newDenominator);
    }

    public Fraction multiply(int value) {
        return new Fraction(numerator * value, denominator, positive);
    }

    public Fraction multiply(Fraction other) {
        return new Fraction(numerator * other.numerator,
                denominator * other.denominator,
                !(positive ^ other.positive));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Fraction)) return false;

        Fraction fraction = (Fraction) o;

        if (denominator != fraction.denominator) return false;
        if (numerator != fraction.numerator) return false;
        if (positive != fraction.positive) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (positive ? 1 : 0);
        result = 31 * result + numerator;
        result = 31 * result + denominator;
        return result;
    }

    @Override
    public String toString() {
        return (positive? "":"-") + numerator + "/" + denominator;
    }
}
