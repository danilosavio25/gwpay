package br.com.gwpay.pagamento.service;

import br.com.gwpay.pagamento.model.Parametros;
import br.com.gwpay.pagamento.model.ParametrosAutorizacao;

public interface IPagamentoWS {
	
	public String realizarCredito(Parametros params);
	
	public String realizarCreditoAutorizacao(ParametrosAutorizacao params);
	
	public String realizarCreditoConfirmacao(Parametros params);
	
	public String realizarDebito();
	
	public String realizarConsultaTransacao(Parametros params);
	
	public String realizarEstorno();
	
}
