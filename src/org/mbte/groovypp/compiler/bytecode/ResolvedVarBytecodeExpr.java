package org.mbte.groovypp.compiler.bytecode;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.classgen.Variable;
import org.codehaus.groovy.syntax.Token;
import org.codehaus.groovy.syntax.Types;
import org.mbte.groovypp.compiler.CompilerTransformer;
import org.mbte.groovypp.compiler.TypeUtil;
import org.mbte.groovypp.compiler.transformers.ListExpressionTransformer;
import org.objectweb.asm.MethodVisitor;

public class ResolvedVarBytecodeExpr extends ResolvedLeftExpr {
    private final VariableExpression ve;
    private final Variable var;

    public ResolvedVarBytecodeExpr(ClassNode type, VariableExpression ve, CompilerTransformer compiler) {
        super(ve, type);
        this.ve = ve;
        var = compiler.compileStack.getVariable(ve.getName(), true);
    }

    protected void compile(MethodVisitor mv) {
        load(var, mv);
    }

    public BytecodeExpr createAssign(ASTNode parent, Expression right, CompilerTransformer compiler) {
        final ClassNode vtype;
        if (ve.isDynamicTyped()) {
            right = compiler.transform(right);
            if (right instanceof ListExpressionTransformer.UntransformedListExpr)
                right = new ListExpressionTransformer.TransformedListExpr(((ListExpressionTransformer.UntransformedListExpr)right).exp, TypeUtil.ARRAY_LIST_TYPE, compiler);
            vtype = TypeUtil.wrapSafely(right.getType());
            compiler.getLocalVarInferenceTypes().add(ve, vtype);
        } else {
            vtype = ve.getType();
            right = compiler.cast(right, vtype);
        }

        final BytecodeExpr finalRight = (BytecodeExpr) right;

        return new BytecodeExpr(parent, vtype) {
            protected void compile(MethodVisitor mv) {
                finalRight.visit(mv);
                if (ClassHelper.isPrimitiveType(finalRight.getType()) && !ClassHelper.isPrimitiveType(vtype))
                    box(finalRight.getType(), mv);
                storeVar(var, mv);
                load(var, mv);
            }
        };
    }

    public BytecodeExpr createBinopAssign(ASTNode parent, Token method, final BytecodeExpr right, CompilerTransformer compiler) {
        final BinaryExpression op = new BinaryExpression(this, method, right);
        op.setSourcePosition(parent);
        return createAssign(parent, op, compiler);
    }

    public BytecodeExpr createPrefixOp(ASTNode exp, final int type, CompilerTransformer compiler) {
        final org.codehaus.groovy.classgen.Variable var = compiler.compileStack.getVariable(ve.getName(), false);

        if (var != null && var.getType().equals(ClassHelper.int_TYPE)) {
            return new BytecodeExpr(exp, ClassHelper.int_TYPE) {
                protected void compile(MethodVisitor mv) {
                    mv.visitIincInsn(var.getIndex(), type == Types.PLUS_PLUS ? 1 : -1);
                    mv.visitVarInsn(ILOAD, var.getIndex());
                }
            };
        }

        ClassNode vtype = compiler.getLocalVarInferenceTypes().get(ve);
        if (vtype == null)
            vtype = var.getType();

        if (TypeUtil.isNumericalType(vtype) && !vtype.equals(TypeUtil.Number_TYPE)) {
            return new BytecodeExpr(exp, vtype) {
                protected void compile(MethodVisitor mv) {
                    final ClassNode primType = ClassHelper.getUnwrapper(getType());
                    load(getType(), var.getIndex(), mv);
                    if (getType() != primType)
                        unbox(primType, mv);
                    incOrDecPrimitive(primType, type, mv);
                    if (getType() != primType)
                        box(primType, mv);
                    dup(getType(), mv);
                    store(var, mv);
                }
            };
        }

        if (ClassHelper.isPrimitiveType(vtype))
            vtype = TypeUtil.wrapSafely(vtype);

        String methodName = type == Types.PLUS_PLUS ? "next" : "previous";
        final MethodNode methodNode = compiler.findMethod(vtype, methodName, ClassNode.EMPTY_ARRAY);
        if (methodNode == null) {
            compiler.addError("Can't find method " + methodName + " for type " + vtype.getName(), exp);
            return null;
        }

        final BytecodeExpr nextCall = (BytecodeExpr) compiler.transform(new MethodCallExpression(
                new BytecodeExpr(exp, vtype) {
                    protected void compile(MethodVisitor mv) {
                        load(var.getType(), var.getIndex(), mv);
                    }
                },
                methodName,
                new ArgumentListExpression()
        ));

        return new BytecodeExpr(exp, vtype) {
            protected void compile(MethodVisitor mv) {
                nextCall.visit(mv);
                dup(getType(), mv);
                store(var, mv);
            }
        };
    }

    public BytecodeExpr createPostfixOp(ASTNode exp, final int type, CompilerTransformer compiler) {
        final org.codehaus.groovy.classgen.Variable var = compiler.compileStack.getVariable(ve.getName(), false);
        if (var != null && var.getType().equals(ClassHelper.int_TYPE)) {
            return new BytecodeExpr(exp, ClassHelper.int_TYPE) {
                protected void compile(MethodVisitor mv) {
                    mv.visitVarInsn(ILOAD, var.getIndex());
                    mv.visitIincInsn(var.getIndex(), type == Types.PLUS_PLUS ? 1 : -1);
                }
            };
        }

        ClassNode vtype = compiler.getLocalVarInferenceTypes().get(ve);
        if (vtype == null)
            vtype = var.getType();

        if (TypeUtil.isNumericalType(vtype) && !vtype.equals(TypeUtil.Number_TYPE)) {
            return new BytecodeExpr(exp, vtype) {
                protected void compile(MethodVisitor mv) {
                    final ClassNode primType = ClassHelper.getUnwrapper(getType());
                    load(getType(), var.getIndex(), mv);
                    dup(getType(), mv);
                    if (getType() != primType)
                        unbox(primType, mv);
                    incOrDecPrimitive(primType, type, mv);
                    if (getType() != primType)
                        box(primType, mv);
                    store(var, mv);
                }
            };
        }

        if (ClassHelper.isPrimitiveType(vtype))
            vtype = TypeUtil.wrapSafely(vtype);

        String methodName = type == Types.PLUS_PLUS ? "next" : "previous";
        final MethodNode methodNode = compiler.findMethod(vtype, methodName, ClassNode.EMPTY_ARRAY);
        if (methodNode == null) {
            compiler.addError("Can't find method next() for type " + vtype.getName(), exp);
            return null;
        }

        final BytecodeExpr nextCall = (BytecodeExpr) compiler.transform(new MethodCallExpression(
                new BytecodeExpr(exp, vtype) {
                    protected void compile(MethodVisitor mv) {
                        load(var.getType(), var.getIndex(), mv);
                        dup(getType(), mv);
                    }
                },
                methodName,
                new ArgumentListExpression()
        ));

        return new BytecodeExpr(exp, vtype) {
            protected void compile(MethodVisitor mv) {
                nextCall.visit(mv);
                store(var, mv);
            }
        };
    }
}
