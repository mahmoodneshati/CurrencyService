package job;

import core.task.CoinAnalyticsServiceJob;
import core.task.CoinServiceJob;
import core.task.CurrencyAnalyticsServiceJob;
import core.task.CurrencyServiceJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by neshati on 2/7/2017.
 * Behpardaz
 */
public class CurrencyScheduler {
    //private static String EVERY30SECONDS = "0/20 * * * * ? *";
    //private static String EVERYDAY8AM = "0 0 8 * * ? *";
    private static Properties prop = new Properties();

    private org.quartz.Scheduler scheduler;
    private String currencyAnalyticsServiceJobPattern;
    private String currencyServiceSchedulePattern;
    private String coinServiceJobPattern;
    private String CoinAnalyticsServiceJobPattern;

    public CurrencyScheduler() {
        try {
            this.scheduler = new StdSchedulerFactory().getScheduler();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public void start(){
        try {
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public void addJobs(){
        try {
            registerJob(CurrencyServiceJob.class, "CurrencyServiceJob", "group1", currencyServiceSchedulePattern);
            registerJob(CurrencyAnalyticsServiceJob.class, "CurrencyAnalyticsServiceJob", "group1", currencyAnalyticsServiceJobPattern);
            registerJob(CoinServiceJob.class, "CoinServiceJob", "group1", coinServiceJobPattern);
            registerJob(CoinAnalyticsServiceJob.class, "CoinAnalyticsServiceJob", "group1", CoinAnalyticsServiceJobPattern);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    private String setConfigs() {
        InputStream input ;
        try {
            input = new FileInputStream("config.properties");
            prop.load(input);
            currencyServiceSchedulePattern = prop.getProperty("currencyServiceSchedulePattern");
            currencyAnalyticsServiceJobPattern = prop.getProperty("currencyAnalyticsServiceJobPattern");
            coinServiceJobPattern = prop.getProperty("coinServiceJobPattern");
            CoinAnalyticsServiceJobPattern = prop.getProperty("CoinAnalyticsServiceJobPattern");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    public static void main(String[] args) throws SchedulerException {
        CurrencyScheduler scheduler = new CurrencyScheduler();
        scheduler.start();
        scheduler.setConfigs();
        scheduler.addJobs();
    }

    private void registerJob(Class job_class, String job_name, String job_group, String schedule_pattern) throws SchedulerException {
        JobDetail job = JobBuilder.newJob(job_class).withIdentity(job_name, job_group).build();
        Trigger trigger = TriggerBuilder
                .newTrigger()
                .withIdentity(job_name, job_group)
                .withSchedule(
                        CronScheduleBuilder.cronSchedule(schedule_pattern))
                .build();
        scheduler.scheduleJob(job, trigger);
    }
}
