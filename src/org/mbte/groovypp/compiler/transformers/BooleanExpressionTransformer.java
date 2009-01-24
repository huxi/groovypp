package org.mbte.groovypp.compiler.transformers;

import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.expr.BooleanExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.NotExpression;
import org.mbte.groovypp.compiler.CompilerTransformer;
import org.mbte.groovypp.compiler.StaticCompiler;
import org.mbte.groovypp.compiler.bytecode.BytecodeExpr;
import org.objectweb.asm.Label;

public class BooleanExpressionTransformer extends ExprTransformer<BooleanExpression>{
    public Expression transform(BooleanExpression exp, CompilerTransformer compiler) {
        if (exp instanceof NotExpression) {
            final Expression expr = exp.getExpression();
            if (expr instanceof NotExpression) {
                return compiler.transform(((NotExpression)expr).getExpression());
            }
            return transformNotExpression((NotExpression) exp, compiler);
        }
        else {
            final Expression texp = compiler.transform(exp.getExpression());
            if (texp instanceof BooleanExpression)
               return texp;

            BooleanExpression res = new BooleanExpression(texp);
            res.setSourcePosition(exp);
            return res;
        }
    }

    private Expression transformNotExpression(final NotExpression exp, CompilerTransformer compiler) {
        final BytecodeExpr internal = (BytecodeExpr) compiler.transform(exp.getExpression());
        return new MyBytecodeExpr(exp, internal);
    }

    private static class MyBytecodeExpr extends BytecodeExpr {
        private final BytecodeExpr internal;

        public MyBytecodeExpr(NotExpression exp, BytecodeExpr internal) {
            super(exp, ClassHelper.Boolean_TYPE);
            this.internal = internal;
        }

        protected void compile() {
            mv.visitInsn(ICONST_0);
            internal.visit(mv);
            Label ok = new Label();
            StaticCompiler.branch(internal, IFNE, ok, mv);
            mv.visitInsn(POP);
            mv.visitInsn(ICONST_1);
            mv.visitLabel(ok);
            box(ClassHelper.boolean_TYPE);
        }
    }
}