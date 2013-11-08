package net.madz.bcel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import net.madz.bcel.intercept.InterceptContext;
import net.madz.bcel.intercept.InterceptorController;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.InnerClass;
import org.apache.bcel.classfile.InnerClasses;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.BasicType;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ICONST;
import org.apache.bcel.generic.InstructionConstants;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LDC;
import org.apache.bcel.generic.LDC_W;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.Type;

public class MethodInterceptor {

    public static void addWrapper(ClassGen classGen, Method method, int anonymousInnerClassSeq) {
        renameOriginalMethod(classGen, method, classGen.getConstantPool(), classGen.getClassName());
        createWrapperMethodWithCreateNewInnerClass(classGen, method, anonymousInnerClassSeq, classGen.getClassName(),
                method.getName(), classGen.getConstantPool());
    }

    private static void renameOriginalMethod(ClassGen classGen, Method method, final ConstantPoolGen constantPoolGen,
            String cname) {
        MethodGen methgen = new MethodGen(method, cname, constantPoolGen);
        classGen.removeMethod(method);
        String iname = methgen.getName() + "$Impl";
        methgen.setName(iname);
        methgen.removeAnnotationEntries();
        classGen.addMethod(methgen.getMethod());
    }

    private static void createWrapperMethodWithCreateNewInnerClass(ClassGen classGen, Method method,
            int anonymousInnerClassSeq, final String interceptingClass, final String interceptingMethod,
            final ConstantPoolGen constantPoolGen) {
        final InstructionFactory ifact = new InstructionFactory(classGen);
        final InstructionList ilist = new InstructionList();
        try {
            final MethodGen wrapMethodGen = new MethodGen(method, interceptingClass, constantPoolGen);
            wrapMethodGen.setInstructionList(ilist);
            final String innerClassName = createInterceptMethodCode(classGen, anonymousInnerClassSeq,
                    interceptingClass, interceptingMethod, ifact, ilist, constantPoolGen, method);
            // finalize the constructed method
            wrapMethodGen.stripAttributes(true);
            wrapMethodGen.setMaxStack();
            wrapMethodGen.setMaxLocals();
            classGen.addMethod(wrapMethodGen.getMethod());
            //
            processInnerClassesAttributes(classGen, classGen.getConstantPool(), innerClassName);
        } finally {
            ilist.dispose();
        }
    }

