package com.verify.services;

import lombok.extern.slf4j.Slf4j;
import org.openapitools.codegen.ClientOptInput;
import org.openapitools.codegen.DefaultGenerator;
import org.openapitools.codegen.config.CodegenConfigurator;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务类：用于根据 OpenAPI 文档生成 Feign 客户端代码。
 */
@Service
@Slf4j
public class FeignGeneratorService {


    /**
     * 生成 Feign 客户端代码
     *
     * @param openApiDoc OpenAPI 规范文档的路径（本地或 URL）
     * @param outputDir  代码输出目录
     * @return 包含生成代码的目录 File 对象
     * @throws Exception 若生成过程中出现错误
     */
    public File generateFeignClient(String openApiDoc, String outputDir) throws Exception {
        // 配置生成器
        CodegenConfigurator configurator = new CodegenConfigurator()
                .setInputSpec(openApiDoc)              // OpenAPI 规范文档路径
                .setOutputDir(outputDir)               // 输出目录
                .setGeneratorName("spring")            // 使用 Spring 生成器
                .setLibrary("spring-cloud")
                .setTemplateDir("src/main/resources/templates");           // 使用 Spring Cloud 库

        // 设置模板目录
//        configurator.setTemplateDir("./templates/");
//        Map<String, String> globalProperties = new HashMap<>();
//        globalProperties.put("apis", "external-api");  // 只生成 external-api 的接口
//        configurator.setGlobalProperties(globalProperties);
        // 设置附加属性，如启用 Feign 客户端模式
        Map<String, Object> additionalProperties = new HashMap<>();
        additionalProperties.put("interfaceOnly", true); // 仅生成接口
        additionalProperties.put("useFeignAnnotations", true); // 使用 Feign 注解
        additionalProperties.put("dateLibrary", "java8"); // 使用 Java 8 的日期库
        additionalProperties.put("basePackage", "com.example.client"); // 基础包名


//        additionalProperties.put("feignClient", "true");      additionalProperties.put("feignClient", "true");
        additionalProperties.put("feignClient", "true"); // 开启 FeignClient
        additionalProperties.put("interfaceOnly", "true"); // 只生成接口
        additionalProperties.put("useFeignClientUrl", "true"); // 让 url 写到注解中
        additionalProperties.put("useSpringBoot3", "true"); // Spring Boot 3 风格（optional）

// 自定义注解属性（会影响生成的 @FeignClient）
        additionalProperties.put("feignClientName", "orders");
        additionalProperties.put("feignClientUrl", "${application.urls.orders-service}");
        additionalProperties.put("feignClientConfig", "FeignClientConfig");
        additionalProperties.put("hideGenerationTimestamp", "true");
        additionalProperties.put("useJakartaEe", "true"); // 如果你用的是 Jakarta EE 注解
        additionalProperties.put("modelPackage", "com.aiex.aone.ops.client.model");
        additionalProperties.put("apiPackage", "com.aiex.aone.ops.client.api");
        additionalProperties.put("invokerPackage", "com.aiex.aone.ops.client.invoker");
//        additionalProperties.put("dateLibrary", "java17");
        additionalProperties.put("useBeanValidation", "true");
        additionalProperties.put("serializableModel", "true");
        additionalProperties.put("generateModelTests", "false");
        additionalProperties.put("generateApiTests", "false");
        configurator.setAdditionalProperties(additionalProperties);
        configurator.setAdditionalProperties(additionalProperties);

        // 执行代码生成
        DefaultGenerator generator = new DefaultGenerator();
        ClientOptInput clientOptInput = configurator.toClientOptInput();
        List<File> generatedFiles = generator.opts(clientOptInput).generate();

        // 调试信息：打印生成的文件列表
        if (generatedFiles.isEmpty()) {
            log.error("No files were generated.");
        } else {
            for (File file : generatedFiles) {
                log.info("Generated file: " + file.getAbsolutePath());
            }
        }

        // ✅ 将生成代码移动到 src/main/java
        File srcMainJava = new File(outputDir, "src/main/java");
        srcMainJava.mkdirs();

        // 一般生成在 outputDir/src/gen/java 或 outputDir/src/main/java/com...
        File srcGenJava = new File(outputDir, "src/gen/java");
        if (srcGenJava.exists()) {
            org.apache.commons.io.FileUtils.copyDirectory(srcGenJava, new File(outputDir, "src/main/java"));
            org.apache.commons.io.FileUtils.deleteDirectory(srcGenJava); // 清理原目录
        }

        return new File(outputDir);
    }

    /**
     * 生成 Feign 客户端代码
     *
     * @param openApiDoc OpenAPI 规范文档的路径（本地或 URL）
     * @param outputDir  代码输出目录
     * @return 包含生成代码的目录 File 对象
     * @throws Exception 若生成过程中出现错误
     */
    public File generateFeignClient2(String openApiDoc, String outputDir) throws Exception {
        // 配置生成器
        CodegenConfigurator configurator = new CodegenConfigurator()
                .setInputSpec(openApiDoc)              // OpenAPI 规范文档路径
                .setOutputDir(outputDir)               // 输出目录
                .setGeneratorName("java")              // 使用 Java 生成器
                .setLibrary("feign")
                .setValidateSpec(false);                  // 使用 Feign 库（注意是 setLibrary）
        Map<String, String> globalProperties = new HashMap<>();
        globalProperties.put("apis", "external-api");  // 只生成 external-api 的接口

//        globalProperties.put("model", "false");   // 不生成 Model 类
//        globalProperties.put("docs", "false");    // 不生成文档
//        globalProperties.put("supportingFiles", "false"); // 不生成支持文件
        configurator.setGlobalProperties(globalProperties);



        // 设置附加属性，如启用 Feign 客户端模式
        Map<String, Object> additionalProperties = new HashMap<>();
        additionalProperties.put("feignClient", "true");
        additionalProperties.put("hideGenerationTimestamp", "true");
        additionalProperties.put("useJakartaEe", "true"); // 如果你用的是 Jakarta EE 注解
        additionalProperties.put("modelPackage", "com.aiex.aone.ops.client.model");
        additionalProperties.put("apiPackage", "com.aiex.aone.ops.client.api");
        additionalProperties.put("invokerPackage", "com.aiex.aone.ops.client.invoker");
        additionalProperties.put("dateLibrary", "java17");
        additionalProperties.put("useBeanValidation", "true");
        additionalProperties.put("serializableModel", "true");
        additionalProperties.put("generateModelTests", "false");
        additionalProperties.put("generateApiTests", "false");
        configurator.setAdditionalProperties(additionalProperties);
        // 执行代码生成
        System.out.println(configurator.toString()); // 打印当前配置以进行调试

        DefaultGenerator generator = new DefaultGenerator();
        generator.opts(configurator.toClientOptInput());
        List<File> files = generator.generate();

        if (files.isEmpty()) {
            throw new RuntimeException("No code was generated by OpenAPI Generator.");
        }

        return new File(outputDir);


/*        // ✅ 将生成代码移动到 src/main/java
        File srcMainJava = new File(outputDir, "src/main/java");
        srcMainJava.mkdirs();

        // 一般生成在 outputDir/src/gen/java 或 outputDir/src/main/java/com...
        File src = new File(outputDir, "src");
        if (src.exists()) {
            org.apache.commons.io.FileUtils.copyDirectory(src, new File(outputDir, "src/main/java"));
            org.apache.commons.io.FileUtils.deleteDirectory(src); // 清理原目录
        }

        return new File(outputDir);*/
    }
}
