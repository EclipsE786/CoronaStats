package com.isaevco.coronastats.screens.info;

import com.isaevco.coronastats.pojo.Country;

import java.util.List;

public interface CoronaInfoView {

    void showData(List<Country> countries);

    void showError(Throwable throwable);

    void setOldData(List<Country> countries);
}
