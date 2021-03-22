package com.isaevco.coronastats.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Tests {
    @SerializedName("1M_pop")
    @Expose
    private Object _1MPop;
    @SerializedName("total")
    @Expose
    private Object total;

    public Object get1MPop() {
        return _1MPop;
    }

    public void set1MPop(Object _1MPop) {
        this._1MPop = _1MPop;
    }

    public Object getTotal() {
        return total;
    }

    public void setTotal(Object total) {
        this.total = total;
    }
}
