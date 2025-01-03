/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2024 Objectionary.com
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
package org.eolang.parser;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.cactoos.Text;
import org.cactoos.iterable.Mapped;
import org.cactoos.list.ListOf;
import org.cactoos.text.UncheckedText;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Accumulates all parsing errors.
 *
 * @since 0.30.0
 */
final class ParsingErrors extends BaseErrorListener implements Iterable<Directive> {

    /**
     * Errors accumulated.
     */
    private final List<ParsingException> errors;

    /**
     * The source.
     */
    private final List<Text> lines;

    /**
     * Ctor.
     * @param lines The source in lines
     */
    ParsingErrors(final Text... lines) {
        this(new ListOf<>(lines));
    }

    /**
     * Ctor.
     * @param src The source in lines
     */
    ParsingErrors(final List<Text> src) {
        this.errors = new LinkedList<>();
        this.lines = src;
    }

    // @checkstyle ParameterNumberCheck (10 lines)
    @Override
    public void syntaxError(
        final Recognizer<?, ?> recognizer,
        final Object symbol,
        final int line,
        final int position,
        final String msg,
        final RecognitionException error
    ) {
        // @checkstyle MethodBodyCommentsCheck (20 lines)
        // @todo #3332:30min Add more specific error messages.
        //  Currently we write just "error: no viable alternative at input" for all errors.
        //  It's better to use 'Recognizer<?, ?> recognizer' parameter of the current method
        //  to retrieve more specific error messages.
        if (error instanceof NoViableAltException) {
            final Token token = (Token) symbol;
            this.errors.add(
                new ParsingException(
                    String.format(
                        "[%d:%d] %s:%n%s",
                        line, position,
                        "error: no viable alternative at input",
                        new UnderlinedMessage(
                            this.line(line).orElse("EOF"),
                            position,
                            Math.max(token.getStopIndex() - token.getStartIndex(), 1)
                        ).formatted()
                    ),
                    error,
                    line
                )
            );
        } else {
            this.errors.add(
                new ParsingException(
                    String.format(
                        "[%d:%d] %s: \"%s\"",
                        line, position, msg, this.line(line).orElse("EOF")
                    ),
                    error,
                    line
                )
            );
        }
    }

    @Override
    public Iterator<Directive> iterator() {
        return new org.cactoos.iterable.Joined<>(
            new Mapped<Iterable<Directive>>(
                error -> new Directives()
                    .xpath("/program")
                    .strict(1)
                    .addIf("errors")
                    .strict(1)
                    .add("error")
                    .attr("check", "eo-parser")
                    .attr("line", error.line())
                    .attr("severity", "critical")
                    .set(error.getMessage()),
                this.errors
            )
        ).iterator();
    }

    /**
     * How many errors?
     * @return Count of errors accumulated
     */
    public int size() {
        return this.errors.size();
    }

    /**
     * Get the line by number.
     * @param number The line number.
     * @return The line.
     */
    private Optional<String> line(final int number) {
        final Optional<String> result;
        if (number < 1 || number > this.lines.size()) {
            result = Optional.empty();
        } else {
            result = Optional.ofNullable(this.lines.get(number - 1))
                .map(UncheckedText::new)
                .map(UncheckedText::asString);
        }
        return result;
    }
}
