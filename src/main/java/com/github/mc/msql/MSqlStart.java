package com.github.mc.msql;

import com.github.mc.msql.annotations.MInsert;
import com.github.mc.msql.annotations.MSelect;
import com.github.mc.msql.annotations.MUpdate;
import com.github.mc.msql.utils.PackageUtil;
import com.github.mc.msql.utils.Utils;
import com.github.mc.msql.annotations.MDelete;
import com.github.mc.msql.annotations.*;
import com.github.mc.msql.constant.Constant;
import com.github.mc.msql.constant.Case;
import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.annotation.Annotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import java.util.List;

final class MSqlStart {
    private static final Logger logger = LoggerFactory.getLogger(MSqlStart.class);
    private static final ClassPool pool = ClassPool.getDefault();
    static {
        pool.insertClassPath(new ClassClassPath(MSqlStart.class));
    }

    static void scan(String[] value, boolean showSentence) {
        for (String s : value) {
            if(StringUtils.hasText(s)){
                scan(s, showSentence);
            }
        }
    }

    private static void scan(String value, boolean showSentence) {
        try {
            List<String> classNames = PackageUtil.getClassName(value);
            for (String className : classNames) {
                mapperScan(className, showSentence);
            }
        } catch (Exception e){
            logger.error("Msql解析异常", e);
        }

    }

    private static void mapperScan(String classname, boolean showSentence) throws Exception {
        CtClass cc = pool.get(classname);
        CtMethod[] methods = cc.getDeclaredMethods();
        boolean ism = false;
        for (CtMethod method : methods) {
            MInsert mInsert = (MInsert) method.getAnnotation(MInsert.class);
            if(mInsert != null){
                foundInsert(method, mInsert);
                ism = true;
            }
            MUpdate mUpdate = (MUpdate) method.getAnnotation(MUpdate.class);
            if(mUpdate != null){
                foundUpdate(method, mUpdate);
                ism = true;
            }
            MSelect mSelect = (MSelect) method.getAnnotation(MSelect.class);
            if(mSelect != null){
                foundSelect(method, mSelect);
                ism = true;
            }
            MDelete mDelete = (MDelete) method.getAnnotation(MDelete.class);
            if(mDelete != null){
                foundDelete(method, mDelete);
                ism = true;
            }
        }
        if(ism){
            cc.toClass();

            if(showSentence){
                logger.info(cc.getName());
                CtMethod[] methods1 = cc.getDeclaredMethods();
                for (CtMethod ctMethod : methods1) {
                    Object[] annotations = ctMethod.getAnnotations();
                    for (Object annotation : annotations) {
                        logger.info(annotation.toString());
                    }
                    logger.info(ctMethod.getName());
                }
            }
        }
    }

    private static void changeAnnotate(CtMethod method, String annotate, String sql) {
        MethodInfo info = method.getMethodInfo();
        ConstPool constpool = info.getConstPool();

        AnnotationsAttribute attr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);

