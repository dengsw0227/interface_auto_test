package util;

import com.sun.codemodel.JCatchBlock;
import data.Constants;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author balala
 * @data 2021/9/1
 **/
public class JDBCUtils {

    public static Connection getConnection() {
        //定义数据库连接
        //Oracle：jdbc:oracle:thin:@localhost:1521:DBName
        //SqlServer：jdbc:microsoft:sqlserver://localhost:1433; DatabaseName=DBName
        //MySql：jdbc:mysql://localhost:3306/DBName
        String url = "jdbc:mysql://"+ Constants.DB_BASE_URL+"/"+Constants.DB_NAME+"?useUnicode=true&characterEncoding=utf-8";
        String user = Constants.DB_USERNAME;
        String password = Constants.DB_PSW;
        //定义数据库连接对象
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * 关闭数据库链接 -- 连接了数据库，进行操作后，最后要关闭数据库连接
     * @param connection 数据库连接对象
     * @return
     */
    public static void closeConnection(Connection connection) {
        //最好判空一下，如果不为空，就关闭一下，如果为空就不用关了
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        }
    }

    /**
     * sql的更新操作，包括 增删改
     * @param sql 要执行的sql语句
     */
    public static void update(String sql) {
        Connection connection = getConnection();
        QueryRunner queryRunner = new QueryRunner();
        //虽然也可以抛出异常，但不可能每次调用都去处理一下异常
        //所以直接在封装内部就把异常用try catch处理掉
        try {
            queryRunner.update(connection,sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            //要在finally中关闭数据库
            //因为如果上面try catch抛出了异常，就不会走关闭数据库的代码了
            //为了简化代码，将关闭数据库代码封装成了closeConnection方法
            closeConnection(connection);

        }

    }

    /**
     * sql查询所有的结果集 -- MapListHandler
     * 查询到的结果集是一个有很多map组成的列表，所以是List<Map<String,Object>>
     * @param sql 要执行的sql语句
     *
     */
    public static List<Map<String,Object>> queryAll(String sql){
        Connection connection = getConnection();
        QueryRunner queryRunner = new QueryRunner();
        //查询结果是需要返回的，返回结果是一个有很多map组成的列表，所以是List<Map<String,Object>>
        //先在外面定义为null
        List<Map<String,Object>> result=null;
        try {
            //再在try中接收结果
            result=queryRunner.query(connection,sql,new MapListHandler());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            //要在finally中关闭数据库
            //因为如果上面try catch抛出了异常，就不会走关闭数据库的代码了
            //为了简化代码，将关闭数据库代码封装成了closeConnection方法
            closeConnection(connection);

        }
        //最后在外面返回
        return result;
    }


    /**
     * sql查询结果集中的第一条 -- MapHandler
     * 查询到的结果是一个map，所以是Map<String,Object>
     * @param sql 要执行的sql语句
     */
    public static Map<String,Object> queryOne(String sql){
        Connection connection = getConnection();
        QueryRunner queryRunner = new QueryRunner();
        //查询结果是需要返回的，返回结果是一个有很多map组成的列表，所以是List<Map<String,Object>>
        //先在外面定义为null
        Map<String,Object> result=null;
        try {
            //再在try中接收结果
            result=queryRunner.query(connection,sql,new MapHandler());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            //要在finally中关闭数据库
            //因为如果上面try catch抛出了异常，就不会走关闭数据库的代码了
            //为了简化代码，将关闭数据库代码封装成了closeConnection方法
            closeConnection(connection);

        }
        //最后在外面返回
        return result;
    }

    /**
     * sql查询单条数据 -- ScalarHandler<Object>
     * 查询到的结果是一个map，所以是Map<String,Object>
     * @param sql 要执行的sql语句
     *
     */
    public static Object querySingle(String sql){
        Connection connection = getConnection();
        QueryRunner queryRunner = new QueryRunner();
        //查询结果是需要返回的，返回结果的数据类型不一定，所以是Object
        //先在外面定义为null
        Object result=null;
        try {
            //再在try中接收结果
            result=queryRunner.query(connection,sql,new ScalarHandler<Object>());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            //要在finally中关闭数据库
            //因为如果上面try catch抛出了异常，就不会走关闭数据库的代码了
            //为了简化代码，将关闭数据库代码封装成了closeConnection方法
            closeConnection(connection);
        }
        //最后在外面返回
        return result;
    }

}