package core.trigger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Properties;

/**
 * Created by neshati on 4/4/2017.
 * Behpardaz
 */
public class CurrencyAnalyticsTrigger extends TriggerCaller {
    Properties prop = new Properties();
    private static String sdpTriggerAnalytics;

    // JSON Keys are
    private static String currencyTypeKEY = "currencyType";
    private static String  currentValueKEY = "currentValue";
    private static String  lastWeeValueKEY = "lastWeekValue";
    private static String  severityValueKEY = "currencyChangeSeverity";

    private String currencyType;
    private double currentValue;
    private double lastWeekValue;
    private String severityValue;


    public CurrencyAnalyticsTrigger(String currencyType, double currentValue, double lastWeekValue, String severity) {
        init();
        this.currencyType = currencyType;
        this.currentValue = currentValue;
        this.lastWeekValue = lastWeekValue;
        this.severityValue = severity;
    }


    private void init(){
        try {
            params = new HashMap<>();
            InputStream input = new FileInputStream("config.properties");
            prop.load(input);
            sdpTriggerAnalytics= prop.getProperty("sdpTriggerCurrencyAnalytics");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    @Override
    public void fillParams() {
        DecimalFormat format = new DecimalFormat("0.0");
        params.put(TriggerCaller.SDPURLKEY,sdpTriggerAnalytics);
        params.put(currencyTypeKEY,currencyType);
        params.put(currentValueKEY,format.format(currentValue));
        params.put(lastWeeValueKEY,format.format(lastWeekValue));
        params.put(severityValueKEY,severityValue);
    }


}
