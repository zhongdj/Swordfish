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

    public static void addWrapper(ClassGen cgen, Method method, int anonymousInnerClassSeq) {
        final String interceptingClass = cgen.getClassName();
        final String interceptingMethod = method.getName();
        // set up the construction tools
        InstructionFactory ifact = new InstructionFactory(cgen);
        InstructionList ilist = new InstructionList();
        ConstantPoolGen constantPoolGen = cgen.getConstantPool();
        String cname = cgen.getClassName();
        MethodGen wrapgen = new MethodGen(method, cname, constantPoolGen);
        // int interceptingMethodIndex = constantPoolGen.addMethodref(wrapgen);
        wrapgen.setInstructionList(ilist);
        // rename a copy of the original method
        MethodGen methgen = new MethodGen(method, cname, constantPoolGen);
        cgen.removeMethod(method);
        String iname = methgen.getName() + "$Impl";
        methgen.setName(iname);
        methgen.removeAnnotationEntries();
        cgen.addMethod(methgen.getMethod());
        // constantPoolGen.addMethodref(methgen);
        Type result = methgen.getReturnType();
        // compute the size of the calling parameters
        Type[] types = methgen.getArgumentTypes();
        int slot = methgen.isStatic() ? 0 : 1;
        for ( int i = 0; i < types.length; i++ ) {
            slot += types[i].getSize();
        }
        // Step 1. InterceptorController controller = new
        // InterceptorController();
        ilist.append(ifact.createNew(InterceptorController.class.getName()));
        ilist.append(InstructionFactory.DUP);
        ilist.append(ifact.createInvoke(InterceptorController.class.getName(), "<init>", Type.VOID, Type.NO_ARGS,
                Constants.INVOKESPECIAL));
        final int controllerIndex = slot;
        final ObjectType controllerType = new ObjectType(InterceptorController.class.getName());
        slot += controllerType.getSize();
        ilist.append(InstructionFactory.createStore(controllerType, controllerIndex));
        // Step 2. final InterceptContext<Void> context = new
        // InterceptContext<Void>(getClass(), this, "allocateResources",
        // new Class[] { Long.class, Long.class, Long.class });
        // 2.1 getClass()
        ilist.append(ifact.createNew(InterceptContext.class.getName()));
        ilist.append(InstructionFactory.DUP);
        ilist.append(new LDC(cgen.getConstantPool().lookupClass(interceptingClass)));
        // 2.2 load this
        ilist.append(InstructionFactory.createLoad(new ObjectType(interceptingClass), 0));// this
        // 2.3 load intercepting method
        int methodNameIndex = cgen.getConstantPool().lookupString(interceptingMethod);
        if ( -1 >= methodNameIndex ) {
            methodNameIndex = cgen.getConstantPool().addString(interceptingMethod);
        }
        ilist.append(new LDC(methodNameIndex));// methodName
        // 2.4 calculate argument size and allocate an array with same size
        ilist.append(new ICONST(types.length));
        ilist.append(ifact.createNewArray(new ObjectType("java.lang.Class"), (short) 1));
        // 2.5 assign value for each element in array
        for ( int i = 0; i < types.length; i++ ) {
            ilist.append(InstructionFactory.DUP);
            ilist.append(new ICONST(i));
            Type type = types[i];
            if ( type instanceof BasicType ) {
                ilist.append(ifact.createGetStatic(convertType2ClassName(type), "TYPE", new ObjectType("java.lang.Class")));
            } else {
                int argumentClassIndex = convertType2ClassIndex(cgen, type);
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
        final int contextIndex = slot;
        final ObjectType contextType = new ObjectType(InterceptContext.class.getName());
        slot += contextType.getSize();
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
        for ( int i = 0; i < types.length; i++ ) {
            ilist.append(InstructionFactory.createLoad(types[i], localIndex));
            localIndex += types[i].getSize();
        }
        // 3.2 calculating constructor argument types
        Type[] inner_constructor_arg_types = new Type[types.length + 1];
        inner_constructor_arg_types[0] = new ObjectType(interceptingClass);
        for ( int i = 1; i <= types.length; i++ ) {
            inner_constructor_arg_types[i] = types[i - 1];
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
        // finalize the constructed method
        wrapgen.stripAttributes(true);
        wrapgen.setMaxStack();
        wrapgen.setMaxLocals();
        cgen.addMethod(wrapgen.getMethod());
        ConstantPoolGen constantPool = cgen.getConstantPool();
        int innerClasses_index = constantPool.lookupUtf8("InnerClasses");
        Attribute[] attributes = cgen.getAttributes();
        boolean innerClassFound = false;
        for ( Attribute attribute : attributes ) {
            if ( attribute instanceof InnerClasses ) {
                InnerClasses ics = (InnerClasses) attribute;
                ArrayList<InnerClass> iclist = new ArrayList<InnerClass>();
                InnerClass[] innerClasses = ics.getInnerClasses();
                for ( InnerClass innerClass : innerClasses ) {
                    iclist.add(innerClass);
                }
                iclist.add(new InnerClass(constantPool.lookupClass(innerClassName), constantPool.lookupClass(cgen
                        .getClassName()),
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
        if ( innerClassFound ) {} else {
            innerClasses_index = constantPool.addUtf8("InnerClasses");
            final InnerClasses inner = new InnerClasses(innerClasses_index, 10, new InnerClass[] { new InnerClass(
                    constantPool.lookupClass(innerClassName), constantPool.lookupClass(cgen.getClassName()),
                    // If C is anonymous (JLS 15.9.5), the value of the
                    // inner_name_index item must be zero.
                    0,
                    // They should be set to zero in generated class files and
                    // should be ignored by Java Virtual Machine
                    // implementations.
                    0) }, constantPool.getConstantPool());
            cgen.addAttribute(inner);
        }
        ilist.dispose();
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
                java.util.concurrent.Callable.class.getName(), new Type[] { new ObjectType(Void.class.getName()) }, null);
        c.doGenerate();
        MethodInterceptor.addWrapper(cgen, interceptingMethod, innerClassSeq);
    }
}
