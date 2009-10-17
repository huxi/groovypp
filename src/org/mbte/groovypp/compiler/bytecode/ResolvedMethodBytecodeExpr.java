package org.mbte.groovypp.compiler.bytecode;

import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ArrayExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.classgen.BytecodeHelper;
import org.mbte.groovypp.compiler.ClassNodeCache;
import org.mbte.groovypp.compiler.CompiledClosureBytecodeExpr;
import org.mbte.groovypp.compiler.CompilerTransformer;
import org.mbte.groovypp.compiler.TypeUtil;
import org.mbte.groovypp.compiler.transformers.VariableExpressionTransformer;

import java.util.ArrayList;

public class ResolvedMethodBytecodeExpr extends BytecodeExpr {
    private final MethodNode methodNode;
    private final BytecodeExpr object;
    private final String methodName;
    private final ArgumentListExpression bargs;
    private final ClassNode genericType;

    public ResolvedMethodBytecodeExpr(ASTNode parent, MethodNode methodNode, BytecodeExpr object, ArgumentListExpression bargs, CompilerTransformer compiler) {
        super(parent, methodNode.getReturnType().equals(ClassHelper.VOID_TYPE) ? TypeUtil.NULL_TYPE : methodNode.getReturnType());
        this.methodNode = methodNode;
        this.object = object;
        this.methodName = methodNode.getName();
        this.bargs = bargs;
        genericType = object != null ? TypeUtil.getSubstitutedType(methodNode.getReturnType(),
                methodNode.getDeclaringClass(), object.getType()) : null;

        tryImproveClosureType(methodNode, bargs);

        Parameter[] parameters = methodNode.getParameters();
        boolean isVarArg = parameters.length > 0 && parameters[parameters.length - 1].getType().isArray();

        if (isVarArg) {
            if (bargs.getExpressions().size() == parameters.length - 1) {
                BytecodeExpr last = (BytecodeExpr) compiler.transform(new ArrayExpression(parameters[parameters.length - 1].getType().getComponentType(), new ArrayList<Expression>()));
                bargs.getExpressions().add(last);
            } else {
                BytecodeExpr be = (BytecodeExpr) bargs.getExpressions().get(parameters.length - 1);
                if (!be.getType().isArray() && !be.getType().equals(parameters[parameters.length - 1].getType())) {
                    int add = bargs.getExpressions().size() - (parameters.length - 1);
                    ArrayList<Expression> list = new ArrayList<Expression>();

                    if (add != 1 || !bargs.getExpressions().get(parameters.length - 1).getType().equals(TypeUtil.NULL_TYPE)) {
                        for (int i = 0; i != add; ++i) {
                            final BytecodeExpr arg = (BytecodeExpr) bargs.getExpressions().get(parameters.length - 1 + i);

                            if (parameters[parameters.length - 1].getType().getComponentType().equals(ClassHelper.STRING_TYPE)) {
                                if (!arg.getType().equals(ClassHelper.STRING_TYPE)) {
                                    list.add(new BytecodeExpr(arg, ClassHelper.STRING_TYPE) {
                                        protected void compile() {
                                            arg.visit(mv);
                                            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "toString", "()Ljava/lang/String;");
                                        }
                                    });
                                } else
                                    list.add(arg);
                            } else
                                list.add(arg);
                        }

                        while (bargs.getExpressions().size() > parameters.length - 1)
                            bargs.getExpressions().remove(parameters.length - 1);

                        BytecodeExpr last = (BytecodeExpr) compiler.transform(new ArrayExpression(parameters[parameters.length - 1].getType().getComponentType(), list));
                        bargs.getExpressions().add(last);
                    }
                }
            }
        }

