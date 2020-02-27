package com.mutu.plus;


import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import org.springframework.context.annotation.PropertySource;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

@PropertySource(value = {"classpath: local.properties", "file:${spring.profiles.path}/local.properties"},
        ignoreResourceNotFound = true)
public class MybatiesPlusGenerator {

    public static void main(String[] args) {

        Properties properties;
        try {
            properties = new Properties();
            // jar包传配置文件形式
//            String filePath = "local.properties";
            // 读取resources下的配置文件
            String filePath = "src/main/resources/local.properties";
            InputStream in = new BufferedInputStream(new FileInputStream(filePath));
            properties.load(new InputStreamReader(in, "UTF-8"));
        }catch (Exception e){
            e.printStackTrace();
            return;
        }

        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();

        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");
        gc.setOutputDir(projectPath + properties.getProperty("output.dir", "/src/main/java"));
        gc.setAuthor(properties.getProperty("author", "nobody"));
        gc.setOpen(false);
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl(properties.getProperty("data.url"));
        dsc.setDriverName(properties.getProperty("data.driver.name"));
        dsc.setUsername(properties.getProperty("data.username"));
        dsc.setPassword(properties.getProperty("data.password"));
        mpg.setDataSource(dsc);

        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setModuleName(properties.getProperty("model.name"));
        pc.setParent(properties.getProperty("model.parent", "com"));
        mpg.setPackageInfo(pc);

        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                HashMap<String, Object> map = new HashMap<>();
                map.put("nameSpace", pc.getParent());
                this.setMap(map);
            }
        };

        // Mapper.xml文件生成。如果模板引擎是 freemarker
        String templatePath = properties.getProperty("templatePath", "/templates/mapper.xml.ftl");

        // 自定义mapper.xml配置
        List<FileOutConfig> focList = new ArrayList<>();
        focList.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                return projectPath + "/src/main/java/com/baomidou/" + pc.getModuleName()
                        + "/mapper/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
            }
        });
        /*
        cfg.setFileCreate(new IFileCreate() {
            @Override
            public boolean isCreate(ConfigBuilder configBuilder, FileType fileType, String filePath) {
                // 判断自定义文件夹是否需要创建
                checkDir("调用默认方法创建的目录");
                return false;
            }
        });
        */
        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);

        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig();

        // 配置自定义输出模板
        //指定自定义模板路径，注意不要带上.ftl/.vm, 会根据使用的模板引擎自动识别
        // templateConfig.setEntity("templates/entity2.java");
        // templateConfig.setService();
        // templateConfig.setController();

        templateConfig.setXml(null);
        mpg.setTemplate(templateConfig);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        strategy.setSuperEntityClass(properties.getProperty("super.entity.class"));
        strategy.setEntityLombokModel(true);
        strategy.setRestControllerStyle(Boolean.valueOf(properties.getProperty("rest.controller")));
        // 公共父类
        strategy.setSuperControllerClass(properties.getProperty("super.controller.class"));
        strategy.setInclude(properties.getProperty("tables").split(","));
        strategy.setControllerMappingHyphenStyle(true);
        strategy.setTablePrefix(pc.getModuleName() + "_");
        mpg.setStrategy(strategy);
        mpg.setTemplateEngine(new FreemarkerTemplateEngine());
        mpg.execute();
    }
}
