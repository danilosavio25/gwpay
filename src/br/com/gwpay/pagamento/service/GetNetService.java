package br.com.gwpay.pagamento.service;

import java.util.HashMap;

import br.com.gwpay.pagamento.exception.AdquirenteException;
import br.com.gwpay.pagamento.model.Parametros;
import br.com.gwpay.pagamento.model.ParametrosAutorizacao;
import br.com.gwpay.pagamento.model.ResultadoWS;
import br.com.gwpay.pagamento.plugin.MPIPlugin;

public class GetNetService implements IPagamentoWS{

	@Override
	public String realizarCredito(Parametros params) {
		return null;
	}

	@Override
	public ResultadoWS realizarCreditoAutorizacao(ParametrosAutorizacao params) throws AdquirenteException{
		MPIPlugin plugin = new MPIPlugin();
		HashMap< String, String> camposRetorno = plugin.realizarCreditoAutorizacao(params);

		ResultadoWS result = new ResultadoWS();

		if(camposRetorno.containsKey("error_code") && camposRetorno.get("error_code").equals("erroPerform")){
			
			AdquirenteException exception = new AdquirenteException("Erro na chamada do serviço Adquirente");
			// Preencher Descricao e acao vinda do banco
			exception.setInfoFault(camposRetorno.get("error_code"), camposRetorno.get("error_text") , "Descricao vinda do banco" , "Acao vinda do banco");
			throw exception;
	
		}else if(camposRetorno.containsKey("error_code") && !camposRetorno.get("error_code").equals("")){
			
			AdquirenteException exception = new AdquirenteException("Erro na chamada do serviço Adquirente");
			// Preencher Descricao e acao vinda do banco
			exception.setInfoFault(camposRetorno.get("error_code"), camposRetorno.get("error_text") , "Descricao vinda do banco" , "Acao vinda do banco");
			throw exception;
	
		}else{
			// colocar descricao vinda do banco
			result.setCodigoResposta(camposRetorno.get("responsecode"));
			result.setMensagemResposta(camposRetorno.get("result"));
			result.setDescricaoResposta(camposRetorno.get("Descricao vinda do banco"));
			
		}
		
		result.setCodigoNSU(camposRetorno.get("tranid"));
		result.setCodigoRastreio(camposRetorno.get("trackid"));
		
		return result;
	}

	@Override
	public String realizarCreditoConfirmacao(Parametros params) {
		return null;
	}

	@Override
	public String realizarDebito() {
		return null;
	}

	@Override
	public String realizarConsultaTransacao(Parametros params) {
		return null;
	}

	@Override
	public String realizarEstorno() {
		return null;
	}

}
