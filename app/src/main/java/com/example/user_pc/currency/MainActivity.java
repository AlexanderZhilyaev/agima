package com.example.user_pc.currency;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.hardware.camera2.CameraCharacteristics;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.AbstractSequentialList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MainActivity extends AppCompatActivity {

    public EditText mValueLeftEditText;
    public EditText mValueRightEditText;
    public EditText mDateText;
    public Calendar today;
    public FrameLayout mFrameLayout;
    public ListView mListView;
    private TextView mValueUsdTextView;
    private TextView mValueEurTextView;
    ValueOfCurrency mValueOfCurrency;
    public ListView mListViewR;
    public Spinner mSpinnerLeftCurr;
    public Spinner mSpinnerRightCurr;

    Map<String, String> myMap = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        today = Calendar.getInstance();

        mDateText = (EditText) findViewById(R.id.dateText);
        mValueRightEditText = (EditText) findViewById(R.id.valueRightEditText);
        mValueLeftEditText = (EditText) findViewById(R.id.valueLeftEditText);
        mFrameLayout = (FrameLayout) findViewById(R.id.FrameLayout);
        mValueUsdTextView = (TextView) findViewById(R.id.valueUsdTextView);
        mValueEurTextView = (TextView) findViewById(R.id.valueEurTextView);
        mListView = (ListView) findViewById(R.id.listView);
        mListViewR = (ListView) findViewById(R.id.listViewr);
        mSpinnerLeftCurr = (Spinner) findViewById(R.id.spinnerLeftCurr);
        mSpinnerRightCurr = (Spinner) findViewById(R.id.spinnerRightCurr);

        mDateText.setText(currentDate());

        //получение списка валют
        recValues(currentDate());
        //выбор даты
        mDatePicker();

    }



    //Получение списка валют
    public void recValues(String date) {
        mValueOfCurrency = new ValueOfCurrency();
        mValueOfCurrency.execute(date);

        try {
            myMap = mValueOfCurrency.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        clickOfValueL(myMap);
        clickOfValueR(myMap);
        ratioValue();
        SelectOfCurrency(myMap);
        mValueUsdTextView.setText("USD: " + myMap.get("USD"));
        mValueEurTextView.setText("EUR: " + myMap.get("EUR"));
    }

    //Текущая дата
    public String currentDate() {
        return new SimpleDateFormat("dd/MM/yyyy").format(System.currentTimeMillis());
    }

    public void ratioCurrency() {
        float Value = 0;
        float ValueCurrencyLeft = Float.parseFloat((myMap.get(mSpinnerLeftCurr.getSelectedItem().toString()).toString()).replace(',', '.'));
        float ValueCurrencyRight = Float.parseFloat((myMap.get(mSpinnerRightCurr.getSelectedItem().toString()).toString()).replace(',', '.'));


        if (mValueLeftEditText.length() == 0) {
            Value = ValueCurrencyLeft / ValueCurrencyRight;
        } else {
            float NumberCurrency = Float.parseFloat(mValueLeftEditText.getText().toString());
            Value = ValueCurrencyLeft / ValueCurrencyRight * NumberCurrency;
        }

        mValueRightEditText.setText(String.valueOf(Value));
    }



    //Изменение значение количеста валют
    public void ratioValue() {
        mValueLeftEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                ratioCurrency();
            }
        });
    }

    //Выбор правой валюты
    public void clickOfValueR(Map myMap) {
        final String[] val = new String[myMap.size()];
        int i = 0;
        for (Object key : myMap.keySet()) {
            val[i] = key.toString();
            i++;
        }
        mSpinnerRightCurr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                ratioCurrency();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    //Выбор левой валюты
    public void clickOfValueL(final Map myMap) {
        final String[] val = new String[myMap.size()];
        int i = 0;
        for (Object key : myMap.keySet()) {
            val[i] = key.toString();
            i++;
        }
        mSpinnerLeftCurr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                ratioCurrency();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void SelectOfCurrency(Map myMap) {
        String[] val = new String[myMap.size()];
        int i = 0;
        for (Object key : myMap.keySet()) {
            val[i] = key.toString();
            i++;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, val);
        mSpinnerLeftCurr.setAdapter(adapter);
        mSpinnerRightCurr.setAdapter(adapter);
    }

    public void mDatePicker() {
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfEyar, int dayOfMonth) {
                today.set(Calendar.YEAR, year);
                today.set(Calendar.MONTH, monthOfEyar);
                today.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                try {
                    updateLabel();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        };
        mDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(MainActivity.this, date,
                        today.get(Calendar.YEAR),
                        today.get(Calendar.MONTH),
                        today.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    private void updateLabel() throws ExecutionException, InterruptedException {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        mDateText.setText(sdf.format(today.getTime()));
        recValues(sdf.format(today.getTime()));
    }


}