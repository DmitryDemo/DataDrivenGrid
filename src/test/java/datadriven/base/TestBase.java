package datadriven.base;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import utilities.ExtentManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    public static ThreadLocal<RemoteWebDriver> remoteDriver = new ThreadLocal<RemoteWebDriver>();
    public RemoteWebDriver driver = null;
    public Properties OR = new Properties();
    public Properties config = new Properties();
    public FileInputStream fis;
    public Logger log = Logger.getLogger("devpinoyLogger");
    public WebDriverWait wait;
    public ExtentReports report = ExtentManager.getInstance();
    public ExtentTest test;
    public static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<ExtentTest>();
    public static String screenshotPath;
    public static String screenshotName;

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

    public void openBrowser(String browser) throws MalformedURLException {

        DesiredCapabilities capabilities = null;

        if ("firefox".equalsIgnoreCase(browser)) {
            System.setProperty("webdriver.gecko.driver", System.getProperty("user.dir") + "\\src\\test\\resources\\executables\\geckodriver.exe");
            FirefoxOptions options = new FirefoxOptions();
            capabilities = DesiredCapabilities.firefox();
            capabilities.setCapability(FirefoxOptions.FIREFOX_OPTIONS, options);
            capabilities.setBrowserName("firefox");
            capabilities.setPlatform(Platform.ANY);
        } else if ("chrome".equalsIgnoreCase(browser)) {
            ChromeOptions options = new ChromeOptions();
            capabilities = DesiredCapabilities.chrome();
            capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        } else if ("ie".equalsIgnoreCase(browser)) {
            capabilities = DesiredCapabilities.internetExplorer();
            capabilities.setBrowserName("iexplore");
            capabilities.setPlatform(Platform.WINDOWS);
        }

        driver = new RemoteWebDriver(new URL("http://192.168.99.100:4444/wd/hub/"), capabilities);
        setDriver(driver);
        getDriver().manage().timeouts().implicitlyWait(Integer.parseInt(config.getProperty("implicit.wait")), TimeUnit.SECONDS);
        getDriver().manage().window().maximize();
        getExtentTest().log(LogStatus.INFO, String.format("Browser '%s' opened.", browser));
    }

    public void navigate(String url) {
        getDriver().get(url);
        getExtentTest().log(LogStatus.INFO, "Navigating to " + url);
    }

    public void openBasePage() {
        navigate(config.getProperty("testsiteurl"));
    }

    public void click(String locator) {

        try {
            getElement(locator).click();
        } catch (Throwable throwable) {
            logTestFailed(String.format("Cannot click the %s element.", locator));
        }
        getExtentTest().log(LogStatus.INFO, String.format("Click %s button.", locator));
    }

    public void type(String locator, String value) {

        try {
            getElement(locator).sendKeys(value);
        } catch (Throwable thr) {
            logTestFailed(String.format("Failed because cannot type '%s' value into %s field.", value, locator));
        }
        getExtentTest().log(LogStatus.INFO, String.format("Type '%s' text into %s field.", value, locator));
    }

    static WebElement dropdown;

    public void select(String locator, String value) {

            try {
                Select select = new Select(getElement(locator));
                select.selectByVisibleText(value);
            } catch (Throwable throwable) {
                logTestFailed(String.format("Failed because cannot select '%s' value in %s select.", value, locator));
            }

            getExtentTest().log(LogStatus.INFO, String.format("Select '%s' option from %s select.", value, locator));
    }

    public boolean isElementPresent(By by) {
        try {
            getDriver().findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public void captureScreenshot() {

        File scrFile = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.FILE);
        screenshotName = "Screenshot_" + new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date()) + ".jpeg";
        try {
            FileUtils.copyFile(scrFile, new File(System.getProperty("user.dir") + "\\reports\\screenshots\\" + screenshotName));
        } catch (IOException e) {}
        getExtentTest().log(LogStatus.INFO, "<span class=\"label label-fail\">Error!</span> ", getExtentTest().addScreenCapture(
                System.getProperty("user.dir") + "\\reports\\screenshots\\" + screenshotName));
    }

    public void logTestPassed(String testName) {
        getExtentTest().log(LogStatus.PASS, testName + " executed successfully!");
    }

    public void logTestFailed(String testName) {
        getExtentTest().log(LogStatus.FAIL, testName + " failed!");
        captureScreenshot();
        Assert.fail(testName + " failed!");
    }

    public WebDriver getDriver() {
        return remoteDriver.get();
    }

    public void setDriver(RemoteWebDriver driver) {
        remoteDriver.set(driver);
    }

    public ExtentTest getExtentTest() {
        return extentTest.get();
    }

    public void setExtentTest(ExtentTest extentTest) {
        this.extentTest.set(extentTest);
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
