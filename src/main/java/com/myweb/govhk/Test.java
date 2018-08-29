package com.myweb.govhk;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;

import javax.imageio.ImageReader;
import java.awt.image.BufferedImage;
import java.io.IOException;


public class Test {

    public HtmlPage page = null;

    public static void main(String[] args) throws Exception {
        Test test = new Test();
        while (test.page == null) {
            test.initPage();
        }
        String comName = "The Hongkong and Shanghai Banking Corporation Limited";
        for (int i = 0; i < 10; i++) {
            test.start(comName);
            System.out.println(i + "++++++++++++++++++++++++++++++++++++++++++");
        }
    }

    public boolean start(String companyName) throws Exception {
        search(companyName);
        parser();
        try {
            while (page.getAnchorByText("下一頁") != null) {
                page = page.getAnchorByText("下一頁").click();
                parser();
            }
        } catch (ElementNotFoundException e) {
            page = page.getAnchorByText("重新查詢").click();
        }
        return true;
    }

    public boolean parserMore(HtmlElement element) throws IOException {
        page = element.getElementsByTagName("a").get(0).click();
        page.getElementsById("cont_table").forEach(t -> {
            t.getElementsByTagName("tr").forEach(e -> {
                if(e.getAttribute("class").equals("colourlightgray")) {
                    System.out.println("____________________");
                    System.out.println(e.getTextContent().trim().replaceAll(" {2,}", " "));
                    System.out.println("____________________");
                }
            });
        });
        page = page.getAnchorByText("返回上頁").click();
        return true;
    }

    public boolean parser() throws Exception {
        page.getElementsById("cont_table").forEach(t -> {
            t.getElementsByTagName("td").forEach(e -> {
                if (e.getElementsByTagName("a").size() == 1 && e.getFirstChild().getNextSibling().getNodeName().equals("a")) {
                    System.out.println("____________________");
                    System.out.println(e.getTextContent().trim().replaceAll(" {2,}", " "));
                    System.out.println("____________________");
                    try {
                        parserMore(e);
                    } catch (IOException e1) {
                    }
                }
            });
        });
        return true;
    }


    public boolean search(String companyName) throws Exception {
        page.getElementByName("companyName").setAttribute("value", companyName);
        ((HtmlSelect) page.getElementByName("businessAddArea")).getOptionByValue("").click();
        ((HtmlSelect) page.getElementByName("businessAddArea")).getOptionByValue("A").click();
        final HtmlImage[] valiCodeImg = {null};
        page.getElementsByTagName("img").forEach(e -> {
            if (e.getAttribute("src").contains("captchaServlet")) {
                valiCodeImg[0] = (HtmlImage) e;
            }
        });
        ImageReader imageReader = valiCodeImg[0].getImageReader();
        BufferedImage bufferedImage = imageReader.read(0);
        page.getElementByName("captchaStr").setAttribute("value", getCode(bufferedImage));
        page = page.getAnchorByText("遞交").click();
        return true;
    }


    public static String getCode(BufferedImage bufferedImage) throws Exception {
        ITesseract instance = new Tesseract();
        BufferedImage textImage = ClearImageHelper.cleanImage(bufferedImage);
        return instance.doOCR(textImage).trim();
    }


    public boolean initPage() throws IOException, InterruptedException {
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getOptions().setCssEnabled(true);
        webClient.getOptions().setAppletEnabled(true);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setRedirectEnabled(true);
        page = webClient.getPage("https://www.gov.hk/sc/residents/taxes/etax/services/brn_enquiry.htm");
        page = page.getAnchorByHref("/sc/apps/irdbrnenquiry.htm").click();
        Thread.sleep(10000);
        page = page.getElementsByTagName("input").get(0).click();
        page = page.getAnchorByText("開始使用服務").click();
        page = page.getAnchorByText("我已閱讀上述收集個人資料聲明及說明").click();
        page.getElementsByTagName("input").forEach(e -> {
            if (e.getAttribute("value").equals("BRE")) {
                try {
                    e.click();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        page = page.getAnchorByText("繼續").click();
        return true;
    }
}
