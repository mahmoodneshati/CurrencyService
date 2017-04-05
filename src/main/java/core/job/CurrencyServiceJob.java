package core.job;

/**
 * Created by neshati on 1/22/2017.
 * Behpardaz
 */

import core.Currency;
import core.MicroServiceStates;
import core.trigger.CurrencyThresholdTrigger;
import core.trigger.TriggerCaller;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import util.DBHelper;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import static util.StringUtil.convertPersianDigitToEnglish;


public class CurrencyServiceJob implements Job {
    String serviceURL = "http://parsijoo.ir/api?serviceType=price-API&query=Currency";
    //String sdpTriggerCurrentValue = "http://localhost:1515/api/trigger/currency/currentValue";
    private static Properties prop = new Properties();


    public CurrencyServiceJob() {
        if(prop==null)
            setConfigs();
    }

    private String setConfigs() {
        InputStream input ;
        try {
            input = new FileInputStream("config.properties");
            prop.load(input);
            serviceURL = prop.getProperty("parsijoo_currency_service");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    public void execute(JobExecutionContext context)
            throws JobExecutionException {

        try {
            System.out.println("New run of program");
            runService();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void runService() throws IOException {
        ArrayList<Currency> newCurrencies = callRemoteCurrencyService();
        fireElligibleTriggers(newCurrencies);
        updateMicroServiceStateVariables(newCurrencies);
        updateCurrencyDB(newCurrencies);

    }

    protected ArrayList<Currency> callRemoteCurrencyService() throws IOException {
        Document doc = Jsoup.connect(serviceURL).get();
        Elements currency_items = doc.select("item");
        ArrayList<Currency> newCurrencies = new ArrayList<>();
        for (org.jsoup.nodes.Element currencyItem : currency_items) {
            String currencyName = currencyItem.select("name").get(0).text().trim();
            Double currencyPrice = getDoubleValue(currencyItem.select("price").get(0).text());
            Currency currency = new Currency(currencyName, null, currencyPrice);
            newCurrencies.add(currency);
        }
        return newCurrencies;
    }

    protected void updateMicroServiceStateVariables(ArrayList<Currency> newCurrencies) {
        for (Currency currency : newCurrencies) {
            MicroServiceStates.getInstance().setLastPrice(currency);
        }

    }

    protected void fireElligibleTriggers(ArrayList<Currency> newCurrencies) {
        fireTresholdTriggers(newCurrencies);
    }

    private void fireTresholdTriggers(ArrayList<Currency> newCurrencies) {
        ArrayList<TriggerCaller> allTrigers = new ArrayList<>();
        for (Currency currency : newCurrencies) {
            ArrayList<TriggerCaller> trigers = getTresholdTriggers(currency);
            allTrigers.addAll(trigers);
        }
        for (TriggerCaller trigger : allTrigers) {
            try {
                trigger.fillParams();
                trigger.fire();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private ArrayList<TriggerCaller> getTresholdTriggers(Currency currency) {
        Double lastPrice = MicroServiceStates.getInstance().getLastPrice(currency.englishName);

        ArrayList<TriggerCaller> allTreshholds = new ArrayList<>();
        if (lastPrice == null) return allTreshholds;
        ArrayList<TriggerCaller> validTreshholdsLower =
                DBHelper.getInstance().getValidCurrencyTresholdsLower(currency.englishName,
                        lastPrice,
                        currency.price);
        ArrayList<TriggerCaller> validTreshholdsUpper =
                DBHelper.getInstance().getValidCurrencyTresholdsUpper(currency.englishName,
                        lastPrice,
                        currency.price);
        allTreshholds.addAll(validTreshholdsLower);
        allTreshholds.addAll(validTreshholdsUpper);
        for (TriggerCaller next : allTreshholds) {
            ((CurrencyThresholdTrigger) next).currentValue = currency.price;
        }
        return allTreshholds;
    }


    private Double getDoubleValue(String price) {
        try {
            price = price.replaceAll(",", "");
            return Double.parseDouble(convertPersianDigitToEnglish((price)));
        } catch (NumberFormatException e) {
            e.printStackTrace(); //prints error
        }

        return null;
    }

    private void updateCurrencyDB(ArrayList<Currency> currencyList) {
        // should update current value of the given currency list
        for (Currency currency : currencyList) {
            int result = DBHelper.getInstance().insertCurrency(currency);
            if (result == 0) {
                // error on insert
                return;
            }
        }

    }


    public static void main(String[] args) throws IOException {

        CurrencyServiceJob currencyServiceJob = new CurrencyServiceJob();
        currencyServiceJob.runService();
    }


}
