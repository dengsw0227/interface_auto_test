package webauto;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * @author balala
 * @data 2021/2/23
 **/
public class WebLocateAPI {
    public static void main(String[] args) throws Exception {
        //1、打开浏览器
        WebDriver driver = Open("firefox");
        //2、访问浏览器、元素定位等操作
        driver.get("https://www.baidu.com");
        WebDriver.Window window = driver.manage().window();

        //3、关闭浏览器
        Close(driver);
    }
//关闭浏览器方法
    private static void Close(WebDriver driver) throws InterruptedException {
        //停止n秒
        Thread.sleep(3000);
        //关闭驱动
        driver.quit();

    }

//统一封装浏览器打开方法
    private static WebDriver Open(String type) {

        WebDriver driver=null;
        if("chrome".equalsIgnoreCase(type)){
            //chrome
            //1、设置浏览器驱动位置
            System.setProperty("webdriver.chrome.driver","src/test/resources/chromedriver.exe");
            //2、创建驱动对象
            driver =new ChromeDriver();
        }else if("IE".equalsIgnoreCase(type)){
            //IE
            //1、设置ie启动项
            DesiredCapabilities capabilities =new DesiredCapabilities();
            //1.1忽略缩放
            capabilities.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING,true);
            //1.2忽略保护模式
            capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,true);
            //1.3设置初始化浏览器地址
            capabilities.setCapability(InternetExplorerDriver.INITIAL_BROWSER_URL,true);
            //2、设置浏览器驱动位置
            System.setProperty("webdriver.ie.driver","src/test/resources/IEDriverServer.exe");
            //3、创建ie驱动对象
            driver =new InternetExplorerDriver(capabilities);
        }else if("Firefox".equalsIgnoreCase(type)){
            //Firefox
            //1、设置浏览器驱动位置
            System.setProperty("webdriver.gecko.driver","src/test/resources/geckodriver.exe");
            //1.1firefox安装路径在非C盘，需要设置路径，安装在C盘则不需设置
            System.setProperty("webdriver.firefox.bin","F:\\Mozilla Firefox\\firefox.exe");
            //2、创建驱动对象
            driver =new FirefoxDriver();
        }
        return driver;
    }

    private static void Firefox() throws InterruptedException {
//Firefox

        //1、设置浏览器驱动位置
        System.setProperty("webdriver.gecko.driver","src/test/resources/geckodriver.exe");
        System.setProperty("webdriver.firefox.bin", "F:\\Mozilla Firefox\\firefox.exe");
        //2、创建驱动对象
        FirefoxDriver driver =new FirefoxDriver();
        //3、访问地址
        driver.get("https://www.baidu.com");
        //4、停止5秒
        Thread.sleep(5000);
        //5、关闭驱动
        driver.quit();
    }

    private static void Chrome() throws InterruptedException {
        //chrome
        //1、设置浏览器驱动位置
        System.setProperty("webdriver.chrome.driver","src/test/resources/chromedriver.exe");
        //2、创建驱动对象
        ChromeDriver driver =new ChromeDriver();
        //3、访问地址
        driver.get("https://www.baidu.com");
        //4、停止5秒
        Thread.sleep(5000);
        //5、关闭驱动
        driver.quit();
    }

    private static void IE() throws InterruptedException {
        //IE
        //1、设置ie启动项
        DesiredCapabilities capabilities =new DesiredCapabilities();
        //1.1忽略缩放
        capabilities.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING,true);
        //1.2忽略保护模式
        capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,true);
        //1.3设置初始化浏览器地址
        capabilities.setCapability(InternetExplorerDriver.INITIAL_BROWSER_URL,true);
        //2、设置浏览器驱动位置
        System.setProperty("webdriver.ie.driver","src/test/resources/IEDriverServer.exe");
        //3、创建ie驱动对象
        InternetExplorerDriver driver =new InternetExplorerDriver(capabilities);
        //4、访问地址
        driver.get("https://www.baidu.com");
        //5、停止5秒
        Thread.sleep(5000);
        //6、关闭驱动
        driver.quit();
    }
}
