package utilities;

import com.relevantcodes.extentreports.DisplayOrder;
import com.relevantcodes.extentreports.ExtentReports;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExtentManager {

    private static ExtentReports extent;

    public static ExtentReports getInstance() {
        if (extent == null) {
            String date = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
            String reportName = "Extent_Report_" + date + ".html";
            extent = new ExtentReports(System.getProperty("user.dir")
                    + "\\reports\\" + reportName ,true, DisplayOrder.OLDEST_FIRST);
            extent.loadConfig(new File(System.getProperty("user.dir") + "\\src\\test\\resources\\extentconfig\\ReportsConfig.xml"));
        }
        return extent;
    }
}
