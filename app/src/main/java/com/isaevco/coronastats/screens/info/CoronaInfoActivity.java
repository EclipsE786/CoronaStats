package com.isaevco.coronastats.screens.info;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.isaevco.coronastats.R;
import com.isaevco.coronastats.pojo.Country;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class CoronaInfoActivity extends AppCompatActivity implements CoronaInfoView, DatePickerDialog.OnDateSetListener {

    // Initializing views used in activity
    private RadioGroup radioGroupStatsType;
    private EditText editTextCountry;
    private CheckBox checkBoxUzbekistan;
    private TextView textViewCountry;
    private TextView textViewPopulation;
    private TextView textViewCases;
    private TextView textViewRecovered;
    private TextView textViewDeaths;
    private TextView textViewFromDate;
    private TextView textViewToDate;
    private RadioGroup radioGroupDates;
    private LinearLayout linearLayout;

    // Values used for DatePicker
    private boolean isFromDate = false;
    private boolean isToDate = false;
    private final String DATE_PICKER_TAG = "datePicker";

    // Stats parameter variables
    private String typeOfStats;
    private boolean fromDateExists = false;
    private String toDate = "";
    private String fromDate = "";

    // Presenter
    private CoronaInfoPresenter presenter;

    // Values used for calculating statistics between two dates
    private boolean oldDataExists = false;
    private int casesOld;
    private int recoveredOld;
    private int deathsOld;

    // Value to know is given enough info to download data
    private boolean canDownloadData = false;

    // Date format pattern used by API
    private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";

    // PARAMETERS USED BY API
    private final String PARAMS_ALL_COUNTRIES = "All";
    private final String PARAMS_COUNTRY_UZBEKISTAN = "Uzbekistan";

    private Timer timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        presenter = new CoronaInfoPresenter(this);

        linearLayout = findViewById(R.id.linearLayoutDates);
        radioGroupStatsType = findViewById(R.id.radioGroupStatsType);
        editTextCountry = findViewById(R.id.editTextCountry);
        checkBoxUzbekistan = findViewById(R.id.checkBoxUzbekistan);
        textViewCountry = findViewById(R.id.textViewCountry);
        textViewPopulation = findViewById(R.id.textViewPopulation);
        textViewCases = findViewById(R.id.textViewCases);
        textViewRecovered = findViewById(R.id.textViewRecovered);
        textViewDeaths = findViewById(R.id.textViewDeaths);
        textViewFromDate = findViewById(R.id.textViewFromDate);
        textViewToDate = findViewById(R.id.textViewToDate);
        radioGroupDates = findViewById(R.id.radioGroupDates);

        timer = new Timer();
        startTimer();
    }

    private void startTimer() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadData();
                    }
                });
            }
        }, 10000, 10000);
    }

    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    protected void onDestroy() {
        presenter.disposeDisposables();
        cancelTimer();
        super.onDestroy();
    }


    private void setTypeOfStats() {
        int id = radioGroupStatsType.getCheckedRadioButtonId();
        String countryText = editTextCountry.getText().toString().trim();

        switch (id) {
            case R.id.radioButtonAll:
                typeOfStats = PARAMS_ALL_COUNTRIES;
                checkBoxUzbekistan.setChecked(false);
                checkBoxUzbekistan.setVisibility(View.GONE);
                editTextCountry.setVisibility(View.GONE);
                editTextCountry.getText().clear();
                break;

            case R.id.radioButtonExactCountry:
                editTextCountry.setVisibility(View.VISIBLE);
                checkBoxUzbekistan.setVisibility(View.VISIBLE);
                if (checkBoxUzbekistan.isChecked()) {
                    typeOfStats = PARAMS_COUNTRY_UZBEKISTAN;
                    editTextCountry.getText().clear();
                } else if (!countryText.isEmpty()) {
                    typeOfStats = countryText;
                } else {
                    typeOfStats = PARAMS_ALL_COUNTRIES;
                }
                break;
        }

        int idDates = radioGroupDates.getCheckedRadioButtonId();

        switch (idDates) {
            case R.id.radioButtonCurrent:
                fromDateExists = false;
                textViewFromDate.setText(getResources().getString(R.string.from_date_hint));
                textViewToDate.setText(getResources().getString(R.string.to_date_hint));
                linearLayout.setVisibility(View.GONE);
                setCurrentDate();
                canDownloadData = true;
                break;
            case R.id.radioButtonBetween:
                fromDate = textViewFromDate.getText().toString();
                toDate = textViewToDate.getText().toString();
                linearLayout.setVisibility(View.VISIBLE);
                if (fromDate.equals(getResources().getString(R.string.from_date_hint)) && toDate.equals(getResources().getString(R.string.to_date_hint))) {
                    canDownloadData = false;
                } else if (fromDate.equals(getResources().getString(R.string.from_date_hint))) {
                    fromDateExists = false;
                    canDownloadData = true;
                } else {
                    fromDateExists = true;
                    canDownloadData = true;
                }
                break;
        }
    }

    // Getting and formatting Current date
    private void setCurrentDate() {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT_PATTERN, Locale.getDefault());
        toDate = df.format(date);
    }

    public void onClickChangeStatsType(View view) {
        setTypeOfStats();
    }

    public void onClickDownloadData(View view) {
        loadData();
    }

    private void loadData() {
        if (!oldDataExists) {
            setTypeOfStats();
        }
        if (canDownloadData) {
            if (fromDateExists) {
                presenter.downloadData(fromDateExists, typeOfStats, fromDate);
                fromDateExists = false;
            } else if (!fromDateExists && oldDataExists) {
                presenter.downloadData(fromDateExists, typeOfStats, toDate);
            } else {
                presenter.downloadData(fromDateExists, typeOfStats, toDate);
            }
        } else {
            Toast.makeText(CoronaInfoActivity.this, getResources().getString(R.string.warning_enter_at_least_end_date), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void setOldData(List<Country> countries) {
        if (countries.size() == 0){
            casesOld = 0;
            recoveredOld = 0;
            deathsOld = 0;
        } else {
            casesOld = countries.get(0).getCases().getTotal() == null ? 0 : countries.get(0).getCases().getTotal();
            recoveredOld = countries.get(0).getCases().getRecovered() == null ? 0 : countries.get(0).getCases().getRecovered();
            try {
                deathsOld = countries.get(0).getDeaths().getTotal();
            } catch (Exception e) {
                e.printStackTrace();
                deathsOld = 0;
            }
        }
        oldDataExists = true;
        loadData();
    }

    @Override
    public void showData(List<Country> countries) {

        Country country = countries.get(0);

        String countryName = country.getCountryName().equals(PARAMS_ALL_COUNTRIES) ? "Все страны" : country.getCountryName();
        String population = country.getPopulation() == null ? "" : Integer.toString(country.getPopulation());
        int cases = country.getCases().getTotal();
        int recovered = country.getCases().getRecovered();
        int deaths = country.getDeaths().getTotal();

        if (oldDataExists) {
            oldDataExists = false;
            cases = Math.max(cases - casesOld, 0);
            recovered = Math.max(recovered - recoveredOld, 0);
            deaths = Math.max(deaths - deathsOld, 0);
        }

        // Should set data to textViews!!!
        Toast.makeText(CoronaInfoActivity.this, getResources().getString(R.string.warning_successfull_download), Toast.LENGTH_SHORT).show();

        setDataToTextViews(countryName, population, Integer.toString(cases), Integer.toString(recovered), Integer.toString(deaths));
    }

    private void setDataToTextViews(String countryName, String population, String cases, String recovered, String deaths) {
        textViewCountry.setText(countryName);
        textViewPopulation.setText(population);
        textViewCases.setText(cases);
        textViewRecovered.setText(recovered);
        textViewDeaths.setText(deaths);
    }


    @Override
    public void showError(Throwable throwable) {
        Toast.makeText(CoronaInfoActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
        Log.i("myErrors", throwable.getMessage());
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
        if (isFromDate) {
            textViewFromDate.setText(date);
            isFromDate = false;
        } else if (isToDate) {
            textViewToDate.setText(date);
            isToDate = false;
        }
    }

    public void onClickAddFromDate(View view) {
        isFromDate = true;
        DialogFragment fragment = new DatePickerFragment();
        fragment.show(getSupportFragmentManager(), DATE_PICKER_TAG);
    }

    public void onClickAddToDate(View view) {
        isToDate = true;
        DialogFragment fragment = new DatePickerFragment();
        fragment.show(getSupportFragmentManager(), DATE_PICKER_TAG);
    }

    // DatePicker class
    public static class DatePickerFragment extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) getActivity(), year, month, day);
        }
    }

}