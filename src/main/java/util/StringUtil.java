package util;

import java.util.HashMap;

/**
 * Created by neshati on 1/22/2017.
 * Behpardaz
 */
public class StringUtil {

    // TODO define all Persian Currencies Here!
    public static String USD_PERSIAN = "\\u062F\\u0644\\u0627\\u0631";
    public static String EUR_PERSIAN = "\\u06CC\\u0648\\u0631\\u0648";
    public static String GBP_PERSIAN = "\\u067E\\u0648\\u0646\\u062F";

    public static String Severity_HIGH_PERSIAN ="\\u062d\\u0633\\u0627\\u0633\\u06cc\\u062a \\u0628\\u0627\\u0644\\u0627";


    public static String Severity_MEDIUM_PERSIAN="\\u062d\\u0633\\u0627\\u0633\\u06cc\\u062a \\u0645\\u062a\\u0648\\u0633\\u0637";
    public static String Severity_LOW_PERSIAN = "\\u062d\\u0633\\u0627\\u0633\\u06cc\\u062a\\u0020\\u067e\\u0627\\u06cc\\u06cc\\u0646";
    public static String AED_PERSIAN = "\\u062F\\u0631\\u0647\\u0645 \\u0627\\u0645\\u0627\\u0631\\u0627\\u062A";

    public static String Severity_LOW="low";
    public static String Severity_MEDIUM="medium";
    public static String Severity_HIGH="high";

    static char[] englishChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    static char[] arabicCharCode = {1776, 1777, 1778, 1779, 1780, 1781, 1782, 1783, 1784, 1785};
    private static HashMap<String, String> currencyName_currencySymbol;
    private static HashMap<String, String> currencySymbol_currencyName;

    public static String Complete_Coin = "Complete_Coin";
    public static String Complete_Coin_PERSIAN = "\\u0633\\u06A9\\u0647 \\u06A9\\u0627\\u0645\\u0644";

    public static String Half_Coin = "Half_Coin";
    public static String Half_Coin_PERSIAN = "\\u0646\\u06CC\\u0645 \\u0633\\u06A9\\u0647";

    public static String ROB_Coin = "ROB_Coin";
    public static String ROB_Coin_PERSIAN = "\\u0631\\u0628\\u0639 \\u0633\\u06A9\\u0647";


    public static HashMap<String, String> CurrencyNameMapper() {
        if (currencyName_currencySymbol == null) {
            currencyName_currencySymbol = new HashMap<>();
            currencyName_currencySymbol.put(USD_PERSIAN, "USD");
            currencyName_currencySymbol.put(EUR_PERSIAN, "EUR");
            currencyName_currencySymbol.put(GBP_PERSIAN, "GBP");
            currencyName_currencySymbol.put(AED_PERSIAN, "AED");
        }
        return currencyName_currencySymbol;
    }

    public static HashMap<String, String> CurrencyNameMapperReverse() {
        if (currencySymbol_currencyName == null) {
            currencySymbol_currencyName = new HashMap<>();
            currencySymbol_currencyName.put("USD", USD_PERSIAN);
            currencySymbol_currencyName.put("EUR", EUR_PERSIAN);
            currencySymbol_currencyName.put("GBP", GBP_PERSIAN);
            currencySymbol_currencyName.put("AED", AED_PERSIAN);
        }
        return currencySymbol_currencyName;
    }


    public static String convertPersianDigitToEnglish(String input) {

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) >= 1776 && input.charAt(i) <= 1785) {

                int index = getIndexByname(input.charAt(i));
                if (index == -1) {
                    System.err.println("Error Converting digits");
                    break;
                } else {
                    builder.append(englishChars[index]);
                }
            } else if (input.charAt(i) == 46) {
                builder.append('.');
            }

        }
        return builder.toString().trim();
    }


    private static int getIndexByname(char ch) {
        int index = 0;
        for (char _item : arabicCharCode) {
            if (_item == ch)
                return index;
            index++;
        }
        return -1;
    }

}
