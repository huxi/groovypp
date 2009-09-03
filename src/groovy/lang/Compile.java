package groovy.lang;

import org.codehaus.groovy.transform.GroovyASTTransformationClass;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.PACKAGE})
@GroovyASTTransformationClass("org.mbte.groovypp.compiler.CompileASTTransform")
public @interface Compile {
    boolean debug () default false;

    CompilePolicy value () default CompilePolicy.STATIC;
}
