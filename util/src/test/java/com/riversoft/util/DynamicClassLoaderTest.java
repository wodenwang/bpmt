/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.util;

import org.junit.Assert;
import org.junit.Test;

import com.riversoft.util.dynamicbean.DynamicClassLoader;

/**
 * @author Borball
 * 
 */
public class DynamicClassLoaderTest {

    @Test
    public void testNormal() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        String fullName = "com.riversoft.test.DynaClass1";

        // Here we specify the source code of the class to be compiled
        StringBuilder src = new StringBuilder();
        src.append("package com.riversoft.test;\n");
        src.append("\n");
        src.append("import com.riversoft.util.MD5;\n");
        src.append("import com.riversoft.platform.web.crud.BaseCRUDAction;\n");
        src.append("\n");
        src.append("public class DynaClass1 {\n");
        src.append("    public String toString() {\n");
        src.append("        return \"Hello, I am \" + this.getClass().getSimpleName() \n");
        src.append("             + \"I import: \" + BaseCRUDAction.class.getName() \n");
        src.append("             + \"MD5.md5 = \" + MD5.md5(\"test\");\n");
        src.append("    }\n");
        src.append("}\n");

        System.out.println(src);

        try {
            DynamicClassLoader.getInstance().compile(fullName, src.toString());
            Class<?> clazz = DynamicClassLoader.getInstance().loadClass(fullName);
            Assert.assertNotNull(clazz);
            Assert.assertNotNull(clazz.newInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testReload() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        String fullName = "com.riversoft.test.DynaClass1";

        // Here we specify the source code of the class to be compiled
        StringBuilder src = new StringBuilder();
        src.append("package com.riversoft.test;\n");
        src.append("\n");
        src.append("import com.riversoft.util.MD5;\n");
        src.append("\n");
        src.append("public class DynaClass1 {\n");
        src.append("    public String toString() {\n");
        src.append("        return \"Hello, I am \" + this.getClass().getSimpleName() \n");
        src.append("             + \"MD5.md5 = \" + MD5.md5(\"test\");\n");
        src.append("    }\n");
        src.append("}\n");

        System.out.println(src);

        // compile
        DynamicClassLoader.getInstance().compile(fullName, src.toString());

        Class<?> clazz1 = DynamicClassLoader.getInstance().loadClass(fullName);
        Assert.assertNotNull(clazz1);
        Assert.assertNotNull(clazz1.newInstance());

        // loading
        Class<?> clazz2 = DynamicClassLoader.getInstance().loadClass(fullName);
        Assert.assertNotNull(clazz2);
        Assert.assertNotNull(clazz2.newInstance());
    }

    @Test
    public void testCompileAndLoading() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        String fullName = "com.riversoft.test.DynaClass3";

        // Here we specify the source code of the class to be compiled
        StringBuilder src = new StringBuilder();
        src.append("package com.riversoft.test;\n");
        src.append("\n");
        src.append("import com.riversoft.util.MD5;\n");
        src.append("\n");
        src.append("public class DynaClass3 {\n");
        src.append("    public String toString() {\n");
        src.append("        return \"Hello, I am \" + this.getClass().getSimpleName() \n");
        src.append("             + \"MD5.md5 = \" + MD5.md5(\"test\");\n");
        src.append("    }\n");
        src.append("}\n");

        System.out.println(src);

        Class<?> clazz1 = DynamicClassLoader.getInstance().compileAndLoadClass(fullName, src.toString());
        Assert.assertNotNull(clazz1);
        Assert.assertNotNull(clazz1.newInstance());

        // reloading
        Class<?> clazz2 = DynamicClassLoader.getInstance().loadClass(fullName);
        Assert.assertNotNull(clazz2);
        Assert.assertNotNull(clazz2.newInstance());
    }

    @Test
    public void testCompileError() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        String fullName = "com.riversoft.test.DynaClass4";

        // Here we specify the source code of the class to be compiled
        StringBuilder src = new StringBuilder();
        src.append("package com.riversoft.test;\n");
        src.append("\n");
        src.append("import com.riversoft.util.MD5;\n");
        src.append("import com.riversoft.platform.web.crud.BaseCRUDAction;\n");
        src.append("\n");
        src.append("public class DynaClass4 {\n");
        src.append("    public String toString() {\n");
        src.append("        return \"Hello, I am \" + this.getClass().getSimpleName() \n");
        src.append("             + \"I import: \" + BaseCRUDAction.class.getName1() \n");
        src.append("             + \"MD5.md5 = \" + MD5.md5(\"test\");\n");
        src.append("    }\n");
        src.append("}\n");

        System.out.println(src);

        try {
            DynamicClassLoader.getInstance().compile(fullName, src.toString());
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof RuntimeException);
        }
    }

    @Test
    public void testLoadStatic() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        String fullName = "java.lang.String";

        Class<?> clazz = DynamicClassLoader.getInstance().loadClass(fullName);
        Assert.assertNotNull(clazz);
        Assert.assertNotNull(clazz.newInstance());
    }

}
