package com.github.mc.msql.utils;

import javassist.CtClass;
import javassist.NotFoundException;

import java.util.ArrayList;
import java.util.Arrays;

public class ClassUtil {

    public static boolean isSubclass(CtClass ctClass, Class zClass) throws NotFoundException {
        ArrayList<CtClass> ctClasses = new ArrayList<>(Arrays.asList(ctClass.getInterfaces()));
        int ch = 0;
        while (ch < ctClasses.size()) {
            CtClass aClass = ctClasses.get(ch);
            if(zClass.getName().equals(aClass.getName())) {
                return true;
            }
            ctClasses.addAll(Arrays.asList(aClass.getInterfaces()));
            ch++;
        }
        return false;
    }

    public static boolean isArray(CtClass ctClass) {
        String name = ctClass.getName();
        return name.endsWith("[]") || name.startsWith("[L");
    }
}
