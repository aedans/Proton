/*
 * Copyright 2020 Aedan Smith
 */
package io.github.proton.plugins.style;

import static com.googlecode.lanterna.TextColor.Factory.fromString;

import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import io.github.proton.display.Style;
import org.pf4j.Extension;

@Extension
public final class SolarizedStyle implements Style {
    TextColor base03 = fromString("#002b36");
    TextColor base01 = fromString("#586E75");
    TextColor base0 = fromString("#839496");
    TextColor green = fromString("#859900");

    @Override
    public TextCharacter base(char character) {
        return new TextCharacter(character, base0, base03);
    }

    @Override
    public TextCharacter style(String scope, char character) {
        if (scope.startsWith("keyword")) {
            return base(character).withForegroundColor(green);
        }
        if (scope.startsWith("comment")) {
            return base(character).withForegroundColor(base01);
        }
        return base(character);
    }
}
