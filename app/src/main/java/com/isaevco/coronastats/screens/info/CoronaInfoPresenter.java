package com.isaevco.coronastats.screens.info;

import com.isaevco.coronastats.api.ApiFactory;
import com.isaevco.coronastats.api.ApiService;
import com.isaevco.coronastats.pojo.Cases;
import com.isaevco.coronastats.pojo.Country;
import com.isaevco.coronastats.pojo.Deaths;
import com.isaevco.coronastats.pojo.Response;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class CoronaInfoPresenter {

    public static final String API_KEY = "3e26202b8amsh751c2e5c99b688ap13fd78jsnd6e5e07c5e81";
    public static final String HOST = "covid-193.p.rapidapi.com";

    private CompositeDisposable compositeDisposable;
    private CoronaInfoView view;

    public CoronaInfoPresenter(CoronaInfoView view) {
        this.view = view;
    }

    public void downloadData(boolean fromDateExists, String typeOfStats, String... dates) {

        ApiFactory apiFactory = ApiFactory.getInstance();
        ApiService apiService = apiFactory.getApiService();
        compositeDisposable = new CompositeDisposable();

        Disposable disposable = apiService.getHistoryUsingDate(typeOfStats, dates[0])
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Response>() {
                    @Override
                    public void accept(Response response) throws Exception {
                        if (fromDateExists){
                            // This case works if inputted both from and to dates for statistics
                            view.setOldData(response.getCountries());
                        } else {
                            view.showData(response.getCountries());
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        view.showError(throwable);
                    }
                });
        compositeDisposable.add(disposable);
    }


    public void disposeDisposables() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
    }
}
