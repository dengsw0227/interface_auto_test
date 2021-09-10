package data;

/**常量类：设置一些常量属性，比如地址、要读取的文件路径等
 * 这些属性在代码运行是都是不改的
 * @author balala
 * @data 2021/8/26
 **/
public class Constants {
    //日志输出配置，输出到控制台or日志文件中,true是输出到的文件中（判断代码在BaseTest->request中）
    public static final boolean LOG_TO_FILE =true;
    //excel文件的路径
    public static final String EXCEL_FILE_PATH="src/test/resources/api_testcases_futureloan_v3.xls";
    //公共地址
    public static final String Baseurl="http://api.lemonban.com/futureloan";
    //数据库信息
    public static final String DB_BASE_URL="api.lemonban.com";
    public static final String DB_NAME ="futureloan";
    public static final String DB_USERNAME="future";
    public static final String DB_PSW="123456";
}
