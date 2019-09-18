package com.github.mc.msql.utils;

import com.github.mc.msql.annotations.MTable;
import com.github.mc.msql.annotations.MField;
import com.github.mc.msql.annotations.MID;
import com.github.mc.msql.annotations.MIgnore;
import com.github.mc.msql.TableAttribute;
import com.github.mc.msql.constant.Style;
import javassist.*;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.*;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.Map;

public class Utils {
    /***
     * 下划线命名转为驼峰命名
     *
     * @param para
     *        下划线命名的字符串
     */

    public static String UnderlineToHump(String para) {
        StringBuilder result = new StringBuilder();
        String a[] = para.split("_");
        for (String s : a) {
            if (result.length() == 0) {
                result.append(s.toLowerCase());
            } else {
                result.append(s.substring(0, 1).toUpperCase());
                result.append(s.substring(1).toLowerCase());
            }
        }
        return result.toString();
    }

    /***
     * 驼峰命名转为下划线命名
     *
     * @param para
     *        驼峰命名的字符串
     */

    private static String HumpToUnderline(String para) {
        StringBuilder sb = new StringBuilder(para);
        int temp = 0;//定位
        for (int i = 0; i < para.length(); i++) {
            if (Character.isUpperCase(para.charAt(i))) {
                sb.insert(i + temp, "_");
                temp += 1;
            }
        }
        return sb.toString();
    }

    /***
     * 驼峰命名转为下划线命名 小写
     *
     * @param para
     *        驼峰命名的字符串
     */

    public static String HumpToUnderlineI(String para) {
        String s = HumpToUnderline(para).toLowerCase();
        if (s.charAt(0) == '_') {
            s = s.substring(1);
        }
        return s;
    }

    /***
     * 驼峰命名转为下划线命名 大写
     *
     * @param para
     *        驼峰命名的字符串
     */

    public static String HumpToUnderlineX(String para) {
        String s = HumpToUnderline(para).toUpperCase();
        if (s.charAt(0) == '_') {
            s = s.substring(1);
        }
        return s;
    }

    /**
     * 语句解析失败
     *
     * @throws Exception
     */
    public static void twe() throws Exception {
        throw new Exception("sql语句解析失败");
    }

    /**
     * 解析转换表名
     *
     * @param s  原字段名称
     * @param style
     * @return
     */
    public static String analyzeTableName(String s, Style style) {
        if (style.equals(Style.UNDERLINE)) {
            s = Utils.HumpToUnderlineI(s);
        }
        return s;
    }

    /**
     * 解析转换字段名
     *
     * @param s  原字段名称
     * @param style
     * @return
     */
    public static String analyzeFieldName(String s, Style style) {
        if (style.equals(Style.UNDERLINE)) {
            s = Utils.HumpToUnderlineI(s);
        }
        return s;
    }

    /**
     * 获取表名+Code 的字段 没有返回null
     *
     * @param ctClass
     * @return
     */
    public static String getCode(CtClass ctClass) {
        String name = ctClass.getSimpleName() + "Code";
        name = name.substring(0, 1).toLowerCase() + name.substring(1);
        CtField code = null;
        try {
            code = ctClass.getDeclaredField(name);
        } catch (Exception ignored) {
        }
        if (code == null) {
            name = null;
        }
        return name;
    }

    public static String[] getParamAnnotations(Object[][] parameterAnnotations) {
        String[] values = new String[parameterAnnotations.length];
        for (int i = 0; i < parameterAnnotations.length; i++) {
            if (parameterAnnotations[i].length == 1 && parameterAnnotations[i][0] instanceof Param) {
                values[i] = ((Param) parameterAnnotations[i][0]).value();
            } else {
                values[i] = null;
            }

        }
        return values;
    }

