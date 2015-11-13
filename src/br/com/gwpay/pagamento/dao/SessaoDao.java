package br.com.gwpay.pagamento.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import br.com.gwpay.pagamento.model.Sessao;



public class SessaoDao {
	
	public Connection conn;
	
	public  SessaoDao(){
	}
	
	public boolean inserirSessao(Sessao sessao){
		
		try {
			
				ConnectionFactory connectionFactory = new ConnectionFactory();
				Connection conn = connectionFactory.getConnection();
				System.out.println("after getconn");
				PreparedStatement pstmt;
				
				String sql = "INSERT INTO sessao(terminal_id, token, login, dat_expiracao, dat_acesso, cliente_id) VALUES (?, ?, ?, ?, ?, ?)";
				
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setString(1, null);
				pstmt.setString(2, sessao.getToken());
				pstmt.setString(3, sessao.getLogin());
				pstmt.setTimestamp(4, sessao.getDataExpiracao());
				pstmt.setTimestamp(5, sessao.getDataAcesso());
				pstmt.setInt(6, sessao.getClienteId());
				
				pstmt.executeUpdate();
	
				pstmt.close();
				conn.close();
				
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		
	}
	
	public int autenticar(String token){
		
		try {
			ConnectionFactory connectionFactory = new ConnectionFactory();
			Connection conn = connectionFactory.getConnection();
			System.out.println("after getconn");
			PreparedStatement pstmt;
			
			pstmt = conn.prepareStatement("SELECT ID FROM SESSAO  WHERE TOKEN = ?");
										

			pstmt.setString(1, token);


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
	
	public Sessao getSessao(String token){
		
		try {
			
			ConnectionFactory connectionFactory = new ConnectionFactory();
			Connection conn = connectionFactory.getConnection();
			
			System.out.println("after getconn");
			PreparedStatement pstmt;
			
			pstmt = conn.prepareStatement("SELECT ID, token, dat_expiracao ,dat_acesso, cliente_id  FROM SESSAO  WHERE TOKEN = ?");
										

			pstmt.setString(1, token);


			ResultSet rs = pstmt.executeQuery();
				
			
			Sessao sessao = new Sessao();
			while (rs.next()) {
				sessao.setToken(rs.getString("token"));
				sessao.setDataExpiracao(rs.getTimestamp("dat_expiracao"));
				sessao.setDataAcesso(rs.getTimestamp("dat_acesso"));
				sessao.setClienteId(rs.getInt("cliente_id"));
				
			}
			rs.close();

			pstmt.close();
			conn.close();
			
			return sessao;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
		
	}
	
	public boolean deletarSessao(String token){
		
		try {
			ConnectionFactory connectionFactory = new ConnectionFactory();
			Connection conn = connectionFactory.getConnection();
			System.out.println("after getconn");
			PreparedStatement pstmt;
			
			pstmt = conn.prepareStatement("DELETE FROM SESSAO  WHERE TOKEN = ?");
										

			pstmt.setString(1, token);
			
			pstmt.executeUpdate();

			pstmt.close();
			conn.close();
			
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
		
	}
	
	public static void main(String[] args) {
		Sessao s = new Sessao();
		s.setToken("xxxxxxx-xxxxxxx-xxxxxx-xxxxxxxxx");
		s.setLogin("danilo.savio");
		
		Date data =  new Date();
		Timestamp hoje = new Timestamp(data.getTime());
		Timestamp expiracao = new Timestamp(data.getTime() + 12 * 60 * 60 * 1000);
		
		
		s.setDataAcesso(hoje);
		s.setDataExpiracao(expiracao);
		s.setClienteId(1);
		
		
		SessaoDao h = new SessaoDao();
		//h.inserirSessao(s);
		
	}


	
	
	
	
	
}