        Annotation annot = Utils.foundAnnotation(constpool, annotate, sql);
        attr.addAnnotation(annot);
        AnnotationsAttribute annotationsAttribute =(AnnotationsAttribute) info.getAttribute(AnnotationsAttribute.visibleTag);
        Annotation[] annotations = annotationsAttribute.getAnnotations();
        for (Annotation annotation : annotations) {
            if(!Constant.MINSERT.equals(annotation.getTypeName()) &&
                    !Constant.MUPDATE.equals(annotation.getTypeName()) &&
                    !Constant.MSELECT.equals(annotation.getTypeName()) &&
                    !Constant.MDELETE.equals(annotation.getTypeName())){
                attr.addAnnotation(annotation);
            }
        }
        info.addAttribute(attr);
    }

    private static void foundDelete(CtMethod method, MDelete mDelete) throws Exception {
        checkValue(mDelete.value());
        TableAttribute tableAttribute = iudTableAttribute(method, mDelete.value(), mDelete.pojo());
        changeAnnotate(method, Constant.DELETE, CreateStatement.deleteSql(tableAttribute));
    }

    private static void foundSelect(CtMethod method, MSelect mSelect) throws Exception {
        checkValue(mSelect.value());
        TableAttribute tableAttribute = sTableAttribute(method, mSelect.value(), mSelect.pojo());
        tableAttribute.setRestricts(mSelect.restrict());
        tableAttribute.setGroups(mSelect.group());
        tableAttribute.setHavings(mSelect.having());
        tableAttribute.setOrders(mSelect.order());
        CtClass returnType = Utils.returnType(method);
        changeAnnotate(method, Constant.SELECT, CreateStatement.selectSql(tableAttribute, returnType));
    }

    private static void foundUpdate(CtMethod method, MUpdate mUpdate) throws Exception {
        checkValue(mUpdate.value());
        TableAttribute tableAttribute = iudTableAttribute(method, mUpdate.value(), mUpdate.pojo());
        changeAnnotate(method, Constant.UPDATE, CreateStatement.updateSql(tableAttribute));
    }

    private static void foundInsert(CtMethod method, MInsert mInsert) throws Exception {
        checkValue(mInsert.value());
        TableAttribute tableAttribute = iudTableAttribute(method, mInsert.value(), mInsert.pojo());
        changeAnnotate(method, Constant.INSERT, CreateStatement.insertSql(tableAttribute));
    }

    private static void checkValue(String[] values) throws Exception {
       if(values.length > 2){
           Utils.twe();
       }
       for (String value : values) {
           checkCase(value);
       }
    }

    private static void checkCase(String value) throws Exception {
        if(!value.equals(Case.CAMEL_CASE) && !value.equals(Case.UNDER_SCORE_CASE)) {
            Utils.twe();
        }
    }

    /**
     *  insert update delete 语句获取表属性
     *  方法限定 pojo 为 Object 时 解析的方法第一个参数必须为 实例 pojo
     */
    private static TableAttribute iudTableAttribute(CtMethod method, String[] values, Class pojo) throws NotFoundException, ClassNotFoundException {
        CtClass[] parameterTypes = method.getParameterTypes();
        CtClass ctClass;
        if (!"java.lang.Object".equals(pojo.getName())) {
            ctClass = pool.get(pojo.getName());
        } else {
            ctClass = parameterTypes[0];
        }
        return Utils.tableAttribute(ctClass, method, values);
    }

    /**
     *  select 语句获取表属性
     *   方法限定 pojo 为 Object 解析的方法返回结果为Map. int. List(没有填写泛型) 时 第一个参数必须为 实例 pojo
     *   否则返回结果为 pojo
     */
    private static TableAttribute sTableAttribute(CtMethod method, String[] values, Class pojo) throws NotFoundException, ClassNotFoundException {
        CtClass[] parameterTypes = method.getParameterTypes();
        CtClass returnType = method.getReturnType();
        CtClass ctClass;
        if (!"java.lang.Object".equals(pojo.getName())) {
            ctClass = pool.get(pojo.getName());
        } else if (!"java.util.Map".equals(returnType.getName()) && !"int".equals(returnType.getName()) && !"java.lang.Integer".equals(returnType.getName())) {
            if ("java.util.List".equals(returnType.getName())) {
                String genericSignature = method.getGenericSignature();
                if (genericSignature == null) {
                    ctClass = parameterTypes[0];
                } else {
                    genericSignature = genericSignature.replaceAll("^\\((.*)\\)", "");
                    genericSignature = genericSignature.substring(17, genericSignature.length() - 3);
                    genericSignature = genericSignature.replace("/", ".");
                    ctClass = pool.get(genericSignature);
                }
            } else {
                ctClass = returnType;
            }
        } else {
            ctClass = parameterTypes[0];
        }
        return Utils.tableAttribute(ctClass, method, values);
    }

}
