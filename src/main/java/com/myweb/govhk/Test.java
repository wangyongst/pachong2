package com.myweb.govhk;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;

public class Test {

    public static void main(String[] args) throws IOException, InterruptedException {
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setAppletEnabled(true);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setRedirectEnabled(true);
        HtmlPage page = webClient.getPage("https://www.gov.hk/sc/residents/taxes/etax/services/brn_enquiry.htm");
        System.out.println(page.asXml());
        page = page.getAnchorByText("立即查询").click();
        Thread.sleep(10000);
        page = page.getElementsByTagName("input").get(0).click();
        page = page.getAnchorByText("開始使用服務").click();
        page = page.getAnchorByText("我已閱讀上述收集個人資料聲明及說明").click();
        page.getElementsByTagName("input").forEach(e->{
            if(e.getAttribute("value").equals("BRE")) {
                try {
                    e.click();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        page = page.getAnchorByText("繼續").click();
        System.out.println(page.asXml());

    }
}
