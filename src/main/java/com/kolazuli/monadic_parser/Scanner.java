package com.kolazuli.monadic_parser;

/**
 * A simple scanner class that keeps track of the current position in a text
 * stream.
 * This class is useful for parsing and tokenizing text streams, such as source
 * code or configuration files.
 */
public class Scanner {
    private int line;
    private int column;
    private int position;
    private char last;

    /**
     * Initializes the scanner with default values for line, column, position, and
     * last.
     * The line and column are set to 0, indicating the start of the text stream.
     * The position is also set to 0, indicating the start of the stream.
     * The last attribute is set to null, indicating that no character or token has
     * been scanned yet.
     */
    public Scanner() {
        this.line = 0;
        this.column = 0;
        this.position = 0;
        this.last = '\0';
    }

    /**
     * Returns the current line number in the text stream.
     *
     * @return the current line number
     */
    public int getLine() {
        return line;
    }

    /**
     * Sets the current line number in the text stream.
     *
     * @param line the new line number
     */
    public void setLine(int line) {
        this.line = line;
    }

    /**
     * Returns the current column number in the text stream.
     *
     * @return the current column number
     */
    public int getColumn() {
        return column;
    }

    /**
     * Sets the current column number in the text stream.
     *
     * @param column the new column number
     */
    public void setColumn(int column) {
        this.column = column;
    }

    /**
     * Returns the current position in the text stream.
     *
     * @return the current position
     */
    public int getPosition() {
        return position;
    }

    /**
     * Sets the current position in the text stream.
     *
     * @param position the new position
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Returns the last character or token that was scanned.
     *
     * @return the last scanned character or token, or null if no character or token
     *         has been scanned yet
     */
    public char getLast() {
        return last;
    }

    /**
     * Sets the last character or token that was scanned.
     *
     * @param last the new last scanned character or token
     */
    public void setLast(char last) {
        this.last = last;
    }

    /**
     * Returns a string representation of the scanner's current state.
     *
     * @return a string representation of the scanner
     */
    @Override
    public String toString() {
        return "Scanner{" +
                "line=" + line +
                ", column=" + column +
                ", position=" + position +
                ", last='" + last + '\'' +
                '}';
    }
}