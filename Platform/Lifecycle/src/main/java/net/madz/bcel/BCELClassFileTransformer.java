package net.madz.bcel;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.madz.lifecycle.annotations.LifecycleMeta;
import net.madz.lifecycle.annotations.ReactiveObject;
import net.madz.lifecycle.annotations.Transition;

import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.InnerClasses;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.Type;

public class BCELClassFileTransformer implements ClassFileTransformer {

    private static final Logger log = Logger.getLogger("Lifecycle Framework Byte Code Transformer");
    public static final String TRANSITION_ANNOTATION_TYPE = "L" + Transition.class.getName().replaceAll("\\.", "/") + ";";
    public static final String LIFECYLEMETA_ANNOTATION_TYPE = "L" + LifecycleMeta.class.getName().replaceAll("\\.", "/") + ";";
    public static final String REACTIVE_ANNOTATION_TYPE = "L" + ReactiveObject.class.getName().replaceAll("\\.", "/") + ";";
    private String[] ignoredPackages = new String[] { "java.", "javax.", "sun.", "org." };

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer)
            throws IllegalClassFormatException {
        if ( shouldIgnore(className) ) {
            return classfileBuffer;
        }
        final String location = protectionDomain.getCodeSource().getLocation().getPath();
        try (final ByteArrayInputStream bais = new ByteArrayInputStream(classfileBuffer)) {
            final JavaClass jclas = new ClassParser(bais, className).parse();
            if ( !isTransformNeeded(jclas) ) {
                return classfileBuffer;
            }
            final ClassGen classGen = new ClassGen(jclas);
            int innerClassSeq = nextInnerClassSeqOf(classGen);
            for ( final Method method : jclas.getMethods() ) {
                if ( null == method.getAnnotationEntries() ) {
                    continue;
                }
                for ( final AnnotationEntry entry : method.getAnnotationEntries() ) {
                    if ( isTransformNeeded(entry) ) {
                        doTransform(classGen, innerClassSeq++, method, location);
                        break;
                    }
                }
            }
            return classGen.getJavaClass().getBytes();
        } catch (Throwable e) {
            log.log(Level.SEVERE, "Failed to transform class " + className, e);
            throw new IllegalClassFormatException();
        }
    }

    private boolean shouldIgnore(String className) {
        for ( int i = 0; i < ignoredPackages.length; i++ ) {
            if ( className.startsWith(ignoredPackages[i]) ) {
                return true;
            }
        }
        return false;
    }

    private boolean isTransformNeeded(AnnotationEntry entry) {
        return TRANSITION_ANNOTATION_TYPE.equals(entry.getAnnotationType());
    }

    private int nextInnerClassSeqOf(final ClassGen cgen) {
        int innerClassSeq = 1;
        for ( final Attribute attribute : cgen.getAttributes() ) {
            if ( attribute instanceof InnerClasses ) {
                InnerClasses icAttr = (InnerClasses) attribute;
                innerClassSeq += icAttr.getInnerClasses().length;
            }
        }
        return innerClassSeq;
    }

    private boolean isTransformNeeded(final JavaClass jclas) {
        final AnnotationEntry[] annotationEntries = jclas.getAnnotationEntries();
        boolean foundLifecycleMeta = false;
        for ( final AnnotationEntry annotationEntry : annotationEntries ) {
            if ( LIFECYLEMETA_ANNOTATION_TYPE.equals(annotationEntry.getAnnotationType()) ) {
                foundLifecycleMeta = true;
            } else if ( REACTIVE_ANNOTATION_TYPE.equals(annotationEntry.getAnnotationType()) ) {
                foundLifecycleMeta = true;
            }
        }
        return foundLifecycleMeta;
    }

    /**
     * 在main函数执行前，执行的函数
     * 
     * @param options
     * @param ins
     */
    public static void premain(String options, Instrumentation ins) {
        // 注册我自己的字节码转换器
        if ( log.isLoggable(Level.FINE) ) {
            log.fine("======================premain==========================");
        }
        ins.addTransformer(new BCELClassFileTransformer());
    }

    public static void agentmain(String args, Instrumentation inst) {
        // 注册我自己的字节码转换器
        if ( log.isLoggable(Level.FINE) ) {
            log.fine("======================agentmain==========================");
        }
        inst.addTransformer(new BCELClassFileTransformer());
    }

    private static void doTransform(ClassGen cgen, int innerClassSeq, Method interceptingMethod, String location) throws Throwable {
        JavaAnonymousInnerClass c = new JavaAnonymousInnerClass(cgen.getClassName(), interceptingMethod.getName(), interceptingMethod.getArgumentTypes(),
                innerClassSeq, Object.class.getName(), new Type[0], java.util.concurrent.Callable.class.getName(), new Type[] { new ObjectType(
                        Void.class.getName()) }, location);
        ClassGen doGenerate = c.doGenerate();
        doGenerate.getJavaClass().getBytes();
        MethodInterceptor.addWrapper(cgen, interceptingMethod, innerClassSeq);
        if ( "true".equals(System.getProperty("net.madz.bcel.save.original")) ) {
            final String fileName;
            if ( '\\' == File.separatorChar ) {
                fileName = location + cgen.getClassName().replaceAll("\\.", "\\\\") + ".class";
            } else {
                fileName = location + cgen.getClassName().replaceAll("\\.", File.separator) + ".class";
            }
            FileOutputStream fos = new FileOutputStream(fileName);
            cgen.getJavaClass().dump(fos);
            fos.close();
        }
    }
}
