package com.myweb.govhk;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import java.sql.SQLException;

public class JDBCUtil {

    static void insert(CompanyInfo companyInfo) throws SQLException {
        String sql = "insert into companyinfo(no,enname,address,infono,corpname,corpename,branchname,branchename) values(?,?,?,?,?,?,?,?)";
        new QueryRunner(JDBCDataSource.getDataSource()).update(sql, companyInfo.getNo(), companyInfo.getEnname(), companyInfo.getAddress(), companyInfo.getInfono(), companyInfo.getCorpname(), companyInfo.getCorpename(), companyInfo.getBranchname(), companyInfo.getBranchename());
    }

    static CompanyInfo isExsit(CompanyInfo companyInfo) throws SQLException {
        String sql = "select * from companyinfo where enname = ? and address = ?";
        Object[] param = {companyInfo.getEnname(), companyInfo.getAddress()};
        return (CompanyInfo) new QueryRunner(JDBCDataSource.getDataSource()).query(sql, param, new BeanHandler(CompanyInfo.class));
    }

    static Company select() throws SQLException {
        String sql = "select * from company where no = (select max(c.no) from company c left join companyinfo i on c.no = i.no where i.no is null and c.status = '仍注册')";
        return (Company) new QueryRunner(JDBCDataSource.getDataSource()).query(sql, new BeanHandler(Company.class));
    }
}
