package com.myweb.govhk;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;

import javax.imageio.ImageReader;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;


public class Test {

    public WebClient webClient = new WebClient(BrowserVersion.CHROME);
    public HtmlPage page = null;

    public static void main(String[] args) {
        Test test = new Test();
        while (test.page == null) {
            while (!test.initPage()) ;
        }
        CompanyInfo companyInfo = new CompanyInfo();
        companyInfo.setName("CA SERVICES LIMITED");
        while (!test.start(companyInfo)) ;
        companyInfo.setName("The Hongkong and Shanghai Banking Corporation Limited");
        while (!test.start(companyInfo)) ;

    }

    public boolean start(CompanyInfo companyInfo) {
        while (!search(companyInfo)) ;
        while (!parserTwo(companyInfo)) ;
        try {
            while (page.getAnchorByText("下一頁") != null) {
                page = page.getAnchorByText("下一頁").click();
                System.out.println(page.getBaseURL().toString());
                while (!parserTwo(companyInfo)) ;
            }
        } catch (ElementNotFoundException e) {
            try {
                page = page.getAnchorByText("重新查詢").click();
            } catch (Exception e1) {
                return false;
            }
            System.out.println(page.getBaseURL().toString());
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public boolean parser(CompanyInfo companyInfo) {
        DomElement table = page.getElementsById("cont_table").get(2);
        List<HtmlElement> tds = table.getElementsByTagName("td");
        // for (HtmlElement e : tds) {
        for (int i = 0; i < tds.size(); i++) {
            HtmlElement e = tds.get(i);
            if (e.getElementsByTagName("a").size() == 1 && e.getFirstChild().getNextSibling().getNodeName().equals("a")) {
                System.out.println(e.getTextContent().trim().replaceAll(" {2,}", " "));
                companyInfo.setAddress(e.getTextContent().trim().replaceAll(" {2,}", " "));
                //parserDetail(companyInfo, e, table);
                System.out.println(companyInfo.getName() + "----------   " + companyInfo.getBranchename() + "----------   " + companyInfo.getBranchname() + "-----------    " + companyInfo.getInfono() + "------------    " + companyInfo.getCorpname() + "--------------    " + companyInfo.getCortename() + "--------------    " + companyInfo.getAddress());
            }
        }
        return true;
    }


    public boolean parserTwo(CompanyInfo companyInfo) {
        DomElement table = page.getElementsById("cont_table").get(2);
        List<HtmlElement> tds = table.getElementsByTagName("td");
        for (int i = 0; i < tds.size(); i++) {
            HtmlElement e = tds.get(i);
            if (e.getElementsByTagName("a").size() == 1 && e.getFirstChild().getNextSibling().getNodeName().equals("a")) {
                try {
                    HtmlAnchor anchor = (HtmlAnchor) e.getElementsByTagName("a").get(0);
                    companyInfo.setAddress(anchor.getTextContent());
                    System.out.println(anchor.getOnClickAttribute());
                    page = (HtmlPage) page.executeJavaScript(anchor.getOnClickAttribute()).getNewPage();
                    System.out.println(page.getBaseURL().toString());
                    table = page.getElementsById("cont_table").get(2);
                    List<HtmlElement> trs = table.getElementsByTagName("tr");
                    for (HtmlElement n : trs) {
                        if (n.getChildElementCount() == 2) {
                            companyInfo.setBranchename(null);
                            companyInfo.setBranchname(null);
                            companyInfo.setCortename(null);
                            companyInfo.setCorpname(null);
                            companyInfo.setInfono(((HtmlTableRow) n).getCell(1).getLastChild().getTextContent().trim().replaceAll(" {2,}", " "));
                            if (((HtmlTableRow) n).getCell(0).getFirstChild().getTextContent().contains("商業登記號碼")) {
                                companyInfo.setInfono(((HtmlTableRow) n).getCell(1).getLastChild().getTextContent().trim().replaceAll(" {2,}", " "));
                            } else if (((HtmlTableRow) n).getCell(0).getFirstChild().getTextContent().contains("業務/法團名稱(中文)")) {
                                companyInfo.setCorpname(((HtmlTableRow) n).getCell(1).getLastChild().getTextContent().trim().replaceAll(" {2,}", " "));
                            } else if (((HtmlTableRow) n).getCell(0).getFirstChild().getTextContent().contains("業務/法團名稱(英文)")) {
                                companyInfo.setCortename(((HtmlTableRow) n).getCell(1).getLastChild().getTextContent().trim().replaceAll(" {2,}", " "));
                            } else if (((HtmlTableRow) n).getCell(0).getFirstChild().getTextContent().contains("分行名稱(中文)")) {
                                companyInfo.setBranchname(((HtmlTableRow) n).getCell(1).getLastChild().getTextContent().trim().replaceAll(" {2,}", " "));
                            } else if (((HtmlTableRow) n).getCell(0).getFirstChild().getTextContent().contains("分行名稱(英文)")) {
                                companyInfo.setBranchename(((HtmlTableRow) n).getCell(1).getLastChild().getTextContent().trim().replaceAll(" {2,}", " "));
                            }
                        }
                    }
                    System.out.println(companyInfo.getName() + "----------   " + companyInfo.getBranchename() + "----------   " + companyInfo.getBranchname() + "-----------    " + companyInfo.getInfono() + "------------    " + companyInfo.getCorpname() + "--------------    " + companyInfo.getCortename() + "--------------    " + companyInfo.getAddress());
                    page = page.getAnchorByText("返回上頁").click();
                    System.out.println(page.getBaseURL().toString());
                } catch (Exception e1) {
                    return false;
                }
            }
        }
        return true;
    }


    public boolean search(CompanyInfo companyInfo) {
        try {
            page.getElementByName("companyName").setAttribute("value", companyInfo.getName());
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
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public static String getCode(BufferedImage bufferedImage) throws Exception {
        ITesseract instance = new Tesseract();
        BufferedImage textImage = ClearImageHelper.cleanImage(bufferedImage);
        return instance.doOCR(textImage).trim();
    }


    public boolean initPage() {
        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getOptions().setCssEnabled(true);
        webClient.getOptions().setAppletEnabled(true);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setRedirectEnabled(true);
        webClient.waitForBackgroundJavaScript(3000);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        try {
            page = webClient.getPage("https://www.gov.hk/sc/apps/irdbrnenquiry.htm");
            System.out.println(page.getBaseURL().toString());
            page = page.getElementsByTagName("input").get(0).click();
            System.out.println(page.getBaseURL().toString());
            page = page.getAnchorByText("開始使用服務").click();
            System.out.println(page.getBaseURL().toString());
            page = page.getAnchorByText("我已閱讀上述收集個人資料聲明及說明").click();
            System.out.println(page.getBaseURL().toString());
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
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
