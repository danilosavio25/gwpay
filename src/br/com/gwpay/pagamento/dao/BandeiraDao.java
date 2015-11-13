package br.com.gwpay.pagamento.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.sql.DataSource;


public class BandeiraDao {
	
	public Connection conn;
	
	public BandeiraDao() {
		ConnectionFactory connectionFactory = new ConnectionFactory();
		conn = connectionFactory.getConnection();
	}

	
	public int getBandeiraId(String bandeira){
		
		try {
			
			System.out.println("after getconn");
			PreparedStatement pstmt;
			
			pstmt = conn.prepareStatement("SELECT ID FROM BANDEIRA WHERE NOME = ? ");
										

			pstmt.setString(1,bandeira);
		

			ResultSet rs = pstmt.executeQuery();
				
			
			int id = 0;
			while (rs.next()) {
				id =  rs.getInt("id");
			}
			rs.close();

			pstmt.close();
			conn.close();
			
			return id;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return 0;
		
	}
	
}
