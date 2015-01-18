package com.tonyjs.pocketview.request;

import com.tonyjs.pocketview.response.NewsFeedResponse;
import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by tony.park on 15. 1. 18..
 */
public interface ApiInterface {
    String END_POINT = "https://api.instagram.com";

    @GET("/v1/users/self/feed?access_token=635592037.1fb234f.23e61a42c4fc42bca0e6a1f007045648")
    public void getItems(Callback<NewsFeedResponse> callback);
}
