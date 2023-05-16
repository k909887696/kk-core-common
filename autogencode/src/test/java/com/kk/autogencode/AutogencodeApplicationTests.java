package com.kk.autogencode;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.github.jeffreyning.mybatisplus.service.IMppService;
import com.github.jeffreyning.mybatisplus.service.MppServiceImpl;
import com.kk.common.dao.mapper.RootMapper;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.*;


class AutogencodeApplicationTests {

    private static String ENTITY_TEMPLATE = "templates/entity.java";
    /** mapper.xml输出模板 */
    private static String XML_TEMPLATE = "templates/mapper.xml";
    /** mapper.java输出模板 */
    private static String MAPPER_TEMPLATE = "templates/mapper.java";
   /** service输出模板 */
    private static String SERVICE_TEMPLATE = "templates/service.java";
   /** serviceImpl输出模板 */
    private static String SERVICE_IMPL_TEMPLATE = "templates/serviceImpl.java";
   /** controller输出模板 */
    private static String CONTROLLER_TEMPLATE = "templates/controller.java";

    @Test
    void autocode() {
        List<String> tables = new ArrayList<>();

  /*      tables.add("collection_policy");
        tables.add("collection_task");
        tables.add("collection_task_history");
        tables.add("cn_m");
        tables.add("concept");

        tables.add("concept_detail");
        tables.add("concept_money_flow");
         tables.add("concept_daily");
        tables.add("daily");
        tables.add("daily_basic");
        tables.add("daily_time");
        tables.add("index_basic");
        tables.add("index_classify");
        tables.add("index_member");
        tables.add("invoke_type");
        tables.add("kdj_cross");
        tables.add("max_pct_chg");
        tables.add("money_flow");
        tables.add("monthly");
        tables.add("security_selection");
        tables.add("serial_no");
        tables.add("stock_basic");
        tables.add("stock_day_kdj");
        tables.add("stock_fluctuation");
        tables.add("sy_user");
        tables.add("trade_cal");
        tables.add("weekly");
        tables.add("index_daily");
        tables.add("collection_task");
        tables.add("collection_task_history");*/
        tables.add("cn_m");
        tables.add("concept");
        tables.add("concept_daily");
        tables.add("concept_detail");
        tables.add("concept_money_flow");
        tables.add("daily");

        FastAutoGenerator.
                //create("jdbc:mysql://192.168.90.126:3309/quantization?useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowMultiQueries=true","hotel","^d4DD8$g,uccDB=F")
                 create("jdbc:mysql://192.168.2.235:3306/quantization?useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowMultiQueries=true","root","-random8201227")
                .globalConfig(builder -> {
                    builder.author("kk")               //作者
                            .outputDir(System.getProperty("user.dir")+"\\src\\main\\java")    //输出路径(写到java目录)
                            .enableSwagger()           //开启swagger
                            .dateType(DateType.ONLY_DATE)
                            .commentDate("yyyy-MM-dd")

                            .fileOverride();            //开启覆盖之前生成的文件

                })
                .packageConfig(builder -> {
                    builder.parent("com.kk.business")
                            .moduleName("quantization")
                            .entity("dao.entity")
                            .service("service")
                            .serviceImpl("service.impl")
                            .controller("controller")
                            .mapper("dao.mapper")
                            .xml("mapper")
                            .other("model")
                            .pathInfo(Collections.singletonMap(OutputFile.mapperXml,System.getProperty("user.dir")+"\\src\\main\\resources\\mapper"));
                })
                .strategyConfig(builder -> {
                    builder.addInclude(tables)
                            //.addTablePrefix("p_")
                            .serviceBuilder()

                            //.formatServiceFileName("%sService")
                            .formatServiceImplFileName("%sServiceImpl")
                            .superServiceClass(IMppService.class)
                            .superServiceImplClass(MppServiceImpl.class)
                            .entityBuilder()
                            .enableLombok()
                            //.logicDeleteColumnName("deleted")
                            .enableTableFieldAnnotation()

                            .controllerBuilder()

                            .formatFileName("%sController")
                            .enableRestStyle()
                            .mapperBuilder().enableBaseColumnList().enableBaseResultMap()
                            .superClass(RootMapper.class)
                            .formatMapperFileName("%sMapper")

                            //.enableMapperAnnotation()
                            .formatXmlFileName("%sMapper")
                    ;
                })
                .templateConfig(builder -> {
                    builder.controller(CONTROLLER_TEMPLATE)
                            .entity(ENTITY_TEMPLATE)
                            .mapper(MAPPER_TEMPLATE)
                            .service(SERVICE_TEMPLATE)
                            .serviceImpl(SERVICE_IMPL_TEMPLATE)
                            .mapperXml(XML_TEMPLATE);
                })

                .templateEngine(new EnhanceFreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .injectionConfig(builder -> {
                    /**自定义生成模板参数**/
                    Map<String,Object> paramMap = new HashMap<>();
                    List<String> customImplNamespace = new ArrayList<>();
                    customImplNamespace.add("com.kk.common.utils.MapperUtils");
                    customImplNamespace.add("com.kk.common.base.model.PageResult");
                    customImplNamespace.add("com.kk.common.exception.BusinessException");

                    paramMap.put("customImplNamespace",customImplNamespace);
                    List<String> customServiceNamespace = new ArrayList<>();
                    customServiceNamespace.add("com.kk.common.base.model.PageResult");
                    paramMap.put("customServiceNamespace",customServiceNamespace);

                    /** 自定义 生成类**/
                    Map<String,String> customFileMap = new HashMap<>();
                    /**PO实体**/

                    /**Vo实体**/
                    customFileMap.put("vo"+File.separator+"%sListVo.java", "/templates/listvo.java.ftl");
                    customFileMap.put("vo"+File.separator+"%sAddVo.java", "/templates/addvo.java.ftl");
                    customFileMap.put("vo"+File.separator+"%sEditVo.java", "/templates/editvo.java.ftl");
                    customFileMap.put("vo"+File.separator+"%sDeleteVo.java", "/templates/deletevo.java.ftl");
                    customFileMap.put("vo"+File.separator+"%sDetailsVo.java", "/templates/detailsvo.java.ftl");
                    /**DTO实体**/
                    customFileMap.put("dto"+File.separator+"%sDto.java", "/templates/dto.java.ftl");
                    customFileMap.put("dto"+File.separator+"%sListDto.java", "/templates/listdto.java.ftl");
                    builder.customMap(paramMap)
                            .customFile(customFileMap);
                })
                .execute();
    }



}
