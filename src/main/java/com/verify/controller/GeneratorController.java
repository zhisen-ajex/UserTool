package com.verify.controller;

import com.verify.services.FeignGeneratorService;
import com.verify.services.JarBuilderService;
import com.verify.services.OpenApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping("/api/generate")
public class GeneratorController {

    @Autowired
    private OpenApiService openApiService;

    @Autowired
    private FeignGeneratorService feignGeneratorService;

    @Autowired
    private JarBuilderService jarBuilderService;

    @PostMapping
    public ResponseEntity<Resource> generateJar() {
        try {//@RequestParam String swaggerUri  ?swaggerUri=https://api-aone-stage.aj-ex.com/v3/api-docs/addresses
        /*    // 1. 下载Swagger文档
            String openApiDoc = openApiService.fetchOpenApiDoc(swaggerUri);

            // 2. 将 OpenAPI 文档保存为临时 JSON 文件（⭐ 关键）
            File tempDir = Files.createTempDirectory("feign-gen").toFile();
            File openApiFile = new File(tempDir, "openapi.json");
            Files.write(openApiFile.toPath(), openApiDoc.getBytes(StandardCharsets.UTF_8));
*/

            // 1. 获取本地 swagger 文件（从 classpath）
            ClassPathResource resource1 = new ClassPathResource("swagger.json");

            // 创建临时目录
            File tempDir = Files.createTempDirectory("feign-gen").toFile();
            File openApiFile = new File(tempDir, "openapi.json");

            // 复制资源到临时文件
            Files.copy(resource1.getInputStream(), openApiFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);

//            Files.copy(resource1.getInputStream(), openApiFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            // 3. 生成 Feign 代码
            feignGeneratorService.generateFeignClient(openApiFile.getAbsolutePath(), tempDir.getAbsolutePath());

            // 3. 编译打包
            File jarFile = jarBuilderService.buildJar(tempDir);

            // 4. 返回JAR文件
            Resource resourceResult = new FileSystemResource(jarFile);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + jarFile.getName() + "\"")
                    .body(resourceResult);
          /*  Path path = Paths.get(jarFile.getAbsolutePath());
            Resource resource = new FileSystemResource(path);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + jarFile.getName() + "\"")
                    .body(resource);
*/
        } catch (Exception e) {

            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}