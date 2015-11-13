package br.com.gwpay.pagamento.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import br.com.gwpay.pagamento.model.HistoricoTransacaoErro;


public class HistoricoTransacaoErroDao {
	
	public Connection conn;
	
	public  HistoricoTransacaoErroDao(){
		ConnectionFactory connectionFactory = new ConnectionFactory();
		conn = connectionFactory.getConnection();
	}
	
	public boolean inserirHistoricoTransacaoErro(HistoricoTransacaoErro transacao){
		
		try {
				
				System.out.println("after getconn");
				PreparedStatement pstmt;
				
				
				String sql = "INSERT INTO historico_transacao_erro(terminal_id, trs_loja_id, valor, moeda, tip_pagamento,                                        	"
						+"														num_pcl,num_cartao, mes_vct_cartao, ano_vct_cartao, nme_ptd_cartao,    	"
						+"														udf1, udf2, udf3, udf4, udf5,                                          	"
						+"														nsu_id, dat_trs, trs_original_id, dsc_resposta, cod_resposta,          	"
						+"														cod_erro_gwy, dsc_erro_gwy, val_primeira_pcl, val_outras_pcl,val_total,	"
						+"														taxa_juros, juros_ins_fin, cod_erro_ws, dsc_erro_ws,valor_cancelado,   	"
						+"														tip_trs_id, cliente_id, bandeira_id, tip_cancelamento_id, cod_seguranca_cartao)              	"
						+"					    VALUES (?, ?, ?, ?, ?,                                                                           	"
						+"						    	?, ?, ?, ?, ?,                                                                               	"
						+"						   	    ?, ?, ?, ?, ?,                                                                                	"
						+"						   	    ?, current_timestamp, ?,?,?,                                                                 	"
						+"						   	    ?, ?, ?, ?, ?,                                                                             	"
						+"						   	    ?, ?, ?, ?, ?,                                                                             	"
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
				pstmt.setString(20, transacao.getCodErroGateway());
				pstmt.setString(21, transacao.getDescricaoErroGateway());
				pstmt.setDouble(22, transacao.getValorPrimeiraParcela());
				pstmt.setDouble(23, transacao.getValorOutrasParcelas());
				pstmt.setDouble(24, transacao.getValorTotal() );
				pstmt.setDouble(25, transacao.getTaxaJuros());
				pstmt.setDouble(26, transacao.getJurosInstFinanceira());
				pstmt.setString(27, transacao.getCodErroWS());
				pstmt.setString(28, transacao.getDescricaoErroWS());
				pstmt.setDouble(29, transacao.getValorCancelado());
				pstmt.setInt(30, transacao.getTipoTransacaoId());
				pstmt.setInt(31, transacao.getClienteId());
				pstmt.setInt(32, transacao.getBandeiraId());
				pstmt.setInt(33, transacao.getTipoCancelamentoId());
				pstmt.setString(34, transacao.getCodSegurancaCartao());

	
				
				pstmt.executeUpdate();
	
				pstmt.close();
				conn.close();
				
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		
	}
	
	

}
