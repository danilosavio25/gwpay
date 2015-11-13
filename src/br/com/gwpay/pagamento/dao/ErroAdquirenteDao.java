package br.com.gwpay.pagamento.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import javax.naming.InitialContext;
import javax.sql.DataSource;


public class ErroAdquirenteDao {
	
	
	public  ErroAdquirenteDao(){
	}


	
	public HashMap getDadosErro(String codErro, String adquirente){
		
		try {
			ConnectionFactory connectionFactory = new ConnectionFactory();
			Connection conn = connectionFactory.getConnection();
			System.out.println("after getconn");
			PreparedStatement pstmt;
			
			pstmt = conn.prepareStatement("SELECT COD_ERRO, ERRO, DESCRICAO, ACAO FROM ERRO_ADQUIRENTE E "
					+"	JOIN ADQUIRENTE A ON E.ADQ_ID = A.ID "
					+"	AND A.NOME = ? "
					+"	AND E.COD_ERRO = ? ");


			pstmt.setString(1,adquirente);
			pstmt.setString(2, codErro);

			ResultSet rs = pstmt.executeQuery();
			
			HashMap camposRetornoErro = new HashMap<String, String>();
			camposRetornoErro.put("cod_erro", "");
			camposRetornoErro.put("erro", "");
			camposRetornoErro.put("descricao", "");
			camposRetornoErro.put("acao", "");
			
			
			while (rs.next()) {
				
				camposRetornoErro.put("cod_erro", rs.getString("cod_erro"));
				camposRetornoErro.put("erro", rs.getString("erro"));
				camposRetornoErro.put("descricao", rs.getString("descricao"));
				camposRetornoErro.put("acao", rs.getString("acao"));
				System.out.println(rs.getString("cod_erro"));
				System.out.println(rs.getString("descricao"));
				System.out.println(rs.getString("acao"));
			}
			rs.close();
			pstmt.close();
			conn.close();
			
			return camposRetornoErro;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
		
	}
	
}