    /**
     * 解析并获取方法参数及注解
     *
     * @param method
     * @return LinkedHashMap key = name, value = type
     */
    public static Map<String, String> params(CtMethod method) throws ClassNotFoundException, NotFoundException {
        Object[][] parameterAnnotations = method.getParameterAnnotations();
        LinkedHashMap<String, String> params = new LinkedHashMap();
        CtClass[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            if (parameterAnnotations[i].length == 1 && parameterAnnotations[i][0] instanceof Param) {
                params.put(((Param) parameterAnnotations[i][0]).value(), parameterTypes[i].getName());
            } else {
                CtField[] fields = parameterTypes[i].getDeclaredFields();
                for (CtField field : fields) {
                    Object mIgnore = field.getAnnotation(MIgnore.class);
                    if (mIgnore == null) {
                        params.put(field.getName(), field.getType().getName());
                    }
                }
            }
        }
        return params;
    }

    public static TableAttribute tableAttribute(CtClass ctClass, CtMethod method, Style[] values) throws ClassNotFoundException, NotFoundException {
        TableAttribute tableAttribute = new TableAttribute();
        Object mTable = ctClass.getAnnotation(MTable.class);
        String table = ctClass.getSimpleName();
        if (mTable != null) {
            tableAttribute.setTable(new String[]{table, ((MTable) mTable).value()});
        } else {
            tableAttribute.setTable(new String[]{table, Utils.analyzeTableName(table, values[1])});
        }

        LinkedHashMap<String, String[]> principal_linkage = new LinkedHashMap<>();
        LinkedHashMap<String, String[]> field = new LinkedHashMap<>();
        Object[][] parameterAnnotations = method.getParameterAnnotations();
        CtClass[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            Param param = null;
            for (int j = 0; j < parameterAnnotations[i].length; j++) {
                if (parameterAnnotations[i][j] instanceof Param) {
                    param = (Param) parameterAnnotations[i][j];
                    break;
                }
            }
            if (param != null) {
                String value = param.value();
                if ("id".equals(value)) {
                    principal_linkage.put(value, new String[]{"id", parameterTypes[i].getName()});
                } else {
                    field.put(value, new String[]{Utils.analyzeFieldName(value, values[0]), parameterTypes[i].getName()});
                }
            } else {
                CtField[] fields = parameterTypes[i].getDeclaredFields();
                for (CtField cField : fields) {
                    Object mIgnore = cField.getAnnotation(MIgnore.class);
                    boolean isFinal = Modifier.isFinal(cField.getModifiers());
                    if (mIgnore == null && !isFinal) {
                        Object mID = cField.getAnnotation(MID.class);
                        Object mField = cField.getAnnotation(MField.class);
                        if (mID != null) {
                            String value = ((MID) mID).value();
                            principal_linkage.put(cField.getName(), new String[]{value, parameterTypes[i].getName()});
                        } else if (mField != null) {
                            String value = ((MField) mField).value();
                            field.put(cField.getName(), new String[]{value, parameterTypes[i].getName()});
                        } else {
                            String value = cField.getName();
                            if ("id".equals(value)) {
                                principal_linkage.put(value, new String[]{value, cField.getType().getName()});
                            } else {
                                field.put(value, new String[]{Utils.analyzeFieldName(value, values[0]), cField.getType().getName()});
                            }
                        }
                    }
                }
            }
        }
        tableAttribute.setPrincipal_linkage(principal_linkage);
        tableAttribute.setField(field);
        return tableAttribute;
    }

    /**
     * 创建Javassist Annotation
     *
     * @param constpool
     * @param annotate
     * @param sql
     * @return
     */
    public static Annotation foundAnnotation(ConstPool constpool, String annotate, String sql) {
        Annotation annot = new Annotation(annotate, constpool);
        StringMemberValue[] StringMemberValues = {new StringMemberValue(sql, constpool)};
        ArrayMemberValue arrayMemberValue = new ArrayMemberValue(constpool);
        arrayMemberValue.setValue(StringMemberValues);
        annot.addMemberValue("value", arrayMemberValue);
        return annot;
    }


    /**
     * 获取方法返回类型
     *
     * @param method
     * @return
     * @throws NotFoundException
     */
    public static CtClass returnType(CtMethod method) throws NotFoundException {
        return method.getReturnType();
    }

}