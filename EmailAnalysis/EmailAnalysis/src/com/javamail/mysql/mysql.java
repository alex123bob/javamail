package com.javamail.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mysql.jdbc.ResultSetMetaData;

public class mysql {
	public mysql() {
		this.connectMysql();
	}

	public Connection con;
	
	public Statement stmt;

	private void connectMysql() {
		String urlstr = "jdbc:mysql://localhost:3306/javamail?seUnicode=true&characterEncoding=UTF-8";
		Connection con;
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (java.lang.ClassNotFoundException e) {
			System.err.print("classnotfoundexception :");
			System.err.print(e.getMessage());
		}
		try {
			con = DriverManager.getConnection(urlstr, "root", "root");
			this.con = con;
		} catch (SQLException ex) {
			System.err.println("sqlexception :" + ex.getMessage());
		}
	}

	public int insertData(String sql) {
		int num = -1;
		try {
			this.stmt = this.con.createStatement();
			num = this.stmt.executeUpdate(sql);
		} catch (SQLException ex) {
			System.err.println("sqlexception :" + ex.getMessage());
		}
		return num;
	}
	
	public void executeSql (String sql) {
		try {
			this.stmt = this.con.createStatement();
			boolean hasResult = stmt.execute(sql);
			ResultSet rs;
			if (hasResult) {
				rs = stmt.getResultSet();
				java.sql.ResultSetMetaData rsmd = rs.getMetaData();
				int columnCount = rsmd.getColumnCount();
				
				while (rs.next()) {
					for (int i = 0; i < columnCount; i++) {
						System.out.println(rs.getString(i+1) + "\t");
					}
					System.out.println();
				}
			}
			else {
				System.out.println("该SQL语句影响的记录有" + stmt.getUpdateCount() + "条");
			}
		}
		catch (SQLException ex) {
			System.err.println("sqlexception :" + ex.getMessage());
		}
	}
	
	public int deleteData(String sql) {
		int num = -1;
		try {
			this.stmt = this.con.createStatement();
			num = this.stmt.executeUpdate(sql);
		} catch (SQLException ex) {
			System.err.println("sqlexception :" + ex.getMessage());
		}
		return num;
	}

	public ResultSet getData(String sql) {
		ResultSet rs = null;
		try {
			this.stmt = this.con.createStatement();
			rs = this.stmt.executeQuery(sql);
		} catch (SQLException ex) {
			System.err.println("sqlexception :" + ex.getMessage());
		}
		return rs;
	}

	public void deConnectMysql() {
		try {
			this.con.close();
		} catch (SQLException ex) {
			System.err.println("sqlexception :" + ex.getMessage());
		}
	}
}
