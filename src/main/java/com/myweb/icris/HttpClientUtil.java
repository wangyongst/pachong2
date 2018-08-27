package com.myweb.icris;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

/*
 * 利用HttpClient进行post请求的工具类
 */
public class HttpClientUtil {

    public HttpClient httpClient;

    public HttpClientUtil() throws Exception {
        if (httpClient == null) {
            httpClient = SSLClient.SslHttpClientBuild();
        }
    }

    public HttpResponse changelocate(String url) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        return httpClient.execute(httpGet);
    }

    public HttpResponse agree() throws IOException {
        String url = "https://www.icris.cr.gov.hk/csci/login_i.do?loginType=iguest&CHKBOX_01=false&OPT_01=1&OPT_02=0&OPT_03=0&OPT_04=0&OPT_05=0&OPT_06=0&OPT_07=0&OPT_08=0&OPT_09=0&username=iguest";
        HttpGet httpGet = new HttpGet(url);
        return httpClient.execute(httpGet);
    }

    public HttpResponse searchCompany(int no) throws IOException {
        HttpPost httpPost = new HttpPost("https://www.icris.cr.gov.hk/csci/DIS_SearchCompany.do");
        //设置参数
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        Iterator iterator = searchCompanyMap(no).entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, String> elem = (Entry<String, String>) iterator.next();
            list.add(new BasicNameValuePair(elem.getKey(), elem.getValue()));
        }
        if (list.size() > 0) {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list);
            httpPost.setEntity(entity);
        }
        return httpClient.execute(httpPost);
    }

    public HttpResponse searchDocmentPage(String no) throws IOException {
        HttpPost httpPost = new HttpPost("https://www.icris.cr.gov.hk/csci/docs_list.do");
        //设置参数
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        Iterator iterator = searchDocumentPageMap(no).entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, String> elem = (Entry<String, String>) iterator.next();
            list.add(new BasicNameValuePair(elem.getKey(), elem.getValue()));
        }
        if (list.size() > 0) {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list);
            httpPost.setEntity(entity);
        }
        return httpClient.execute(httpPost);
    }

    public HttpResponse searchDocment(String no,int page,int select) throws IOException {
        HttpPost httpPost = new HttpPost("https://www.icris.cr.gov.hk/csci/docs_list.do");
        //设置参数
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        Iterator iterator = searchDocumentMap(no,page,select).entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, String> elem = (Entry<String, String>) iterator.next();
            list.add(new BasicNameValuePair(elem.getKey(), elem.getValue()));
        }
        if (list.size() > 0) {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list);
            httpPost.setEntity(entity);
        }
        return httpClient.execute(httpPost);
    }

    public Map searchCompanyMap(int no) {
        Map<String, String> createMap = new HashMap<String, String>();
        ObjectMapper mapper = new ObjectMapper();
        createMap.put("companyName", "");
        createMap.put("CRNo", String.format("%0" + 7 + "d", no));
        createMap.put("language", "en");
        createMap.put("mode", "EXACT+NAME");
        createMap.put("nextAction", "DIS_SearchCompany");
        createMap.put("page", "1");
        createMap.put("radioButton", "BYCRNO");
        createMap.put("searchMode", "BYCRNO");
        createMap.put("searchPage", "True");
        createMap.put("showMedium", "true");
        return createMap;
    }

    public Map searchDocumentPageMap(String no) {
        Map<String, String> createMap = new HashMap<String, String>();
        ObjectMapper mapper = new ObjectMapper();
        createMap.put("crno", no);
        createMap.put("doc_group", "all");
        createMap.put("filing_year", "all");
        createMap.put("newSearch", "true");
        createMap.put("searchPage", "True");
        return createMap;
    }

    public Map searchDocumentMap(String no,int page,int select) {
        Map<String, String> createMap = new HashMap<String, String>();
        ObjectMapper mapper = new ObjectMapper();
        createMap.put("rno", no);
        createMap.put("barcode", "");
        createMap.put("bulkyInd", "");
        createMap.put("certcontent", "");
        createMap.put("companyType", "I");
        createMap.put("crno", no);
        createMap.put("desc", "");
        createMap.put("direview", "false");
        createMap.put("doc_group", "all");
        createMap.put("docGroupInd", "all");
        createMap.put("docName", "");
        createMap.put("docSize", "");
        createMap.put("filing_year", "all");
        createMap.put("filingYearInd", "all");
        createMap.put("isScreenSearch", "");
        createMap.put("itemDesc", "Document+index+search,cr+no+is:"+no);
        createMap.put("lsaInd", "");
        createMap.put("moduleName", "DIS_SearchCompany");
        createMap.put("newSearch", "");
        createMap.put("PageNO", page+"");
        createMap.put("productCode", "SCRPRT");
        createMap.put("screenPrintInd", "Document+index+search,cr+no+is:"+no);
        createMap.put("searchPage", "True");
        createMap.put("SelectPage", select+"");
        return createMap;
    }
}
