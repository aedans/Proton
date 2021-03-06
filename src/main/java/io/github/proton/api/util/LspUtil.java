package io.github.proton.api.util;

import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

public final class LspUtil {
    public static boolean inRange(Position position, Range range) {
        return greaterThan(range.getStart(), position) && lessThan(range.getEnd(), position);
    }

    public static boolean greaterThan(Position start, Position position) {
        if (start.getLine() < position.getLine()) {
            return true;
        } else if (start.getLine() > position.getLine()) {
            return false;
        } else {
            return start.getCharacter() <= position.getCharacter();
        }
    }

    public static boolean lessThan(Position end, Position position) {
        if (end.getLine() < position.getLine()) {
            return false;
        } else if (end.getLine() > position.getLine()) {
            return true;
        } else {
            return end.getCharacter() > position.getCharacter();
        }
    }
}
