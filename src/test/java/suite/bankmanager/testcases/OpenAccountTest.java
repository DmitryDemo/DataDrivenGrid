package suite.bankmanager.testcases;
import datadriven.base.TestBase;
import org.testng.annotations.Test;
import utilities.Constants;
import utilities.DataProviders;
import utilities.DataUtil;
import utilities.ExcelReader;

import java.net.MalformedURLException;
import java.util.Map;

public class OpenAccountTest extends TestBase {

    @Test(dataProviderClass = DataProviders.class, dataProvider = "bankManagerDP")
    public void openAccountTest(Map<String, String> data) throws MalformedURLException {
        super.setUp();
        test = report.startTest("Open Account Test - " + data.get("browser"));
        setExtentTest(test);
        ExcelReader excel = new ExcelReader(Constants.SUITE1_XL_PATH);
        DataUtil.checkExecution("BankManagerSuite", "OpenAccountTest", data.get("Runmode"), excel);
        openBrowser(data.get("browser"));
        openBasePage();
        click("bmlBtn_CSS");
        click("openaccount_CSS");
        select("customer_CSS", data.get("customer"));
        select("currency_CSS", data.get("currency"));
        click("process_CSS");
        logTestPassed("Open Account Test");
    }
}
