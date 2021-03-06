package server;

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;
import core.pojo.CoinRegisterInterface;
import core.pojo.GoldCurrentPriceRequest;
import core.service.GoldService;
import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import util.StringUtil;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * Created by neshati on 4/4/2017.
 * Behpardaz
 */

@Path("coin")
public class SDPCoinInterface {
    private static Properties prop = new Properties();
    private static String gold_endpoint;


    @GET
    @Produces("application/json")
    public String getCoinTypes() throws JSONException {
        JSONArray array = new JSONArray();
        array.put(getJsonObject(StringUtil.Complete_Coin, StringEscapeUtils.unescapeJava(StringUtil.Complete_Coin_PERSIAN)));
        array.put(getJsonObject(StringUtil.Half_Coin, StringEscapeUtils.unescapeJava(StringUtil.Half_Coin_PERSIAN)));
        array.put(getJsonObject(StringUtil.ROB_Coin, StringEscapeUtils.unescapeJava(StringUtil.ROB_Coin_PERSIAN)));
        return array.toString();
    }

    private JSONObject getJsonObject(String usd, String name) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("Code", usd);
        json.put("Title", name);
        return json;
    }

    public static void main(String[] args) throws IOException {
        setConfigs();
        HttpServer server = HttpServerFactory.create(gold_endpoint);
        server.start();

        System.out.println("Coin Service is running");
        System.out.println("Hit return to stop...");
        System.in.read();
        System.out.println("Stopping server");
        server.stop(0);
        System.out.println("Server stopped");
    }

    private static String setConfigs() {
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream input = loader.getResourceAsStream("config.properties");
            prop.load(input);
            gold_endpoint = prop.getProperty("gold_endpoint");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Path("instant")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON )
    public String getCurrentGoldPrice(GoldCurrentPriceRequest goldPriceRequest) throws JSONException, UnsupportedEncodingException {
        JSONObject out = new JSONObject();
        String message= GoldService.getInstance().getCurrentPriceMessage();
        out.put("message", message);
        return new String(out.toString().getBytes(),"UTF-8");
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
        public Response registerCurrenyService(CoinRegisterInterface service) throws JSONException {
        int result = service.insertCoinThresholdService();
        if(result>0) {
            return Response.status(200).build();
        }else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }


    // The Java method will process HTTP GET requests
    @Path("HobabLevel")
    @GET
    // The Java method will produce content identified by the MIME Media type "text/plain"
    @Produces("application/json")
    public String getHobabLevel() throws JSONException {
        JSONArray array = new JSONArray();
        array.put(getJsonObject(StringUtil.Hobab_Level_HIGH, StringEscapeUtils.unescapeJava(StringUtil.Hobab_Level_HIGH_PERSIAN)));
        array.put(getJsonObject(StringUtil.Hobab_Level_MEDIUM, StringEscapeUtils.unescapeJava(StringUtil.Hobab_Level_MEDIUM_PERSIAN)));
        return array.toString();
    }

}
