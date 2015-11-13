package br.com.gwpay.pagamento.dao;

import java.sql.Connection;
import java.sql.DriverManager;

import javax.naming.InitialContext;
import javax.sql.DataSource;

public class ConnectionFactory {
	
	DataSource ds = null;
	InitialContext ic = null;
	boolean CONNECTION_DATA_SOURCE = true;
	
	public ConnectionFactory() {
		
	}
	
	public Connection getConnection(){

		Connection connection = null;
		try {
			
			
			if(CONNECTION_DATA_SOURCE){
				System.out.println("Conectando com DataSource");
				 ic = new InitialContext();  
				 ds = (DataSource) ic.lookup("java:jboss/datasources/PostgreSQLDS");  
				 connection = ds.getConnection();
			}else{
				String url = "jdbc:postgresql://localhost:5432/GWPayBD";  
				 String usuario = "GWPayAdminBD";  
				 String senha = "GWPayAdminBD00";
				 
				 Class.forName("org.postgresql.Driver").newInstance();  
			      connection = DriverManager.getConnection(url, usuario, senha);
			}
			

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
}