        for (int i = 0; i != parameters.length; ++i) {
            final BytecodeExpr arg = (BytecodeExpr) bargs.getExpressions().get(i);
            ClassNode ptype = parameters[i].getType();
            if (ptype.equals(ClassHelper.STRING_TYPE)) {
                if (!arg.getType().equals(ClassHelper.STRING_TYPE)) {
                    bargs.getExpressions().set(i, new BytecodeExpr(arg, ptype) {
                        protected void compile() {
                            arg.visit(mv);
                            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "toString", "()Ljava/lang/String;");
                        }
                    });
                }
            } else if (ptype.isArray()) {
                final ClassNode componentType = ptype.getComponentType();
                if (ClassHelper.isPrimitiveType(componentType)) {
                    bargs.getExpressions().set(i, new BytecodeExpr(arg, ptype) {
                        protected void compile() {
                            arg.visit(mv);
                            if (componentType == ClassHelper.byte_TYPE)
                                mv.visitMethodInsn(INVOKESTATIC, "org/codehaus/groovy/runtime/typehandling/DefaultTypeTransformation", "convertToByteArray", "(Ljava/lang/Object;)[B");
                            else if (componentType == ClassHelper.boolean_TYPE)
                                mv.visitMethodInsn(INVOKESTATIC, "org/codehaus/groovy/runtime/typehandling/DefaultTypeTransformation", "convertToBooleanArray", "(Ljava/lang/Object;)[Z");
                            else if (componentType == ClassHelper.short_TYPE)
                                mv.visitMethodInsn(INVOKESTATIC, "org/codehaus/groovy/runtime/typehandling/DefaultTypeTransformation", "convertToShortArray", "(Ljava/lang/Object;)[S");
                            else if (componentType == ClassHelper.int_TYPE)
                                mv.visitMethodInsn(INVOKESTATIC, "org/codehaus/groovy/runtime/typehandling/DefaultTypeTransformation", "convertToIntArray", "(Ljava/lang/Object;)[I");
                            else if (componentType == ClassHelper.char_TYPE)
                                mv.visitMethodInsn(INVOKESTATIC, "org/codehaus/groovy/runtime/typehandling/DefaultTypeTransformation", "convertToCharArray", "(Ljava/lang/Object;)[C");
                            else if (componentType == ClassHelper.long_TYPE)
                                mv.visitMethodInsn(INVOKESTATIC, "org/codehaus/groovy/runtime/typehandling/DefaultTypeTransformation", "convertToLongArray", "(Ljava/lang/Object;)[L");
                            else if (componentType == ClassHelper.float_TYPE)
                                mv.visitMethodInsn(INVOKESTATIC, "org/codehaus/groovy/runtime/typehandling/DefaultTypeTransformation", "convertToFloatArray", "(Ljava/lang/Object;)[F");
                            else if (componentType == ClassHelper.double_TYPE)
                                mv.visitMethodInsn(INVOKESTATIC, "org/codehaus/groovy/runtime/typehandling/DefaultTypeTransformation", "convertToDoubleArray", "(Ljava/lang/Object;)[D");
                        }
                    });
                } else if (componentType.equals(ClassHelper.OBJECT_TYPE) && ClassHelper.isPrimitiveType(arg.getType().getComponentType())) {
                    bargs.getExpressions().set(i, new BytecodeExpr(arg, ptype) {
                        protected void compile() {
                            arg.visit(mv);
                            mv.visitMethodInsn(INVOKESTATIC, "org/codehaus/groovy/runtime/typehandling/DefaultTypeTransformation", "primitiveArrayBox", "(Ljava/lang/Object;)[Ljava/lang/Object;");
                        }
                    });
                } else if (componentType.equals(ClassHelper.STRING_TYPE) && arg.getType().getComponentType().equals(ClassHelper.GSTRING_TYPE)) {
                    bargs.getExpressions().set(i, new BytecodeExpr(arg, ptype) {
                        protected void compile() {
                            arg.visit(mv);
                            mv.visitMethodInsn(INVOKESTATIC, "org/mbte/groovypp/runtime/DefaultGroovyPPMethods", "gstringArrayToStringArray", "([Lgroovy/lang/GString;)[Ljava/lang/String;");
                        }
                    });
                }
            }
        }
    }

    private void tryImproveClosureType(MethodNode methodNode, ArgumentListExpression bargs) {
        final Parameter[] parameters = methodNode.getParameters();
        if (parameters.length > 0) {
            final Parameter lastParam = parameters[parameters.length - 1];
            if (lastParam.getType().getName().equals(TypeUtil.TCLOSURE.getName())) {
                if (lastParam.getType().isUsingGenerics()) {
                    final GenericsType[] genericsTypes = lastParam.getType().getGenericsTypes();
                    if (genericsTypes != null) {
                        Object param = bargs.getExpressions().get(parameters.length - 1);
                        if (param instanceof CompiledClosureBytecodeExpr) {
                            CompiledClosureBytecodeExpr ce = (CompiledClosureBytecodeExpr) param;
                            ce.getType().getInterfaces()[0].setGenericsTypes(new GenericsType[]{new GenericsType(genericsTypes[0].getType())});
                        }
                    }
                }
            }
        }
    }

    public void compile() {
        int op = INVOKEVIRTUAL;
        final String classInternalName;
        final String methodDescriptor;
        if (methodNode instanceof ClassNodeCache.DGM) {
            op = INVOKESTATIC;

            if (object != null) {
                object.visit(mv);
                box(object.getType());
                if (methodNode.getDeclaringClass() != ClassHelper.OBJECT_TYPE)
                    mv.visitTypeInsn(CHECKCAST, BytecodeHelper.getClassInternalName(methodNode.getDeclaringClass()));
            }

            classInternalName = ((ClassNodeCache.DGM) methodNode).callClassInternalName;
            methodDescriptor = ((ClassNodeCache.DGM) methodNode).descr;
        } else {
            if (methodNode.isStatic())
                op = INVOKESTATIC;
            else if (methodNode.getDeclaringClass().isInterface())
                op = INVOKEINTERFACE;

            if (object != null) {
                if (object instanceof VariableExpressionTransformer.Super) {
                    op = INVOKESPECIAL;
                }

                object.visit(mv);
                box(object.getType());
            }

            if (op == INVOKESTATIC && object != null) {
                if (ClassHelper.long_TYPE == object.getType() || ClassHelper.double_TYPE == object.getType())
                    mv.visitInsn(POP2);
                else
                    mv.visitInsn(POP);
            }
            classInternalName = BytecodeHelper.getClassInternalName(methodNode.getDeclaringClass());
            methodDescriptor = BytecodeHelper.getMethodDescriptor(methodNode.getReturnType(), methodNode.getParameters());
        }

        Parameter[] parameters = methodNode.getParameters();
        for (int i = 0; i != parameters.length; ++i) {
            BytecodeExpr be = (BytecodeExpr) bargs.getExpressions().get(i);
            be.visit(mv);
            final ClassNode paramType = parameters[i].getType();
            final ClassNode type = be.getType();
            box(type);
            be.cast(ClassHelper.getWrapper(type), ClassHelper.getWrapper(paramType));
            be.unbox(paramType);
        }
        mv.visitMethodInsn(op, classInternalName, methodName, methodDescriptor);
        if (methodNode.getReturnType().equals(ClassHelper.VOID_TYPE))
            mv.visitInsn(ACONST_NULL);
    }
}
