package com.masstudio.selmy.tmc.retrofit;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by tech lap on 14/03/2017.
 */

public interface ApiInterface {
    /*
    @GET("json?origins={origin}&destinations={destination}&departure_time={time}&traffic_model={tModel}&key={key}")
    Call<MatrixResponse> getEstimations(@Path("origin") String origin, @Path("destination") String destination,@Path("time") String time,@Path("tModel") String tModel,@Path("key") String key);*/
    @GET("json")
    Call<MatrixResponse> getEstimations(@QueryMap Map<String, String> options);
}
