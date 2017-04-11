package core.pojo;

import core.trigger.CoinThresholdTrigger;

import java.io.IOException;

/**
 * Created by Mahmood on 4/5/2017.
 * mahmood.neshati@gmail.com
 */
public class CoinRegisterInterface {
    private int sdp_appletId;
    private int sdp_userId;
    private String channelName;
    private String serviceName;
    private FilterVaribales filters;
    private ChannelUserData channelUserData;

    public int getSdp_appletId() {
        return sdp_appletId;
    }

    public void setSdp_appletId(int sdp_appletId) {
        this.sdp_appletId = sdp_appletId;
    }

    public int getSdp_userId() {
        return sdp_userId;
    }

    public void setSdp_userId(int sdp_userId) {
        this.sdp_userId = sdp_userId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public FilterVaribales getFilters() {
        return filters;
    }

    public void setFilters(FilterVaribales filters) {
        this.filters = filters;
    }

    public ChannelUserData getChannelUserData() {
        return channelUserData;
    }

    public void setChannelUserData(ChannelUserData channelUserData) {
        this.channelUserData = channelUserData;
    }

    public void insertCoinThresholdService() {
        // This function act like service registration switch.
        // Decide about the service that should be registered using the serviceName attribute of the the incoming registration request!
        try {
            if(serviceName.equalsIgnoreCase("upper"))
                CoinThresholdTrigger.addTreshold(this.filters.coinType, this.filters.treshold, CoinThresholdTrigger.GOUP);
            else if(serviceName.equalsIgnoreCase("lower"))
                CoinThresholdTrigger.addTreshold(this.filters.coinType, this.filters.treshold, CoinThresholdTrigger.GODOWN);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static class FilterVaribales {
        private String coinType;
        private double treshold;

        public String getCoinType() {
            return coinType;
        }

        public void setCoinType(String coinType) {
            this.coinType = coinType;
        }

        public double getTreshold() {
            return treshold;
        }

        public void setTreshold(double treshold) {
            this.treshold = treshold;
        }
    }

    static class ChannelUserData {
    }

}