    private static String createInterceptMethodCode(ClassGen classGen, int anonymousInnerClassSeq,
            final String interceptingClass, final String interceptingMethod, final InstructionFactory ifact,
            final InstructionList ilist, final ConstantPoolGen constantPoolGen, Method originalMethod) {
        final Type[] argumentTypes = originalMethod.getArgumentTypes(); 
        int localVariableSlotCursor = nextLocalVariableSlotCursor(originalMethod);
        // Step 1. InterceptorController controller = new
        // InterceptorController();
        ilist.append(ifact.createNew(InterceptorController.class.getName()));
        ilist.append(InstructionFactory.DUP);
        ilist.append(ifact.createInvoke(InterceptorController.class.getName(), "<init>", Type.VOID, Type.NO_ARGS,
                Constants.INVOKESPECIAL));
        final int controllerIndex = localVariableSlotCursor;
        final ObjectType controllerType = new ObjectType(InterceptorController.class.getName());
        localVariableSlotCursor += controllerType.getSize();
        ilist.append(InstructionFactory.createStore(controllerType, controllerIndex));
        // Step 2. final InterceptContext<Void> context = new
        // InterceptContext<Void>(getClass(), this, methodName,
        // new Class[] { Long.class, Long.class, Long.class },
        // Object[] arguments);
        // 2.1 getClass()
        ilist.append(ifact.createNew(InterceptContext.class.getName()));
        ilist.append(InstructionFactory.DUP);
        ilist.append(new LDC(constantPoolGen.lookupClass(interceptingClass)));
        // 2.2 load this
        ilist.append(InstructionFactory.createLoad(new ObjectType(interceptingClass), 0));// this
        // 2.3 load intercepting method
        int methodNameIndex = constantPoolGen.lookupString(interceptingMethod);
        if ( -1 >= methodNameIndex ) {
            methodNameIndex = constantPoolGen.addString(interceptingMethod);
        }
        ilist.append(new LDC(methodNameIndex));// methodName
        // 2.4 calculate argument size and allocate an array with same size
        ilist.append(new ICONST(argumentTypes.length));
        ilist.append(ifact.createNewArray(new ObjectType("java.lang.Class"), (short) 1));
        // 2.5 assign value for each element in array
        for ( int i = 0; i < argumentTypes.length; i++ ) {
            ilist.append(InstructionFactory.DUP);
            ilist.append(new ICONST(i));
            Type type = argumentTypes[i];
            if ( type instanceof BasicType ) {
                ilist.append(ifact.createGetStatic(convertType2ClassName(type), "TYPE", new ObjectType(
                        "java.lang.Class")));
            } else {
                int argumentClassIndex = convertType2ClassIndex(classGen, type);
                if ( type.getSize() > 4 ) {
                    ilist.append(new LDC_W(argumentClassIndex));
                } else {
                    ilist.append(new LDC(argumentClassIndex));
                }
            }
            ilist.append(InstructionConstants.AASTORE);
        }
        // 2.6 new InterceptContext<Void>(...
        final Type[] interceptor_method_arg_types = new Type[4];
        interceptor_method_arg_types[0] = new ObjectType("java.lang.Class");
        interceptor_method_arg_types[1] = new ObjectType("java.lang.Object");
        interceptor_method_arg_types[2] = new ObjectType("java.lang.String");
        interceptor_method_arg_types[3] = new ArrayType("java.lang.Class", 1);
        ilist.append(ifact.createInvoke(InterceptContext.class.getName(), "<init>", Type.VOID,
                interceptor_method_arg_types, Constants.INVOKESPECIAL));
        /*
         * ilist.append(new LDC_W(interceptingMethodIndex));
         * ilist.append(ifact.createInvoke(InterceptContext.class.getName(),
         * "<init>", Type.VOID,
         * new Type[]{new ObjectType("java.lang.Class"), new
         * ObjectType("java.lang.Object"), new
         * ObjectType("java.lang.reflect.Method")}, Constants.INVOKESPECIAL));
         */
        final int contextIndex = localVariableSlotCursor;
        final ObjectType contextType = new ObjectType(InterceptContext.class.getName());
        localVariableSlotCursor += contextType.getSize();
        ilist.append(InstructionFactory.createStore(contextType, contextIndex));
        ilist.append(InstructionFactory.createLoad(controllerType, controllerIndex));
        ilist.append(InstructionFactory.createLoad(contextType, contextIndex));
        // Step 3 Create Inner Class Instance.
        // net.madz.lifecycle.solutionOne.ServiceOrder$1(net.madz.lifecycle.solutionOne.ServiceOrder
        // summaryPlanId, long arg1, long truckResourceId, long arg3)
        final String innerClassName = interceptingClass + "$" + anonymousInnerClassSeq;
        ilist.append(ifact.createNew(innerClassName));
        ilist.append(InstructionFactory.DUP);
        // 3.1 load constructor arguments variables
        ilist.append(InstructionFactory.createThis());
        int localIndex = 1;
        for ( int i = 0; i < argumentTypes.length; i++ ) {
            ilist.append(InstructionFactory.createLoad(argumentTypes[i], localIndex));
            localIndex += argumentTypes[i].getSize();
        }
        // 3.2 calculating constructor argument types
        Type[] inner_constructor_arg_types = new Type[argumentTypes.length + 1];
        inner_constructor_arg_types[0] = new ObjectType(interceptingClass);
        for ( int i = 1; i <= argumentTypes.length; i++ ) {
            inner_constructor_arg_types[i] = argumentTypes[i - 1];
        }
        // 3.3 invoke constructor method
        ilist.append(ifact.createInvoke(innerClassName, "<init>", Type.VOID, inner_constructor_arg_types,
                Constants.INVOKESPECIAL));
        // Step 4. Invoke InterceptorController.exec
        ilist.append(ifact.createInvoke(InterceptorController.class.getName(), "exec", Type.OBJECT, new Type[] {
                new ObjectType(InterceptContext.class.getName()), new ObjectType(Callable.class.getName()) },
                Constants.INVOKEVIRTUAL));
        // Step 5.
        ilist.append(InstructionConstants.POP);
        ilist.append(InstructionConstants.RETURN);
        return innerClassName;
    }

    private static int nextLocalVariableSlotCursor(Method originalMethod) {
        int localVariableSlotCursor = originalMethod.isStatic() ? 0 : 1;
        for ( Type type : originalMethod.getArgumentTypes() )
        // compute the size of the calling parameters
        {
            localVariableSlotCursor += type.getSize();
        }
        return localVariableSlotCursor;
    }

