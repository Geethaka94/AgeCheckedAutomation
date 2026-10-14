package api.utilities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentReportManager implements ITestListener {

    private ExtentSparkReporter sparkReporter;
    private ExtentReports extent;

    private String reportName;

    @Override
    public void onStart(ITestContext context) {

        String currentDateTime = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

        reportName = "Test_Report_" + currentDateTime + ".html";

        sparkReporter = new ExtentSparkReporter("./reports/" + reportName);

        sparkReporter.config().setDocumentTitle("Automation Report");
        sparkReporter.config().setReportName("AgeChecked API Automation");
        sparkReporter.config().setTheme(Theme.DARK);

        extent = new ExtentReports();
        
        extent.attachReporter(sparkReporter);

        extent.setSystemInfo("Project", "AgeChecked API Automation");
        extent.setSystemInfo("Test Framework", "Rest Assured");
        extent.setSystemInfo("User Name", System.getProperty("user.name"));
        extent.setSystemInfo("Environment", "STG");

        List<String> includedGroups =context.getCurrentXmlTest().getIncludedGroups();

        if (!includedGroups.isEmpty()) {
            extent.setSystemInfo("Groups", includedGroups.toString());
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {

        ExtentTest test = extent.createTest(displayName(result));

        test.assignCategory(result.getMethod().getGroups());

        test.log(Status.PASS, "Test passed successfully.");

        System.out.println("PASSED : " + displayName(result));
    }

    @Override
    public void onTestFailure(ITestResult result) {

        ExtentTest test = extent.createTest(displayName(result));

        test.assignCategory(result.getMethod().getGroups());

        test.log(Status.FAIL, "Test failed.");

        Throwable error = result.getThrowable();

        if (error != null) {
            test.log(Status.INFO, String.valueOf(error.getMessage()));
        }

        System.out.println("FAILED : " + displayName(result));
    }

    @Override
    public void onTestSkipped(ITestResult result) {

        ExtentTest test = extent.createTest(displayName(result));

        test.assignCategory(result.getMethod().getGroups());

        test.log(Status.SKIP, "Test was skipped.");

        Throwable error = result.getThrowable();

        if (error != null) {
            test.log(Status.INFO, String.valueOf(error.getMessage()));
        }

        System.out.println("SKIPPED: " + displayName(result));
    }

    @Override
    public void onFinish(ITestContext context) {

        if (extent != null) {
            extent.flush();
        }
    }

    // Generate readable test names, including DataProvider values
    private String displayName(ITestResult result) {

    	String name = result.getMethod().getDescription(); 
    	
    	if (name == null || name.trim().isEmpty()) 
    	{
    		name = result.getMethod().getMethodName();
    		}

        Object[] params = result.getParameters();

        if (params != null && params.length > 0) {

            // Display the first parameter only
            // Avoid displaying secret keys or sensitive test data
            name += " [" + String.valueOf(params[0]) + "]";
        }

        return name;
    }
}
