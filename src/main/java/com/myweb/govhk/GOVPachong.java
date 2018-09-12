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
import java.sql.SQLException;
import java.util.List;


public class GOVPachong {

    public WebClient webClient = new WebClient(BrowserVersion.CHROME);
    public HtmlPage page = null;

    public static void main(String[] args) {
        GOVPachong test = new GOVPachong();
        while (test.page == null) {
            while (!test.initPage()) ;
        }
        CompanyInfo companyInfo = new CompanyInfo();
        companyInfo.setEnname("CA SERVICES LIMITED");
        while (!test.start(companyInfo)) ;
        companyInfo = new CompanyInfo();
        companyInfo.setEnname("The Hongkong and Shanghai Banking Corporation Limited");
        while (!test.start(companyInfo)) ;
    }

    public boolean start(CompanyInfo companyInfo) {
        while (!search(companyInfo)) ;
        while (!parserTwo(companyInfo)) ;
        while (!parser(companyInfo)) ;
        try {
            while (page.getAnchorByText("下一頁") != null) {
                page = page.getAnchorByText("下一頁").click();
                System.out.println(page.getBaseURL().toString());
                while (!parser(companyInfo)) ;
            }
        } catch (ElementNotFoundException e) {
            try {
                page = page.getAnchorByText("重新查詢").click();
            } catch (Exception e1) {
                e1.printStackTrace();
                return false;
            }
            System.out.println(page.getBaseURL().toString());
        } catch (IOException e) {
            e.printStackTrace();
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
                System.out.println(companyInfo.getEnname() + "--------------    " + companyInfo.getAddress());
                try {
                    if(JDBCUtil.isExsit(companyInfo) == null){
                        JDBCUtil.insert(companyInfo);
                        clear(companyInfo);
                    }
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    return false;
                }
            }
        }
        return true;
    }

    public CompanyInfo getCompany(){
        CompanyInfo companyInfo = new CompanyInfo();
        try {
            Company company = JDBCUtil.select();
            companyInfo.setNo(company.getNo());
            companyInfo.setEnname(company.getEnname());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return companyInfo;
    }


    public boolean parserTwo(CompanyInfo companyInfo) {
        if (page.getElementsById("cont_table").size() < 3) return false;
        DomElement table = page.getElementsById("cont_table").get(2);
        List<HtmlElement> tds = table.getElementsByTagName("td");
        for (int i = 0; i < tds.size(); i++) {
            HtmlElement e = tds.get(i);
            if (e.getElementsByTagName("a").size() == 1 && e.getFirstChild().getNextSibling().getNodeName().equals("a")) {
                try {
                    HtmlAnchor anchor = (HtmlAnchor) e.getElementsByTagName("a").get(0);
                    companyInfo.setAddress(anchor.getTextContent().trim().replaceAll(" {2,}", " "));
                    page = (HtmlPage) page.executeJavaScript(anchor.getOnClickAttribute()).getNewPage();
                    if (page.getElementsById("cont_table").size() < 3) {
                        System.out.println("数据不对，出错了");
                        return false;
                    }
                    table = page.getElementsById("cont_table").get(2);
                    List<HtmlElement> trs = table.getElementsByTagName("tr");
                    for (HtmlElement n : trs) {
                        if (n.getChildElementCount() == 2) {
                            if (((HtmlTableRow) n).getCell(0).getFirstChild().getTextContent().contains("商業登記號碼")) {
                                companyInfo.setInfono(((HtmlTableRow) n).getCell(1).getLastChild().getTextContent().trim().replaceAll(" {2,}", " "));
                            } else if (((HtmlTableRow) n).getCell(0).getFirstChild().getTextContent().contains("業務/法團名稱(中文)")) {
                                companyInfo.setCorpname(((HtmlTableRow) n).getCell(1).getLastChild().getTextContent().trim().replaceAll(" {2,}", " "));
                            } else if (((HtmlTableRow) n).getCell(0).getFirstChild().getTextContent().contains("業務/法團名稱(英文)")) {
                                companyInfo.setCorpename(((HtmlTableRow) n).getCell(1).getLastChild().getTextContent().trim().replaceAll(" {2,}", " "));
                            } else if (((HtmlTableRow) n).getCell(0).getFirstChild().getTextContent().contains("分行名稱(中文)")) {
                                companyInfo.setBranchname(((HtmlTableRow) n).getCell(1).getLastChild().getTextContent().trim().replaceAll(" {2,}", " "));
                            } else if (((HtmlTableRow) n).getCell(0).getFirstChild().getTextContent().contains("分行名稱(英文)")) {
                                companyInfo.setBranchename(((HtmlTableRow) n).getCell(1).getLastChild().getTextContent().trim().replaceAll(" {2,}", " "));
                            }
                        }
                    }
                    System.out.println(companyInfo.getEnname() + "----------   " + companyInfo.getBranchename() + "----------   " + companyInfo.getBranchname() + "-----------    " + companyInfo.getInfono() + "------------    " + companyInfo.getCorpname() + "--------------    " + companyInfo.getCorpename() + "--------------    " + companyInfo.getAddress());
                    JDBCUtil.insert(companyInfo);
                    clear(companyInfo);
                    page = page.getAnchorByText("返回上頁").click();
                    return true;
                } catch (Exception e1) {
                    e1.printStackTrace();
                    return false;
                }
            }
        }
        return true;
    }


    public void clear(CompanyInfo companyInfo){
        companyInfo.setInfono(null);
        companyInfo.setAddress(null);
        companyInfo.setBranchename(null);
        companyInfo.setBranchname(null);
        companyInfo.setCorpename(null);
        companyInfo.setCorpname(null);
    }


    public boolean search(CompanyInfo companyInfo) {
        try {
            page.getElementByName("companyName").setAttribute("value", companyInfo.getEnname());
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
        instance.setDatapath("/root/pachong2-1.0-SNAPSHOT");
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
            page = webClient.getPage("https://www.gov.hk/sc/residents/taxes/etax/services/brn_enquiry.htm");
            System.out.println(page.getBaseURL().toString());
            page = page.getAnchorByHref("/sc/apps/irdbrnenquiry.htm").click();
            Thread.sleep(10000);
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
