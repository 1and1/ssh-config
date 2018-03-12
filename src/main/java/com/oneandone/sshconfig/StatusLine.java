/*
 * Copyright 2018 1&1 Internet SE.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.oneandone.sshconfig;

import java.io.PrintStream;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Status line on console.
 * @author Stephan Fuhrmann
 * */
public class StatusLine {
    /** Width of last printed line in characters. */
    private int width;
    /** Stream to print to. */
    private final PrintStream out;

    public StatusLine(PrintStream printStream) {
        this.out = Objects.requireNonNull(printStream);
    }

    /** Format and print a String.
     * @see String#format(String, Object...)
     * */
    public void printf(String format, Object... args) {
        print(String.format(format, args));
    }

    /** Print a String.
     * @param str the String to print.
     * */
    public synchronized void print(String str) {
        int lastWidth = width;
        String fill = "";
        if (str.length() < lastWidth) {
            fill = IntStream.range(0, lastWidth - str.length()).mapToObj(s -> " ").collect(Collectors.joining());
        }
        out.print("\r"+str+fill);
        width = str.length();
    }
}
