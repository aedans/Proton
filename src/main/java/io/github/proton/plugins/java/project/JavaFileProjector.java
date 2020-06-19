/*
 * Copyright 2020 Aedan Smith
 */
package io.github.proton.plugins.java.project;

import io.github.proton.editor.Projection;
import io.github.proton.editor.Projector;
import io.github.proton.editor.VectorProjection;
import io.github.proton.plugins.java.tree.*;
import io.vavr.collection.Vector;
import org.pf4j.Extension;

@Extension
public final class JavaFileProjector implements Projector<JavaFile> {
    @Override
    public Class<JavaFile> clazz() {
        return JavaFile.class;
    }

    @Override
    public Projection<JavaFile> project(JavaFile javaFile) {
        Projection<JavaFile> packageProjection = Projector.get(JavaPackageDeclaration.class)
                .project(javaFile.packageDeclaration)
                .map(packageDeclaration ->
                        new JavaFile(packageDeclaration, javaFile.importDeclarations, javaFile.classDeclarations));
        Projector<JavaImportDeclaration> importProjector = Projector.get(JavaImportDeclaration.class);
        Projection<JavaFile> importsProjection = new VectorProjection<>(
                        javaFile.importDeclarations, importProjector, new JavaImportDeclaration(new JavaIdentifier("")))
                .map(x -> new JavaFile(javaFile.packageDeclaration, x, javaFile.classDeclarations))
                .indentVertical(1);
        Projector<JavaClassDeclaration> classProjector = Projector.get(JavaClassDeclaration.class);
        Projection<JavaFile> classProjection = new VectorProjection<>(
                        javaFile.classDeclarations, classProjector, new JavaClassDeclaration(
                                new JavaIdentifier(""), Vector.empty()))
                .map(x -> new JavaFile(javaFile.packageDeclaration, javaFile.importDeclarations, x));
        return packageProjection.combineVertical(importsProjection).combineVertical(classProjection);
    }
}
