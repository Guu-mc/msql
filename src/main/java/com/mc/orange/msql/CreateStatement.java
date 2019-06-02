package com.mc.orange.msql;

import com.mc.orange.msql.annotations.Group;
import com.mc.orange.msql.annotations.Having;
import com.mc.orange.msql.annotations.Order;
import com.mc.orange.msql.annotations.Restrict;
import javassist.CtClass;
import java.util.LinkedHashMap;

/**
 * 解析生成SQL语句
 */
public class CreateStatement {

    public static String insertSql(TableAttribute tableAttribute) {
        String[] table = tableAttribute.getTable();
        LinkedHashMap<String, String[]> principal_linkage = tableAttribute.getPrincipal_linkage();
        LinkedHashMap<String, String[]> field =  tableAttribute.getField();
        if(principal_linkage.size() == 0 && field.size() == 0){
            return "";
        }
        StringBuilder builder = new StringBuilder("insert into `");
        StringBuilder t = new StringBuilder();
        builder.append(table[1])
                .append("`(");
        for (String s : principal_linkage.keySet()) {
            builder.append("`")
            .append(principal_linkage.get(s)[0])
                    .append("`,");
            t.append("#{")
                    .append(s)
                    .append("},");
        }
        for (String s : field.keySet()) {
            builder.append("`")
                    .append(field.get(s)[0])
                    .append("`,");
            t.append("#{")
                    .append(s)
                    .append("},");
        }
        builder.setLength(builder.length() - 1);
        t.setLength(t.length() - 1);
        builder.append(")values(")
                .append(t.toString())
                .append(")");
        return builder.toString();
    }

    public static String updateSql(TableAttribute tableAttribute) {
        String[] table = tableAttribute.getTable();
        LinkedHashMap<String, String[]> principal_linkage = tableAttribute.getPrincipal_linkage();
        LinkedHashMap<String, String[]> field = tableAttribute.getField();
        if(principal_linkage.size() == 0 && field.size() == 0){
            return "";
        }

        StringBuilder builder = new StringBuilder("update `");
        builder.append(table[1])
                .append("` set ");
        for (String s : field.keySet()) {
            builder.append("`")
                    .append(field.get(s)[0])
                    .append("`=#{")
                    .append(s)
                    .append("},");
        }
        builder.setLength(builder.length() - 1);
        builder.append(" where");
        for (String s : principal_linkage.keySet()) {
            builder.append(" `")
                    .append(principal_linkage.get(s)[0])
                    .append("`=#{")
                    .append(s)
                    .append("} and");
        }
        builder.setLength(builder.length() - 4);
        return builder.toString();
    }

    public static String selectSql(TableAttribute tableAttribute, CtClass returnType) {
        String[] table = tableAttribute.getTable();
        LinkedHashMap<String, String[]> principal_linkage = tableAttribute.getPrincipal_linkage();
        LinkedHashMap<String, String[]> field = tableAttribute.getField();
        StringBuilder builder;
        if("int".equals(returnType.getName()) ||
                "java.lang.Integer".equals(returnType.getName())){
            builder = new StringBuilder("<script> select count(1) from `");
        } else {
            builder = new StringBuilder("<script>select * from `");
        }
        builder.append(table[1]);
        if(principal_linkage.size() == 0 && field.size() == 0){
            builder.append("`</script>");
        } else {
            builder.append("`<where>");
            Restrict[] restricts = tableAttribute.getRestricts();
            selectSqlWhere(builder, principal_linkage, restricts);
            selectSqlWhere(builder, field, restricts);
            builder.append("</where>");
            selectSqlOrder(builder, principal_linkage, field, tableAttribute.getOrders());
            selectSqlHaving(builder, principal_linkage, field, tableAttribute.getHavings());
            selectSqlGroup(builder, principal_linkage, field, tableAttribute.getGroups());
            builder.append("</script>");
        }
        return builder.toString();
    }

