package datadriven.base;

import org.apache.log4j.Logger;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class TestBase {

    // TIME STOPPED = 13:20

    /*
    This class is going to initialize:
    - WebDriver
    - Logs
    - Properties
    - Excel
    - DataBase
    - Email
    - ExtentReport
     */

    public static ThreadLocal<RemoteWebDriver> threadLocal = new ThreadLocal<RemoteWebDriver>();
    public RemoteWebDriver driver = null;
    public Properties OR = new Properties();
    public Properties config = new Properties();
    public FileInputStream fis;
    public Logger log = Logger.getLogger("devpinoyLogger");
    public WebDriverWait wait;

    @BeforeSuite
    public void setUp() {

        if (driver == null) {
            // Load config.properties
            try {
                fis = new FileInputStream(System.getProperty("user.dir") + "\\src\\test\\resources\\properties\\Config.properties");
            } catch (FileNotFoundException e) {}
            try {
                config.load(fis);
            } catch (IOException e) {}

            // Load OR.properties
            try {
                fis = new FileInputStream(System.getProperty("user.dir") + "\\src\\test\\resources\\properties\\OR.properties");
            } catch (FileNotFoundException e) {}
            try {
                OR.load(fis);
            } catch (IOException e) {}
        }

    }

    @AfterSuite
    public void tearDown() {

    }
}
