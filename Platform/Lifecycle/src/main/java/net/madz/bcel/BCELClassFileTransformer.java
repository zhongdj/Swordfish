package net.madz.bcel;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

import net.madz.lifecycle.annotations.LifecycleMeta;
import net.madz.lifecycle.annotations.Transition;

import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.InnerClasses;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.Type;

public class BCELClassFileTransformer implements ClassFileTransformer {

    public static final String TRANSITION_ANNOTATION_TYPE = "L" + Transition.class.getName().replaceAll("\\.", "/")
            + ";";
    public static final String LIFECYLEMETA_ANNOTATION_TYPE = "L"
            + LifecycleMeta.class.getName().replaceAll("\\.", "/") + ";";

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        // TODO Auto-generated method stub
        final String fileName = className;
        final String location = protectionDomain.getCodeSource().getLocation().getPath();
        try (ByteArrayInputStream bais = new ByteArrayInputStream(classfileBuffer);) {
            final JavaClass jclas = new ClassParser(bais, fileName).parse();
            final AnnotationEntry[] annotationEntries = jclas.getAnnotationEntries();
            boolean foundLifecycleMeta = false;
            for ( AnnotationEntry annotationEntry : annotationEntries ) {
                if ( LIFECYLEMETA_ANNOTATION_TYPE.equals(annotationEntry.getAnnotationType()) ) {
                    foundLifecycleMeta = true;
                }
            }
            if ( !foundLifecycleMeta ) return classfileBuffer;
            final ClassGen cgen = new ClassGen(jclas);
            final Method[] methods = jclas.getMethods();
            int index;
            for ( index = 0; index < methods.length; index++ ) {
                if ( methods[index].getAnnotationEntries() != null ) {
                    boolean found = false;
                    for ( AnnotationEntry entry : methods[index].getAnnotationEntries() ) {
                        if ( TRANSITION_ANNOTATION_TYPE.equals(entry.getAnnotationType()) ) {
                            found = true;
                            break;
                        }
                    }
                    if ( found ) break;
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
                doGenerateAll(cgen, innerClassSeq, interceptingMethod, location);
                return cgen.getJavaClass().getBytes();
            } else {
                System.err.println("Method with annotation" + TRANSITION_ANNOTATION_TYPE + " not found in " + fileName);
            }
        } catch (ClassFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        System.out.println("className: " + fileName);
        System.out.println("classBeingRedefined: " + classBeingRedefined);
        System.out.println("protectionDomain: " + protectionDomain);
        return classfileBuffer;
    }

    /**
     * 在main函数执行前，执行的函数
     * 
     * @param options
     * @param ins
     */
    public static void premain(String options, Instrumentation ins) {
        // 注册我自己的字节码转换器
        System.out.println("======================premain==========================");
        ins.addTransformer(new BCELClassFileTransformer());
    }

    public static void agentmain(String args, Instrumentation inst) {
        // 注册我自己的字节码转换器
        System.out.println("======================agentmain==========================");
        inst.addTransformer(new BCELClassFileTransformer());
    }

    private static void doGenerateAll(ClassGen cgen, int innerClassSeq, Method interceptingMethod, String location)
            throws Throwable {
        JavaAnonymousInnerClass c = new JavaAnonymousInnerClass(cgen.getClassName(), interceptingMethod.getName(),
                interceptingMethod.getArgumentTypes(), innerClassSeq, Object.class.getName(), new Type[0],
                java.util.concurrent.Callable.class.getName(), new Type[] { new ObjectType(Void.class.getName()) },
                location);
        ClassGen doGenerate = c.doGenerate();
        doGenerate.getJavaClass().getBytes();
        MethodInterceptor.addWrapper(cgen, interceptingMethod, innerClassSeq);
    }
}
