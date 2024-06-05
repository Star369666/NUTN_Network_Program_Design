package com.mycompany.s11059003_hw10;
import java.io.*;
import java.util.*;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.util.stream.*;

class Bucket {
    String name;
    String code;
    String price;
    String loss;
    String volume;
    int volume_value;
    
    Bucket(String[] data) {
        name = data[0];
        code = data[1];
        price = data[2];
        loss = data[3];
        volume = data[4];
    }
    
    public void convert() {
        if(this.valid_check() == true) {
            String temp = volume.replaceAll(",", "");
            volume_value = Integer.parseInt(temp);
        }
    }
    
    public boolean valid_check() {
        return !volume.isEmpty() && volume != null && !volume.equals("-");
    }
}

public class S11059003_HW10 {
    static ArrayList<Bucket> bucket = new ArrayList<>();
    static ArrayList<Bucket> comparable_bucket = new ArrayList<>();
    final static String css_main = "li[class=List(n)]";
    final static String css_name = "div[class=Lh(20px) Fw(600) Fz(16px) Ell]";
    final static String css_name_fix = "div[class=Lh(20px) Fw(600) Fz(14px) Ell]";
    final static String css_code = "div[class=D(f) Ai(c)]";
    final static String css_price = "div[class=Fxg(1) Fxs(1) Fxb(0%) Ta(end) Mend($m-table-cell-space) Mend(0):lc Miw(68px)] > span";
    final static String css_loss = "div[class=Fxg(1) Fxs(1) Fxb(0%) Ta(end) Mend($m-table-cell-space) Mend(0):lc Miw(74px)] > span";
    final static String css_volume = "div[class=Fxg(1) Fxs(1) Fxb(0%) Miw($w-table-cell-min-width) Ta(end) Mend($m-table-cell-space) Mend(0):lc]";
    final static int[] valid_sector = {1,  2,  3,  4,  6,
                                       7,  37, 38, 9,  10,
                                       11, 12, 13, 40, 41,
                                       42, 43, 44, 45, 46,
                                       47, 19, 20, 21, 22,
                                       24, 39, 25, 26, 29,
                                       48, 49, 30, 31, 32,
                                       33, 51, 52, 95, 96,
                                       94, 93};
    private static S11059003_HW10_GUI main;
    private static int[] select = {51};
    private static WebClient web = new WebClient(BrowserVersion.BEST_SUPPORTED);
    private static int time = 0;
    
    S11059003_HW10(S11059003_HW10_GUI main) throws UnsupportedEncodingException {
        S11059003_HW10.main = main;
        System.setOut(new PrintStream(System.out, true, "UTF-8"));
        web.getOptions().setJavaScriptEnabled(false);
    }
    
    public void start(String str) throws IOException {
        select = get_select(str);
        if(select.length != 0) {
            parse_html(web, select);
        }
    }
    
    public void parse_html(WebClient web, int[] select) throws IOException {
        int i;
        bucket.clear();
        comparable_bucket.clear();
        
        for(int value: select) {
            String url = "https://tw.stock.yahoo.com/class-quote?sectorId=" + value + "&exchange=TAI";
            HtmlPage html = web.getPage(url);
            WebResponse response = html.getWebResponse();
            String content = response.getContentAsString();

            String[] data = new String[5];
            Document document = Jsoup.parse(content);
            Elements list = document.select(css_main);

            if(list.isEmpty()) {
                main.out.append("Not found any stock in your conditions!\n");
            }
            else {
                System.out.println("total: " + list.size());
                for(i = 0; i < list.size(); i++) {
                    data[0] = get_css_content(css_name, list, i);
                    data[1] = get_css_content(css_code, list, i);
                    data[2] = get_css_content(css_price, list, i);
                    data[3] = get_css_content(css_loss, list, i);
                    data[4] = get_css_content(css_volume, list, i);
                    Bucket temp = new Bucket(data);
                    temp.convert();
                    //System.out.printf(String.format("%-40s\t%s\t\t%s\t\t%s%n", temp.name + "(" +  temp.code + ")" , temp.price, temp.loss, temp.volume));
                    bucket.add(temp);
                    if(temp.valid_check()) {
                        comparable_bucket.add(temp);
                    }
                }
            }
        }
        
        get_top10_volume();
    }
    
    private static String get_css_content(String css, Elements element, int index) {
        Elements temp = element.get(index).select(css);
        
        if (!temp.isEmpty()) {
            return temp.get(0).text();
        }

        if (css.equals(css_name)) {
            Elements temp1 = element.get(index).select(css_name_fix);
            if (!temp1.isEmpty()) {
                return temp1.get(0).text();
            }
        }

        return "-";
    }
    
    public int[] get_select(String str) {
        if (str == null || str.trim().isEmpty()) {
            main.out.append("Invalid sector id was inputed!\n");
            return new int[0];
        }

        String temp = str.trim().replaceAll("\\s+", "");
        if (!temp.matches("\\d+(,\\d+)*")) {
            main.out.append("Invalid sector id was inputed!\n");
            return new int[0];
        }

        String[] num = temp.split(",");
        int[] result = new int[num.length];
        ArrayList<Integer> exist = new ArrayList<>();

        for (int i = 0; i < result.length; i++) {
            int check = Integer.parseInt(num[i]);
            if(IntStream.of(valid_sector).anyMatch(x -> x == check) && !exist.contains(check)) {
                result[i] = Integer.parseInt(num[i]);
                exist.add(result[i]);
            }
            else {
                main.out.append("Invalid sector id was inputed!\n");
                return new int[0];
            }
        }
        
        Arrays.sort(result);
        return result;
    }
    
    private static void get_top10_volume() {
        int i;
        Collections.sort(comparable_bucket, Comparator.comparingInt((Bucket b) -> b.volume_value).reversed());
        System.out.println("\n\n");
        main.out.append("The " + (++time) + "th search: total is " + Math.min(10, comparable_bucket.size()) + " records\n");
        main.out.append("-----------------------------------------------------------------------------------------------------------------------------\n");
        for (i = 0; i < Math.min(10, comparable_bucket.size()); i++) {
            Bucket t = comparable_bucket.get(i);
            main.out.append(String.format("Top " + (i + 1) + ":%-40s\t%s\t\t%s\t\t%s%n", t.name + "(" +  t.code + ")" , t.price, t.loss, t.volume));
            //System.out.printf("Top " + (i + 1) + ":%-40s\t%s\t%s\t%s\t%n", t.name + "(" +  t.code + ")" , t.price, t.loss, t.volume);
        }
        main.out.append("-----------------------------------------------------------------------------------------------------------------------------\n");
    }
}