package br.com.gwpay.pagamento.service;

import br.com.gwpay.pagamento.exception.AdquirenteException;
import br.com.gwpay.pagamento.exception.GWPayException;
import br.com.gwpay.pagamento.model.Parametros;
import br.com.gwpay.pagamento.model.ParametrosAutorizacao;
import br.com.gwpay.pagamento.model.ResultadoWS;

public interface IPagamentoWS {
	
	public String realizarCreditoCompleto(ParametrosAutorizacao params) throws AdquirenteException, GWPayException;
	
	public ResultadoWS realizarCreditoAutorizacao(ParametrosAutorizacao params) throws AdquirenteException, GWPayException;
	
	public String realizarCreditoConfirmacao(Parametros params) throws AdquirenteException, GWPayException;
	
	public String realizarDebito();
	
	public String realizarCreditoAutenticacao();
	
	public String realizarConsultaTransacao(Parametros params) throws AdquirenteException, GWPayException;
	
	public String realizarCancelamento();
	
}
