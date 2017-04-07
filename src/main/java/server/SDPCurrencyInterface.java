package server;

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;
import core.entity.Currency;
import core.pojo.CurrencyDailyInterface;
import core.pojo.CurrencyRegisterInterface;
import core.service.CurrencyDailyService;
import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import util.StringUtil;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by neshati on 1/29/2017.
 Behpardaz
 */

@Path("/currency")
public class SDPCurrencyInterface {
    private static Properties prop = new Properties();
    private static String currency_endpoint;

        // The Java method will process HTTP GET requests
        @GET
    // The Java method will produce content identified by the MIME Media type "text/plain"
    @Produces("application/json")
    public String getCurrencyTypes() throws JSONException {
        // Return some cliched textual content
        JSONArray array = new JSONArray();

        array.put(getJsonObject("USD", StringEscapeUtils.unescapeJava(StringUtil.USD_PERSIAN)));
        array.put(getJsonObject("AED",StringEscapeUtils.unescapeJava(StringUtil.AED_PERSIAN)));
        array.put(getJsonObject("EUR",StringEscapeUtils.unescapeJava(StringUtil.EUR_PERSIAN)));
        array.put(getJsonObject("GBP",StringEscapeUtils.unescapeJava(StringUtil.GBP_PERSIAN)));

        return array.toString();
    }


    // The Java method will process HTTP GET requests
    @Path("/severity")
    @GET
    // The Java method will produce content identified by the MIME Media type "text/plain"
    @Produces("application/json")
    public String getSeverityTypes() throws JSONException {
        // Return some cliched textual content
        JSONArray array = new JSONArray();

        array.put(getJsonObject(StringUtil.Severity_HIGH, StringEscapeUtils.unescapeJava(StringUtil.Severity_HIGH_PERSIAN)));
        array.put(getJsonObject(StringUtil.Severity_MEDIUM,StringEscapeUtils.unescapeJava(StringUtil.Severity_MEDIUM_PERSIAN)));
        array.put(getJsonObject(StringUtil.Severity_LOW,StringEscapeUtils.unescapeJava(StringUtil.Severity_LOW_PERSIAN)));
        return array.toString();
    }




    private JSONObject getJsonObject(String usd, String name) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("Code", usd);
        json.put("Title", name);
        return json;
    }


    @Path("/daily")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    public String registerCurrenyDailyService(CurrencyDailyInterface service) throws JSONException {
        //service.insertCurrencyDailyService();\
        JSONObject jsonObject = new JSONObject();
        try {
            ArrayList<Currency> newCurrencies = CurrencyDailyService.getInstance().callRemoteCurrencyService();
            for (Currency next : newCurrencies) {
                if(next.englishName==null)
                    continue;
                if(next.englishName.equalsIgnoreCase(service.currencyType)){
                    jsonObject.put("englishName",next.englishName);
                    jsonObject.put("persianName",next.persianName);
                    jsonObject.put("price",next.price);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }





    // for services which needs to be registered!
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerCurrenyService(CurrencyRegisterInterface service) throws JSONException {
        service.insertCurrencyThresholdService();
        return  Response.status(200).build();
    }


    public static void main(String[] args) throws IOException {
        setConfigs();
        HttpServer server = HttpServerFactory.create(currency_endpoint);
        server.start();

        System.out.println("Server running");
        //System.out.println("Visit: http://localhost:9990/currencyTypes");
        System.out.println("Hit return to stop...");
        System.in.read();
        System.out.println("Stopping server");
        server.stop(0);
        System.out.println("Server stopped");
    }

    private static String setConfigs() {
        InputStream input ;
        try {
            input = new FileInputStream("config.properties");
            prop.load(input);
            currency_endpoint = prop.getProperty("currency_endpoint");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



}

