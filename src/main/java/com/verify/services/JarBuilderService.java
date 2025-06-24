package com.verify.services;

import org.apache.maven.cli.MavenCli;
import org.apache.maven.model.*;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import org.apache.maven.shared.invoker.*;

import org.apache.maven.shared.invoker.*;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Collections;
@Service
public class JarBuilderService {


    public File buildJar2(File projectDir) throws Exception {
        // 设置 Maven 日志级别为详细模式
        System.setProperty("org.slf4j.simpleLogger.log.org.apache.maven.cli", "debug");

        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(new File(projectDir, "pom.xml"));
        request.setGoals(Collections.singletonList("clean package"));

        Invoker invoker = new DefaultInvoker();
        invoker.setWorkingDirectory(projectDir);

        try {
            InvocationResult result = invoker.execute(request);
            if (result.getExitCode() != 0) {
                throw new RuntimeException("Maven build failed: " + result.getExecutionException());
            }
        } catch (MavenInvocationException e) {
            throw new RuntimeException("Maven invocation failed", e);
        }

        return new File(projectDir, "target/" + getArtifactFileName(projectDir));
    }

    private String getArtifactFileName(File projectDir) throws Exception {
        // 读取 pom.xml 并解析 artifactId 和 version
        File pomFile = new File(projectDir, "pom.xml");
        SAXReader reader = new SAXReader();
        Document document = reader.read(pomFile);
        Element rootElement = document.getRootElement();

        String artifactId = rootElement.element("artifactId").getTextTrim();
        String version = rootElement.element("version").getTextTrim();
        return artifactId + "-" + version + ".jar";
    }
    public File buildJar(File sourceDir) throws Exception {
        // 创建 Maven 模型
        Model model = new Model();
        model.setModelVersion("4.0.0");
        model.setGroupId("com.example.generated");
        model.setArtifactId("feign-client");
        model.setVersion("1.0.0");
        model.setPackaging("jar");

        // 设置 Java 编译版本
        Properties properties = new Properties();
        properties.setProperty("maven.compiler.source", "17");
        properties.setProperty("maven.compiler.target", "17");
        model.setProperties(properties);

        // 添加依赖 - Feign
        Dependency feignDep = new Dependency();
        feignDep.setGroupId("org.springframework.cloud");
        feignDep.setArtifactId("spring-cloud-starter-openfeign");
        feignDep.setVersion("4.0.4");
        model.addDependency(feignDep);

        // 添加构建插件
        Build build = new Build();

        // maven-compiler-plugin
        Plugin compilerPlugin = new Plugin();
        compilerPlugin.setGroupId("org.apache.maven.plugins");
        compilerPlugin.setArtifactId("maven-compiler-plugin");
        compilerPlugin.setVersion("3.10.1");

        PluginExecution execution = new PluginExecution();
        execution.setPhase("compile");
        execution.setGoals(List.of("compile"));
        compilerPlugin.addExecution(execution);

        build.addPlugin(compilerPlugin);

        // maven-jar-plugin（可选）
        Plugin jarPlugin = new Plugin();
        jarPlugin.setGroupId("org.apache.maven.plugins");
        jarPlugin.setArtifactId("maven-jar-plugin");
        jarPlugin.setVersion("3.2.2");
        build.addPlugin(jarPlugin);

        model.setBuild(build);

        // 写入 pom.xml
        File pomFile = new File(sourceDir, "pom.xml");
        try (FileOutputStream fos = new FileOutputStream(pomFile)) {
            MavenXpp3Writer writer = new MavenXpp3Writer();
            writer.write(fos, model);
        }

        // 执行 Maven 构建
        MavenCli maven = new MavenCli();
        int result = maven.doMain(new String[]{"clean", "package"}, sourceDir.getAbsolutePath(), System.out, System.out);

        if (result != 0) {
            throw new RuntimeException("Maven build failed");
        }

        return new File(sourceDir, "target/feign-client-1.0.0.jar");
    }
}