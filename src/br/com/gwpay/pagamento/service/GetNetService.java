package br.com.gwpay.pagamento.service;

import br.com.gwpay.pagamento.model.Parametros;
import br.com.gwpay.pagamento.model.ParametrosAutorizacao;
import br.com.gwpay.pagamento.plugin.MPIPlugin;

public class GetNetService implements IPagamentoWS{

	@Override
	public String realizarCredito(Parametros params) {
		return null;
	}

	@Override
	public String realizarCreditoAutorizacao(ParametrosAutorizacao params) {
		MPIPlugin plugin = new MPIPlugin();
		return plugin.realizarCreditoAutorizacao(params);
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
