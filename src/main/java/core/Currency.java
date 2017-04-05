package core;

import org.apache.commons.lang.StringEscapeUtils;
import util.StringUtil;

import java.util.HashMap;

/**
 * Created by neshati on 1/22/2017.
 * Behpardaz
 */
public class Currency {
    public String persianName;
    public String englishName;
    public Double price;




    public Currency(String persianName, String englishName, Double price) {
        this.persianName = persianName;
        if (englishName == null)
            this.englishName = getEnglishName(persianName);
        else if (persianName == null)
            this.persianName = getPersianName(englishName);

        this.price = price;
    }

    private String getPersianName(String englishName) {
        HashMap<String, String> code_name = StringUtil.CurrencyNameMapperReverse();
        for (String name : code_name.keySet()) {
            if (name.equalsIgnoreCase(englishName))
                return code_name.get(name);
        }
        return null;


    }

    private String getEnglishName(String persianName) {
        String test = StringEscapeUtils.escapeJava(persianName);
        HashMap<String, String> name_code = StringUtil.CurrencyNameMapper();
        for (String name : name_code.keySet()) {
            if (name.equalsIgnoreCase(test))
                return name_code.get(name);
        }
        return null;

    }

    @Override
    public String toString() {
        return persianName + "\t" + englishName + "\t" + price;
    }

}
