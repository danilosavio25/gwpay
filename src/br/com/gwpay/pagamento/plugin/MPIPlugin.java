package br.com.gwpay.pagamento.plugin;

import java.net.URL;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.aciworldwide.commerce.gateway.plugins.UniversalPlugin;

import br.com.gwpay.pagamento.model.Parametros;
import br.com.gwpay.pagamento.model.ParametrosAutenticacao;
import br.com.gwpay.pagamento.model.ParametrosAutorizacao;

public class MPIPlugin extends HttpServlet{
	
	
	public static void main(String[] args) {
		/*MPIPlugin m = new MPIPlugin();
		Parametros params = new Parametros();
		params.setCodCliente("D087729101");
		params.setCodNSU("	");
		params.setValor(300);
	

		m.realizarConsultaTransacao(params);
		//m.getPath();
		
		ParametrosAutorizacao params = new ParametrosAutorizacao();
		params.setCodCliente("D087729101");
		params.setNumCartao("4012001038166662");
		params.setCodSegurancaCartao("456");
		params.setAnoVencimento(2017);
		params.setMesVencimento(04) ;
		params.setNomePortador("JOAO SOUZA");
		params.setValor(200);
		params.setCodRastreio("000000011");
		params.setTipoParcelamento("SGL");
		params.setNumParcelas(1);
		
		m.realizarCreditoCompleto(params);*/
		
		MPIPlugin m = new MPIPlugin();
		System.out.println(m.getPath());
		
		ParametrosAutorizacao params = new ParametrosAutorizacao();
		params.setCodCliente("D087729101");
		params.setCodNSU("463730071653131");
		params.setValor(200);
		params.setCodRastreio("000000011");
		
		m.realizarConsultaTransacao(params);
	}
	
	public HashMap realizarConsultaTransacao(Parametros params){
		//D087729102

		
		String path = getPath();
		
		UniversalPlugin plugin = new UniversalPlugin();
		
		plugin.setResourcePath(path);
		plugin.setTerminalAlias(params.getCodCliente());
		plugin.setTransactionType("TranPortal");
		plugin.setVersion("1");
		plugin.set("action" , "8");
		plugin.set("type" , "");
		plugin.set("transid" , params.getCodNSU());
		plugin.set("trackid" , params.getCodRastreio());
		plugin.set("currencycode" , "986");
		
		HashMap camposRetorno = new HashMap<String, String>();
		
		if(!plugin.performTransaction()){
			System.out.println( "Erro : " + plugin.getErrorText() );
			camposRetorno.put("error_code", "erroPerform");
			camposRetorno.put("error_text", plugin.getErrorText());
			return camposRetorno;
		}else{
			String error_code = plugin.get("error_code_tag");
			String error_text = plugin.get("error_text");
			if(error_code != null && error_code.length() > 0 ){
				System.out.println( "CodigoErro : " + error_code );
				System.out.println( "Mensagem : " + error_text );
				camposRetorno.put("error_code", error_code);
				camposRetorno.put("error_text", error_text);
				return camposRetorno;
				
			} else{
				System.out.println( "Codigo Resposta : " + plugin.get("result") );
				System.out.println( "Resposta : " + plugin.get("responsecode"));
				System.out.println( "tranid : " + plugin.get("tranid"));
				System.out.println(plugin.getResponseFields());
				
				camposRetorno.put("result", plugin.get("result"));
				camposRetorno.put("responsecode", plugin.get("responsecode"));
				camposRetorno.put("otranid", plugin.get("otranid"));
				camposRetorno.put("tranid", plugin.get("tranid"));
				camposRetorno.put("trackid", plugin.get("trackid"));
				camposRetorno.put("postdate", plugin.get("postdate"));
				
				return camposRetorno;
				
			}
		}
		
		
	}
	
