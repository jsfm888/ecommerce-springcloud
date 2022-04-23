package com.njust.ecommerce;

import cn.smallbun.screw.core.Configuration;
import cn.smallbun.screw.core.engine.EngineConfig;
import cn.smallbun.screw.core.engine.EngineFileType;
import cn.smallbun.screw.core.engine.EngineTemplateType;
import cn.smallbun.screw.core.execute.DocumentationExecute;
import cn.smallbun.screw.core.process.ProcessConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DBDocTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void buildDBDoc() {

        DataSource dataSourceMysql = applicationContext.getBean(DataSource.class);

        // 生成文件配置
        EngineConfig engineConfig = EngineConfig.builder()
                // 生成文件路径
                .fileOutputDir("/Users/qinwenqiang/Documents/轨迹/e-commerce")
                // 打开目录
                .openOutputDir(false)
                // 文件类型
                .fileType(EngineFileType.HTML)
                .produceType(EngineTemplateType.freemarker).build();

        // 生成文档配置, 包含自定义版本号、描述等等
        // 数据库名_description_version.html
        Configuration config = Configuration.builder()
                .version("v1.0")
                .description("e-commerce-springcloud")
                .dataSource(dataSourceMysql)
                .engineConfig(engineConfig)
                .produceConfig(getProduceConfig())
                .build();

        // 执行生成
        new DocumentationExecute(config).execute();
    }

    /**
     * 配置想要生成的数据表和想要忽略的数据表
     * */
    private ProcessConfig getProduceConfig() {
        //想要忽略的数据表
        List<String> ignoreTableName = Collections.singletonList("undo_log");
        //忽略表前缀，忽略以 a、b开头的数据库表
        List<String> ignorePrefix = Arrays.asList("a", "b");
        //忽略表后缀
        List<String> ignoreSuffix = Arrays.asList("_test", "_Test");

        return ProcessConfig.builder()
                .designatedTableName(Collections.emptyList())
                .designatedTablePrefix(Collections.emptyList())
                .designatedTableSuffix(Collections.emptyList())
                .ignoreTableName(ignoreTableName)
                .ignoreTablePrefix(ignorePrefix)
                .ignoreTableSuffix(ignoreSuffix)
                .build();
    }
}
