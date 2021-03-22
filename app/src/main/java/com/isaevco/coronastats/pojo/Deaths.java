package com.isaevco.coronastats.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Deaths {
    @SerializedName("new")
    @Expose
    private Object _new;
    @SerializedName("1M_pop")
    @Expose
    private Object _1MPop;
    @SerializedName("total")
    @Expose
    private int total;

    public Object getNew() {
        return _new;
    }

    public void setNew(Object _new) {
        this._new = _new;
    }

    public Object get1MPop() {
        return _1MPop;
    }

    public void set1MPop(Object _1MPop) {
        this._1MPop = _1MPop;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