    private static void selectSqlOrder(StringBuilder builder,
                                       LinkedHashMap<String, String[]> principal_linkage,
                                       LinkedHashMap<String, String[]> field,
                                       Order[] orders) {
        for (int i = 0; i < orders.length; i++) {
            Order order = orders[i];
            String name = principal_linkage.get(order.key())[0];
            if(name == null) name = field.get(order.key())[0];
            if(name == null) throw new RuntimeException("order by is not key");
            if(i == 0) {
                builder.append(" order by ");
            }
            builder.append("`")
                    .append(name)
                    .append("` ")
                    .append(order.value());
            if(i != orders.length-1) {
                builder.append(", ");
            }
        }
    }

    private static void selectSqlHaving(StringBuilder builder,
                                        LinkedHashMap<String, String[]> principal_linkage,
                                        LinkedHashMap<String, String[]> field,
                                        Having[] havings) {
        for (int i = 0; i < havings.length; i++) {
            Having having = havings[i];
            String name = principal_linkage.get(having.key())[0];
            if(name == null) name = field.get(having.key())[0];
            if(name == null) throw new RuntimeException("having is not key");
            if(i != 0) {
                if(having.isAnd()) {
                    builder.append(" and ");
                } else {
                    builder.append(" or ");
                }
            } else {
                builder.append(" having ");
            }
            builder.append(having.value().replaceAll("@", "`"+name+"`")
                    .replaceAll("#", "#{"+having.key()+"}"));
        }

    }

    private static void selectSqlGroup(StringBuilder builder,
                                       LinkedHashMap<String, String[]> principal_linkage,
                                       LinkedHashMap<String, String[]> field,
                                       Group[] groups) {
        for (int i = 0; i < groups.length; i++) {
            Group group = groups[i];
            String name = principal_linkage.get(group.key())[0];
            if(name == null) name = field.get(group.key())[0];
            if(name == null) throw new RuntimeException("group is not key");
            if(i == 0) {
                builder.append(" group by ");
            }
            builder.append("`")
                    .append(name)
                    .append("`");
            if(i != groups.length-1) {
                builder.append(", ");
            }
        }

    }

    private static void selectSqlWhere(StringBuilder builder, LinkedHashMap<String, String[]> map, Restrict[] restricts){
        for (String s : map.keySet()) {
            builder.append("<if test='")
                    .append(s);
            if("java.lang.String".equals(map.get(s)[1])){
                builder.append(" != null and ")
                        .append(s)
                        .append(" != \"\"'> ");
            } else {
                builder.append(" != null'> ");
            }
            Restrict restrict = null;
            for (Restrict t : restricts) {
                if(s.equals(t.key())) {
                    restrict = t;
                    break;
                }
            }
            if(restrict == null){
                builder.append("and `")
                        .append(map.get(s)[0])
                        .append("`=#{")
                        .append(s)
                        .append("}</if>");
            }else {
                if(restrict.isAnd()) {
                    builder.append("and ");
                } else {
                    builder.append("or ");
                }
                builder.append(restrict.value().replaceAll("@", "`"+map.get(s)[0]+"`")
                                .replaceAll("#", "#{"+s+"}"))
                        .append("</if>");
            }
        }
    }

    public static String deleteSql(TableAttribute tableAttribute) {
        String[] table = tableAttribute.getTable();
        LinkedHashMap<String, String[]> principal_linkage = tableAttribute.getPrincipal_linkage();
        if(principal_linkage.size() == 0){
            return "";
        }
        StringBuilder builder = new StringBuilder("delete from `");
        builder.append(table[1])
                .append("` where ");
        int i = 0;
        for (String s : principal_linkage.keySet()) {
            if(i != 0){
                builder.append(" and ");
            }
            builder.append("<if test='")
                    .append(s);
            if("java.lang.String".equals(principal_linkage.get(s)[1])){
                builder.append(" != null and ")
                        .append(s)
                        .append(" != \"\"'> ");
            } else {
                builder.append(" != null'> `");
            }
            builder.append(principal_linkage.get(s)[0])
                    .append("`=#{")
                    .append(s)
                    .append("}</if>");
            i++;
        }
        return builder.toString();
    }

}