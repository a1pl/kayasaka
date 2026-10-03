package cc.squall.client.module;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class Processor extends AbstractProcessor {

    private final List<String> moduleClassNames = new ArrayList<>();
    private boolean generated = false;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        if (generated || env.processingOver()) {
            return true;
        }
        generated = true;

        TypeElement moduleAnnotation = processingEnv.getElementUtils()
                .getTypeElement("cc.squall.client.module.ModuleAnnotation");
        if (moduleAnnotation != null) {
            for (Element e : env.getElementsAnnotatedWith(moduleAnnotation)) {
                if (e instanceof TypeElement) {
                    moduleClassNames.add(((TypeElement) e).getQualifiedName().toString());
                }
            }
        }

        try {
            writeModulesClass();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate GeneratedModules", e);
        }
        return true;
    }

    private void writeModulesClass() throws Exception {
        Collections.sort(moduleClassNames);

        JavaFileObject file = processingEnv.getFiler()
                .createSourceFile("cc.squall.client.module.generated.GeneratedModules");

        try (Writer w = file.openWriter()) {
            w.write("package cc.squall.client.module.generated;\n");
            w.write("\n");
            w.write("public final class GeneratedModules {\n");
            w.write("    private GeneratedModules() {}\n");
            w.write("\n");
            w.write("    public static void registerAll() {\n");
            for (String cls : moduleClassNames) {
                w.write("        new " + cls + "();\n");
            }
            w.write("    }\n");
            w.write("}\n");
        }
    }
}