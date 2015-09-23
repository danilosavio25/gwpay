package br.com.gwpay.pagamento.service;

import br.com.gwpay.pagamento.plugin.MPIPlugin;

public class GetNetService implements IPagamentoWS{

	@Override
	public String realizarCredito() {
		return null;
	}

	@Override
	public String realizarCreditoAutorizacao() {
		return null;
	}

	@Override
	public String realizarCreditoConfirmacao() {
		return null;
	}

	@Override
	public String realizarDebito() {
		return null;
	}

	@Override
	public String realizarConsultaTransacao(String codigoCliente,String codigoRastreio) {
		MPIPlugin plugin = new MPIPlugin();
		return plugin.consulta(codigoCliente, codigoRastreio);
	}

	@Override
	public String realizarEstorno() {
		return null;
	}

}
