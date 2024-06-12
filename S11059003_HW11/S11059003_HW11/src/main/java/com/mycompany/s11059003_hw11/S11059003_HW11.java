/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.s11059003_hw11;
import java.io.*;
import java.util.*;
import java.net.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

class Stock {
    public String date;
    public int day;
    public double price;
    public double avg5;
    public double avg10;
    
    public void get_day() {
        if(date.length() > 2) {
            //System.out.print("day:" + date.substring(date.length() - 2));
            day = Integer.parseInt(date.substring(date.length() - 2));
        }
    }
}
    
public class S11059003_HW11 {
    private String code = "";
    private String month = "";
    private int total = 0;
    private gui graph;
    private final static String year = "2024";
    private int[] days;
    
    private String title;
    StringBuilder buf;
    JSONArray data;
   
    S11059003_HW11(gui graph, String month, String code) {
        this.graph = graph;
        this.month = month;
        this.code = code;
    }
    
    String[] space1 = {"\t\t", "\t\t", "\t\t\t", "\t\t", "\t\t", "\t\t", "\t\t", "\t\t", "\t\t"};
    String[] space2 = {"\t", "\t", "\t\t", "\t\t", "\t\t", "\t\t", "\t\t", "\t\t", "\t\t"};
    
    public void start() throws ParseException {
        Stock[] stock = parse_json();
        if(stock != null) {
            graph.state.setText("");
            for(Stock s: stock) {
                get_average(stock);
            }
            graph.paint(graph.out.getGraphics(), stock);
            select_day(days);
            //print_json();
            //print_stock(stock);
        }
        else {
            graph.state.setText("ERROR");
            graph.repaint(graph.out.getGraphics(), graph.g2, graph.width, graph.height);
        }
    }

    private Stock[] parse_json() throws ParseException {
        int i;
        String temp;
        Stock[] result = null;
        JSONParser parser = new JSONParser();
        
        String url = "https://www.twse.com.tw/exchangeReport/STOCK_DAY?response=json&date=" + year + month + "01&stockNo=" + code;
        try {
            URL u = new URL(url);
            HttpURLConnection connect = (HttpURLConnection) u.openConnection();
            connect.setRequestMethod("GET");
            
            StringBuilder res;
            try (BufferedReader read = new BufferedReader(new InputStreamReader(connect.getInputStream()))) {
                String line;
                res = new StringBuilder();
                while((line = read.readLine()) != null) {
                    res.append(line);
                }
            }
            
            JSONObject obj = (JSONObject) parser.parse(res.toString());
            
            total = Math.toIntExact((long) obj.get("total"));
            if(total != 0) {
                result = get_json(obj);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    private Stock[] get_json(JSONObject obj) {
        int i;
        
        // title
        title = (String) obj.get("title");
        
        // fields
        String temp;
        i = 0;
        JSONArray array = (JSONArray) obj.get("fields");
        Iterator<String> iter = array.iterator();
        buf = new StringBuilder();
        while(iter.hasNext()) {
            temp = iter.next();
            buf.append(temp + space1[i++]);
        }
        
        // data
        data = (JSONArray) obj.get("data");
        Stock[] result = new Stock[total];
        days = new int[total];

        for(i = 0; i < data.size(); i++) {
            JSONArray record = (JSONArray) data.get(i);
            result[i] = new Stock();
            result[i].date = ((String) record.get(0)).replace("\\/", "/");
            result[i].price = Double.parseDouble(((String) record.get(6)).replace(",", ""));
            result[i].get_day();
            days[i] = result[i].day;
        }
        return result;
    }
    
    private void get_average(Stock[] stock) {
        double sum = 0;
        int i, j;
        
        for(i = 0; i < stock.length; i++) {
            Stock current = stock[i];
            
            if(i > 3) {
                for(j = 0; j < 5; j++) {
                    sum += stock[i-j].price;
                }
                current.avg5 = sum / 5;
            }
            else {
                current.avg5 = -1;
                current.avg10 = -1;
                continue;
            }
            
            if(i > 8) {
                sum = 0;
                for(j = 0; j < 10; j++) {
                    sum += stock[i-j].price;
                }
                current.avg10 = sum / 10;
            }
            else {
                current.avg10 = -1;
            }
        }
    }
    
    private void select_day(int[] a) {
        int i;
        int n = a.length;
        if (n < 1) {
            throw new IllegalArgumentException("Array length must be at least 1");
        }

        int[] b = {0, 0, 0, 0, 0, 0};

        b[0] = a[0];
        if (n == 1) {
            return;
        }

        b[5] = a[n - 1];
        if (n <= 6) {
            System.arraycopy(a, 1, b, 1, n - 1 - 1);
        } 
        else {
            int step = (n - 1) / 5;
            for(i = 1; i < 5; i++) {
                b[i] = a[i * step];
            }
        }
        
        String[] d = {"Date1", "Date2", "Date3", "Date4", "Date5", "Date6"};
        java.awt.Label[] dates = {graph.date1, graph.date2, graph.date3, graph.date4, graph.date_5, graph.date6};
        for (i = 0; i < dates.length; i++) {
            dates[i].setText(d[i]);
            if(b[i] != 0) {
                dates[i].setText(String.valueOf(b[i]));
            }
        }
    }
    
    private void print_json() {
        System.out.println(title);
        System.out.println(buf);
        for(int i = 0; i < total; i++) {
            JSONArray record = (JSONArray) data.get(i);
            System.out.print(((String) record.get(0)).replace("\\/", "/") + space2[0]);
            for(int j = 1; j < record.size(); j++) {
                System.out.print(record.get(j) + space2[j]);
            }
            System.out.println();  
        }
        System.out.println();
    }
    
    private void print_stock(Stock[] stock) {
        for(Stock s: stock) {
            System.out.printf("Date:%-10s\tDay:%s\tPrice:%s\tAvg5:%s\tAvg10:%s", s.date, s.day, s.price, s.avg5, s.avg10 + "\n");
        }
        System.out.println();
    }
}