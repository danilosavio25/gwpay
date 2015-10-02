package br.com.gwpay.pagamento.service;

import java.util.HashMap;

import br.com.gwpay.pagamento.dao.HistoricoTransacaoDao;
import br.com.gwpay.pagamento.exception.AdquirenteException;
import br.com.gwpay.pagamento.model.HistoricoTransacao;
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
		
		String terminalIdComposto = "";
		int bandeiraId = 0;
		if(params.getBandeira().equalsIgnoreCase("VISA")){
			terminalIdComposto = "D" + params.getCodCliente() + "01";
			bandeiraId  = 1;
		}else{
			terminalIdComposto = "D" + params.getCodCliente() + "02";		
			bandeiraId = 2;
		}
		
		params.setCodCliente(terminalIdComposto);
		
		HashMap< String, String> camposRetorno = plugin.realizarCreditoAutorizacao(params);
		
		//VERIFICAR OS CAMPOS OBRIGATORIOS
		
		// SALVAR TRANSACAO NO BANCO
		HistoricoTransacao transacao = new HistoricoTransacao();
		transacao.setCodCliente(terminalIdComposto);
		transacao.setCodRastreio(params.getCodRastreio());
		transacao.setValor(params.getValor());
		transacao.setMoeda("986");			
		transacao.setTipoPagamento(params.getTipoParcelamento());
		transacao.setNumParcelas(params.getNumParcelas());
		transacao.setNumCartao(params.getNumCartao());
		transacao.setMesVencimentoCartao(params.getMesVencimento());
		transacao.setAnoVencimentoCartao(params.getAnoVencimento());
		transacao.setNomePortador(params.getNomePortador());
		
		if(camposRetorno.containsKey("tranid")){
			transacao.setCodTransacaoOriginal(camposRetorno.get("tranid"));
			transacao.setCodNSU(camposRetorno.get("tranid"));
			transacao.setDescricaoResposta(camposRetorno.get("result"));
			transacao.setCodResposta(camposRetorno.get("responsecode"));

		}else{
			transacao.setCodTransacaoOriginal("");
			transacao.setCodErroGateway(camposRetorno.get("error_code"));
			transacao.setDescricaoErroGateway(camposRetorno.get("error_text"));
			transacao.setCodErroWS(camposRetorno.get(""));
			transacao.setDescricaoErroWS(camposRetorno.get(""));
		}
		
		
	//	transacao.setValorPrimeiraParcela(params.getV);
	//	transacao.setValorOutrasParcelas("");
		transacao.setValorTotal(params.getValor()) ;
		transacao.setTaxaJuros(0.0);
		transacao.setJurosInstFinanceira(0.0);
		//buscar no BD
		transacao.setTipoTransacaoId(2);
		transacao.setClienteId(1);
		transacao.setBandeiraId(bandeiraId);
		transacao.setCodSegurancaCartao(params.getCodSegurancaCartao());
		transacao.setTipoCancelamentoId(1);
		
		
		System.out.println("TRANSSSSIDDDD: " + transacao.getCodCliente());
		if(transacao.getCodNSU() == null || transacao.getCodNSU().equals("")){
			transacao.setCodNSU("");
		}
		
		HistoricoTransacaoDao dao = new HistoricoTransacaoDao();
		dao.inserirHistoricoTransacao(transacao);
		
		
		
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
	
		}
		
		// colocar descricao vinda do banco
		result.setCodigoResposta(camposRetorno.get("responsecode"));
		result.setMensagemResposta(camposRetorno.get("result"));
		result.setDescricaoResposta(camposRetorno.get("Descricao vinda do banco"));
		
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