	public HashMap realizarCreditoCompleto(ParametrosAutorizacao params){

		System.out.println(params.getCodCliente());
		System.out.println(params.getNumCartao());
		System.out.println(params.getCodSegurancaCartao());
		System.out.println(params.getAnoVencimento());
		System.out.println(params.getMesVencimento() );
		System.out.println(params.getNomePortador());
		System.out.println(params.getValor());
		System.out.println(params.getCodRastreio());
		System.out.println(params.getTipoParcelamento());
		System.out.println(params.getNumParcelas());

		
		String path = getPath();
		
		//String path = "\\resourceGetNet";
		System.out.println(path);
		
		UniversalPlugin plugin = new UniversalPlugin();
		plugin.setResourcePath(path);
		plugin.setTerminalAlias(params.getCodCliente());
		plugin.setTransactionType("TranPortal");
		plugin.setVersion("1");
		plugin.set("card" , params.getNumCartao());
		plugin.set("cvv2" , params.getCodSegurancaCartao());
		plugin.set("expyear" , params.getAnoVencimento() + "");
		plugin.set("expmonth" , params.getMesVencimento() + "");
		plugin.set("action" , "1");
		plugin.set("type" , "CC");
		plugin.set("transid" , "");
		plugin.set("member" , params.getNomePortador());
		plugin.set("amt" , params.getValor() + "");
		plugin.set("currencycode" , "986");
		plugin.set("trackid" , params.getCodRastreio());
		plugin.set("instType" , params.getTipoParcelamento());
		plugin.set("instNum" , params.getNumParcelas() + "");
		
		HashMap camposRetorno = new HashMap<String, String>();
		
		if(!plugin.performTransaction()){
			System.out.println( "Erro : " + plugin.getErrorText() );
			camposRetorno.put("error_code", "erroPerform");
			camposRetorno.put("error_text", plugin.getErrorText());
			return camposRetorno;
		}else{
			String error_code = plugin.get("error_code_tag");
			String error_text = plugin.get("error_text");
			if(error_code != null && error_code.length() > 0 ){
				System.out.println( "CodigoErro : " + error_code );
				System.out.println( "Mensagem : " + error_text );
				camposRetorno.put("error_code", error_code);
				camposRetorno.put("error_text", error_text);
				return camposRetorno;
				
			} else{
				System.out.println( "Codigo Resposta : " + plugin.get("result") );
				System.out.println( "Resposta : " + plugin.get("responsecode"));
				System.out.println( "tranid : " + plugin.get("tranid"));
				System.out.println(plugin.getResponseFields());
				
				camposRetorno.put("result", plugin.get("result"));
				camposRetorno.put("responsecode", plugin.get("responsecode"));
				camposRetorno.put("otranid", plugin.get("otranid"));
				camposRetorno.put("tranid", plugin.get("tranid"));
				camposRetorno.put("trackid", plugin.get("trackid"));
				camposRetorno.put("postdate", plugin.get("postdate"));
				
				return camposRetorno;
				
			}
		}
		
	}

	
	public HashMap realizarCreditoAutorizacao(ParametrosAutorizacao params){
	
		String path = getPath();
		System.out.println(path);
		UniversalPlugin plugin = new UniversalPlugin();
		plugin.setResourcePath(path);
		
		plugin.setTerminalAlias(params.getCodCliente());
		plugin.setTransactionType("TranPortal");
		plugin.setVersion("1");
		plugin.set("card" , params.getNumCartao());
		plugin.set("cvv2" , params.getCodSegurancaCartao());
		plugin.set("expyear" , params.getAnoVencimento() + "");
		plugin.set("expmonth" , params.getMesVencimento() + "");
		plugin.set("action" , "4");
		plugin.set("type" , "CC");
		plugin.set("transid" , "");
		plugin.set("member" , params.getNomePortador());
		plugin.set("amt" , params.getValor() + "");
		plugin.set("currencycode" , "986");
		plugin.set("trackid" , params.getCodRastreio());
		plugin.set("instType" , params.getTipoParcelamento());
		plugin.set("instNum" , params.getNumParcelas() + "");
		
		
		HashMap camposRetorno = new HashMap<String, String>();
		
		if(!plugin.performTransaction()){
			System.out.println( "Erro : " + plugin.getErrorText() );
			camposRetorno.put("error_code", "erroPerform");
			camposRetorno.put("error_text", plugin.getErrorText());
			return camposRetorno;
		}else{
			String error_code = plugin.get("error_code_tag");
			String error_text = plugin.get("error_text");
			if(error_code != null && error_code.length() > 0 ){
				System.out.println( "CodigoErro : " + error_code );
				System.out.println( "Mensagem : " + error_text );
				camposRetorno.put("error_code", error_code);
				camposRetorno.put("error_text", error_text);
				return camposRetorno;
				
			} else{
				System.out.println( "Codigo Resposta : " + plugin.get("result") );
				System.out.println( "Resposta : " + plugin.get("responsecode"));
				System.out.println( "tranid : " + plugin.get("tranid"));
				System.out.println(plugin.getResponseFields());
				
				camposRetorno.put("result", plugin.get("result"));
				camposRetorno.put("responsecode", plugin.get("responsecode"));
				camposRetorno.put("otranid", plugin.get("otranid"));
				camposRetorno.put("tranid", plugin.get("tranid"));
				camposRetorno.put("trackid", plugin.get("trackid"));
				camposRetorno.put("postdate", plugin.get("postdate"));
				
				return camposRetorno;
				
			}
		}
		
	}

	
	public HashMap realizarCreditoConfirmacao(Parametros params){
		
		String path = getPath();
		
		UniversalPlugin plugin = new UniversalPlugin();
		plugin.setResourcePath(path);
		plugin.setTerminalAlias(params.getCodCliente());
		plugin.setTransactionType("TranPortal");
		plugin.setVersion("1");
		plugin.set("action" , "5");
		plugin.set("type" , "CC");
		plugin.set("transid" , params.getCodNSU());
		plugin.set("amt" , params.getValor() + "");
		plugin.set("currencycode" , "986");
		plugin.set("trackid" , params.getCodRastreio());
		
		HashMap camposRetorno = new HashMap<String, String>();
		
		if(!plugin.performTransaction()){
			System.out.println( "Erro : " + plugin.getErrorText() );
			camposRetorno.put("error_code", "erroPerform");
			camposRetorno.put("error_text", plugin.getErrorText());
			return camposRetorno;
		}else{
			String error_code = plugin.get("error_code_tag");
			String error_text = plugin.get("error_text");
			if(error_code != null && error_code.length() > 0 ){
				System.out.println( "CodigoErro : " + error_code );
				System.out.println( "Mensagem : " + error_text );
				camposRetorno.put("error_code", error_code);
				camposRetorno.put("error_text", error_text);
				return camposRetorno;
				
			} else{
				System.out.println( "Codigo Resposta : " + plugin.get("result") );
				System.out.println( "Resposta : " + plugin.get("responsecode"));
				System.out.println( "tranid : " + plugin.get("tranid"));
				System.out.println(plugin.getResponseFields());
				
				camposRetorno.put("result", plugin.get("result"));
				camposRetorno.put("responsecode", plugin.get("responsecode"));
				camposRetorno.put("otranid", plugin.get("otranid"));
				camposRetorno.put("tranid", plugin.get("tranid"));
				camposRetorno.put("trackid", plugin.get("trackid"));
				camposRetorno.put("postdate", plugin.get("postdate"));
				
				return camposRetorno;
				
			}
		}
	
	}
	
