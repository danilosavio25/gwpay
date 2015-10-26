package br.com.gwpay.pagamento.service;

import java.sql.SQLException;
import java.util.HashMap;

import br.com.gwpay.pagamento.dao.BandeiraDao;
import br.com.gwpay.pagamento.dao.ClienteDao;
import br.com.gwpay.pagamento.dao.HistoricoTransacaoDao;
import br.com.gwpay.pagamento.dao.OperacaoDao;
import br.com.gwpay.pagamento.dao.TipoTransacaoDao;
import br.com.gwpay.pagamento.exception.AdquirenteException;
import br.com.gwpay.pagamento.exception.GWPayException;
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
	public ResultadoWS realizarCreditoAutorizacao(ParametrosAutorizacao params) throws AdquirenteException, GWPayException{
		MPIPlugin plugin = new MPIPlugin();
		
		String terminalIdComposto = "";
		HashMap<String, String> camposRetornoTerminal = new HashMap<>();
		int bandeiraId = 0;
		HashMap<String, String> camposRetorno = new HashMap<>();
		int tipoTransacaoId = 0;
		int clienteId = 0;
		
		//### TRY TRATA AS EXCECOES SQL E DE ERRO DE CONEXAO DO PLUGIN ###
		try {
			
			// ### Busca os dados de terminal ID no banco de dados ####
			OperacaoDao oDao = new OperacaoDao();
			camposRetornoTerminal = oDao.getDadosTerminalId(params.getBandeira(), "CREDITO", "GETNET");
			
			// ### Busca o ID da bandeira no banco de dados ####	
			BandeiraDao bDao =  new BandeiraDao();
			bandeiraId = bDao.getBandeiraId(params.getBandeira());
			
			// ### Busca o ID do tipoTransacao no banco de dados ####	
			TipoTransacaoDao trDao = new TipoTransacaoDao();
			tipoTransacaoId =  trDao.getTipoTransacaoId("AUTORIZACAO");
			
			// ### Busca o ID do cliente no banco de dados ####	
			ClienteDao cDao = new ClienteDao();
			clienteId = cDao.getClienteId(params.getCodGWPay());
			
			
			
			
			// ### VERIFICACOES DE SEGURANCA ###
			if(clienteId == 0){
				//@EXCEPTION CRIAR CÓDIGO
				GWPayException exception = new GWPayException("Parâmetro Incorreto.");
				exception.setInfoFault("GW02", "Parâmetro incorreto ou cliente não existe." , "Paramêtro cliente incorreto ou cliente não existe." , "Favor verificar seu código GWPay");
			}
			
			
			if (!camposRetornoTerminal.containsKey("sufixo")) {
				//@EXCEPTION CRIAR CÓDIGO
				GWPayException exception = new GWPayException("Parâmetro Incorreto.");
				exception.setInfoFault("GW01", "Parâmetro incorreto." , "Paramêtro bandeira incorreto." , "Favor verificar parametro");
			}
			
			if(params.getTipoParcelamento() == null){
				params.setTipoParcelamento("SGL");
			}
			
			
			// ### Seta o terminal ID composto
			terminalIdComposto = camposRetornoTerminal.get("prefixo") + params.getCodCliente() + camposRetornoTerminal.get("sufixo");
			System.out.println("terminalIdComposto: " + terminalIdComposto);
			params.setCodCliente(terminalIdComposto);
			
			
			// ### EXECUTA METODO DO PLUGIN ###
			camposRetorno = plugin.realizarCreditoAutorizacao(params);
			
			//### SALVA TRANSACAO NO BANCO DE DADOS ###
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
			
			transacao.setValorTotal(params.getValor()) ;
			transacao.setTaxaJuros(0.0);
			transacao.setJurosInstFinanceira(0.0);
			transacao.setTipoTransacaoId(tipoTransacaoId);
			transacao.setClienteId(clienteId);
			transacao.setBandeiraId(bandeiraId);
			transacao.setCodSegurancaCartao(params.getCodSegurancaCartao());
			
			// ### Não é cancelamento tipo = 0 ###
			transacao.setTipoCancelamentoId(0);
			
			System.out.println("TRANSSSSIDDDD: " + transacao.getCodCliente());
			if(transacao.getCodNSU() == null || transacao.getCodNSU().equals("")){
				transacao.setCodNSU("");
			}
			
			HistoricoTransacaoDao dao = new HistoricoTransacaoDao();
			dao.inserirHistoricoTransacao(transacao);
		
		
		} catch (Exception e) {
			
			//@EXCEPTION CRIAR CÓDIGO
			GWPayException exception = new GWPayException("Erro de conexão.");
			exception.setInfoFault("GW00", "Erro de conexão" , "Ocorreu um erro de conexão no sistema GWPay." , "Favor entar em contato com a GWPay");
			throw exception;
		}
		
		
		
		ResultadoWS result = new ResultadoWS();
		// ### SE HOUVER ERRO RETORNA EXCECAO ADQUIRENTE ###
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

	@Override
	public String realizarCreditoAutenticacao() {
		// TODO Auto-generated method stub
		return null;
	}

}
