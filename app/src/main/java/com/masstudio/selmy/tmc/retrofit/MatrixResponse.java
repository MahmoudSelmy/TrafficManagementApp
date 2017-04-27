package com.masstudio.selmy.tmc.retrofit;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by tech lap on 14/03/2017.
 */

public class MatrixResponse {

    @SerializedName("destination_addresses")
    private List<String> destinationAddresses;
    @SerializedName("origin_addresses")
    private List<String> originAddresses;
    @SerializedName("rows")
    private List<Elements> rows;
    @SerializedName("status")
    private String status;

    public MatrixResponse(List<String> destinationAddresses, List<String> originAddresses, List<Elements> rows, String status) {
        this.destinationAddresses = destinationAddresses;
        this.originAddresses = originAddresses;
        this.rows = rows;
        this.status = status;
    }

    public List<String> getDestinationAddresses() {
        return destinationAddresses;
    }

    public void setDestinationAddresses(List<String> destinationAddresses) {
        this.destinationAddresses = destinationAddresses;
    }

    public List<String> getOriginAddresses() {
        return originAddresses;
    }

    public void setOriginAddresses(List<String> originAddresses) {
        this.originAddresses = originAddresses;
    }

    public List<Elements> getRows() {
        return rows;
    }

    public void setRows(List<Elements> rows) {
        this.rows = rows;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
