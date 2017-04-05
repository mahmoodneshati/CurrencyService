package core.job;

import core.entity.Currency;
import core.trigger.CurrencyAnalyticsTrigger;
import core.trigger.TriggerCaller;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import util.DBHelper;
import util.StringUtil;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by neshati on 4/4/2017.
 * Behpardaz
 */
public class CurrencyAnalyticsServiceJob extends CurrencyServiceJob implements Job {

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
    }


    protected void fireElligibleTriggers(ArrayList<Currency> newCurrencies) {
        ArrayList<TriggerCaller> allTrigers = new ArrayList<>();
        for (Currency currency : newCurrencies) {
            ArrayList<TriggerCaller> trigers = getEligibleTriggers(currency);
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

    private ArrayList<TriggerCaller> getEligibleTriggers(Currency currency) {
        // This is Core Function
        ArrayList<TriggerCaller> out = new ArrayList<>();
        // if currency is larger than mean + sigma then fire
        double mean_std[]= DBHelper.getInstance().getLastWeekMeanSDTEV(currency.englishName);
        if(Math.abs(mean_std[0]) <0.00001 && Math.abs(mean_std[1])<0.00001) // No data found
            return out;
        highSeverity(currency, out, mean_std);
        mediumSeverity(currency, out, mean_std);
        lowSeverity(currency, out, mean_std);
        return out;
    }

    private void mediumSeverity(Currency currency, ArrayList<TriggerCaller> out, double[] mean_std) {
        if((currency.price >= mean_std[0] + 2 * mean_std[1])
                || (currency.price <= mean_std[0] - 2* mean_std[1])
                ) // very sensitive to price change!
        {
            out.add(new CurrencyAnalyticsTrigger(currency.persianName,currency.price,mean_std[0], StringUtil.Severity_MEDIUM));
        }

    }

    private void lowSeverity(Currency currency, ArrayList<TriggerCaller> out, double[] mean_std) {
        if((currency.price >= mean_std[0] + 3 * mean_std[1])
                || (currency.price <= mean_std[0] - 3* mean_std[1])
                ) // very sensitive to price change!
        {
            out.add(new CurrencyAnalyticsTrigger(currency.persianName,currency.price,mean_std[0], StringUtil.Severity_LOW));
        }

    }

    private void highSeverity(Currency currency, ArrayList<TriggerCaller> out, double[] mean_std) {
        if((currency.price >= mean_std[0] + mean_std[1])
            || (currency.price <= mean_std[0] - mean_std[1])
                ) // very sensitive to price change!
        {
            out.add(new CurrencyAnalyticsTrigger(currency.persianName,currency.price,mean_std[0], StringUtil.Severity_HIGH));
        }
    }


}
