package com.myweb.icris;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import java.sql.SQLException;

public class JDBCUtil {

    static void insert(Company company) throws SQLException {
        String sql = "insert into company(no,name,enname,type,incorporation,status,remarks,mode,dissorexit,charges,note) values(?,?,?,?,?,?,?,?,?,?,?)";
        new QueryRunner(JDBCDataSource.getDataSource()).update(sql, company.getNo(), company.getName(), company.getEnname(), company.getType(), company.getIncorporation(), company.getStatus(), company.getRemarks(), company.getMode(), company.getDissorexit(), company.getCharges(), company.getNote());
    }

    static void update(Company company) throws SQLException {
        String sql = "update company set name = ?,enname = ?,type = ?,incorporation = ?,status = ?,remarks = ?,mode = ?,dissorexit = ?,charges = ?,note = ? where no = ?";
        new QueryRunner(JDBCDataSource.getDataSource()).update(sql, company.getName(), company.getEnname(), company.getType(), company.getIncorporation(), company.getStatus(), company.getRemarks(), company.getMode(), company.getDissorexit(), company.getCharges(), company.getNote(), company.getNo());
    }

    static Company select(Company company) throws SQLException {
        String sql = "select * from company where no = ?";
        return (Company) new QueryRunner(JDBCDataSource.getDataSource()).query(sql, company.getNo(), new BeanHandler(Company.class));
    }

    static Company count() throws SQLException {
        String sql = "select * from company where no = (select max(no)  from company)";
        return (Company) new QueryRunner(JDBCDataSource.getDataSource()).query(sql, new BeanHandler(Company.class));
    }

    static void insert(History history) throws SQLException {
        String sql = "insert into history(no,name,enname,date) values(?,?,?,?)";
        new QueryRunner(JDBCDataSource.getDataSource()).update(sql, history.getNo(), history.getName(), history.getEnname(), history.getDate());
    }

    static void update(History history) throws SQLException {
        String sql = "update history set name = ?,enname = ? where no = ? and date=?";
        new QueryRunner(JDBCDataSource.getDataSource()).update(sql, history.getName(), history.getEnname(), history.getNo(), history.getDate());
    }

    static History select(History history) throws SQLException {
        String sql = "select * from history where no = '" + history.getNo() + "' and date = '" + history.getDate() + "'";
        return (History) new QueryRunner(JDBCDataSource.getDataSource()).query(sql, new BeanHandler(History.class));
    }

    static void insert(DocFile docFile) throws SQLException {
        String sql = "insert into docfile(id,no,name,year,submission,status) values(?,?,?,?,?,?)";
        new QueryRunner(JDBCDataSource.getDataSource()).update(sql, docFile.getId(), docFile.getNo(), docFile.getName(), docFile.getYear(), docFile.getSubmission(), docFile.getStatus());
    }

    static void update(DocFile docFile) throws SQLException {
        String sql = "update docfile set name = ?,year = ?,submission = ?,status = ? where no= ? and id = ?";
        new QueryRunner(JDBCDataSource.getDataSource()).update(sql, docFile.getName(), docFile.getYear(), docFile.getSubmission(), docFile.getStatus(), docFile.getNo(), docFile.getId());
    }

    static DocFile select(DocFile docFile) throws SQLException {
        String sql = "select * from docfile where no = '" + docFile.getNo() + "' and id = '" + docFile.getId() + "'";
        return (DocFile) new QueryRunner(JDBCDataSource.getDataSource()).query(sql, new BeanHandler(DocFile.class));
    }
}
