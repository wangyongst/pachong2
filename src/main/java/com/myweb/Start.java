package com.myweb;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.myweb.govhk.Test;

public class Start {
    public static void main(String[] args) throws Exception {
        Test test = new Test();
        HtmlPage page = test.initPage();
        page = test.search(page, "The Hongkong and Shanghai Banking Corporation Limited");
        page.getElementsById("cont_table").forEach(t -> {
            t.getElementsByTagName("td").forEach(e -> {
                if (e.getElementsByTagName("a").size() == 1) {
                    System.out.println(e.getTextContent());
                }
            });
        });
    }
}
