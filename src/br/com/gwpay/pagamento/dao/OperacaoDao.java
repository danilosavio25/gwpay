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
	
	DataSource ds = null;
	InitialContext ic = null;
	
	
	public Connection getConnection(){
		

		Connection connection = null;

		try {
			System.out.println("Conectando com DataSource");
			 ic = new InitialContext();  
			 ds = (DataSource) ic.lookup("java:jboss/datasources/PostgreSQLDS");  
			 connection = ds.getConnection();
			 
/*			 String url = "jdbc:postgresql://localhost:5432/GWPayBD";  
			 String usuario = "GWPayAdminBD";  
			 String senha = "GWPayAdminBD00";
			 
			 Class.forName("org.postgresql.Driver").newInstance();  
		      connection = DriverManager.getConnection(url, usuario, senha);  */
		      

		} catch (Exception e) {

			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return null;
		}

		if (connection != null) {
			System.out.println("Conectado com sucesso!");
			return connection;
		} else {
			System.out.println("Conexao FAlhou!");
			return null;

		}

		
	}

	
	public HashMap getDadosTerminalId(String bandeira, String tipoOperacao, String adquirente){
		
		try {
			
			Connection conn = getConnection();
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
