package br.com.gwpay.pagamento.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import javax.naming.InitialContext;
import javax.sql.DataSource;


public class UsuarioDao {
	
	public Connection conn;
	
	public UsuarioDao() {
		ConnectionFactory connectionFactory = new ConnectionFactory();
		conn = connectionFactory.getConnection();
	}

	
	public int autenticar(String login, String senha){
		
		try {
			
			System.out.println("after getconn");
			PreparedStatement pstmt;
			
			pstmt = conn.prepareStatement("SELECT ID FROM USUARIO  WHERE LOGIN = ? AND SENHA = ?");
										

			pstmt.setString(1, login);
			pstmt.setString(2, senha);

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
