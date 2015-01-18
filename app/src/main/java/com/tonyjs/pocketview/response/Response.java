package com.tonyjs.pocketview.response;

import com.google.gson.annotations.SerializedName;
import com.tonyjs.pocketview.model.Meta;

/**
 * Created by tonyjs on 15. 1. 8..
 */
public class Response {
    @SerializedName("meta") private Meta meta;

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }
}
