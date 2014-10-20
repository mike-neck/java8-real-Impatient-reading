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

public class Matrix {

    private final int leftTop; private final int rightTop;

    private final int leftBottom; private final int rightBottom;

    private Matrix(int leftTop, int rightTop, int leftBottom, int rightBottom) {
        this.leftTop = leftTop;
        this.rightTop = rightTop;
        this.leftBottom = leftBottom;
        this.rightBottom = rightBottom;
    }

    public Matrix(Vector left, Vector right) {
        this(left.getX(), right.getX(), left.getY(), right.getY());
    }

    public int getDeterminant() {
        return leftTop * rightBottom - leftBottom * rightTop;
    }

    public Matrix multiply(Matrix o) {
        Vector t = Vector.make(leftTop, rightTop);
        Vector b = Vector.make(leftBottom, rightBottom);
        Vector l = Vector.make(o.leftTop, o.leftBottom);
        Vector r = Vector.make(o.rightTop, o.rightBottom);
        return new Matrix(Vector.make(t.multiply(l).total(), b.multiply(l).total()), Vector.make(t.multiply(r).total(), b.multiply(r).total()));
    }

    public int getLeftTop() {
        return leftTop;
    }

    @Override
    public String toString() {
        return "|" + String.format("%4d", leftTop) + " " + String.format("%4d", rightTop) + "|\n" +
                "|" + String.format("%4d", leftBottom) + " " + String.format("%4d", rightBottom) + "|";
    }

    public static class Vector {

        public static Vector make(int x, int y) {
            return new Vector(x, y);
        }

        private final int x;
        private final int y;

        private Vector(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Vector multiply(Vector other) {
            return new Vector(x * other.x, y * other.y);
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int total() {
            return x + y;
        }
    }

    private static class MatrixBuilder implements LeftTop, RightTop, LeftBottom, RightBottom {

        private int lt; private int rt;
        private int lb; private int rb;

        MatrixBuilder(int lt) {
            this.lt = lt;
        }

        @Override
        public RightBottom rightBottom(int rb) {
            this.rb = rb;
            return this;
        }

        @Override
        public RightTop rightTop(int rt) {
            this.rt = rt;
            return this;
        }

        @Override
        public Matrix make() {
            return new Matrix(lt, rt, lb, rb);
        }

        @Override
        public LeftBottom leftBottom(int lb) {
            this.lb = lb;
            return this;
        }
    }

    public static LeftTop leftTop(int lt) {
        return new MatrixBuilder(lt);
    }

    public interface LeftTop {
        public RightTop rightTop(int rt);
    }

    public interface RightTop {
        public LeftBottom leftBottom(int lb);
    }

    public interface LeftBottom {
        public RightBottom rightBottom(int rb);
    }

    public interface RightBottom {
        public Matrix make();
    }
}
