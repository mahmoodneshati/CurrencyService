package core.service;

import core.entity.Currency;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import static util.StringUtil.convertPersianDigitToEnglish;

/**
 * Created by neshati on 2/14/2017.
 * Behpardaz
 */
public class CurrencyDailyService {

    static String  serviceURL = "http://parsijoo.ir/api?serviceType=price-API&query=Currency";
    private static CurrencyDailyService currencyDailyService;
    private static Properties prop = new Properties();


    private static String setConfigs() {
/*        InputStream input ;
        try {
            input = new FileInputStream("config.properties");
            prop.load(input);
            serviceURL = prop.getProperty("parsijoo_currency_service");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;*/
        return "http://parsijoo.ir/api?serviceType=price-API&query=Currency";
    }


    public static CurrencyDailyService getInstance(){
        if(currencyDailyService==null){
            currencyDailyService = new CurrencyDailyService();
            setConfigs();

        }
        return currencyDailyService;
    }


    public ArrayList<Currency> callRemoteCurrencyService() throws IOException {
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

    private Double getDoubleValue(String price) {
        try {
            price = price.replaceAll(",", "");
            return Double.parseDouble(convertPersianDigitToEnglish((price)));
        } catch (NumberFormatException e) {
            e.printStackTrace(); //prints error
        }

        return null;
    }


}
