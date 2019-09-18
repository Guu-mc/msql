package com.github.mc.msql;

import com.github.mc.msql.constant.Style;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
        prefix = MsqlProperties.MSQL_PREFIX
)
public class MsqlProperties {
    public static final String MSQL_PREFIX = "msql";
    /**
     * 显示 mybatis sql 语句
     */
    private boolean showSql = true;
    /**
     * 表命名风格
     * 驼峰命名: Style.HUMP
     * 下划线命名: Style.UNDERLINE
     */
    private Style namedTableStyle = Style.UNDERLINE;
    /**
     * 表字段命名风格
     * 驼峰命名: Style.HUMP
     * 下划线命名: Style.UNDERLINE
     */
    private Style namedTableFieldStyle = Style.UNDERLINE;

    public boolean isShowSql() {
        return showSql;
    }

    public void setShowSql(boolean showSql) {
        this.showSql = showSql;
    }

    public Style getNamedTableStyle() {
        return namedTableStyle;
    }

    public void setNamedTableStyle(Style namedTableStyle) {
        this.namedTableStyle = namedTableStyle;
    }

    public Style getNamedTableFieldStyle() {
        return namedTableFieldStyle;
    }

    public void setNamedTableFieldStyle(Style namedTableFieldStyle) {
        this.namedTableFieldStyle = namedTableFieldStyle;
    }
}