	public HashMap realizarDebito(ParametrosAutenticacao params){
		
		String path = getPath();
		
		UniversalPlugin plugin = new UniversalPlugin();
		plugin.setResourcePath(path);
		plugin.setTerminalAlias("E087729003");
		plugin.setTransactionType("MPIVerifyEnrollment");
		plugin.setVersion("1");
		plugin.set("card" , "4012001038166662");
		plugin.set("cvv2" , "456");
		plugin.set("expyear" , "2017");
		plugin.set("expmonth" , "04");
		plugin.set("action" , "1");
		plugin.set("type" , "VPASD");
		plugin.set("transid" , "");
		plugin.set("zip" , "010100100");
		plugin.set("addr" , "xxx");
		plugin.set("member" , "ANTONIO NUNES SILVA");
		plugin.set("amt" , "200");
		plugin.set("currencycode" , "986");
		plugin.set("trackid" , "");
		plugin.set("instType" , "");
		plugin.set("instNum" , "");
		if(true) {
			plugin.set("brazilMobileNumber" , "5511999999999");
			plugin.set("brazilTranType" , "00");
			plugin.set("brazilAccountType" , "02");
		}
		
		
	HashMap camposRetorno = new HashMap<String, String>();
		
		if(!plugin.performTransaction()){
			System.out.println( "Erro : " + plugin.getErrorText() );
			camposRetorno.put("error_code", "erroPerform");
			camposRetorno.put("error_text", plugin.getErrorText());
			return camposRetorno;
		}else{
			String error_code = plugin.get("error_code_tag");
			String error_text = plugin.get("error_text");
			if(error_code != null && error_code.length() > 0 ){
				System.out.println( "CodigoErro : " + error_code );
				System.out.println( "Mensagem : " + error_text );
				camposRetorno.put("error_code", error_code);
				camposRetorno.put("error_text", error_text);
				return camposRetorno;
				
			} else{
				System.out.println( "Codigo Resposta : " + plugin.get("result") );
				System.out.println( "Resposta : " + plugin.get("responsecode"));	
				System.out.println( "tranid : " + plugin.get("tranid"));
				System.out.println(plugin.getResponseFields());
				
			
				return camposRetorno;
				
			}
		}
		
	}
	
