package core.trigger;

import util.DBHelper;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Properties;

/**
 * Created by neshati on 1/24/2017.
 * Behpardaz
 */
public class CurrencyThresholdTrigger extends TriggerCaller {
    public String currencyType;

    // JSON Keys are
    private static String currencyTypeKEY = "currencyType";
    private static String  valueKEY = "value";
    private static String  currentValueKEY = "currentValue";
    public static int GOUP=1;
    public static int GODOWN=-1;


    public double value;
    public Double currentValue;


    public int goUpper; // -1 if go lower
    Properties prop = new Properties();



    private static String sdpTriggerUpper;// = "http://172.16.4.199:1515/api/trigger/currency/upper";
    private static String sdpTriggerLower;// = "http://172.16.4.199:1515/api/trigger/currency/lower";

    private void init(){
        try {
            InputStream input = new FileInputStream("config.properties");
            prop.load(input);

            sdpTriggerUpper= prop.getProperty("sdpTriggerUpper");
            sdpTriggerLower= prop.getProperty("sdpTriggerLower");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }





    public CurrencyThresholdTrigger(String currencyType, double value, int goUpper, Double currentValue) {
        init();
        this.currencyType = currencyType;
        this.value = value;
        this.goUpper = goUpper;
        this.currentValue = currentValue;
        params = new HashMap<>();
    }

    @Override
    public String toString() {
        return "Currency Threshold{" +
                "Currency Type='" + currencyType + '\'' +
                ", Value=" + value +
                ", goUpper=" + goUpper +
                '}';
    }


    @Override
    public void fillParams() {
        if(goUpper==GOUP){
            params.put(TriggerCaller.SDPURLKEY,sdpTriggerUpper);
        }
        else if(goUpper==GODOWN){
            params.put(TriggerCaller.SDPURLKEY,sdpTriggerLower);
        }
        else{
            System.err.println("Not supported Trigger");
        }
        params.put(currencyTypeKEY,currencyType);
        DecimalFormat format = new DecimalFormat("0");
        params.put(valueKEY,format.format(value));
        params.put(currentValueKEY,format.format(currentValue));
    }

    public static void addTreshold(String currenyType, double value, int flag) throws IOException {
        DBHelper.getInstance().insertCurrencyTreshhold(new CurrencyThresholdTrigger(currenyType,value,flag,null));
    }

}
