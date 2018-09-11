package com.myweb;

import com.myweb.govhk.CompanyInfo;
import com.myweb.govhk.GOVPachong;

import java.io.File;

public class Start {
    public static void main(String[] args) throws Exception {
        GOVPachong govPachong = new GOVPachong();
        while (true) {
            while (govPachong.page == null) {
                while (!govPachong.initPage()) ;
            }
            CompanyInfo companyInfo = govPachong.getCompany();
            while (!govPachong.start(companyInfo)) ;
        }
    }
}
