package main;

import org.codehaus.commons.compiler.CompilerFactoryFactory;
import org.codehaus.commons.compiler.ISimpleCompiler;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Main {

    static final String PACKAGE_NAME = "test";
    static final String CLASS_NAME = "Test";
    static final String SOURCE_CODE = "package " + PACKAGE_NAME + ";\n" +
            "import javax.annotation.Nonnull;\n" +
            "public class " + CLASS_NAME + " {\n" +
            "  public static void main(@Nonnull String[] args) {\n" +
            "    System.out.println(\"Hello World!\");\n" +
            "  }\n" +
            "}";

    static Class<?> getClazz(String code) throws Exception {
        ISimpleCompiler sc = CompilerFactoryFactory.getDefaultCompilerFactory(Thread.currentThread().getContextClassLoader()).newSimpleCompiler();
        sc.setDebuggingInformation(true, true, true);
        sc.cook(code);

        ClassLoader cl = sc.getClassLoader();
        Class<?> clazz = cl.loadClass(PACKAGE_NAME + '.' + CLASS_NAME);
        if (clazz == null) {
            throw new IllegalArgumentException("no main class found in script");
        }
        if (!Modifier.isPublic(clazz.getModifiers())) {
            throw new IllegalArgumentException("main class must be public");
        }

        return clazz;
    }

    public static Method getMainMethod(Class<?> clazz) throws Exception {
        return clazz.getMethod("main", String[].class);
    }

    public static void main(String[] args) throws Exception {
        Class<?> clazz = getClazz(SOURCE_CODE);
        Method main = getMainMethod(clazz);
        main.invoke(null, new Object[]{ new String[0] });
    }

}
