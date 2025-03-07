package com.ssy.factory;


import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * TODO
 *
 * @author Mr.fengjun
 * @version 1.0
 * @date 2025/3/5
 * @email 3278440884@qq.com
 * YAML文件配置类，由于会读取默认的application.yml,所以要手动重写PropertySourceFactory指定读取文件
 */


public class YamlPropertySourceFactory implements PropertySourceFactory {
    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        // 使用 Spring 提供的 YamlPropertiesFactoryBean 将 YAML 转为 Properties
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(resource.getResource());
        Properties properties = factory.getObject();

        // 若没指定 name，就用文件名做 name
        return new PropertiesPropertySource(
                (name != null ? name : resource.getResource().getFilename()),
                properties
        );
    }
}