package server;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import util.StringUtil;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.Properties;

/**
 * Created by neshati on 4/4/2017.
 * Behpardaz
 */
@Path("/currency")
public class SDPCoinInterface {
    private static Properties prop = new Properties();
    private static String gold_endpoint;


    @GET
    @Produces("application/json")
    public String getCoinTypes() throws JSONException {
        JSONArray array = new JSONArray();
        array.put(getJsonObject(StringUtil.Complete_Coin, StringEscapeUtils.unescapeJava(StringUtil.Complete_Coin_PERSIAN)));
        array.put(getJsonObject(StringUtil.Half_Coin,StringEscapeUtils.unescapeJava(StringUtil.Half_Coin_PERSIAN)));
        array.put(getJsonObject(StringUtil.ROB_Coin,StringEscapeUtils.unescapeJava(StringUtil.ROB_Coin_PERSIAN)));
        return array.toString();
    }

    private JSONObject getJsonObject(String usd, String name) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("Code", usd);
        json.put("Title", name);
        return json;
    }



}
