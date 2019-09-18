package com.github.mc.msql;

import com.github.mc.msql.annotations.Msql;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import java.util.*;

public class Start implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {
    private ResourceLoader resourceLoader;
    private MsqlProperties msqlProperties = new MsqlProperties();

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(MapperScan.class.getName()));
        AnnotationAttributes mAnnoAttrs = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(Msql.class.getName()));
        if (mAnnoAttrs != null && annoAttrs != null) {
            boolean showSql = mAnnoAttrs.getBoolean("showSql");
            String[] values = annoAttrs.getStringArray("value");
            msqlProperties.setShowSql(showSql);
            MSqlStart.scan(values, msqlProperties);
        } else {
            BeanDefinition beanDefinition = beanDefinitionRegistry.getBeanDefinition("org.springframework.boot.autoconfigure.AutoConfigurationPackages");
            ConstructorArgumentValues constructorArgumentValues = beanDefinition.getConstructorArgumentValues();
            Map<Integer, ConstructorArgumentValues.ValueHolder> indexedArgumentValues = constructorArgumentValues.getIndexedArgumentValues();

            for (Integer integer : indexedArgumentValues.keySet()) {
                ConstructorArgumentValues.ValueHolder valueHolder = indexedArgumentValues.get(integer);
                Object value = valueHolder.getValue();
                if(value instanceof String[]) {
                    MSqlStart.scan((String[]) value, msqlProperties);
                }
            }
        }
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setEnvironment(Environment environment) {
        //手动获取配置文件数据
        Boolean showSql = environment.getProperty("msql.show-sql", Boolean.class);
        if(showSql != null) {
            msqlProperties.setShowSql(showSql);
        }
    }
}
