package com.github.mc.msql;

import com.github.mc.msql.annotations.Group;
import com.github.mc.msql.annotations.Order;
import com.github.mc.msql.annotations.Where;
import com.github.mc.msql.annotations.Having;

import java.util.LinkedHashMap;

public class TableAttribute {
    /**
     * table ->  value -- String数组 [0]类名 [1]表名
     */
    private String[] table;
    /**
     * principal_linkage ->  value -- Map - key: 主键字段名字 value: String数组 [0]主键表字段名字 [1] java数据类型
     */
    private LinkedHashMap<String, String[]> principal_linkage;
    /**
     * field ->  value -- Map - key: 字段名字 value: String数组 [0]字段表名字 [1] java数据类型
     */
    private LinkedHashMap<String, String[]> field;

    private Where[] wheres;

    private Group[] groups;

    private Having[] havings;

    private Order[] orders;

    public String[] getTable() {
        return table;
    }

    public LinkedHashMap<String, String[]> getPrincipal_linkage() {
        return principal_linkage;
    }

    public void setPrincipal_linkage(LinkedHashMap<String, String[]> principal_linkage) {
        this.principal_linkage = principal_linkage;
    }

    public LinkedHashMap<String, String[]> getField() {
        return field;
    }

    public void setTable(String[] table) {
        this.table = table;
    }

    public void setField(LinkedHashMap<String, String[]> field) {
        this.field = field;
    }

    public Where[] getWheres() {
        return wheres;
    }

    public void setWheres(Where[] wheres) {
        this.wheres = wheres;
    }

    public Order[] getOrders() {
        return orders;
    }

    public void setOrders(Order[] orders) {
        this.orders = orders;
    }

    public Having[] getHavings() {
        return havings;
    }

    public void setHavings(Having[] havings) {
        this.havings = havings;
    }

    public Group[] getGroups() {
        return groups;
    }

    public void setGroups(Group[] groups) {
        this.groups = groups;
    }
}
