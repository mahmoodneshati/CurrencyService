package core.service;

import core.entity.Currency;
import core.entity.Gold;
import util.DBHelper;

import java.util.HashMap;

/**
 * Created by neshati on 1/29/2017.
 * Behpardaz
 */
public class MicroServiceStates {
    private static MicroServiceStates ms;
    private HashMap<String,Currency> lastCurrencyPrice;
    private HashMap<String,Gold> lastCoinPrice;

    public static MicroServiceStates getInstance(){
        if(ms==null){
            ms = new MicroServiceStates();
        }
        return ms;
    }

    public Double getLastCurrencyPrice(String currencyName){
        if(lastCurrencyPrice ==null) {
            lastCurrencyPrice = new HashMap<>();
            lastCurrencyPrice.putAll(DBHelper.getInstance().loadLastCurrencyPrice());
        }
        return lastCurrencyPrice.get(currencyName)==null?null: lastCurrencyPrice.get(currencyName).price;

    }
    public Double getLastCoinPrice(String coinName) {
        if(lastCoinPrice ==null) {
            lastCoinPrice = new HashMap<>();
            lastCoinPrice.putAll(DBHelper.getInstance().loadLastCoinPrice());
        }
        return lastCoinPrice.get(coinName)==null?null: lastCoinPrice.get(coinName).price;
    }

    public void setLastCurrencyPrice(Currency newCurrency) {
        lastCurrencyPrice.put(newCurrency.englishName, newCurrency);
    }


    public void setLastGoldPrice(Gold gold) {
        lastCoinPrice.put(gold.englishName, gold);
    }
}
