package br.com.gwpay.pagamento.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import br.com.gwpay.pagamento.model.HistoricoTransacao;


public class HistoricoTransacaoDao {
	
	DataSource ds = null;
	InitialContext ic = null;
	
	
	public Connection getConnection(){
		

		Connection connection = null;

		try {
			System.out.println("Conectando com DataSource");
			 ic = new InitialContext();  
			 ds = (DataSource) ic.lookup("java:jboss/datasources/PostgreSQLDS");  
			 connection = ds.getConnection();

			 
		/*	 
			 String url = "jdbc:postgresql://localhost:5432/GWPayBD";  
			 String usuario = "GWPayAdminBD";  
			 String senha = "GWPayAdminBD00";
			 
			 Class.forName("org.postgresql.Driver").newInstance();  
		      connection = DriverManager.getConnection(url, usuario, senha); */
			 
			 
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
	
	
	public boolean inserirHistoricoTransacao(HistoricoTransacao transacao){
		
		try {
				
				Connection conn = getConnection();
				System.out.println("after getconn");
				PreparedStatement pstmt;
				
				
				String sql = "INSERT INTO historico_transacao(terminal_id, trs_loja_id, valor, moeda, tip_pagamento,                                        	"
						+"														num_pcl,num_cartao, mes_vct_cartao, ano_vct_cartao, nme_ptd_cartao,    	"
						+"														udf1, udf2, udf3, udf4, udf5,                                          	"
						+"														nsu_id, dat_trs, trs_original_id, dsc_resposta, cod_resposta,          	"
						+"														val_primeira_pcl, val_outras_pcl,val_total,	"
						+"														taxa_juros, juros_ins_fin, valor_cancelado,   	"
						+"														tip_trs_id, cliente_id, bandeira_id, tip_cancelamento_id, cod_seguranca_cartao)              	"
						+"					    VALUES (?, ?, ?, ?, ?,                                                                           	"
						+"						    	?, ?, ?, ?, ?,                                                                               	"
						+"						   	    ?, ?, ?, ?, ?,                                                                                	"
						+"						   	    ?, current_timestamp, ?,?,?,                                                                 	"
						+"						   	     ?, ?, ?,                                                                             	"
						+"						   	    ?, ?,  ?,                                                                             	"
						+"						    	?, ?, ?, ?,?);                                                                                    	";
	
				
				pstmt = conn.prepareStatement(sql);
	
			
				
				pstmt.setString(1, transacao.getCodCliente());
				pstmt.setString(2,transacao.getCodRastreio());
				pstmt.setDouble(3,transacao.getValor());
				pstmt.setString(4, transacao.getMoeda());			
				pstmt.setString(5, transacao.getTipoPagamento());
				pstmt.setInt(6, transacao.getNumParcelas());
				pstmt.setString(7, transacao.getNumCartao());
				pstmt.setInt(8, transacao.getMesVencimentoCartao());
				pstmt.setInt(9, transacao.getAnoVencimentoCartao());
				
				String nomePortador = "";
				if(transacao.getNomePortador() != null){
					if(transacao.getNomePortador().length() > 26){
						nomePortador = transacao.getNomePortador().substring(0, 26);
					}else{
						nomePortador = transacao.getNomePortador();
					}
				}
				
				pstmt.setString(10, nomePortador);
				pstmt.setString(11, transacao.getCampo1());
				pstmt.setString(12, transacao.getCampo2());
				pstmt.setString(13, transacao.getCampo3());
				pstmt.setString(14, transacao.getCampo4());
				pstmt.setString(15, transacao.getCampo5());
				pstmt.setString(16, transacao.getCodNSU());
				//pstmt.setString(17, current_timestamp);
				pstmt.setString(17, transacao.getCodTransacaoOriginal());
				pstmt.setString(18, transacao.getDescricaoResposta());
				pstmt.setString(19, transacao.getCodResposta());
				pstmt.setDouble(20, transacao.getValorPrimeiraParcela());
				pstmt.setDouble(21, transacao.getValorOutrasParcelas());
				pstmt.setDouble(22, transacao.getValorTotal() );
				pstmt.setDouble(23, transacao.getTaxaJuros());
				pstmt.setDouble(24, transacao.getJurosInstFinanceira());
				pstmt.setDouble(25, transacao.getValorCancelado());
				pstmt.setInt(26, transacao.getTipoTransacaoId());
				pstmt.setInt(27, transacao.getClienteId());
				pstmt.setInt(28, transacao.getBandeiraId());
				pstmt.setInt(29, transacao.getTipoCancelamentoId());
				pstmt.setString(30, transacao.getCodSegurancaCartao());

	
				
				pstmt.executeUpdate();
	
				pstmt.close();
				conn.close();
				
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		
	}
	
	public static void main(String[] args) {
		HistoricoTransacaoDao h = new HistoricoTransacaoDao();
		h.getBandeiraTransacao("2292945311652751");
	}

	
	public HashMap getBandeiraTransacao(String codNSU){
	
		try {
			
			Connection conn = getConnection();
			System.out.println("after getconn");
			PreparedStatement pstmt;
			
			pstmt = conn.prepareStatement("SELECT H.BANDEIRA_ID as BANDEIRA_ID, B.NOME AS NOME FROM HISTORICO_TRANSACAO H "
										  +"JOIN BANDEIRA B ON H.BANDEIRA_ID = B.ID "
										  +"AND H.NSU_ID = ? "
										  +"AND H.TIP_TRS_ID <> 8 ");
	
			//'2292945311652751'
			pstmt.setString(1,codNSU);
			
	
			ResultSet rs = pstmt.executeQuery();
				
			
			HashMap camposRetorno = new HashMap<String, String>();
			camposRetorno.put("bandeira_id", "");
			camposRetorno.put("nome", "");
			while (rs.next()) {
				camposRetorno.put("bandeira_id", rs.getString("bandeira_id"));
				camposRetorno.put("nome", rs.getString("nome"));
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
	
	public HashMap getBandeiraTransacaoOriginal(String codNSUTransacaoOriginal){
		
		try {
			
			Connection conn = getConnection();
			System.out.println("after getconn");
			PreparedStatement pstmt;
			
			pstmt = conn.prepareStatement("SELECT H.BANDEIRA_ID as BANDEIRA_ID, B.NOME AS NOME FROM HISTORICO_TRANSACAO H "
										  +"JOIN BANDEIRA B ON H.BANDEIRA_ID = B.ID "
										  +"AND H.TRS_ORIGINAL_ID = ? "
										  +"AND H.TIP_TRS_ID <> 8 ");
	
			//'2292945311652751'
			pstmt.setString(1,codNSUTransacaoOriginal);
			
	
			ResultSet rs = pstmt.executeQuery();
				
			
			HashMap camposRetorno = new HashMap<String, String>();
			camposRetorno.put("bandeira_id", "");
			camposRetorno.put("nome", "");
			while (rs.next()) {
				camposRetorno.put("bandeira_id", rs.getString("bandeira_id"));
				camposRetorno.put("nome", rs.getString("nome"));
				break;
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
	
	public Timestamp getDataTransacao(String codNSU){
		
		try {
			
			Connection conn = getConnection();
			System.out.println("after getconn");
			PreparedStatement pstmt;
			
			pstmt = conn.prepareStatement("SELECT DAT_TRS FROM HISTORICO_TRANSACAO WHERE NSU_ID = ? AND TIP_TRS_ID <> 8"); 
	
			//'2292945311652751'
			pstmt.setString(1,codNSU);
			
	
			ResultSet rs = pstmt.executeQuery();
				
			
			Timestamp dataTransacao = null;
			while (rs.next()) {
				dataTransacao = rs.getTimestamp("dat_trs");
			}
			
			rs.close();
	
			pstmt.close();
			conn.close();
			
			return dataTransacao;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return null;
	
	}
	
	
	
	
	
}
