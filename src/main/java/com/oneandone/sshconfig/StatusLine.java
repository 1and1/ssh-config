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
