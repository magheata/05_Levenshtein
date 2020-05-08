package Domain.Interfaces;

import java.util.List;

public interface BufferPrims {
    final int NO_NUM = 0, INF = Integer.MAX_VALUE;

    void addLines(List<String> newLines);

    void addLines(int start, List<String> newLines);

    void deleteLines(int start, int end);

    void clearBuffer();

    void readBuffer(String fileName);

    void writeBuffer(String fileName);

    int getCurrentLineNumber();

    String getCurrentLine();

    int goToLine(int n);

    int size();     // Number of lines, as per old Collections

    /**
     * Retrieve one or more lines
     */
    String getLine(int ln);

    List<String> getLines(int i, int j);

    /**
     * Replace first/all occurrences of 'old' regex
     * with 'new' text, in current line only
     */
    void replace(String oldRE, String newStr, boolean all);

    /**
     * Replace first/all occurrences in each line
     */
    void replace(String oldRE, String newStr,
                 boolean all,
                 int startLine, int endLine);

    boolean isUndoSupported();

    /**
     * Undo the most recent operation;
     * optional method
     */
    default void undo() {
        throw new UnsupportedOperationException();
    }
}