package apiauto;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.alibaba.fastjson.JSON;
import io.restassured.RestAssured;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pojo.ExcelPojo;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;

/**
 * @author balala
 * @data 2021/8/22
 **/
public class DataDriverDemo {
    //登录(EXCEL数据驱动)
    @Test(dataProvider = "getLoginData01")
    public void login(ExcelPojo excelPojo) {
        //这个配置是让json小数返回返回类型是BigDecimal：
        RestAssured.config = RestAssured.config().jsonConfig(jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL));
        //配置全局url：
        RestAssured.baseURI = "http://api.lemonban.com/futureloan";

        //接口入参
        String InputParams = excelPojo.getInputParams();
        //接口地址
        String url=excelPojo.getUrl();
        //接口请求头
        String requestHeader = excelPojo.getRequestHeader();
        //把请求头转成map，（用例一般设计成json格式，方便转成map）
        Map requestHeaderMap=JSON.parseObject(requestHeader);

        Response loginres =
                given().
                        //有了全局配置，这行代码就不需要了
                        // config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL))).
                        body(InputParams).
                        headers(requestHeaderMap).
                when().
                        post(url).
                then().
                        log().all().extract().response();
        //断言：
        //1、读取excel里的期望响应结果，获取到的是json格式
        String expected=excelPojo.getExpected();
        //并把获取得到的结果转成map,并声明map中的key是String类型，value是Object类型：
        Map<String,Object> exceptedMap= (Map) JSON.parse(expected);
        //2、循环遍历map，用.keySet()取到期望结果里面的每一个key
        for (String exceptedKey : exceptedMap.keySet()){
            System.out.println("遍历key结果"+exceptedKey);
            //通过获取到的key来获取map里面的期望结果值value
            Object exceptedValue=exceptedMap.get(exceptedKey);
            //3、通过jsonPath表达式取到返回的实际结果value --- loginRes.jsonPath.get(key)
            //注意：exceptedValue是Object类型，所以actualValue也应用Object类型，否则下一步无法做比较
            Object actualValue=loginres.jsonPath().get(exceptedKey);
            //4、把实际值actualValue与期望值exceptedValue做对比
            Assert.assertEquals(actualValue,exceptedValue);
        }


        
    }
    @DataProvider   //方式一：读取excel作为数据驱动
    public Object[] getLoginData01(){
        File file=new File("D:\\学习资料\\java自动化\\接口自动化\\作业\\api_testcases_futureloan_v1.xls");
        //导入的参数对象,可以设置读取到哪一个sheet
        ImportParams importParams=new ImportParams();
        importParams.setStartSheetIndex(1);//读取第二个sheet中的数据
        //使用ExcelImportUtil工具方法 读取EXCEL文件,放到集合datalist中
        List<ExcelPojo> dataList=ExcelImportUtil.importExcel(file,ExcelPojo.class,importParams);
        //dataList是一个集合,但当前访问类型是个一维数组，因此要把集合转换成数组，用.toArray()即可:
        return dataList.toArray();
        //dataprovide支持一维和二维数组，当是一维数组时，就是一数组中的每一个元素作为数据驱动
        //当是二维数组时，就是以每一个一维数组作为驱动，注入到测试方法中去，实现数据驱动

    }

    @DataProvider  //方式二：直接在代码中设置参数数据
    public Object[][] getLoginDatas02(){
        Object[][] datas={
                {"18224417716","12345678"},
                {"13557684763","12345678"},
                {"18224417716","1234"},
        };
        return datas;
    }

//    public static void main(String[] args) {
//        File file=new File("D:\\学习资料\\java自动化\\接口自动化\\作业\\api_testcases_futureloan_v1.xls");
//        //导入的参数对象,可以设置读取到哪一个sheet
//        ImportParams importParams=new ImportParams();
//        importParams.setSheetNum(1);
//        //使用ExcelImportUtil工具方法 读取EXCEL文件
//        List<Object> dataList=ExcelImportUtil.importExcel(file,ExcelPojo.class,importParams);
//        用for循环即可打印出对应sheet的用例：
//        for (Object object : dataList) {
//            System.out.println(object);
//        }



    }

