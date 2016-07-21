package com.example.user_pc.currency;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.hardware.camera2.CameraCharacteristics;
import android.net.Uri;
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

    public EditText mNameCurLeftEditText;
    public EditText mNameCurRightEditText;
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

    Map<String, String> myMap = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        today = Calendar.getInstance();

        mDateText = (EditText) findViewById(R.id.dateText);
        mNameCurLeftEditText = (EditText) findViewById(R.id.nameCurLeftEditText);
        mNameCurRightEditText = (EditText) findViewById(R.id.nameCurRightEditText);
        mValueRightEditText = (EditText) findViewById(R.id.valueRightEditText);
        mValueLeftEditText = (EditText) findViewById(R.id.valueLeftEditText);
        mFrameLayout = (FrameLayout) findViewById(R.id.FrameLayout);
        mValueUsdTextView = (TextView) findViewById(R.id.valueUsdTextView);
        mValueEurTextView = (TextView) findViewById(R.id.valueEurTextView);
        mListView = (ListView) findViewById(R.id.listView);
        mListViewR = (ListView) findViewById(R.id.listViewr);


        mDateText.setText(currentDate());

        //получение списка валют
        recValues(currentDate());
        //Слушатели на выбор валют
        clickSelectOfCurrency();
        clickSelectOfCurrencyR();
       //выбор даты
        mDatePicker();

    }

    //Текущая дата
    public String currentDate() {
        return new SimpleDateFormat("dd/MM/yyyy").format(System.currentTimeMillis());
    }

    public void ratioCurrency(){
        float Value = 0;
        float ValueCurrencyLeft = Float.parseFloat((myMap.get(mNameCurLeftEditText.getText().toString()).toString()).replace(',', '.'));
        float ValueCurrencyRight = Float.parseFloat((myMap.get(mNameCurRightEditText.getText().toString()).toString()).replace(',', '.'));


        if (mValueLeftEditText.length() == 0) {
            Value = ValueCurrencyLeft / ValueCurrencyRight;
        } else {
            float NumberCurrency = Float.parseFloat(mValueLeftEditText.getText().toString());
            Value = ValueCurrencyLeft / ValueCurrencyRight * NumberCurrency;
        }

        mValueRightEditText.setText(String.valueOf(Value));
    }


    public void ratioNameLeft(){
        mNameCurLeftEditText.addTextChangedListener(new TextWatcher() {
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

    public void ratioNAmeRight(){
        mNameCurRightEditText.addTextChangedListener(new TextWatcher() {
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


    //Изменение значение количеста валют
    public void ratioValue(final Map myMap) {
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

    //Показ списка выбора правой валюты
    public void clickSelectOfCurrencyR() {
        mNameCurRightEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListViewR.setVisibility(View.VISIBLE);
            }
        });
    }

    //Показ списка выбора левой валюты
    public void clickSelectOfCurrency() {
        mNameCurLeftEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListView.setVisibility(View.VISIBLE);
            }
        });
    }

    //Выбор правой валюты
    public void clickOfValueR() {
        mListViewR.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {
                TextView textView = (TextView) itemClicked;
                String strText = textView.getText().toString();
                mNameCurRightEditText.setText(strText);
                mListViewR.setVisibility(View.INVISIBLE);
            }
        });
    }

    //Выбор левой валюты
    public void clickOfValue() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {
                TextView textView = (TextView) itemClicked;
                String strText = textView.getText().toString();
                mNameCurLeftEditText.setText(strText);
                mValueRightEditText.setText(myMap.get(strText).toString());
                mListView.setVisibility(View.INVISIBLE);
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
        mListViewR.setAdapter(adapter);
        mListView.setAdapter(adapter);
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
        SelectOfCurrency(myMap);
        clickOfValue();
        clickOfValueR();
        ratioValue(myMap);
        ratioNameLeft();
        ratioNAmeRight();
        mValueUsdTextView.setText("USD: " + myMap.get("USD"));
        mValueEurTextView.setText("EUR: " + myMap.get("EUR"));
    }
}


