package com.myweb.icris;

import com.myweb.Start;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IcrisApi {
    private HttpClientUtil httpClientUtil = null;

    public IcrisApi(){
        try {
            httpClientUtil = new HttpClientUtil();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void agree() throws Exception {
        EntityUtils.toString(httpClientUtil.agree().getEntity(), "UTF-8");
    }

    public void english() throws Exception {
        EntityUtils.toString(httpClientUtil.changelocate("https://www.icris.cr.gov.hk/csci/change_locale_i.do?language=en&country=US").getEntity(), "UTF-8");
    }

    public void chinese() throws Exception {
        EntityUtils.toString(httpClientUtil.changelocate("https://www.icris.cr.gov.hk/csci/change_locale_i.do?language=zh&country=CN").getEntity(), "UTF-8");
    }

    public String searchByNo(int no) throws Exception {
        return EntityUtils.toString(httpClientUtil.searchCompany(no).getEntity(), "UTF-8");
    }

    public String searchDocumentPage(String no) throws Exception {
        return EntityUtils.toString(httpClientUtil.searchDocmentPage(no).getEntity(), "UTF-8");
    }

    public String searchDocument(String no, int page, int select) throws Exception {
        return EntityUtils.toString(httpClientUtil.searchDocment(no, page, select).getEntity(), "UTF-8");
    }

    public Company parseCompany(String result) {
        Company company = new Company();
        Document document = Jsoup.parse(result);
        Elements form = document.select("form[name=docsFilterSearch]").select("table");
        if (form.isEmpty()) {
            return null;
        }
        Elements companyElements = form.first().select("tr");
        companyElements.forEach(e -> System.out.println(e.text()));
        List<Element> elements = new ArrayList<>();
        companyElements.forEach(e -> elements.add(e));
        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            if (i == 0) company.setNo(element.select("td").last().text());
            if (i == 1) {
                if (element.select("td").last().select("span").first().html().contains("<br>")) {
                    company.setEnname(element.select("td").last().select("span").first().html().split("<br>")[0]);
                    company.setName(element.select("td").last().select("span").first().html().split("<br>")[1]);
                } else {
                    company.setEnname(element.select("td").last().text());
                }
            }
            if (i == 2) company.setType(element.select("td").last().text());
            if (i == 3) company.setIncorporation(element.select("td").last().text());
            if (i == 4) company.setStatus(element.select("td").last().text());
            if (i == 5) company.setRemarks(element.select("td").last().text());
            if (i == 6) company.setMode(element.select("td").last().text());
            if (i == 7) company.setDissorexit(element.select("td").last().text());
            if (i == 8) company.setCharges(element.select("td").last().text());
            if (i == 9) company.setNote(element.select("td").last().text());
        }
        return company;
    }

    public List<History> parseHistory(String result, String no) {
        List<History> histories = new ArrayList<History>();
        Document document = Jsoup.parse(result);
        Elements form = document.select("form[name=docsFilterSearch]").select("table");
        if (form.isEmpty()) {
            return null;
        }
        Elements historyElements = form.last().select("tr");
        historyElements.forEach(e -> System.out.println(e.text()));
        List<Element> elements = new ArrayList<>();
        historyElements.forEach(e -> elements.add(e));
        for (int i = 1; i < elements.size(); i++) {
            Element element = elements.get(i);
            History history = new History();
            if (element.select("td").size() > 1) {
                history.setNo(no);
                history.setEnname(element.select("td").last().text());
                history.setDate(element.select("td").first().text());
                if (i < elements.size() - 1 && elements.get(i + 1).select("td").size() == 1) history.setName(elements.get(i + 1).select("td").text());
                histories.add(history);
            }
        }
        return histories;
    }

    public int parseDocumentPage(String result) {
        Document document = Jsoup.parse(result);
        Elements form = document.select("form[name=cmm_order_type]").select("table");
        if (form.isEmpty()) {
            return 1;
        }
        if (form.last().select("option").size() > 0) return Integer.parseInt(form.last().select("option").last().text());
        else return 1;
    }

    public List<DocFile> parseDocument(String result, String no) {
        List<DocFile> docFiles = new ArrayList<>();
        Document document = Jsoup.parse(result);
        Elements docElements = document.select("form[name=cmm_order_type]").select("table[dwcopytype=CopyTableRow]").select("tr[bgcolor=#CCCCCC]");
        if (docElements.isEmpty()) {
            return null;
        }
        docElements.forEach(e -> {
            System.out.println(e.text());
            if (e.select("td").size() > 9) {
                DocFile docFile = new DocFile();
                docFile.setNo(no);
                docFile.setId(e.select("td").get(3).text());
                if(e.select("td").get(4).html().contains("<br>")) docFile.setName(e.select("td").get(4).html().split("<br>")[0]);
                else docFile.setName(e.select("td").get(4).text());
                docFile.setYear(e.select("td").get(5).text());
                docFile.setSubmission(e.select("td").get(6).text());
                docFile.setStatus(e.select("td").get(10).text());
                docFiles.add(docFile);
            }
        });
        return docFiles;
    }

    public int getMax() throws SQLException {
        Company company = JDBCUtil.count();
        if (company == null) return 1;
        else return Integer.parseInt(company.getNo());
    }

    public boolean pachong(int no) throws Exception {
        String comReslut = searchByNo(no);
        Company company = parseCompany(comReslut);
        if (company == null) {
            System.out.println("__________________________________________________________________________________");
            System.out.println("链接失败，请先停止程序，稍候再试！！！");
            System.out.println("__________________________________________________________________________________");
            return false;
        }
        Start.executorService.execute(new Thread(() -> {
            try {
                if (JDBCUtil.select(company) == null) JDBCUtil.insert(company);
                else JDBCUtil.update(company);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }));
        List<History> histories = parseHistory(comReslut, company.getNo());
        histories.forEach(e -> {
            if (e != null) {
                Start.executorService.execute(new Thread(() -> {
                    try {
                        History history = JDBCUtil.select(e);
                        if (history == null) JDBCUtil.insert(e);
                        else JDBCUtil.update(e);
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }));
            }
        });
        int page = parseDocumentPage(searchDocumentPage(company.getNo()));
        if(page > 5) page = 5;
        List<DocFile> docFiles = new ArrayList<DocFile>();
        for (int i = 1; i <= page; i++) {
            Thread.sleep(Start.SLEEP);
            List<DocFile> docFileList = parseDocument(searchDocument(company.getNo(), i, page), company.getNo());
            if (docFileList != null && docFileList.size() > 0) docFiles.addAll(docFileList);
        }
        docFiles.forEach(e -> {
            if (e != null) {
                Start.executorService.execute(new Thread(() -> {
                    try {
                        DocFile docFile = JDBCUtil.select(e);
                        if (docFile == null) JDBCUtil.insert(e);
                        else {
                            e.setId(docFile.getId());
                            JDBCUtil.update(e);
                        }
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }));
            }
        });
        return true;
    }

}
