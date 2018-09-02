package suite.bankmanager.testcases;

import datadriven.base.TestBase;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import utilities.Constants;
import utilities.DataProviders;
import utilities.DataUtil;
import utilities.ExcelReader;

import java.net.MalformedURLException;
import java.util.Map;

public class AddCustomerTest extends TestBase {

    @Test(dataProviderClass = DataProviders.class, dataProvider = "bankManagerDP")
    public void addCustomerTest(Map<String, String> data) throws MalformedURLException {
        super.setUp();
        test = report.startTest("Add Custormer Test - " + data.get("browser"));
        setExtentTest(test);
        ExcelReader excel = new ExcelReader(Constants.SUITE1_XL_PATH);
        DataUtil.checkExecution("BankManagerSuite", "AddCustomerTest", data.get("Runmode"), excel);
        openBrowser(data.get("browser"));
        openBasePage();
        logTestPassed("Add Customer Test");
    }

    @AfterMethod
    public void tearDown() {
        if (report != null) {
            report.endTest(getExtentTest());
            report.flush();
        }
        getDriver().quit();
    }
}
