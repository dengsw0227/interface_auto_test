package data;

import java.util.HashMap;
import java.util.Map;

/**
 * 环境变量
 * @author balala
 * @data 2021/8/25
 **/
public class Environment {
//    public static String token;
//    public static int memberid;
    //用Map作为环境变量，而不是具体的变量，
    // 因为Map可以存很多键值对，用的时候取出来即可
    public static Map<String,Object> envData = new HashMap<String,Object>();



}