	public void realizarCreditoAutenticacao(ParametrosAutenticacao params){
		
	/*	String transId = autorizacao(trackId);
		
			String path = getPath();
		
		UniversalPlugin plugin = new UniversalPlugin();
		plugin.setResourcePath(path);
		plugin.setTerminalAlias("D087729104");
		plugin.setTransactionType("TranPortal");
		plugin.setVersion("1");
		plugin.set("action" , "5");
		plugin.set("type" , "");
		plugin.set("transid" , transId);
		plugin.set("trackid" , "");
		plugin.set("amt" , "200.00");
		plugin.set("currencycode", "986");
		
		if(!plugin.performTransaction()){
			System.out.println( "Erro Conf: " + plugin.getErrorText() );
		}else{
			String error_code = plugin.get("error_code_tag");
			String error_text = plugin.get("error_text");
			if(error_code != null && error_code.length() > 0 ){
				System.out.println( "C�digo Erro Conf: " + error_code );
				System.out.println( "Mensagem Conf: " + error_text );
			} else{
				System.out.println( "C�digo Resposta Conf: " + plugin.get("result") );
				System.out.println( "Resposta Conf: " + plugin.get("responsecode"));
				System.out.println("GetFields Conf: " + plugin.getResponseFields());

			}
		}*/
	}
	
	public HashMap realizarCancelamento(Parametros params){
		
		String path = getPath();
		
		UniversalPlugin plugin = new UniversalPlugin();
		plugin.setResourcePath(path);
		plugin.setTerminalAlias(params.getCodCliente());
		plugin.setTransactionType("TranPortal");
		plugin.setVersion("1");
		plugin.set("action" , "9");
		plugin.set("type" , "");
		plugin.set("transid" , params.getCodNSU());
		plugin.set("trackid" , params.getCodRastreio());
		plugin.set("amt" , params.getValor() + "");
		plugin.set("currencycode" , "986");
		
		HashMap camposRetorno = new HashMap<String, String>();
		
		if(!plugin.performTransaction()){
			System.out.println( "Erro : " + plugin.getErrorText() );
			camposRetorno.put("error_code", "erroPerform");
			camposRetorno.put("error_text", plugin.getErrorText());
			return camposRetorno;
		}else{
			String error_code = plugin.get("error_code_tag");
			String error_text = plugin.get("error_text");
			if(error_code != null && error_code.length() > 0 ){
				System.out.println( "CodigoErro : " + error_code );
				System.out.println( "Mensagem : " + error_text );
				camposRetorno.put("error_code", error_code);
				camposRetorno.put("error_text", error_text);
				return camposRetorno;
				
			} else{
				System.out.println( "Codigo Resposta : " + plugin.get("result") );
				System.out.println( "Resposta : " + plugin.get("responsecode"));
				System.out.println( "tranid : " + plugin.get("tranid"));
				System.out.println(plugin.getResponseFields());
				
				camposRetorno.put("result", plugin.get("result"));
				camposRetorno.put("responsecode", plugin.get("responsecode"));
				camposRetorno.put("otranid", plugin.get("otranid"));
				camposRetorno.put("tranid", plugin.get("tranid"));
				camposRetorno.put("trackid", plugin.get("trackid"));
				camposRetorno.put("postdate", plugin.get("postdate"));
				
				return camposRetorno;
				
			}
		}
	}
	
	public String getPath(){
	/*	MPIPlugin mpi = new MPIPlugin();
		ServletContext context = mpi.getServletContext();
		String fullPath = context.getRealPath("/WEB-INF/resource.cgn");
		System.out.println("fullPath " + fullPath);*/
		//System.out.println("url " + url);
		
		//System.out.println(System.getProperty("jboss.server.base.dir"));
	//	String sUrl = System.getProperty("jboss.server.base.dir") + "\\deployments";
		
		/*String path = System.getProperty("jboss.home.dir");
	//	sUrl = path + "\\standalone\\deployments\\pagamentows.war\\WEB-INF\\classes\\br\\com\\gwpay\\pagamento\\plugin";

		sUrl = path + "/standalone/deployments/pagamentows.war/WEB-INF/classes/br/com/gwpay/pagamento/plugin/";
		if(sUrl.contains("resource.cgn")){
			sUrl = sUrl.replace("resource.cgn", "");
		}
		System.out.println(path);*/
	//	URL url = this.getClass().getResource("");
		//String sUrl = url.getPath();
		
		
		String sUrl = "\\resourceGetNet";
		
		System.out.println("sUrl:" +  sUrl);
		return sUrl;
	}
	
}

