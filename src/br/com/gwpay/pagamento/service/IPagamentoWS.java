package br.com.gwpay.pagamento.service;


public interface IPagamentoWS {
	
	public String realizarCredito();
	
	public String realizarCreditoAutorizacao();
	
	public String realizarCreditoConfirmacao();
	
	public String realizarDebito();
	
	public String realizarConsultaTransacao(String codigoCliente,String codigoRastreio);
	
	public String realizarEstorno();
	
}
