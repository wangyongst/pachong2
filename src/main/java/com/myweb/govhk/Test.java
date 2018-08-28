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
        page.getElementByName("companyName").setAttribute("value", "CA SERVICES LIMITED");
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
        System.out.println(page.asXml());
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