    private static void processInnerClassesAttributes(ClassGen classGen, final ConstantPoolGen constantPoolGen,
            final String innerClassName) {
        int innerClasses_index = constantPoolGen.lookupUtf8("InnerClasses");
        boolean innerClassFound = false;
        for ( Attribute attribute : classGen.getAttributes() ) {
            if ( attribute instanceof InnerClasses ) {
                InnerClasses ics = (InnerClasses) attribute;
                ArrayList<InnerClass> iclist = new ArrayList<InnerClass>();
                InnerClass[] innerClasses = ics.getInnerClasses();
                for ( InnerClass innerClass : innerClasses ) {
                    iclist.add(innerClass);
                }
                iclist.add(new InnerClass(constantPoolGen.lookupClass(innerClassName), constantPoolGen
                        .lookupClass(classGen.getClassName()),
                // If C is anonymous (JLS 15.9.5), the value of the
                // inner_name_index item must be zero.
                        0,
                        // They should be set to zero in generated class files
                        // and
                        // should be ignored by Java Virtual Machine
                        // implementations.
                        0));
                ics.setInnerClasses(iclist.toArray(new InnerClass[iclist.size()]));
                ics.setLength(ics.getLength() + 8);
                innerClassFound = true;
                break;
            }
        }
        if ( !innerClassFound ) {
            innerClasses_index = constantPoolGen.addUtf8("InnerClasses");
            final InnerClasses inner = new InnerClasses(innerClasses_index, 10, new InnerClass[] { new InnerClass(
                    constantPoolGen.lookupClass(innerClassName), constantPoolGen.lookupClass(classGen.getClassName()),
                    // If C is anonymous (JLS 15.9.5), the value of the
                    // inner_name_index item must be zero.
                    0,
                    // They should be set to zero in generated class files and
                    // should be ignored by Java Virtual Machine
                    // implementations.
                    0) }, constantPoolGen.getConstantPool());
            classGen.addAttribute(inner);
        }
    }

    private static int convertType2ClassIndex(ClassGen cgen, Type type) {
        if ( type instanceof ObjectType ) {
            String className = type.getSignature();
            if ( className.startsWith("L") ) {
                className = className.substring(1);
            }
            int leftArrow = className.indexOf("<");
            if ( -1 < leftArrow ) {
                className = className.substring(0, leftArrow);
            }
            if ( className.endsWith(";") ) {
                className = className.substring(0, className.length() - 1);
            }
            int argumentClassIndex = cgen.getConstantPool().lookupClass(className);
            if ( -1 >= argumentClassIndex ) {
                argumentClassIndex = cgen.getConstantPool().addClass(className);
            }
            return argumentClassIndex;
        } else if ( type instanceof ArrayType ) {
            // unsupport for now
        }
        // wrong return
        throw new UnsupportedOperationException();
    }

    private static String convertType2ClassName(Type type) {
        if ( Type.BOOLEAN.equals(type) ) {
            return Boolean.class.getName();
        } else if ( Type.BYTE.equals(type) ) {
            return Byte.class.getName();
        } else if ( Type.CHAR.equals(type) ) {
            return Character.class.getName();
        } else if ( Type.DOUBLE.equals(type) ) {
            return Double.class.getName();
        } else if ( Type.FLOAT.equals(type) ) {
            return Float.class.getName();
        } else if ( Type.INT.equals(type) ) {
            return Integer.class.getName();
        } else if ( Type.LONG.equals(type) ) {
            return Long.class.getName();// long.class.getName();
        } else if ( Type.SHORT.equals(type) ) {
            return Short.class.getName();
        } else if ( type instanceof ArrayType ) {}
        throw new UnsupportedOperationException();
    }

    public static void main(String[] argv) throws Throwable {
        if ( argv.length == 2 && argv[0].endsWith(".class") ) {
            try {
                JavaClass jclas = new ClassParser(argv[0]).parse();
                ClassGen cgen = new ClassGen(jclas);
                Method[] methods = jclas.getMethods();
                int index;
                for ( index = 0; index < methods.length; index++ ) {
                    if ( methods[index].getName().equals(argv[1]) ) {
                        break;
                    }
                }
                if ( index < methods.length ) {
                    int innerClassSeq = 1;
                    Attribute[] attributes = cgen.getAttributes();
                    for ( Attribute attribute : attributes ) {
                        if ( attribute instanceof InnerClasses ) {
                            InnerClasses icAttr = (InnerClasses) attribute;
                            innerClassSeq += icAttr.getInnerClasses().length;
                        }
                    }
                    Method interceptingMethod = methods[index];
                    doGenerateAll(cgen, innerClassSeq, interceptingMethod);
                    FileOutputStream fos = new FileOutputStream(argv[0]);
                    cgen.getJavaClass().dump(fos);
                    fos.close();
                } else {
                    System.err.println("Method " + argv[1] + " not found in " + argv[0]);
                }
            } catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
        } else {
            System.out.println("Usage: BCELMethodInterceptor class-file method-name");
        }
    }

    private static void doGenerateAll(ClassGen cgen, int innerClassSeq, Method interceptingMethod) throws Throwable {
        JavaAnonymousInnerClass c = new JavaAnonymousInnerClass(cgen.getClassName(), interceptingMethod.getName(),
                interceptingMethod.getArgumentTypes(), innerClassSeq, Object.class.getName(), new Type[0],
                java.util.concurrent.Callable.class.getName(), new Type[] { new ObjectType(Void.class.getName()) },
                null);
        c.doGenerate();
        MethodInterceptor.addWrapper(cgen, interceptingMethod, innerClassSeq);
    }
}
