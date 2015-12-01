package br.com.gwpay.pagamento.service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import br.com.gwpay.pagamento.dao.AdquirenteDao;
import br.com.gwpay.pagamento.dao.BandeiraDao;
import br.com.gwpay.pagamento.dao.ClienteDao;
import br.com.gwpay.pagamento.dao.ErroAdquirenteDao;
import br.com.gwpay.pagamento.dao.HistoricoTransacaoDao;
import br.com.gwpay.pagamento.dao.HistoricoTransacaoErroDao;
import br.com.gwpay.pagamento.dao.OperacaoDao;
import br.com.gwpay.pagamento.dao.SessaoDao;
import br.com.gwpay.pagamento.dao.TipoCancelamentoDao;
import br.com.gwpay.pagamento.dao.TipoTransacaoDao;
import br.com.gwpay.pagamento.dao.UsuarioDao;
import br.com.gwpay.pagamento.exception.AdquirenteException;
import br.com.gwpay.pagamento.exception.GWPayException;
import br.com.gwpay.pagamento.model.HistoricoTransacao;
import br.com.gwpay.pagamento.model.HistoricoTransacaoErro;
import br.com.gwpay.pagamento.model.Parametros;
import br.com.gwpay.pagamento.model.ParametrosAutorizacao;
import br.com.gwpay.pagamento.model.ResultadoWS;
import br.com.gwpay.pagamento.model.Sessao;
import br.com.gwpay.pagamento.plugin.MPIPlugin;

public class GetNetService implements IPagamentoWS{

