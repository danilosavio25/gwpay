package br.com.gwpay.pagamento.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import javax.naming.InitialContext;
import javax.sql.DataSource;


public class OperacaoDao {
	
	
	public  OperacaoDao(){
	}

	
	public HashMap getDadosTerminalId(String bandeira, String tipoOperacao, String adquirente){
		
		try {
			
			ConnectionFactory connectionFactory = new ConnectionFactory();
			Connection conn = connectionFactory.getConnection();
			System.out.println("after getconn");
			PreparedStatement pstmt;
			
			pstmt = conn.prepareStatement("SELECT SUFIXO, PREFIXO FROM OPERACAO_ADQUIRENTE O "
											+"JOIN BANDEIRA B ON O.BANDEIRA_ID = B.ID "
											+"JOIN TIPO_OPERACAO T ON O.TIP_OPERACAO_ID = T.ID "
											+"JOIN ADQUIRENTE A ON O.ADQ_ID = A.ID " 
											+"AND B.NOME = ? "
											+"AND T.DESCRICAO = ? "
											+"AND A.NOME = ?");


			pstmt.setString(1,bandeira);
			pstmt.setString(2, tipoOperacao);
			pstmt.setString(3, adquirente);

			ResultSet rs = pstmt.executeQuery();
				
			
			HashMap camposRetorno = new HashMap<String, String>();
			while (rs.next()) {
				
				camposRetorno.put("sufixo", rs.getString("sufixo"));
				camposRetorno.put("prefixo", rs.getString("prefixo"));
				
			}
			
			rs.close();

			pstmt.close();
			conn.close();
			
			return camposRetorno;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
		
	}
	
}
