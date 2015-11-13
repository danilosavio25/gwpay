package br.com.gwpay.pagamento.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import javax.naming.InitialContext;
import javax.sql.DataSource;


public class TipoCancelamentoDao {
	
	public  TipoCancelamentoDao(){
	}

	public int getTipoCancelamentoId(String descricao){
		
		try {
			
			ConnectionFactory connectionFactory = new ConnectionFactory();
			Connection conn = connectionFactory.getConnection();
			System.out.println("after getconn");
			PreparedStatement pstmt;
			
			pstmt = conn.prepareStatement("SELECT ID FROM TIPO_CANCELAMENTO WHERE DESCRICAO = ?");
										

			pstmt.setString(1,descricao);
		

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
