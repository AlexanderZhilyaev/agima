package com.example.user_pc.currency;

import android.os.AsyncTask;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class ValueOfCurrency extends AsyncTask<String, Void, Map> {

    public static Map<String, String> currency = new HashMap<String, String>();
    static final String URL_ADRRESS = "http://www.cbr.ru/scripts/XML_daily.asp?date_req=";
    @Override
    public Map<String, String> doInBackground(String... date) {
        try {
            String dateStr = Arrays.toString(date);

            dateStr= dateStr.substring(1,dateStr.length()-1);

            URL url = new URL(URL_ADRRESS + dateStr);
            URLConnection conn = url.openConnection();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(conn.getInputStream());

            NodeList nodes = doc.getElementsByTagName("Valute");
            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);
                NodeList key = element.getElementsByTagName("CharCode");
                NodeList value = element.getElementsByTagName("Value");
                NodeList nominal = element.getElementsByTagName("Nominal");
                float val = Float.parseFloat(value.item(0).getTextContent().replace(',','.'))
                        * Float.parseFloat(nominal.item(0).getTextContent().replace(',','.'));
                currency.put(key.item(0).getTextContent(), String.valueOf(val));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currency;
    }
    @Override
    public void onPostExecute(Map result) {
        super.onPostExecute(result);
    }
}
