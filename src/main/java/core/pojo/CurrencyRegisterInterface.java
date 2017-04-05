package core.pojo;

import core.trigger.CurrencyThresholdTrigger;

import java.io.IOException;

/**
 * Created by neshati on 1/31/2017.
 * Behpardaz
 */
public class CurrencyRegisterInterface {
    private int sdp_appletId;
    private int sdp_userId;
    private String channelName;
    private String serviceName;
    private FilterVaribales filters;
    private ChannelUserData channelUserData;

    public ChannelUserData getChannelUserData() {
        return channelUserData;
    }

    public void setChannelUserData(ChannelUserData channelUserData) {
        this.channelUserData = channelUserData;
    }

    @Override
    public String toString() {
        return "CurrencyRegisterInterface{" +
                "sdp_appletId=" + sdp_appletId +
                ", sdp_userId=" + sdp_userId +
                ", channelName='" + channelName + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", filters=" + filters + '}';
    }

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

    public void insertCurrencyThresholdService() {
        try {
            if(serviceName.equalsIgnoreCase("upper"))
            CurrencyThresholdTrigger.addTreshold(this.filters.currencyType, this.filters.value, CurrencyThresholdTrigger.GOUP);
            else if(serviceName.equalsIgnoreCase("upper"))
                CurrencyThresholdTrigger.addTreshold(this.filters.currencyType, this.filters.value, CurrencyThresholdTrigger.GODOWN);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    static class FilterVaribales {
        private String currencyType;
        private double value;
        //private int goUpper;

        @Override
        public String toString() {
            return "FilterVaribales{" +
                    "currencyType='" + currencyType + '\'' +
                    ", value=" + value +
                    //", goUpper=" + goUpper +
                    '}';
        }

        public String getCurrencyType() {
            return currencyType;
        }

        public void setCurrencyType(String currencyType) {
            this.currencyType = currencyType;
        }

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }


    }

    static class ChannelUserData {
    }


}
