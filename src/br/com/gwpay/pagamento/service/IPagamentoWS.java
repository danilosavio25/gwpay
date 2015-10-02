package br.com.gwpay.pagamento.service;

import br.com.gwpay.pagamento.exception.AdquirenteException;
import br.com.gwpay.pagamento.model.Parametros;
import br.com.gwpay.pagamento.model.ParametrosAutorizacao;
import br.com.gwpay.pagamento.model.ResultadoWS;

public interface IPagamentoWS {
	
	public String realizarCredito(Parametros params);
	
	public ResultadoWS realizarCreditoAutorizacao(ParametrosAutorizacao params) throws AdquirenteException;
	
	public String realizarCreditoConfirmacao(Parametros params);
	
	public String realizarDebito();
	
	public String realizarConsultaTransacao(Parametros params);
	
	public String realizarEstorno();
	
}
