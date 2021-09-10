package common;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import data.Constants;
import data.Environment;
import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.response.Response;
import net.bytebuddy.asm.Advice;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import pojo.ExcelPojo;
import util.JDBCUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;

/**
 * @author balala
 * @data 2021/8/26
 **/
public class BaseTest {
    @BeforeTest
    /**全局配置
     * 要在所有测试类之前运行，所以用Befortest
     */
    public void Globle() throws FileNotFoundException {
        //这个配置是让json小数返回返回类型是BigDecimal：
        RestAssured.config = RestAssured.config().jsonConfig(jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL));
        //配置全局url：
        RestAssured.baseURI = Constants.Baseurl;
        //--------以下代码是将所有请求日志都生成并存放在同一个路径下（log中），但这样并不方便查看
        //那么可以将测试用例日志单独保存，并且可以把日志放进Allure报表中，更方便查看，具体见封装在request中对日志放的相关代码------------------
        //全局重定向输出到指定文件中(通过REST-Assured的filters过滤器实现)
        //先创建file对象，传入指定文件夹的路径，用System.getProperty("user.dir")可以得到项目的根目录
        //再加上指定存放的文件夹”\log“即可 ，注意斜杠要转义所以有两个斜杠。
//        File file = new File(System.getProperty("user.dir")+"\\log");
//        //判断如果不存在则创建
//        if(!file.exists()){
//            file.mkdir();
//        }
//        PrintStream fileOutPutStream = new PrintStream(new File("log/test_all.log"));
//        RestAssured.filters(new RequestLoggingFilter(fileOutPutStream),new ResponseLoggingFilter(fileOutPutStream));
    }

    /**
     * 对 post、get、put、patch请求的二次封装
     *
     * @param excelPojo 是excel每行数据对应的对象
     * @return 接口相应结果
     */

    public Response request(ExcelPojo excelPojo,String interfaceModuleName)  {
        //为每个请求作单独的日志保存：
        //判断日志输出到文件or控制台，配置在Constants中
        //true的话，就指定输出文件相关配置即执行以下日志配置代码，false则不会走下面代码则是输出到控制台中
        String logFilePath;
        if(Constants.LOG_TO_FILE) {
            File dirPath = new File(System.getProperty("user.dir") + "\\log\\"+interfaceModuleName);
            //判断，如果文件不存在则创建，因为创建的文件夹是多层，所以用mkdirs
            if (!dirPath.exists()) {
                //创建目录层级，log/接口模块
                dirPath.mkdirs();
            }
            logFilePath = dirPath  + "\\test"+excelPojo.getCaseId() + ".log";
            PrintStream fileOutPutStream = null;
            try {
                fileOutPutStream = new PrintStream(new File(logFilePath));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            RestAssured.config = RestAssured.config().logConfig(LogConfig.logConfig().defaultStream(fileOutPutStream));
        }


        //请求接口地址、方法、请求头、请求参数
        String url = excelPojo.getUrl();
        String method = excelPojo.getMethod();
        String headers = excelPojo.getRequestHeader();
        String params = excelPojo.getInputParams();
        //请求头转Map
        Map<String, Object> headersMap = JSON.parseObject(headers);

        Response res = null;
        //对post、get、put、patch请求做封装
        if ("get".equalsIgnoreCase(method)) {
            res = given().log().all().headers(headersMap).when().get(url).then().log().all().extract().response();
        } else if ("post".equalsIgnoreCase(method)) {
            res = given().log().all().headers(headersMap).body(params).when().post(url).then().log().all().extract().response();
        } else if ("put".equalsIgnoreCase(method)) {
            res = given().log().all().headers(headersMap).body(params).when().put(url).then().log().all().extract().response();
        } else if ("patch".equalsIgnoreCase(method)) {
            res = given().log().all().headers(headersMap).body(params).when().patch(url).then().log().all().extract().response();
        }
        //请求发生之后再将日志作为附件展示在Allure报表中，"接口请求响应信息"是附件信息，后面是日志路径
        //但同上面日志保存的配置一样，需要判断一下日志是否输出到文件中，如果配置true，才会走下面代码，将日志作为附件展示在Allure报表中
        if(Constants.LOG_TO_FILE) {
            try {
                Allure.addAttachment("接口请求响应信息", new FileInputStream(logFilePath));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        return res;
    }


    /**
     * 读取excel指定sheet的某些行的数据，封装为readSpecifyExcelData()方法
     *
     * @param sheetNum sheet编号（从1开始）
     * @param startRow 读取的起使行（默认从0开始）
     * @param readRow  要读取多少行
     * @return
     */
    public List<ExcelPojo> readSpecifyExcelData(int sheetNum, int startRow, int readRow) {

        File file = new File(Constants.EXCEL_FILE_PATH);
        //设置导入的参数对象,以便可以设置读取到哪一个sheet
        ImportParams importParams = new ImportParams();
        //设置读取哪一个sheet中的数据
        //正常思维是读取第n个sheet，但sheet的索引是从0开始，所以这里设置成sheet-1
        importParams.setStartSheetIndex(sheetNum - 1);
        //设置读取的起始行 -- 从第一行开始，索引从0开始
        importParams.setStartRows(startRow);
        //设置要读取的行数 -- 读取一行
        importParams.setReadRows(readRow);
//        List<ExcelPojo> dataList = ExcelImportUtil.importExcel(file, ExcelPojo.class, importParams);
//        return dataList;
        //上面两行可以简化成下面
        return ExcelImportUtil.importExcel(file, ExcelPojo.class, importParams);
    }


    /**
     * 读取指定sheet中指定开始行到最后一行的所有数据
     * file 文件对象写死
     *
     * @param sheetNum sheet编号（从1开始）
     * @param startRow 读取的起使行（默认从0开始）
     *                 readRow参数被去掉，方法重载，这个方法可以读取从指定开始行到最后一行
     * @return
     */
    public List<ExcelPojo> readSpecifyExcelData(int sheetNum, int startRow) {
        //读取excel
        File file = new File(Constants.EXCEL_FILE_PATH);
        //设置导入的参数对象,以便可以设置读取到哪一个sheet
        ImportParams importParams = new ImportParams();
        //设置读取哪一个sheet中的数据
        //正常思维是读取第n个sheet，但sheet的索引是从0开始，所以这里设置成sheet-1
        importParams.setStartSheetIndex(sheetNum - 1);
        //设置读取的起始行 -- 从第一行开始，索引从0开始
        importParams.setStartRows(startRow);

        //List<ExcelPojo> dataList = ExcelImportUtil.importExcel(file, ExcelPojo.class, importParams);
        //return dataList;
        //上面两行可以简化成下面
        return ExcelImportUtil.importExcel(file, ExcelPojo.class, importParams);
    }

    /**
     * 读取excel指定sheet的所有数据
     * 封装为 readAllExcelData()方法
     * 把 file 文件对象写死在封装方法里面，就不需要再传file了
     *
     * @param sheetNum 表单编号
     */
    public List<ExcelPojo> readAllExcelData(int sheetNum) {
        //读取excel
        File file = new File(Constants.EXCEL_FILE_PATH);
        //设置导入的参数对象,以便可以设置读取到哪一个sheet
        ImportParams importParams = new ImportParams();
        //设置要读取第几个sheet中的数据
        //正常思维是读取第n个sheet，但sheet的索引是从0开始，所以这里设置成sheet-1
        importParams.setStartSheetIndex(sheetNum - 1);

        List<ExcelPojo> dataList = ExcelImportUtil.importExcel(file, ExcelPojo.class, importParams);

        return dataList;
    }


    /**
     * 从环境变量中取得对应的值，通过正则表达式，替换字符后作为参数，并放到环境变量中去（用以传递到下个接口）
     * 将正则表达式替换，并放到环境变量中封装为方法
     *
     * @param orgStr 原始字符串
     *               replaceStr 要替换为的字符
     * @return 替换之后的字符串
     */
    public String regexReplace(String orgStr) {
        //orgStr有时可能为空，例如前置条件不需要断言，所以其期望结果是空的
        //所以此处要判断一下orgStr是否为空，如果不为空则替换，空的就返回orgStr本身
        //如果不做判断，执行的时候就会报空指针
        if (orgStr != null) {
            //Pattern:正则表达式匹配器
            // 注：{ 大括号是正则表达式中的一个特殊字符，所以转义的时候要转义两次，即两个双斜杠
            Pattern pattern = Pattern.compile("\\{\\{(.*?)}}");
            //matcher: 去匹配哪一个原始的字符串,得到匹配对象
            Matcher matcher = pattern.matcher(orgStr);
            //通过find方法和while连续循环查找匹配到的对象
            String result = orgStr;//result初始值设为orgStr
            while (matcher.find()) {
                //group(0)表示获取匹配到的整个大括号包裹起来的整体 {{XXX}}
                String outerStr = matcher.group(0);
                //group(1)表示获取匹配到的整个大括号里面的内容XXX
                String innerStr = matcher.group(1);
                //从环境变量中取到实际的值，用replaceStr来接收
                Object replaceStr = Environment.envData.get(innerStr);
                //orgStr之中有多个字符需要替换，
                //那么要在result的基础上再进行替换（即在前面替换了的基础上再替换），否则替换完后只替换到了最后一个匹配到的字符串
                result = result.replace(outerStr, replaceStr + "");
            }
            return result;
        }
        return orgStr;
    }

    /**
     * 对应的接口返回字段提取并存到环境变量中去
     *
     * @param excelPojo 用例数据对象，可以提取返回json字符串excelPojo.getExtract()
     * @param res       接口返回Response对象
     */
    public void extractToEnvironment(ExcelPojo excelPojo, Response res) {
        //把”提取返回数据“一列的json字符串转成map
        Map<String, Object> extractMap = JSON.parseObject(excelPojo.getExtract());
        //有以下两种方式得到返回数据中要提取的value（memberId 和 token的Gpath路径）：
        //1、extractMap.get("key")中写死key，得到Gpath路径表达式
        //Object memberIdPath=extractMap.get("member_id");
        //Object tokenPath=extractMap.get("token");
        //2、用循环遍历extractMap的方式得到每个key的value，不需要传入指定key
        for (String key : extractMap.keySet()) {
            Object path = extractMap.get(key);
            //根据”提取返回数据“中的路径表达式path去提取实际接口返回的字段对应的值
            Object value = res.jsonPath().get(path.toString());
            Environment.envData.put(key, value);
        }
    }

    /**
     * 用例的数据替换（包括请求头、参数、地址、期望值）
     * 说明：将正则表达式替换并放到环境变量中封装的方法又封装到里这个方法里
     * 正则替换方法见【regexReplace（）】）
     */
    public ExcelPojo casesReplace(ExcelPojo excelPojo) {
        //正则替换：用例中有多个参数可能涉及到需要替换--请求头、参数、地址、期望值、数据库校验
        //1.参数替换。使用场景：要将上一个接口返回的结果作为下个接口发起的请求参数
        //先从excelPojo对象中拿到实际的请求参数，调用正则替换方法，再将替换后的结果用inputparams接收
        String inputParams = regexReplace(excelPojo.getInputParams());
        // 替换后的结果是保存在inputParams中的，并没有在excelPojo中生效
        // 因此还要将excelPojo对象中的InputParams修改为inputParams
        // 所以这一步才是将要发起的请求中的参数修改为正则替换后的结果
        excelPojo.setInputParams(inputParams);
        //2.请求头替换。使用场景：要将上一个接口返回的结果（如token）替换到下个接口的请求头中
        String requestHeader = regexReplace(excelPojo.getRequestHeader());
        excelPojo.setRequestHeader(requestHeader);
        //3.地址替换。使用场景：有些接口的地址中，需要用到上个接口返回的数据
        String Url = regexReplace(excelPojo.getUrl());
        excelPojo.setUrl(Url);
        //4.期望结果替换。使用场景：实际返回结果中含有传入参数的值，要将期望结果与实际返回结果做断言
        //例如注册接口，传入参数中有电话号码，断言时要判断实际返回结果中的电话号码是否与期望结果中的电话号码一致
        //此时就要将期望结果中的电话号码做关联修改，替换为传入的电话号码）
        String Expected = regexReplace(excelPojo.getExpected());
        excelPojo.setExpected(Expected);
        //5.数据库校验替换。使用场景：对查询语句中的变量替换。
        //例如手机号注册接口，要查询数据库中是否存在这条手机号，
        //此时就需要将数据库校验查询语句中的手机号变量替换为随机生成的那个手机号
        String dbAssert = regexReplace(excelPojo.getDbAssert());
        excelPojo.setDbAssert(dbAssert);
        //调用该方法时，传入的是原始的用例数据（excelPojo），最后得到的结果数据还要返回给excelPojo，否则并没有实现替换的效果
        return excelPojo;

    }
    /**
     * 对响应结果断言
     *
     * @param excelPojo 用例数据实体类
     * @param res       接口响应结果
     */
    public void assertResponse(ExcelPojo excelPojo, Response res) {
        //响应结果断言,要判空一下，如果期望结果有值，则进行断言，没有则不执行断言代码
        //如果不判空，当遇到没有期望结果的用例则会报空指针
        if (excelPojo.getExpected() != null) {
            //把获取得到的结果转成map,并声明map中的key是String类型，value是Object类型：
            Map<String, Object> expectedMap = JSONObject.parseObject(excelPojo.getExpected(), Map.class);
            //循环遍历map，用.keySet()取到期望结果里面的每一个key
            for (String expectedKey : expectedMap.keySet()) {
                //通过遍历出来的expectedKey，得到对应的值 expectedValue
                Object expectedValue = expectedMap.get(expectedKey);
                //从接口请求返回的数据中，通过expectedKey得到实际的值actualValue
                Object actualValue = res.jsonPath().get(expectedKey);
                //把实际值actualValue与期望值exceptedValue做对比
                Assert.assertEquals(actualValue, expectedValue);
            }
        }
    }


    /**
     * 对数据库查询结果校验（断言）
     * @param excelPojo  用例数据对象
     *
     */
    public void assertSQL(ExcelPojo excelPojo) {
        String dbAssert = excelPojo.getDbAssert();
        //先判空，如果数据库校验不为空，则进行断言
        if (dbAssert != null) {
            //把拿到的数据库校验语句转成map，再.keySet(),拿到key
            //key其实就是数据库校验中的sql语句,
            Map<String, Object> map = JSONObject.parseObject(dbAssert,Map.class);
            Set<String> keys = map.keySet();
            //通过循环，先把校验字符串中的exceptedValue读取到
            for (String key : keys) {
                //exceptedValue是数据库校验中的查询sql语句的期望值
                Object exceptedValue = map.get(key);
                //从excel中读取到的exceptedValue和实际查询结果actualValue数据类型可能不一致（可能是Long包装类型、BigDecimal或是其他类型等）
                // 当类型不匹配时无法作断言，所以这里要对期望值的数据类型做一个判断(用instanceof)，判断后把期望结果转成实际查询值的类型
                // 注意，如果数据库查询结果是整数类型或小数类型，则需要转类型，如果是字符串则不用转

                if (exceptedValue instanceof BigDecimal) {
                    //当前实践项目(柠檬班前程贷)查询结果中actualValue为小数时，其类型正好也是BigDecimal类型
                    //所以此处没必要转类型，可以直接断言,工作中具体情况具体判断
                    //通过数据库工具类查询到数据库断言结果actualValue

                    Object actualValue = JDBCUtils.querySingle(key);
                    //将期望值和数据库实际断言结果对比
                    Assert.assertEquals(actualValue,exceptedValue);

                } else if (exceptedValue instanceof Integer) {
                    // 当exceptedValue是Integer时，
                    // 先通过longValue()转成与actualValue匹配Long类型，再做断言
                    Long exceptedValue1 = ((Integer) exceptedValue).longValue();
                    Object actualValue = JDBCUtils.querySingle(key);
                    Assert.assertEquals(actualValue,exceptedValue1);
                }
            }

        }


    }
}