package br.com.gwpay.pagamento.autenticacao;

import java.sql.Timestamp;
import java.util.Date;

import br.com.gwpay.pagamento.dao.SessaoDao;
import br.com.gwpay.pagamento.exception.GWPayException;
import br.com.gwpay.pagamento.model.Sessao;

public class Autenticacao {
	
	
	public boolean autenticar(String token) throws GWPayException{
		
		//############################################ AUTENTICACAO ###########################################
		SessaoDao sDao = new SessaoDao();
		Sessao sessao = sDao.getSessao(token);
		boolean sessaoValida = validarSessao(sessao); 
		if(sessao == null || sessaoValida == false){
			GWPayException exception = new GWPayException("Token inválido.");
			exception.setInfoFault("GW00", "Token Inválido" , "Token de acesso inválido." , "Favor verificar seu Token, sua validade pode ter expirado.");
			throw exception;
		}
		
		
		return true;
	}
	
	private boolean validarSessao(Sessao sessao){
		
		if(sessao == null || sessao.getDataExpiracao() == null) return false;
		
		// ### Gera as datas de sessão ###
		Date data =  new Date();
		Timestamp dataHoje = new Timestamp(data.getTime());
		
		System.out.println("dataExpiracao " + sessao.getDataExpiracao());
		System.out.println("dataHoje " + dataHoje);
		
		if( dataHoje.getTime() > sessao.getDataExpiracao().getTime() ){
			SessaoDao sDao = new SessaoDao();
			sDao.deletarSessao(sessao.getToken());
			return false;
		}
		
		return true;
	}
	
	
}
