package com.verify.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class SqlFileLoader {


    private final ResourceLoader resourceLoader;


    @Value("${report.sql-path:classpath:sql/}")
    private String sqlPath;


    public SqlFileLoader(ResourceLoader resourceLoader){

        this.resourceLoader = resourceLoader;

    }


    public String load(String fileName){

        try {

            Resource resource = resourceLoader.getResource(sqlPath + fileName);


            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);


        } catch(Exception e){

            log.error("SQL读取失败:{}",fileName,e);

            throw new RuntimeException(
                    "SQL文件读取失败:"+fileName,e
            );

        }

    }

}
