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
				System.out.println( "C�digo Erro Conf: " + error_code );
				System.out.println( "Mensagem Conf: " + error_text );
				return "C�digo Erro : " + error_code +  " Mensagem : " + error_text;
			} else{
				System.out.println( "C�digo Resposta Conf: " + plugin.get("result") );
				System.out.println( "Resposta Conf: " + plugin.get("responsecode"));
				System.out.println("GetFields Conf: " + plugin.getResponseFields());
				return plugin.get("tranid");
			}
		}*/
		return null;
	}
	
	public static void main(String[] args) {
		MPIPlugin plugin = new MPIPlugin();
		
		
		ParametrosAutorizacao params = new ParametrosAutorizacao();
		/*params.setCodCliente("D087729102");
		params.setBandeira("Master");
		params.setNumCartao("5453010000083303");
		params.setCodSegurancaCartao("321");
		params.setNomePortador("ANTONIO NUNES SILVA");
		params.setAnoVencimento(2017);
		params.setMesVencimento(04);
		params.setValor(100.00);
		System.out.println(plugin.realizarCreditoAutorizacao(params));*/
		
		
		plugin.realizarCancelamento(params);
	//	plugin.cancelamento();
	}
	
	public HashMap realizarCreditoAutorizacao(ParametrosAutorizacao params){
		
		System.out.println(params.getAnoVencimento());
		params.setCodRastreio("");
		System.out.println(params.getCodRastreio());
		System.out.println(params.getCodCliente());
		System.out.println(params.getCodSegurancaCartao());
		System.out.println(params.getMesVencimento());
		System.out.println(params.getNomePortador());
		System.out.println(params.getNumCartao());
		System.out.println(params.getValor());
		params.setCodNSU("");
		System.out.println(params.getCodNSU());
		params.setTipoParcelamento("SGL");
		System.out.println(params.getTipoParcelamento());
		System.out.println(params.getNumParcelas());
		
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
				System.out.println( "C�digo Erro Conf: " + error_code );
				System.out.println( "Mensagem Conf: " + error_text );
			} else{
				System.out.println( "C�digo Resposta Conf: " + plugin.get("result") );
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
				System.out.println( "C�digo Erro Conf: " + error_code );
				System.out.println( "Mensagem Conf: " + error_text );
			} else{
				System.out.println( "C�digo Resposta Conf: " + plugin.get("result") );
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
				System.out.println( "C�digo Erro Conf: " + error_code );
				System.out.println( "Mensagem Conf: " + error_text );
			} else{
				System.out.println( "C�digo Resposta Conf: " + plugin.get("result") );
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
		System.out.println("oi");
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
		}
	}
	
}
