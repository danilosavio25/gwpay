package br.com.gwpay.pagamento.service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import br.com.gwpay.pagamento.dao.BandeiraDao;
import br.com.gwpay.pagamento.dao.ClienteDao;
import br.com.gwpay.pagamento.dao.ErroAdquirenteDao;
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
	public ResultadoWS realizarCreditoCompleto(ParametrosAutorizacao params)  throws AdquirenteException, GWPayException{
		MPIPlugin plugin = new MPIPlugin();
	//############################################ CAMPOS OBRIGATÓRIOS ###########################################	
		// ### Verifica campos obrigatórios
		if((params.getCodCliente().equals("") || params.getCodCliente() == null) ||
			(params.getCodGWPay().equals("") || params.getCodGWPay() == null) ||	
			(params.getValor() == 0) ||
			(params.getBandeira().equals("") || params.getBandeira() == null) ||
			(params.getNumCartao().equals("") || params.getNumCartao() == null) ||
			(params.getNomePortador().equals("") || params.getNomePortador() == null) ||
			(params.getMesVencimento() == 0) ||
			(params.getAnoVencimento() == 0) ||
			(params.getCodSegurancaCartao().equals("") || params.getCodSegurancaCartao() == null)){
			
			GWPayException exception = new GWPayException("Parâmetros Obrigatórios.");
			exception.setInfoFault("GW00", "Há um ou mais Parâmetros obrigatórios faltando." , "Há um ou mais Parâmetros obrigatórios faltando." , "Favor verificar os parâmetros");
			throw exception;
			
		}
	
	//############################################ DADOS ############################################################
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
			tipoTransacaoId =  trDao.getTipoTransacaoId("AUTORIZACAO_CAPTURA");
			
			// ### Busca o ID do cliente no banco de dados ####	
			ClienteDao cDao = new ClienteDao();
			clienteId = cDao.getClienteId(params.getCodGWPay());
			
			// ### VERIFICACOES DE SEGURANCA ###
			if(clienteId == 0){
				//@EXCEPTION CRIAR CÓDIGO
				GWPayException exception = new GWPayException("Parâmetro Incorreto.");
				exception.setInfoFault("GW02", "Parâmetro codGWPay incorreto ou cliente não existe." , "Paramêtro codGWPay incorreto ou cliente não existe." , "Favor verificar seu código GWPay");
				throw exception;
			}
			
			
			if (!camposRetornoTerminal.containsKey("sufixo")) {
				//@EXCEPTION CRIAR CÓDIGO
				GWPayException exception = new GWPayException("Parâmetro Incorreto.");
				exception.setInfoFault("GW01", "Parâmetro incorreto." , "Paramêtro bandeira incorreto." , "Favor verificar parametro");
				throw exception;
			}
			
			if(params.getTipoParcelamento() == null){
				params.setTipoParcelamento("SGL");
			}
			
			
			// ### Seta o terminal ID composto
			terminalIdComposto = camposRetornoTerminal.get("prefixo") + params.getCodCliente() + camposRetornoTerminal.get("sufixo");
			System.out.println("terminalIdComposto: " + terminalIdComposto);
			params.setCodCliente(terminalIdComposto);
			
		//############################################ PLUGIN ############################################################	
			// ### EXECUTA METODO DO PLUGIN ###
			camposRetorno = plugin.realizarCreditoAutorizacao(params);

		//############################################ HISTORICO ############################################################	
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
				transacao.setCodNSU("");
				transacao.setDescricaoResposta("");
				transacao.setCodResposta("");
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
			
			if(transacao.getCodNSU() == null || transacao.getCodNSU().equals("")){
				transacao.setCodNSU("");
			}
			
			HistoricoTransacaoDao dao = new HistoricoTransacaoDao();
			dao.inserirHistoricoTransacao(transacao);
		
		//############################################ RESULTADO ############################################################	
			ResultadoWS result = new ResultadoWS();
			// ### SE HOUVER ERRO RETORNA EXCECAO ADQUIRENTE ###
			if(camposRetorno.containsKey("error_code") && camposRetorno.get("error_code").equals("erroPerform")){
				ErroAdquirenteDao eDao = new ErroAdquirenteDao();
				HashMap camposRetornoErro = eDao.getDadosErro(camposRetorno.get("error_code"), "GETNET");
				
				
				AdquirenteException exception = new AdquirenteException("Erro na chamada do serviço Adquirente");
				exception.setInfoFault(camposRetorno.get("error_code"), camposRetorno.get("error_text") , camposRetornoErro.get("descricao").toString() , camposRetornoErro.get("acao").toString() );
				throw exception;
		
			}else if(camposRetorno.containsKey("error_code") && !camposRetorno.get("error_code").equals("")){
				
				ErroAdquirenteDao eDao = new ErroAdquirenteDao();
				HashMap camposRetornoErro = eDao.getDadosErro(camposRetorno.get("error_code"), "GETNET");
				
				
				AdquirenteException exception = new AdquirenteException("Erro na chamada do serviço Adquirente");
				exception.setInfoFault(camposRetorno.get("error_code"), camposRetorno.get("error_text") , camposRetornoErro.get("descricao").toString() , camposRetornoErro.get("acao").toString() );
				throw exception;
		
			}
			
			// colocar descricao vinda do banco
			result.setCodigoResposta(camposRetorno.get("responsecode"));
			result.setMensagemResposta(camposRetorno.get("result"));
			result.setDescricaoResposta(camposRetorno.get("Descricao vinda do banco"));
			
			result.setCodigoNSU(camposRetorno.get("tranid"));
			result.setCodigoRastreio(camposRetorno.get("trackid"));
			
			return result;
			
		
		}catch (GWPayException e) {
			throw e;
		}catch (AdquirenteException e) {
			throw e;
		}
		catch (Exception e) {
			
			//@EXCEPTION CRIAR CÓDIGO
			GWPayException exception = new GWPayException("Erro de conexão.");
			exception.setInfoFault("GW00", "Erro de conexão" , "Ocorreu um erro de conexão no sistema GWPay." , "Favor entar em contato com a GWPay");
			throw exception;
		}
	}

	@Override
	public ResultadoWS realizarCreditoAutorizacao(ParametrosAutorizacao params) throws AdquirenteException, GWPayException{
		MPIPlugin plugin = new MPIPlugin();
	//############################################ CAMPOS OBRIGATÓRIOS ###########################################	
		// ### Verifica campos obrigatórios
		if((params.getCodCliente().equals("") || params.getCodCliente() == null) ||
			(params.getCodGWPay().equals("") || params.getCodGWPay() == null) ||	
			(params.getValor() == 0) ||
			(params.getBandeira().equals("") || params.getBandeira() == null) ||
			(params.getNumCartao().equals("") || params.getNumCartao() == null) ||
			(params.getNomePortador().equals("") || params.getNomePortador() == null) ||
			(params.getMesVencimento() == 0) ||
			(params.getAnoVencimento() == 0) ||
			(params.getCodSegurancaCartao().equals("") || params.getCodSegurancaCartao() == null)){
			
			GWPayException exception = new GWPayException("Parâmetros Obrigatórios.");
			exception.setInfoFault("GW00", "Há um ou mais Parâmetros obrigatórios faltando." , "Há um ou mais Parâmetros obrigatórios faltando." , "Favor verificar os parâmetros");
			throw exception;
			
		}
	//############################################ DADOS ############################################################
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
				exception.setInfoFault("GW02", "Parâmetro codGWPay incorreto ou cliente não existe." , "Paramêtro codGWPay incorreto ou cliente não existe." , "Favor verificar seu código GWPay");
				throw exception;
			}
			
			
			if (!camposRetornoTerminal.containsKey("sufixo")) {
				//@EXCEPTION CRIAR CÓDIGO
				GWPayException exception = new GWPayException("Parâmetro Incorreto.");
				exception.setInfoFault("GW01", "Parâmetro incorreto." , "Paramêtro bandeira incorreto." , "Favor verificar parametro");
				throw exception;
			}
			
			if(params.getTipoParcelamento() == null){
				params.setTipoParcelamento("SGL");
			}
			
			
			// ### Seta o terminal ID composto
			terminalIdComposto = camposRetornoTerminal.get("prefixo") + params.getCodCliente() + camposRetornoTerminal.get("sufixo");
			System.out.println("terminalIdComposto: " + terminalIdComposto);
			params.setCodCliente(terminalIdComposto);
			
		//############################################ PLUGIN ############################################################	
			// ### EXECUTA METODO DO PLUGIN ###
			camposRetorno = plugin.realizarCreditoAutorizacao(params);
			
		//############################################ HISTORICO ############################################################	
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
				transacao.setCodNSU("");
				transacao.setDescricaoResposta("");
				transacao.setCodResposta("");
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
		
		//############################################ RESULTADO ############################################################
			ResultadoWS result = new ResultadoWS();
			// ### SE HOUVER ERRO RETORNA EXCECAO ADQUIRENTE ###
			if(camposRetorno.containsKey("error_code") && camposRetorno.get("error_code").equals("erroPerform")){
				ErroAdquirenteDao eDao = new ErroAdquirenteDao();
				HashMap camposRetornoErro = eDao.getDadosErro(camposRetorno.get("error_code"), "GETNET");
				
				
				AdquirenteException exception = new AdquirenteException("Erro na chamada do serviço Adquirente");
				exception.setInfoFault(camposRetorno.get("error_code"), camposRetorno.get("error_text") , camposRetornoErro.get("descricao").toString() , camposRetornoErro.get("acao").toString() );
				throw exception;
		
			}else if(camposRetorno.containsKey("error_code") && !camposRetorno.get("error_code").equals("")){
				
				ErroAdquirenteDao eDao = new ErroAdquirenteDao();
				HashMap camposRetornoErro = eDao.getDadosErro(camposRetorno.get("error_code"), "GETNET");
				
				
				AdquirenteException exception = new AdquirenteException("Erro na chamada do serviço Adquirente");
				exception.setInfoFault(camposRetorno.get("error_code"), camposRetorno.get("error_text") , camposRetornoErro.get("descricao").toString() , camposRetornoErro.get("acao").toString() );
				throw exception;
		
			}
			
			// colocar descricao vinda do banco
			result.setCodigoResposta(camposRetorno.get("responsecode"));
			result.setMensagemResposta(camposRetorno.get("result"));
			result.setDescricaoResposta(camposRetorno.get("Descricao vinda do banco"));
			
			result.setCodigoNSU(camposRetorno.get("tranid"));
			result.setCodigoRastreio(camposRetorno.get("trackid"));
			
			return result;
			
		
		}catch (GWPayException e) {
			throw e;
		}catch (AdquirenteException e) {
			throw e;
		}
		catch (Exception e) {
			
			//@EXCEPTION CRIAR CÓDIGO
			GWPayException exception = new GWPayException("Erro de conexão.");
			exception.setInfoFault("GW00", "Erro de conexão" , "Ocorreu um erro de conexão no sistema GWPay." , "Favor entar em contato com a GWPay");
			throw exception;
		}
	}

	@Override
	public ResultadoWS realizarCreditoConfirmacao(Parametros params) throws AdquirenteException, GWPayException {
		MPIPlugin plugin = new MPIPlugin();
	//############################################ CAMPOS OBRIGATORIOS ############################################################	
		// ### Verifica campos obrigatórios
		if((params.getCodCliente().equals("") || params.getCodCliente() == null) ||
			(params.getCodGWPay().equals("") || params.getCodGWPay() == null) ||
			(params.getCodNSU().equals("") || params.getCodNSU() == null) ||
			(params.getValor() == 0)){
			
			GWPayException exception = new GWPayException("Parâmetros Obrigatórios.");
			exception.setInfoFault("GW00", "Há um ou mais Parâmetros obrigatórios faltando." , "Há um ou mais Parâmetros obrigatórios faltando." , "Favor verificar os parâmetros");
			throw exception;
			
		}
	//############################################ DADOS ############################################################	
		String terminalIdComposto = "";
		HashMap<String, String> camposRetornoTerminal = new HashMap<>();
		int bandeiraId = 0;
		HashMap<String, String> camposRetorno = new HashMap<>();
		HashMap<String, String> camposRetornoBandeira = new HashMap<>();
		int tipoTransacaoId = 0;
		int clienteId = 0;
		
		//### TRY TRATA AS EXCECOES SQL E DE ERRO DE CONEXAO DO PLUGIN ###
		try {
			HistoricoTransacaoDao hDao = new HistoricoTransacaoDao();
			camposRetornoBandeira = hDao.getBandeiraTransacao(params.getCodNSU());
			
			
			if(camposRetornoBandeira.get("bandeira_id").equals("")){
				//@EXCEPTION CRIAR CÓDIGO
				GWPayException exception = new GWPayException("Parâmetro Incorreto.");
				exception.setInfoFault("GW04", "Parâmetro codNSU incorreto ou transação não existe." , "Parâmetro codNSU incorreto ou transação não existe." , "Favor verificar o código codNSU");
				throw exception;
			}
			
			// ### Busca os dados de terminal ID no banco de dados ####
			OperacaoDao oDao = new OperacaoDao();
			camposRetornoTerminal = oDao.getDadosTerminalId(camposRetornoBandeira.get("nome"), "CREDITO", "GETNET");
		
			bandeiraId = Integer.parseInt(camposRetornoBandeira.get("bandeira_id"));
			
			// ### Busca o ID do tipoTransacao no banco de dados ####	
			TipoTransacaoDao trDao = new TipoTransacaoDao();
			tipoTransacaoId =  trDao.getTipoTransacaoId("CAPTURA");
			
			// ### Busca o ID do cliente no banco de dados ####	
			ClienteDao cDao = new ClienteDao();
			clienteId = cDao.getClienteId(params.getCodGWPay());
			
			// ### VERIFICACOES DE SEGURANCA ###
			if(clienteId == 0){
				//@EXCEPTION CRIAR CÓDIGO
				GWPayException exception = new GWPayException("Parâmetro Incorreto.");
				exception.setInfoFault("GW02", "Parâmetro codGWPay incorreto ou cliente não existe." , "Paramêtro codGWPay incorreto ou cliente não existe." , "Favor verificar seu código GWPay");
				throw exception;
			}
			
			
			if (!camposRetornoTerminal.containsKey("sufixo")) {
				//@EXCEPTION CRIAR CÓDIGO
				GWPayException exception = new GWPayException("Parâmetro Incorreto.");
				exception.setInfoFault("GW01", "Parâmetro incorreto." , "Paramêtro bandeira incorreto." , "Favor verificar parametro");
				throw exception;
			}
			
			// ### Seta o terminal ID composto
			terminalIdComposto = camposRetornoTerminal.get("prefixo") + params.getCodCliente() + camposRetornoTerminal.get("sufixo");
			System.out.println("terminalIdComposto: " + terminalIdComposto);
			params.setCodCliente(terminalIdComposto);
			
		//############################################ PLUGIN ############################################################	
		
			// ### EXECUTA METODO DO PLUGIN ###
			camposRetorno = plugin.realizarCreditoConfirmacao(params);
		
		//############################################ HISTORICO ############################################################	
	
			//### SALVA TRANSACAO NO BANCO DE DADOS ###
			HistoricoTransacao transacao = new HistoricoTransacao();
			transacao.setCodCliente(terminalIdComposto);
			transacao.setCodRastreio(params.getCodRastreio());
			transacao.setValor(params.getValor());
			transacao.setMoeda("986");			

			
			if(camposRetorno.containsKey("tranid")){
				transacao.setCodTransacaoOriginal(camposRetorno.get("tranid"));
				transacao.setCodNSU(camposRetorno.get("tranid"));
				transacao.setDescricaoResposta(camposRetorno.get("result"));
				transacao.setCodResposta(camposRetorno.get("responsecode"));
				
			}else{
				System.out.println("zerandooo");
				transacao.setCodNSU("");
				transacao.setDescricaoResposta("");
				transacao.setCodResposta("");
				transacao.setCodTransacaoOriginal(params.getCodNSU());
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
			
			// ### Não é cancelamento tipo = 0 ###
			transacao.setTipoCancelamentoId(0);
			
			HistoricoTransacaoDao dao = new HistoricoTransacaoDao();
			dao.inserirHistoricoTransacao(transacao);
		
		//############################################ RESULTADO ############################################################	
	
			ResultadoWS result = new ResultadoWS();
			// ### SE HOUVER ERRO RETORNA EXCECAO ADQUIRENTE ###
			if(camposRetorno.containsKey("error_code") && camposRetorno.get("error_code").equals("erroPerform")){
				ErroAdquirenteDao eDao = new ErroAdquirenteDao();
				HashMap camposRetornoErro = eDao.getDadosErro(camposRetorno.get("error_code"), "GETNET");
				
				AdquirenteException exception = new AdquirenteException("Erro na chamada do serviço Adquirente");
				exception.setInfoFault(camposRetorno.get("error_code"), camposRetorno.get("error_text") , camposRetornoErro.get("descricao").toString() , camposRetornoErro.get("acao").toString() );
				throw exception;
		
			}else if(camposRetorno.containsKey("error_code") && !camposRetorno.get("error_code").equals("")){
				
				ErroAdquirenteDao eDao = new ErroAdquirenteDao();
				HashMap camposRetornoErro = eDao.getDadosErro(camposRetorno.get("error_code"), "GETNET");
				
				AdquirenteException exception = new AdquirenteException("Erro na chamada do serviço Adquirente");
				exception.setInfoFault(camposRetorno.get("error_code"), camposRetorno.get("error_text") , camposRetornoErro.get("descricao").toString() , camposRetornoErro.get("acao").toString() );
				throw exception;
		
			}
			
			// colocar descricao vinda do banco
			result.setCodigoResposta(camposRetorno.get("responsecode"));
			result.setMensagemResposta(camposRetorno.get("result"));
			result.setDescricaoResposta(camposRetorno.get("Descricao vinda do banco"));
			
			result.setCodigoNSU(camposRetorno.get("tranid"));
			result.setCodigoRastreio(camposRetorno.get("trackid"));
			
			return result;
			
		
		}catch (GWPayException e) {
			throw e;
		}catch (AdquirenteException e) {
			throw e;
		}
		catch (Exception e) {
			e.printStackTrace();
			//@EXCEPTION CRIAR CÓDIGO
			GWPayException exception = new GWPayException("Erro de conexão.");
			exception.setInfoFault("GW00", "Erro de conexão" , "Ocorreu um erro de conexão no sistema GWPay." , "Favor entar em contato com a GWPay");
			throw exception;
		}
		
	}

	@Override
	public String realizarDebito() {
		return null;
	}

	@Override
	public ResultadoWS realizarConsultaTransacao(Parametros params) throws AdquirenteException, GWPayException{
		
		MPIPlugin plugin = new MPIPlugin();
	//############################################ CAMPOS OBRIGATÓRIOS ###########################################	

		// ### Verifica campos obrigatórios
		if((params.getCodCliente().equals("") || params.getCodCliente() == null) ||
			(params.getCodGWPay().equals("") || params.getCodGWPay() == null) ||
			(params.getCodNSU().equals("") || params.getCodNSU() == null)){
			
			GWPayException exception = new GWPayException("Parâmetros Obrigatórios.");
			exception.setInfoFault("GW00", "Há um ou mais Parâmetros obrigatórios faltando." , "Há um ou mais Parâmetros obrigatórios faltando." , "Favor verificar os parâmetros");
			throw exception;
			
		}
	//############################################ DADOS ####################################################	

		String terminalIdComposto = "";
		HashMap<String, String> camposRetornoTerminal = new HashMap<>();
		HashMap<String, String> camposRetorno = new HashMap<>();
		String dataTransacaoS = "";
		Timestamp dataTransacao = null;
		
		//### TRY TRATA AS EXCECOES SQL E DE ERRO DE CONEXAO DO PLUGIN ###
		try {
			
			// ### Busca a data da transação
			HistoricoTransacaoDao hDao = new HistoricoTransacaoDao();
			dataTransacao = hDao.getDataTransacao(params.getCodNSU());
			if(dataTransacao != null){
				dataTransacaoS = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(dataTransacao);
			}
			
		
			// ### Busca os dados de terminal ID no banco de dados ####
			OperacaoDao oDao = new OperacaoDao();
			camposRetornoTerminal = oDao.getDadosTerminalId(params.getBandeira(), "CREDITO", "GETNET");
			
			
			if (!camposRetornoTerminal.containsKey("sufixo")) {
				//@EXCEPTION CRIAR CÓDIGO
				GWPayException exception = new GWPayException("Parâmetro Incorreto.");
				exception.setInfoFault("GW01", "Parâmetro incorreto." , "Paramêtro bandeira incorreto." , "Favor verificar parametro");
				throw exception;
			}
			
			// ### Seta o terminal ID composto
			terminalIdComposto = camposRetornoTerminal.get("prefixo") + params.getCodCliente() + camposRetornoTerminal.get("sufixo");
			System.out.println("terminalIdComposto: " + terminalIdComposto);
			params.setCodCliente(terminalIdComposto);
			
		//############################################ PLUGIN ####################################################		

			// ### EXECUTA METODO DO PLUGIN ###
			camposRetorno = plugin.realizarConsultaTransacao(params);
		
		//############################################ RESULTADO ####################################################
			
			ResultadoWS result = new ResultadoWS();
			// ### SE HOUVER ERRO RETORNA EXCECAO ADQUIRENTE ###
			if(camposRetorno.containsKey("error_code") && camposRetorno.get("error_code").equals("erroPerform")){
				ErroAdquirenteDao eDao = new ErroAdquirenteDao();
				HashMap camposRetornoErro = eDao.getDadosErro(camposRetorno.get("error_code"), "GETNET");
				
				AdquirenteException exception = new AdquirenteException("Erro na chamada do serviço Adquirente");
				exception.setInfoFault(camposRetorno.get("error_code"), camposRetorno.get("error_text") , camposRetornoErro.get("descricao").toString() , camposRetornoErro.get("acao").toString() );
				throw exception;
		
			}else if(camposRetorno.containsKey("error_code") && !camposRetorno.get("error_code").equals("")){
				
				ErroAdquirenteDao eDao = new ErroAdquirenteDao();
				HashMap camposRetornoErro = eDao.getDadosErro(camposRetorno.get("error_code"), "GETNET");
				
				AdquirenteException exception = new AdquirenteException("Erro na chamada do serviço Adquirente");
				exception.setInfoFault(camposRetorno.get("error_code"), camposRetorno.get("error_text") , camposRetornoErro.get("descricao").toString() , camposRetornoErro.get("acao").toString() );
				throw exception;
		
			}
			
			// colocar descricao vinda do banco
			result.setCodigoResposta(camposRetorno.get("responsecode"));
			result.setMensagemResposta(camposRetorno.get("result"));
			result.setDescricaoResposta(camposRetorno.get("Descricao vinda do banco"));
			result.setDataTransacao(dataTransacaoS);
			result.setCodigoNSU(camposRetorno.get("tranid"));
			result.setCodigoRastreio(camposRetorno.get("trackid"));
			
			return result;
			
		
		}catch (GWPayException e) {
			throw e;
		}catch (AdquirenteException e) {
			throw e;
		}
		catch (Exception e) {
			e.printStackTrace();
			//@EXCEPTION CRIAR CÓDIGO
			GWPayException exception = new GWPayException("Erro de conexão.");
			exception.setInfoFault("GW00", "Erro de conexão" , "Ocorreu um erro de conexão no sistema GWPay." , "Favor entar em contato com a GWPay");
			throw exception;
		}
		
		
		
	}

	@Override
	public ResultadoWS realizarCancelamento(Parametros params) throws AdquirenteException, GWPayException {
		return null;
	}

	@Override
	public String realizarCreditoAutenticacao() {
		// TODO Auto-generated method stub
		return null;
	}

}
