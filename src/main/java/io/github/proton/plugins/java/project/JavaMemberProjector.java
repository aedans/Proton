package io.github.proton.plugins.java.project;

import io.github.proton.editor.*;
import io.github.proton.plugins.java.tree.JavaMember;
import io.github.proton.plugins.java.tree.expression.JavaIdentifierExpression;
import io.github.proton.plugins.java.tree.member.*;
import io.vavr.collection.Vector;
import io.vavr.control.Option;
import org.pf4j.Extension;

@Extension
public final class JavaMemberProjector implements Projector<JavaMember> {
    @Override
    public Class<JavaMember> clazz() {
        return JavaMember.class;
    }

    @Override
    public Projection<JavaMember> project(JavaMember member) {
        if (member instanceof JavaFieldMember fieldMember) {
            return Projector.get(JavaFieldMember.class).project(fieldMember).map(x -> x)
                .mapChars(c -> methodChar(c, fieldMember))
                .mapChars(c -> setFieldChar(c, fieldMember));
        } else if (member instanceof JavaSetFieldMember setFieldMember) {
            return Projector.get(JavaSetFieldMember.class).project(setFieldMember).map(x -> x)
                .mapChars(c -> setFieldMember.expression().isEmpty()
                    ? methodChar(c, setFieldMember.fieldMember())
                    : c.map(x -> x))
                .mapChars(c -> setFieldMember.expression().isEmpty()
                    ? fieldChar(c, setFieldMember.fieldMember())
                    : c.map(x -> x));
        } else if (member instanceof JavaMethodMember methodMember) {
            return Projector.get(JavaMethodMember.class).project(methodMember).map(x -> x)
                .mapChars(c -> methodMember.statements().isEmpty()
                    ? setFieldChar(c, new JavaFieldMember(methodMember.type(), methodMember.name()))
                    : c.map(x -> x))
                .mapChars(c -> methodMember.statements().isEmpty()
                    ? fieldChar(c, new JavaFieldMember(methodMember.type(), methodMember.name()))
                    : c.map(x -> x));
        } else {
            throw new RuntimeException();
        }
    }

    private <T extends JavaMember> Char<JavaMember> methodChar(Char<T> c, JavaFieldMember fieldMember) {
        return c.modify(
            character -> character == '(' ? Option.some(c.insert(character).map(x -> (JavaMember) x)
                .getOrElse(new JavaMethodMember(
                    fieldMember.type(),
                    fieldMember.name(),
                    Vector.empty())))
                : c.insert(character).map(x -> x),
            () -> c.delete().map(x -> x));
    }

    private <T extends JavaMember> Char<JavaMember> setFieldChar(Char<T> c, JavaFieldMember fieldMember) {
        return c.modify(
            character -> character == '=' ? Option.some(c.insert(character).map(x -> (JavaMember) x)
                .getOrElse(new JavaSetFieldMember(
                    new JavaFieldMember(fieldMember.type(), fieldMember.name()),
                    new JavaIdentifierExpression(""))))
                : c.insert(character).map(x -> x),
            () -> c.delete().map(x -> x));
    }

    private <T extends JavaMember> Char<JavaMember> fieldChar(Char<T> c, JavaFieldMember fieldMember) {
        return c.modify(
            character -> c.insert(character).map(x -> x),
            () -> c.delete().map(x -> (JavaMember) x).orElse(Option.some(fieldMember)));
    }
}
