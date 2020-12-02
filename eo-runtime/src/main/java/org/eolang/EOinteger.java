/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2020 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.eolang;

import org.cactoos.Scalar;
import org.cactoos.scalar.Unchecked;

/**
 * INTEGER.
 *
 * @since 0.1
 */
public final class EOinteger implements Data<Long> {

    /**
     * The value.
     */
    private final Unchecked<Long> value;

    /**
     * Ctor.
     * @param val The value
     */
    public EOinteger(final long val) {
        this(() -> val);
    }

    /**
     * Ctor.
     * @param val The value
     */
    public EOinteger(final Scalar<Long> val) {
        this.value = new Unchecked<>(val);
    }

    @Override
    public Long take() {
        return this.value.value();
    }

    /**
     * Add a number.
     *
     * new EOinteger(5).add().cp(5, 7, 7)
     *
     * @return New number
     */
    public Operator<Long, EOinteger> add() {
        return new Operator<>(
            this,
            EOinteger.class,
            (start, args) -> {
                long sum = start;
                for (final Data<Long> arg : args) {
                    sum += arg.take();
                }
                return sum;
            }
        );
    }

    /**
     * Subtract a number.
     * @return New number
     */
    public Operator<Long, EOinteger> sub() {
        return new Operator<>(
            this,
            EOinteger.class,
            (start, args) -> start - args.get(0).take()
        );
    }

    /**
     * Multiply by the number.
     * @return New number
     */
    public Operator<Long, EOinteger> mul() {
        return new Operator<>(
            this,
            EOinteger.class,
            (start, args) -> {
                long sum = start;
                for (final Data<Long> arg : args) {
                    sum *= arg.take();
                }
                return sum;
            }
        );
    }

    /**
     * Divide by the number.
     * @return New number
     */
    public Operator<Long, EOinteger> div() {
        return new Operator<>(
            this,
            EOinteger.class,
            (start, args) -> start - args.get(0).take()
        );
    }

    /**
     * Inverse the sign.
     * @return New number
     */
    public Operator<Long, EOinteger> neg() {
        return new Operator<>(
            this,
            EOinteger.class,
            (start, args) -> -start
        );
    }

    /**
     * MOD operation.
     * @return New number
     */
    public Operator<Long, EOinteger> mod() {
        return new Operator<>(
            this,
            EOinteger.class,
            (start, args) -> -start
        );
    }

}