	@Override
	public ResultadoWS realizarCreditoCompleto(ParametrosAutorizacao params)  throws AdquirenteException, GWPayException{
		
		MPIPlugin plugin = new MPIPlugin();
		
		//############################################ CAMPOS OBRIGATÓRIOS ###########################################	
			// ### Verifica campos obrigatórios
			validarCamposObrigatoriosAutorizacao(params);
		//############################################ DADOS ############################################################
			String terminalIdComposto = "";
			HashMap<String, String> camposRetornoTerminal = new HashMap<>();
			int bandeiraId = 0;
			HashMap<String, String> camposRetorno = new HashMap<>();
			int tipoTransacaoId = 0;
			int clienteId = 0;
			int adquirenteId = 0;
			
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
				
				// ### Busca o ID do adquirente no banco de dados ####	
				AdquirenteDao aDao = new AdquirenteDao();
				adquirenteId = aDao.getAdquirenteId("GETNET");
				
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
				camposRetorno = plugin.realizarCreditoCompleto(params);
			//############################################ RESULTADO ############################################################

				ResultadoWS result = gerarResultadoAutorizacao(camposRetorno);
				
			//############################################ HISTORICO ############################################################	
				//### SALVA TRANSACAO NO BANCO DE DADOS ###
				HistoricoTransacao transacao = gerarHistoricoAutorizacao(params, terminalIdComposto, camposRetorno, tipoTransacaoId, clienteId, bandeiraId, adquirenteId);
				
				HistoricoTransacaoDao dao = new HistoricoTransacaoDao();
				dao.inserirHistoricoTransacao(transacao);	
				
				return result;
			
			}catch (GWPayException e) {
				
				// ##### Salva histórico de erros ########
				HistoricoTransacaoErro transacao = gerarHistoricoErroAutorizacao(params, terminalIdComposto, camposRetorno, tipoTransacaoId, clienteId, bandeiraId,  adquirenteId);
				transacao.setCodErroGateway(e.getFaultInfo().getCodigoErro());
				transacao.setDescricaoErroGateway(e.getFaultInfo().getDescricaoErro());
				HistoricoTransacaoErroDao eDao =  new HistoricoTransacaoErroDao();
				eDao.inserirHistoricoTransacaoErro(transacao);
				
				throw e;
			}catch (AdquirenteException e) {
				
				// ##### Salva histórico de erros ########
				HistoricoTransacaoErro transacao = gerarHistoricoErroAutorizacao(params, terminalIdComposto, camposRetorno, tipoTransacaoId, clienteId, bandeiraId , adquirenteId);
				HistoricoTransacaoErroDao eDao =  new HistoricoTransacaoErroDao();
				eDao.inserirHistoricoTransacaoErro(transacao);
				
				throw e;
			}
			catch (Exception e) {
				
				//@EXCEPTION CRIAR CÓDIGO
				GWPayException exception = new GWPayException("Erro de conexão.");
				exception.setInfoFault("GW00", "Erro de conexão" , "Ocorreu um erro de conexão no sistema GWPay." , "Caso o erro persista, favor entrar em contato com a GWPay");
				
				// ##### Salva histórico de erros ########
				HistoricoTransacaoErro transacao = gerarHistoricoErroAutorizacao(params, terminalIdComposto, camposRetorno, tipoTransacaoId, clienteId, bandeiraId, adquirenteId);
				transacao.setCodErroGateway(exception.getFaultInfo().getCodigoErro());
				transacao.setDescricaoErroGateway(exception.getFaultInfo().getDescricaoErro());
				HistoricoTransacaoErroDao eDao =  new HistoricoTransacaoErroDao();
				eDao.inserirHistoricoTransacaoErro(transacao);
				
				throw exception;
			}
	}

	@Override
	public ResultadoWS realizarCreditoAutorizacao(ParametrosAutorizacao params) throws AdquirenteException, GWPayException{
		
		MPIPlugin plugin = new MPIPlugin();
	
	//############################################ CAMPOS OBRIGATÓRIOS ###########################################	
		// ### Verifica campos obrigatórios
		validarCamposObrigatoriosAutorizacao(params);
	//############################################ DADOS ############################################################
		
		String terminalIdComposto = "";
		HashMap<String, String> camposRetornoTerminal = new HashMap<>();
		int bandeiraId = 0;
		HashMap<String, String> camposRetorno = new HashMap<>();
		int tipoTransacaoId = 0;
		int clienteId = 0;
		int adquirenteId = 0;
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
			
			// ### Busca o ID do adquirente no banco de dados ####	
			AdquirenteDao aDao = new AdquirenteDao();
			adquirenteId = aDao.getAdquirenteId("GETNET");
			
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
		//############################################ RESULTADO ############################################################
			
			ResultadoWS result = gerarResultadoAutorizacao(camposRetorno);
	
		//############################################ HISTORICO ############################################################	
			
			//### SALVA TRANSACAO NO BANCO DE DADOS ###
			HistoricoTransacao transacao = gerarHistoricoAutorizacao(params, terminalIdComposto, camposRetorno, tipoTransacaoId, clienteId, bandeiraId, adquirenteId);
			
			HistoricoTransacaoDao dao = new HistoricoTransacaoDao();
			dao.inserirHistoricoTransacao(transacao);	
			
			return result;
		
		}catch (GWPayException e) {
			System.out.println("GWPayException a");
			// ##### Salva histórico de erros ########
			HistoricoTransacaoErro transacao = gerarHistoricoErroAutorizacao(params, terminalIdComposto, camposRetorno, tipoTransacaoId, clienteId, bandeiraId, adquirenteId);
			transacao.setCodErroGateway(e.getFaultInfo().getCodigoErro());
			transacao.setDescricaoErroGateway(e.getFaultInfo().getDescricaoErro());
			HistoricoTransacaoErroDao eDao =  new HistoricoTransacaoErroDao();
			eDao.inserirHistoricoTransacaoErro(transacao);
			
			throw e;
		}catch (AdquirenteException e) {
			System.out.println("GWPayException b");
			// ##### Salva histórico de erros ########
			HistoricoTransacaoErro transacao = gerarHistoricoErroAutorizacao(params, terminalIdComposto, camposRetorno, tipoTransacaoId, clienteId, bandeiraId, adquirenteId);
			HistoricoTransacaoErroDao eDao =  new HistoricoTransacaoErroDao();
			eDao.inserirHistoricoTransacaoErro(transacao);
			
			throw e;
		}
		catch (Exception e) {
			
			//@EXCEPTION CRIAR CÓDIGO
			GWPayException exception = new GWPayException("Erro de conexão.");
			exception.setInfoFault("GW00", "Erro de conexão" , "Ocorreu um erro de conexão no sistema GWPay." , "Caso o erro persista, favor entrar em contato com a GWPay");
			
			// ##### Salva histórico de erros ########
			HistoricoTransacaoErro transacao = gerarHistoricoErroAutorizacao(params, terminalIdComposto, camposRetorno, tipoTransacaoId, clienteId, bandeiraId, adquirenteId);
			transacao.setCodErroGateway(exception.getFaultInfo().getCodigoErro());
			transacao.setDescricaoErroGateway(exception.getFaultInfo().getDescricaoErro());
			HistoricoTransacaoErroDao eDao =  new HistoricoTransacaoErroDao();
			eDao.inserirHistoricoTransacaoErro(transacao);
			
			throw exception;
		}
	}

	@Override
	public ResultadoWS realizarCreditoConfirmacao(Parametros params) throws AdquirenteException, GWPayException {
		
		MPIPlugin plugin = new MPIPlugin();
	
	//############################################ CAMPOS OBRIGATORIOS ############################################################	
		
		// ### Verifica campos obrigatórios
		validarCamposObrigatoriosConfirmacao(params);
		
	//############################################ DADOS ############################################################	
		String terminalIdComposto = "";
		HashMap<String, String> camposRetornoTerminal = new HashMap<>();
		int bandeiraId = 0;
		HashMap<String, String> camposRetorno = new HashMap<>();
		HashMap<String, String> camposRetornoBandeira = new HashMap<>();
		int tipoTransacaoId = 0;
		int clienteId = 0;
		int adquirenteId = 0;
		
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
			
			// ### Busca o ID do adquirente no banco de dados ####	
			AdquirenteDao aDao = new AdquirenteDao();
			adquirenteId = aDao.getAdquirenteId("GETNET");
			
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
		
		//############################################ RESULTADO ############################################################	
			
			ResultadoWS result = gerarResultadoConfirmacao(camposRetorno);
			
		//############################################ HISTORICO ############################################################	
			
			//### SALVA TRANSACAO NO BANCO DE DADOS ###
			HistoricoTransacao transacao = gerarHistoricoConfirmacao(params, terminalIdComposto, camposRetorno, tipoTransacaoId, clienteId, bandeiraId, adquirenteId);
			
			HistoricoTransacaoDao dao = new HistoricoTransacaoDao();
			dao.inserirHistoricoTransacao(transacao);
			
			return result;
		}catch (GWPayException e) {
			
			// ##### Salva histórico de erros ########
			HistoricoTransacaoErro transacao = gerarHistoricoErroConfirmacao(params, terminalIdComposto, camposRetorno, tipoTransacaoId, clienteId, bandeiraId, adquirenteId);
			transacao.setCodErroGateway(e.getFaultInfo().getCodigoErro());
			transacao.setDescricaoErroGateway(e.getFaultInfo().getDescricaoErro());
			HistoricoTransacaoErroDao eDao =  new HistoricoTransacaoErroDao();
			eDao.inserirHistoricoTransacaoErro(transacao);
			
			throw e;
		}catch (AdquirenteException e) {
			
			// ##### Salva histórico de erros ########
			HistoricoTransacaoErro transacao = gerarHistoricoErroConfirmacao(params, terminalIdComposto, camposRetorno, tipoTransacaoId, clienteId, bandeiraId, adquirenteId);
			HistoricoTransacaoErroDao eDao =  new HistoricoTransacaoErroDao();
			eDao.inserirHistoricoTransacaoErro(transacao);
			
			throw e;
		}
		catch (Exception e) {
			
			//@EXCEPTION CRIAR CÓDIGO
			GWPayException exception = new GWPayException("Erro de conexão.");
			exception.setInfoFault("GW00", "Erro de conexão" , "Ocorreu um erro de conexão no sistema GWPay." , "Caso o erro persista, favor entrar em contato com a GWPay");
			
			// ##### Salva histórico de erros ########
			HistoricoTransacaoErro transacao = gerarHistoricoErroConfirmacao(params, terminalIdComposto, camposRetorno, tipoTransacaoId, clienteId, bandeiraId, adquirenteId);
			transacao.setCodErroGateway(exception.getFaultInfo().getCodigoErro());
			transacao.setDescricaoErroGateway(exception.getFaultInfo().getDescricaoErro());
			HistoricoTransacaoErroDao eDao =  new HistoricoTransacaoErroDao();
			eDao.inserirHistoricoTransacaoErro(transacao);
			
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
			exception.setInfoFault("GW00", "Parâmetros obrigatórios." , "Há um ou mais Parâmetros obrigatórios faltando." , "Favor verificar os parâmetros");
			throw exception;
			
		}
		
	//############################################ DADOS ####################################################	

		String terminalIdComposto = "";
		HashMap<String, String> camposRetornoTerminal = new HashMap<>();
		HashMap<String, String> camposRetorno = new HashMap<>();
		HashMap<String, String> camposRetornoBandeira = new HashMap<>();
		String dataTransacaoS = "";
		Timestamp dataTransacao = null;
		
		//### TRY TRATA AS EXCECOES SQL E DE ERRO DE CONEXAO DO PLUGIN ###
		try {

			HistoricoTransacaoDao hDao = new HistoricoTransacaoDao();
			camposRetornoBandeira = hDao.getBandeiraTransacao(params.getCodNSU());

			
			// ### Busca a data da transação
			dataTransacao = hDao.getDataTransacao(params.getCodNSU());
			
			if(dataTransacao != null){
				dataTransacaoS = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(dataTransacao);
			}
			
		
			// ### Busca os dados de terminal ID no banco de dados ####
			OperacaoDao oDao = new OperacaoDao();
			camposRetornoTerminal = oDao.getDadosTerminalId(camposRetornoBandeira.get("nome"), "CREDITO", "GETNET");
			
			
			if (!camposRetornoTerminal.containsKey("sufixo")) {
				//@EXCEPTION CRIAR CÓDIGO
				GWPayException exception = new GWPayException("NSU Incorreto.");
				exception.setInfoFault("GW01", "CodNSU não encontrado." , "Nenhuma transação foi encontrada na base de dados para esse codNSU." , "Favor verificar codNSU");
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
			exception.setInfoFault("GW00", "Erro de conexão" , "Ocorreu um erro de conexão no sistema GWPay." , "Caso o erro persista, favor entrar em contato com a GWPay");
			throw exception;
		}
		
		
		
	}

	@Override
	public ResultadoWS realizarCancelamento(Parametros params) throws AdquirenteException, GWPayException {
		MPIPlugin plugin = new MPIPlugin();
		
		//############################################ CAMPOS OBRIGATORIOS ############################################################	
			
			// ### Verifica campos obrigatórios
			validarCamposObrigatoriosCancelamento(params);
			
		//############################################ DADOS ############################################################	
			String terminalIdComposto = "";
			HashMap<String, String> camposRetornoTerminal = new HashMap<>();
			int bandeiraId = 0;
			HashMap<String, String> camposRetorno = new HashMap<>();
			HashMap<String, String> camposRetornoBandeira = new HashMap<>();
			int tipoTransacaoId = 0;
			int clienteId = 0;
			int cancelamentoId = 0;
			int adquirenteId = 0;
			//### TRY TRATA AS EXCECOES SQL E DE ERRO DE CONEXAO DO PLUGIN ###
			try {
				HistoricoTransacaoDao hDao = new HistoricoTransacaoDao();
				camposRetornoBandeira = hDao.getBandeiraTransacaoOriginal(params.getCodNSU());
				
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
				tipoTransacaoId =  trDao.getTipoTransacaoId("CANCELAMENTO");
				
				TipoCancelamentoDao caDao = new TipoCancelamentoDao();
				cancelamentoId = caDao.getTipoCancelamentoId("WS");
				
				// ### Busca o ID do cliente no banco de dados ####	
				ClienteDao cDao = new ClienteDao();
				clienteId = cDao.getClienteId(params.getCodGWPay());
				
				// ### Busca o ID do adquirente no banco de dados ####	
				AdquirenteDao aDao = new AdquirenteDao();
				adquirenteId = aDao.getAdquirenteId("GETNET");
				
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
				camposRetorno = plugin.realizarCancelamento(params);
			
			//############################################ RESULTADO ############################################################	
				
				ResultadoWS result = gerarResultadoCancelamento(camposRetorno);
				
			//############################################ HISTORICO ############################################################	
				
				//### SALVA TRANSACAO NO BANCO DE DADOS ###
				HistoricoTransacao transacao = gerarHistoricoCancelamento(params, terminalIdComposto, camposRetorno, tipoTransacaoId, clienteId, bandeiraId, cancelamentoId, adquirenteId);
				
				HistoricoTransacaoDao dao = new HistoricoTransacaoDao();
				dao.inserirHistoricoTransacao(transacao);
				
				return result;
			}catch (GWPayException e) {
				
				// ##### Salva histórico de erros ########
				HistoricoTransacaoErro transacao = gerarHistoricoErroCancelamento(params, terminalIdComposto, camposRetorno, tipoTransacaoId, clienteId, bandeiraId, adquirenteId);
				transacao.setCodErroGateway(e.getFaultInfo().getCodigoErro());
				transacao.setDescricaoErroGateway(e.getFaultInfo().getDescricaoErro());
				HistoricoTransacaoErroDao eDao =  new HistoricoTransacaoErroDao();
				eDao.inserirHistoricoTransacaoErro(transacao);
				
				throw e;
			}catch (AdquirenteException e) {
				
				// ##### Salva histórico de erros ########
				HistoricoTransacaoErro transacao = gerarHistoricoErroCancelamento(params, terminalIdComposto, camposRetorno, tipoTransacaoId, clienteId, bandeiraId, adquirenteId);
				HistoricoTransacaoErroDao eDao =  new HistoricoTransacaoErroDao();
				eDao.inserirHistoricoTransacaoErro(transacao);
				
				throw e;
			}
			catch (Exception e) {
				
				//@EXCEPTION CRIAR CÓDIGO
				GWPayException exception = new GWPayException("Erro de conexão.");
				exception.setInfoFault("GW00", "Erro de conexão" , "Ocorreu um erro de conexão no sistema GWPay." , "Caso o erro persista, favor entrar em contato com a GWPay");
				
				// ##### Salva histórico de erros ########
				HistoricoTransacaoErro transacao = gerarHistoricoErroCancelamento(params, terminalIdComposto, camposRetorno, tipoTransacaoId, clienteId, bandeiraId, adquirenteId);
				transacao.setCodErroGateway(exception.getFaultInfo().getCodigoErro());
				transacao.setDescricaoErroGateway(exception.getFaultInfo().getDescricaoErro());
				HistoricoTransacaoErroDao eDao =  new HistoricoTransacaoErroDao();
				eDao.inserirHistoricoTransacaoErro(transacao);
				
				throw exception;
			}
			
	}

	@Override
	public String realizarCreditoAutenticacao() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	//####### MÉTODOS INTERNOS AUTORIZACAO ####################
	
	private HistoricoTransacao gerarHistoricoAutorizacao(ParametrosAutorizacao params,String terminalIdComposto, HashMap<String, String> camposRetorno, int tipoTransacaoId, int clienteId, int bandeiraId, int adquirenteId){
		
		HistoricoTransacao transacao = new HistoricoTransacao();
		transacao.setCodCliente(terminalIdComposto);
		transacao.setCodRastreio(params.getCodRastreio());
		transacao.setValor(params.getValor());
		transacao.setMoeda("986");			
		transacao.setTipoPagamento(params.getTipoParcelamento());
		transacao.setNumParcelas(params.getNumParcelas());
		transacao.setNumCartao(mascararCartao(params.getNumCartao()));
		transacao.setMesVencimentoCartao(params.getMesVencimento());
		transacao.setAnoVencimentoCartao(params.getAnoVencimento());
		transacao.setNomePortador(params.getNomePortador());
		
		if(camposRetorno.containsKey("tranid")){
			if(camposRetorno.get("otranid").equals("-1")){
				transacao.setCodTransacaoOriginal(camposRetorno.get("tranid"));
			}else{
				transacao.setCodTransacaoOriginal(camposRetorno.get("otranid"));
			}
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
		transacao.setAdquirenteId(adquirenteId);
		transacao.setCodSegurancaCartao(params.getCodSegurancaCartao());
		
		// ### Não é cancelamento tipo = 0 ###
		transacao.setTipoCancelamentoId(0);
		
		System.out.println("TRANSSSSIDDDD: " + transacao.getCodCliente());
		if(transacao.getCodNSU() == null || transacao.getCodNSU().equals("")){
			transacao.setCodNSU("");
		}
		
		return transacao;
		
	}
	
	private void validarCamposObrigatoriosAutorizacao(ParametrosAutorizacao params) throws GWPayException{
		if((params.getCodCliente().equals("") || params.getCodCliente() == null) ||
				(params.getCodGWPay().equals("") || params.getCodGWPay() == null) ||	
				(params.getValor() == 0) ||
				(params.getBandeira().equals("") || params.getBandeira() == null) ||
				(params.getNumCartao().equals("") || params.getNumCartao() == null || params.getNumCartao().length() != 16) ||
				(params.getNomePortador().equals("") || params.getNomePortador() == null) ||
				(params.getMesVencimento() == 0) ||
				(params.getAnoVencimento() == 0) ||
				(params.getCodSegurancaCartao().equals("") || params.getCodSegurancaCartao() == null)){
				
				GWPayException exception = new GWPayException("Parâmetros Obrigatórios.");
				exception.setInfoFault("GW00", "Parâmetros obrigatórios." , "Há um ou mais Parâmetros obrigatórios faltando." , "Favor verificar os parâmetros");
				// ##### Salva histórico de erros ########
				HistoricoTransacaoErro transacao = gerarHistoricoErroAutorizacao(params, null, new HashMap(), 0, 0, 0, 0);
				transacao.setCodNSU(params.getCodNSU());
				transacao.setCodErroGateway(exception.getFaultInfo().getCodigoErro());
				transacao.setDescricaoErroGateway(exception.getFaultInfo().getDescricaoErro());
				HistoricoTransacaoErroDao eDao =  new HistoricoTransacaoErroDao();
				eDao.inserirHistoricoTransacaoErro(transacao);
				
				throw exception;
				
			}
	}

	private ResultadoWS gerarResultadoAutorizacao(HashMap<String, String> camposRetorno) throws AdquirenteException{

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
		
	}

	private HistoricoTransacaoErro gerarHistoricoErroAutorizacao(ParametrosAutorizacao params,String terminalIdComposto, HashMap<String, String> camposRetorno, int tipoTransacaoId, int clienteId, int bandeiraId, int adquirenteId){
		
		HistoricoTransacaoErro transacao = new HistoricoTransacaoErro();
		transacao.setCodCliente(terminalIdComposto);
		transacao.setCodRastreio(params.getCodRastreio());
		transacao.setValor(params.getValor());
		transacao.setMoeda("986");			
		transacao.setTipoPagamento(params.getTipoParcelamento());
		transacao.setNumParcelas(params.getNumParcelas());
		transacao.setNumCartao(mascararCartao(params.getNumCartao()));
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
		transacao.setAdquirenteId(adquirenteId);
		transacao.setCodSegurancaCartao(params.getCodSegurancaCartao());
		
		// ### Não é cancelamento tipo = 0 ###
		transacao.setTipoCancelamentoId(0);
		
		System.out.println("TRANSSSSIDDDD: " + transacao.getCodCliente());
		if(transacao.getCodNSU() == null || transacao.getCodNSU().equals("")){
			transacao.setCodNSU("");
		}
		
		return transacao;
		
	}

	private String mascararCartao(String numCartao){
		
		if(numCartao == null || numCartao.equals("") || numCartao.length() != 16){
			return "";
		}
		
		String numMascarado = numCartao;
		String parteAterada = numCartao.substring(6, 12);
		numMascarado = numMascarado.replace(parteAterada, "******");
		return numMascarado;
	}
	
	//####### MÉTODOS INTERNOS CONFIRMACAO ####################
	
	private HistoricoTransacao gerarHistoricoConfirmacao(Parametros params,String terminalIdComposto, HashMap<String, String> camposRetorno, int tipoTransacaoId, int clienteId, int bandeiraId, int adquirenteId){
			
			HistoricoTransacao transacao = new HistoricoTransacao();
			transacao.setCodCliente(terminalIdComposto);
			transacao.setCodRastreio(params.getCodRastreio());
			transacao.setValor(params.getValor());
			transacao.setMoeda("986");			
			
			
			if(camposRetorno.containsKey("tranid")){
				transacao.setCodTransacaoOriginal(camposRetorno.get("otranid"));
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
			transacao.setAdquirenteId(adquirenteId);
			// ### Não é cancelamento tipo = 0 ###
			transacao.setTipoCancelamentoId(0);
			
			System.out.println("TRANSSSSIDDDD: " + transacao.getCodCliente());
			if(transacao.getCodNSU() == null || transacao.getCodNSU().equals("")){
				transacao.setCodNSU("");
			}
			
			return transacao;
			
		}
		
	private void validarCamposObrigatoriosConfirmacao(Parametros params) throws GWPayException{
		
			if((params.getCodCliente().equals("") || params.getCodCliente() == null) ||
					(params.getCodGWPay().equals("") || params.getCodGWPay() == null) ||
					(params.getCodNSU().equals("") || params.getCodNSU() == null) ||
					(params.getValor() == 0)){
					
					GWPayException exception = new GWPayException("Parâmetros Obrigatórios.");
					exception.setInfoFault("GW00", "Parâmetros obrigatórios." , "Há um ou mais Parâmetros obrigatórios faltando." , "Favor verificar os parâmetros");
					// ##### Salva histórico de erros ########
					HistoricoTransacaoErro transacao = gerarHistoricoErroConfirmacao(params, null, new HashMap(), 0, 0, 0, 0);
					transacao.setCodNSU(params.getCodNSU());
					transacao.setCodErroGateway(exception.getFaultInfo().getCodigoErro());
					transacao.setDescricaoErroGateway(exception.getFaultInfo().getDescricaoErro());
					HistoricoTransacaoErroDao eDao =  new HistoricoTransacaoErroDao();
					eDao.inserirHistoricoTransacaoErro(transacao);
					
					throw exception;
				}
		}

	private ResultadoWS gerarResultadoConfirmacao(HashMap<String, String> camposRetorno) throws AdquirenteException{

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
			
		}

	private HistoricoTransacaoErro gerarHistoricoErroConfirmacao(Parametros params,String terminalIdComposto, HashMap<String, String> camposRetorno, int tipoTransacaoId, int clienteId, int bandeiraId, int adquirenteId){
			
			HistoricoTransacaoErro transacao = new HistoricoTransacaoErro();
			transacao.setCodCliente(terminalIdComposto);
			transacao.setCodRastreio(params.getCodRastreio());
			transacao.setValor(params.getValor());
			transacao.setMoeda("986");			
			
			if(camposRetorno.containsKey("tranid")){
				transacao.setCodTransacaoOriginal(camposRetorno.get("otranid"));
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
			transacao.setAdquirenteId(adquirenteId);
			// ### Não é cancelamento tipo = 0 ###
			transacao.setTipoCancelamentoId(0);
			
			System.out.println("TRANSSSSIDDDD: " + transacao.getCodCliente());
			if(transacao.getCodNSU() == null || transacao.getCodNSU().equals("")){
				transacao.setCodNSU("");
			}
			
			return transacao;
			
		}

	//####### MÉTODOS INTERNOS CANCELAMENTO ####################
	
	private HistoricoTransacao gerarHistoricoCancelamento(Parametros params,String terminalIdComposto, HashMap<String, String> camposRetorno, int tipoTransacaoId, int clienteId, int bandeiraId, int cancelamentoId, int adquirenteId){
		
		HistoricoTransacao transacao = new HistoricoTransacao();
		transacao.setCodCliente(terminalIdComposto);
		transacao.setCodRastreio(params.getCodRastreio());
		transacao.setValor(params.getValor());
		transacao.setMoeda("986");			
		
		
		if(camposRetorno.containsKey("tranid")){
			transacao.setCodTransacaoOriginal(camposRetorno.get("otranid"));
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
		transacao.setAdquirenteId(adquirenteId);
		transacao.setTipoCancelamentoId(cancelamentoId);
		
		System.out.println("TRANSSSSIDDDD: " + transacao.getCodCliente());
		if(transacao.getCodNSU() == null || transacao.getCodNSU().equals("")){
			transacao.setCodNSU("");
		}
		
		return transacao;
		
	}
	
	private void validarCamposObrigatoriosCancelamento(Parametros params) throws GWPayException{
		
		if((params.getCodCliente().equals("") || params.getCodCliente() == null) ||
				(params.getCodGWPay().equals("") || params.getCodGWPay() == null) ||
				(params.getCodNSU().equals("") || params.getCodNSU() == null) ||
				(params.getValor() == 0)){
				
				GWPayException exception = new GWPayException("Parâmetros Obrigatórios.");
				exception.setInfoFault("GW00", "Parâmetros obrigatórios." , "Há um ou mais Parâmetros obrigatórios faltando." , "Favor verificar os parâmetros");
				// ##### Salva histórico de erros ########
				HistoricoTransacaoErro transacao = gerarHistoricoErroCancelamento(params, null, new HashMap(), 0, 0, 0 ,0);
				transacao.setCodNSU(params.getCodNSU());
				transacao.setCodErroGateway(exception.getFaultInfo().getCodigoErro());
				transacao.setDescricaoErroGateway(exception.getFaultInfo().getDescricaoErro());
				HistoricoTransacaoErroDao eDao =  new HistoricoTransacaoErroDao();
				eDao.inserirHistoricoTransacaoErro(transacao);
				
				throw exception;
			}
	}

	private ResultadoWS gerarResultadoCancelamento(HashMap<String, String> camposRetorno) throws AdquirenteException{

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
		
	}

	private HistoricoTransacaoErro gerarHistoricoErroCancelamento(Parametros params,String terminalIdComposto, HashMap<String, String> camposRetorno, int tipoTransacaoId, int clienteId, int bandeiraId, int adquirenteId){
		
		HistoricoTransacaoErro transacao = new HistoricoTransacaoErro();
		transacao.setCodCliente(terminalIdComposto);
		transacao.setCodRastreio(params.getCodRastreio());
		transacao.setValor(params.getValor());
		transacao.setMoeda("986");			
		
		if(camposRetorno.containsKey("tranid")){
			transacao.setCodTransacaoOriginal(camposRetorno.get("otranid"));
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
		transacao.setAdquirenteId(adquirenteId);
		// ### Não é cancelamento tipo = 0 ###
		transacao.setTipoCancelamentoId(0);
		
		System.out.println("TRANSSSSIDDDD: " + transacao.getCodCliente());
		if(transacao.getCodNSU() == null || transacao.getCodNSU().equals("")){
			transacao.setCodNSU("");
		}
		
		return transacao;
		
	}

	
	
}
