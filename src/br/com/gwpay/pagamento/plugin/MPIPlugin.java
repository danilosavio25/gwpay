package br.com.gwpay.pagamento.plugin;

import java.util.HashMap;

import com.aciworldwide.commerce.gateway.plugins.UniversalPlugin;

import br.com.gwpay.pagamento.exception.AdquirenteException;
import br.com.gwpay.pagamento.exception.GWPayException;
import br.com.gwpay.pagamento.model.Parametros;
import br.com.gwpay.pagamento.model.ParametrosAutenticacao;
import br.com.gwpay.pagamento.model.ParametrosAutorizacao;
import br.com.gwpay.pagamento.ws.PagamentoWS;

public class MPIPlugin {
	
	public HashMap realizarConsultaTransacao(Parametros params){
		//D087729102

		UniversalPlugin plugin = new UniversalPlugin();
		
		plugin.setResourcePath("/resourceGetNet");
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
				camposRetorno.put("tranid", plugin.get("tranid"));
				camposRetorno.put("trackid", plugin.get("trackid"));
				camposRetorno.put("postdate", plugin.get("postdate"));
				
				return camposRetorno;
				
			}
		}
		
		
	}
	
	public HashMap realizarCreditoCompleto(ParametrosAutorizacao params){
	
		
		UniversalPlugin plugin = new UniversalPlugin();
		
		plugin.setResourcePath("/resourceGetNet");
		plugin.setTerminalAlias(params.getCodCliente());
		plugin.setTransactionType("TranPortal");
		plugin.setVersion("1");
		plugin.set("card" , params.getNumCartao());
		plugin.set("cvv2" , params.getCodSegurancaCartao());
		plugin.set("expyear" , params.getAnoVencimento() + "");
		plugin.set("expmonth" , params.getMesVencimento() + "");
		plugin.set("action" , "1");
		plugin.set("type" , "");
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
				camposRetorno.put("tranid", plugin.get("tranid"));
				camposRetorno.put("trackid", plugin.get("trackid"));
				camposRetorno.put("postdate", plugin.get("postdate"));
				
				return camposRetorno;
				
			}
		}
		
	}

	
	public HashMap realizarCreditoAutorizacao(ParametrosAutorizacao params){
	
		
		UniversalPlugin plugin = new UniversalPlugin();
		
		plugin.setResourcePath("/resourceGetNet");
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
				camposRetorno.put("tranid", plugin.get("tranid"));
				camposRetorno.put("trackid", plugin.get("trackid"));
				camposRetorno.put("postdate", plugin.get("postdate"));
				
				return camposRetorno;
				
			}
		}
		
	}

	
	public HashMap realizarCreditoConfirmacao(Parametros params){
		
		UniversalPlugin plugin = new UniversalPlugin();
		
		plugin.setResourcePath("/resourceGetNet");
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
				camposRetorno.put("tranid", plugin.get("tranid"));
				camposRetorno.put("trackid", plugin.get("trackid"));
				camposRetorno.put("postdate", plugin.get("postdate"));
				
				return camposRetorno;
				
			}
		}
	
	}
	
	public HashMap realizarDebito(ParametrosAutenticacao params){
		
		UniversalPlugin plugin = new UniversalPlugin();
		plugin.setResourcePath("/resourceGetNet");
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
	
	public static void main(String[] args) {
		MPIPlugin m = new MPIPlugin();
		/*Parametros params = new Parametros();
		params.setCodCliente("D087729101");
		params.setCodNSU("1654812231453011");
		params.setValor(300);*/
				
		m.realizarDebito(null);
	}
	public void realizarCreditoAutenticacao(ParametrosAutenticacao params){
		
	/*	String transId = autorizacao(trackId);
		
		UniversalPlugin plugin = new UniversalPlugin();
		
		plugin.setResourcePath("/resources");
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
		
		UniversalPlugin plugin = new UniversalPlugin();
		
		plugin.setResourcePath("/resourceGetNet");
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
				camposRetorno.put("tranid", plugin.get("tranid"));
				camposRetorno.put("trackid", plugin.get("trackid"));
				camposRetorno.put("postdate", plugin.get("postdate"));
				
				return camposRetorno;
				
			}
		}
		
	
	}
	
}
