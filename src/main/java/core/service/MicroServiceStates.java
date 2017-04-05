package core.service;

import core.entity.Currency;
import util.DBHelper;

import java.util.HashMap;

/**
 * Created by neshati on 1/29/2017.
 * Behpardaz
 */
public class MicroServiceStates {
    private static MicroServiceStates ms;
    private HashMap<String,Currency> lastPrice;

    public static MicroServiceStates getInstance(){
        if(ms==null){
            ms = new MicroServiceStates();
        }
        return ms;
    }

    public Double getLastPrice(String currencyName){
        if(lastPrice==null) {
            lastPrice = new HashMap<>();
            lastPrice.putAll(DBHelper.getInstance().loadLastPrice());
        }
        return lastPrice.get(currencyName)==null?null:lastPrice.get(currencyName).price;

    }

    public void setLastPrice(Currency newCurrency) {
        lastPrice.put(newCurrency.englishName,newCurrency);
    }
}
