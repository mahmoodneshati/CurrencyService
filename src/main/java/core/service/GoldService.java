package core.service;

import core.entity.Gold;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import static util.StringUtil.convertPersianDigitToEnglish;

/**
 * Created by Mahmood on 4/5/2017.
 * mahmood.neshati@gmail.com
 */
public class GoldService {
    private static GoldService service;
    private static Properties prop = new Properties();
    private static String serviceURL;


    public static GoldService getInstance() {

        if(service==null){
            service = new GoldService();
            setConfigs();

        }
        return service;


    }

    private static String setConfigs() {
        InputStream input ;
        try {
            input = new FileInputStream("config.properties");
            prop.load(input);
            serviceURL = prop.getProperty("parsijoo_gold_service");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Gold> callRemoteCurrencyService() throws IOException {
        Document doc = Jsoup.connect(serviceURL).get();
        Elements gold_items = doc.select("item");
        ArrayList<Gold> newGolds = new ArrayList<>();
        for (org.jsoup.nodes.Element goldItem : gold_items) {
            String goldName = goldItem.select("name").get(0).text().trim();
            Double goldPrice = getDoubleValue(goldItem.select("price").get(0).text());
            Gold gold = new Gold(goldName, null, goldPrice);
            newGolds.add(gold);
        }
        return newGolds;
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
