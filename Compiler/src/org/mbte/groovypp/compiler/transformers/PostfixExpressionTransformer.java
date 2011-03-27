/*
 * Copyright 2009-2011 MBTE Sweden AB.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mbte.groovypp.compiler.transformers;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.expr.*;
import org.mbte.groovypp.compiler.CompilerTransformer;
import org.mbte.groovypp.compiler.TypeUtil;
import org.mbte.groovypp.compiler.bytecode.BytecodeExpr;
import org.mbte.groovypp.compiler.bytecode.ResolvedMethodBytecodeExpr;
import org.mbte.groovypp.compiler.transformers.ExprTransformer;
import org.objectweb.asm.MethodVisitor;

public class PostfixExpressionTransformer extends ExprTransformer<PostfixExpression> {
    public Expression transform(final PostfixExpression exp, final CompilerTransformer compiler) {
        final BytecodeExpr oper = (BytecodeExpr) compiler.transform(exp.getExpression());

        // It's safe to search for unboxing first as long as we have a limited set of classes to apply transparent
        //  dereferencing.
        final MethodNode unboxing = TypeUtil.getReferenceUnboxingMethod(oper.getType());
        if (unboxing != null) {
            final ClassNode t = TypeUtil.getSubstitutedType(unboxing.getReturnType(), unboxing.getDeclaringClass(),
                    oper.getType());
            final MethodNode boxing = TypeUtil.getReferenceBoxingMethod(oper.getType(), t);
            if (boxing != null) {
                return new BytecodeExpr(exp, t) {
                    protected void compile(MethodVisitor mv) {
                        oper.visit(mv);
                        
                        final VariableExpression ref = new VariableExpression(compiler.context.getNextTempVarName());
                        compiler.compileStack.defineVariable(ref, true);
                        final BytecodeExpr refTransformed = (BytecodeExpr) compiler.transform(ref);

                        final BytecodeExpr unboxingCall = ResolvedMethodBytecodeExpr.create(exp, unboxing, refTransformed, new ArgumentListExpression(),
                                compiler);
                        compiler.cast(unboxingCall, t).visit(mv);

                        final VariableExpression v1 = new VariableExpression(compiler.context.getNextTempVarName(), t);
                        v1.setType(t);
                        compiler.compileStack.defineVariable(v1, true);

                        BytecodeExpr v1Transformed = (BytecodeExpr) compiler.transform(v1);
                        final BytecodeExpr postfix = v1Transformed.createPostfixOp(exp,
                                exp.getOperation().getType(), compiler);
                        postfix.visit(mv);

                        final VariableExpression v2 = new VariableExpression(compiler.context.getNextTempVarName(), t);
                        v2.setType(t);
                        compiler.compileStack.defineVariable(v2, true);
                        
                        ResolvedMethodBytecodeExpr.create(exp, boxing, refTransformed, new ArgumentListExpression(v1Transformed),
                                compiler).visit(mv);

                        ((BytecodeExpr) compiler.transform(v2)).visit(mv);
                    }
                };

            }
        }

        return oper.createPostfixOp(exp, exp.getOperation().getType(), compiler);
    }
}
