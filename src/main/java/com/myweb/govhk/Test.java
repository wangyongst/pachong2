package com.myweb.govhk;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;

import javax.imageio.ImageReader;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Test {

    public static void main(String[] args) throws Exception {
        Test test = new Test();
        HtmlPage page = test.initPage();
        page = test.search(page, "The Hongkong and Shanghai Banking Corporation Limited");
        page.getElementsById("cont_table").forEach(t -> {
            t.getElementsByTagName("td").forEach(e -> {
                if (e.getElementsByTagName("a").size() == 1) {
                    System.out.println(e.getTextContent());
                    try {
                        test.searchMore(e.getElementsByTagName("a").get(0).click());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            });
        });
    }

    public void searchMore(HtmlPage page) {
        final String[] info = {new String()};
        page.getElementsById("cont_table").forEach(t -> {
            t.getElementsByTagName("tr").forEach(e -> {
                if (e.getAttribute("class").equals("colourlightgray")) {
                    e.getElementsByTagName("td").forEach(m -> {
                        info[0] = info[0] + e.getTextContent().trim() + "|";
                    });
                }
            });
        });
        System.out.println(info[0]);
    }


    public HtmlPage search(HtmlPage page, String companyName) throws Exception {
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
        return page.getAnchorByText("遞交").click();
    }


    public static String getCode(BufferedImage bufferedImage) throws Exception {
        ITesseract instance = new Tesseract();
        BufferedImage textImage = ClearImageHelper.cleanImage(bufferedImage);
        return instance.doOCR(textImage).trim();
    }


    public HtmlPage initPage() throws IOException, InterruptedException {
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getOptions().setCssEnabled(true);
        webClient.getOptions().setAppletEnabled(true);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setRedirectEnabled(true);
        HtmlPage page = webClient.getPage("https://www.gov.hk/sc/residents/taxes/etax/services/brn_enquiry.htm");
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
        return page.getAnchorByText("繼續").click();
    }
}
