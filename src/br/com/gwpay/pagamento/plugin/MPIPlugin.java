package br.com.gwpay.pagamento.plugin;

import java.util.HashMap;

import com.aciworldwide.commerce.gateway.plugins.UniversalPlugin;

import br.com.gwpay.pagamento.model.Parametros;
import br.com.gwpay.pagamento.model.ParametrosAutenticacao;
import br.com.gwpay.pagamento.model.ParametrosAutorizacao;

public class MPIPlugin {
	
	public String realizarConsultaTransacao(Parametros params){
		//D087729102
		/*UniversalPlugin plugin = new UniversalPlugin();
		plugin.setResourcePath("/resourceGetNet");
		plugin.setTerminalAlias(codigoCliente);
		plugin.setTransactionType("TranPortal");
		plugin.setVersion("1");
		//plugin.set("tran sid" , "4144527330952661");
		plugin.set("trackid" , codigoRastreio);
		plugin.set("action" , "8");
		plugin.set("type" , "");
		plugin.set("currencycode", "986");
		

		if(!plugin.performTransaction()){
			System.out.println( "Erro Conf: " + plugin.getErrorText() );
			return "Erro Conf: " + plugin.getErrorText();
		}else{
			String error_code = plugin.get("error_code_tag");
			String error_text = plugin.get("error_text");
			if(error_code != null && error_code.length() > 0 ){
				System.out.println( "Código Erro Conf: " + error_code );
				System.out.println( "Mensagem Conf: " + error_text );
				return "Código Erro : " + error_code +  " Mensagem : " + error_text;
			} else{
				System.out.println( "Código Resposta Conf: " + plugin.get("result") );
				System.out.println( "Resposta Conf: " + plugin.get("responsecode"));
				System.out.println("GetFields Conf: " + plugin.getResponseFields());
				return plugin.get("tranid");
			}
		}*/
		return null;
	}
	
	public static void main(String[] args) {
		MPIPlugin plugin = new MPIPlugin();
	//	plugin.cancelamento();
	}
	
	public HashMap realizarCreditoAutorizacao(ParametrosAutorizacao params){
		
		UniversalPlugin plugin = new UniversalPlugin();
		
		plugin.setResourcePath("/resourceGetNet");
		plugin.setTerminalAlias(params.getCodCliente());
		plugin.setTransactionType("TranPortal");
		plugin.setVersion("1");
		plugin.set("card" , params.getNumCartao());
		plugin.set("cvv2" , params.getCodSegurancaCartao());
		plugin.set("expyear" , params.getAnoVencimento());
		plugin.set("expmonth" , params.getMesVencimento());
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
				System.out.println( "Código Erro : " + error_code );
				System.out.println( "Mensagem : " + error_text );
				camposRetorno.put("error_code", error_code);
				camposRetorno.put("error_text", error_text);
				return camposRetorno;
				
			} else{
				System.out.println( "Código Resposta : " + plugin.get("result") );
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
	
	
	public void realizarCreditoConfirmacao(Parametros params){
		
	/*	String transId = autorizacao(trackId);
		
		UniversalPlugin plugin = new UniversalPlugin();
		
		plugin.setResourcePath("/resources");
		plugin.setTerminalAlias("D087729102");
		plugin.setTransactionType("TranPortal");
		plugin.setVersion("1");
		plugin.set("action" , "5");
		plugin.set("type" , "");
		plugin.set("transid" , transId);
		plugin.set("trackid" , "");
		plugin.set("amt" , "100.00");
		plugin.set("currencycode", "986");
		
		if(!plugin.performTransaction()){
			System.out.println( "Erro Conf: " + plugin.getErrorText() );
		}else{
			String error_code = plugin.get("error_code_tag");
			String error_text = plugin.get("error_text");
			if(error_code != null && error_code.length() > 0 ){
				System.out.println( "Código Erro Conf: " + error_code );
				System.out.println( "Mensagem Conf: " + error_text );
			} else{
				System.out.println( "Código Resposta Conf: " + plugin.get("result") );
				System.out.println( "Resposta Conf: " + plugin.get("responsecode"));
				System.out.println("GetFields Conf: " + plugin.getResponseFields());

			}
		}*/
	}
	
	public void realizarDebito(ParametrosAutenticacao params){
		
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
				System.out.println( "Código Erro Conf: " + error_code );
				System.out.println( "Mensagem Conf: " + error_text );
			} else{
				System.out.println( "Código Resposta Conf: " + plugin.get("result") );
				System.out.println( "Resposta Conf: " + plugin.get("responsecode"));
				System.out.println("GetFields Conf: " + plugin.getResponseFields());

			}
		}*/
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
				System.out.println( "Código Erro Conf: " + error_code );
				System.out.println( "Mensagem Conf: " + error_text );
			} else{
				System.out.println( "Código Resposta Conf: " + plugin.get("result") );
				System.out.println( "Resposta Conf: " + plugin.get("responsecode"));
				System.out.println("GetFields Conf: " + plugin.getResponseFields());

			}
		}*/
	}
	
	public void realizarCancelamento(Parametros params){
		
		UniversalPlugin plugin = new UniversalPlugin();
		plugin.setResourcePath("/resourceGetNet");
		plugin.setTerminalAlias("D087729102");
		plugin.setTransactionType("TranPortal");
		plugin.setVersion("1");
		plugin.set("action" , "9");
		plugin.set("type" , "CC");
		plugin.set("transid" , "4960347261552731");
		plugin.set("trackid" , "123456789");
		plugin.set("amt" , "100.00");
		plugin.set("currencycode", "986");
		
		if(!plugin.performTransaction()){
			System.out.println( "Erro Conf: " + plugin.getErrorText() );
		}else{
			String error_code = plugin.get("error_code_tag");
			String error_text = plugin.get("error_text");
			if(error_code != null && error_code.length() > 0 ){
				System.out.println( "Código Erro Conf: " + error_code );
				System.out.println( "Mensagem Conf: " + error_text );
			} else{
				System.out.println( "Código Resposta Conf: " + plugin.get("result") );
				System.out.println( "Resposta Conf: " + plugin.get("responsecode"));
				System.out.println("GetFields Conf: " + plugin.getResponseFields());

			}
		}
	}
	
	/*public HashMap performTransaction(UniversalPlugin plugin){
		
		if(!plugin.performTransaction()){
			System.out.println( "Erro : " + plugin.getErrorText() );
			HashMap camposRetorno = new HashMap<String, String>();
			camposRetorno.put("error_text", plugin.getErrorText());
			return camposRetorno;
		}else{
			String error_code = plugin.get("error_code_tag");
			String error_text = plugin.get("error_text");
			if(error_code != null && error_code.length() > 0 ){
				System.out.println( "Código Erro : " + error_code );
				System.out.println( "Mensagem : " + error_text );
				
				//return "Código Erro : " + error_code +  " Mensagem : " + error_text;
				HashMap camposRetorno = new HashMap<String, String>();
				camposRetorno.put("error_code", error_code);
				camposRetorno.put("error_text", error_text);
				return camposRetorno;
			}  else{
				System.out.println( "Código Resposta : " + plugin.get("result") );
				System.out.println( "Resposta : " + plugin.get("responsecode"));
				System.out.println(plugin.getResponseFields());
				HashMap camposRetorno = new HashMap<String, String>();
				camposRetorno.put("result", plugin.get("result"));
				camposRetorno.put("responsecode", plugin.get("responsecode"));
				camposRetorno.put("tranid", plugin.get("tranid"));
				camposRetorno.put("trackid", plugin.get("trackid"));
				
				return camposRetorno;
			}
		}
	}
	*/
	
}
