package datadriven.base;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

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
            } catch (FileNotFoundException e) {
            }
            try {
                config.load(fis);
            } catch (IOException e) {
            }

            // Load OR.properties
            try {
                fis = new FileInputStream(System.getProperty("user.dir") + "\\src\\test\\resources\\properties\\OR.properties");
            } catch (FileNotFoundException e) {
            }
            try {
                OR.load(fis);
            } catch (IOException e) {
            }
        }
    }

    @AfterSuite
    public void tearDown() {
        getDriver().quit();
    }

    public void openBrowser(String browser) throws MalformedURLException {

        DesiredCapabilities capabilities = null;

        if ("firefox".equalsIgnoreCase(browser)) {
            capabilities = DesiredCapabilities.firefox();
            capabilities.setBrowserName("firefox");
            capabilities.setPlatform(Platform.ANY);
        } else if ("chrome".equalsIgnoreCase(browser)) {
            capabilities = DesiredCapabilities.chrome();
            capabilities.setBrowserName("chrome");
            capabilities.setPlatform(Platform.ANY);
        } else if ("ie".equalsIgnoreCase(browser)) {
            capabilities = DesiredCapabilities.internetExplorer();
            capabilities.setBrowserName("iexplore");
            capabilities.setPlatform(Platform.WINDOWS);
        }

        driver = new RemoteWebDriver(new URL("http://192.168.99.100:4444/wd/hub/"), capabilities);
        setDriver(driver);
        getDriver().manage().timeouts().implicitlyWait(Integer.parseInt(config.getProperty("implicit.wait")), TimeUnit.SECONDS);
        getDriver().manage().window().maximize();
    }

    public void navigate(String url) {
        getDriver().get(url);
    }

    public void navigate() {
        navigate(config.getProperty("testsiteurl"));
    }

    public void click(String locator) {

        getElement(locator).click();
    }

    public void type(String locator, String value) {

        getElement(locator).sendKeys(value);
    }

    static WebElement dropdown;

    public void select(String locator, String value) {

            Select select = new Select(getElement(locator));
            select.selectByVisibleText(value);
    }

    public boolean isElementPresent(By by) {
        try {
            getDriver().findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public WebDriver getDriver() {
        return threadLocal.get();
    }

    public void setDriver(RemoteWebDriver driver) {
        threadLocal.set(driver);
    }

    private WebElement getElement(String locator) {

        WebElement element = null;
        try {
            if (locator.endsWith("_CSS")) {
                element = getDriver().findElement(By.cssSelector(OR.getProperty(locator)));
            } else if (locator.endsWith("_XPATH")) {
                element = getDriver().findElement(By.xpath(OR.getProperty(locator)));
            } else if (locator.endsWith("_ID")) {
                element = getDriver().findElement(By.id(OR.getProperty(locator)));
            }
        } catch (Throwable t) {}

        return element;
    }
}
