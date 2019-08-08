package com.github.mc.msql;

import com.github.mc.msql.annotations.Msql;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;

public class Start implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {
    private ResourceLoader resourceLoader;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(MapperScan.class.getName()));
        AnnotationAttributes mAnnoAttrs = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(Msql.class.getName()));
        ClassPathMapperScanner scanner = new ClassPathMapperScanner(beanDefinitionRegistry);

        if (resourceLoader != null) {
            scanner.setResourceLoader(resourceLoader);
        }
        assert mAnnoAttrs != null;
        boolean showSql = mAnnoAttrs.getBoolean("showSql");
        assert annoAttrs != null;
        String[] values = annoAttrs.getStringArray("value");
        MSqlStart.scan(values, showSql);
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
