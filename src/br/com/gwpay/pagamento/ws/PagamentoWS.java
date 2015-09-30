package br.com.gwpay.pagamento.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;
import javax.xml.bind.annotation.XmlElement;

import br.com.gwpay.pagamento.model.Parametros;
import br.com.gwpay.pagamento.model.ParametrosAutorizacao;
import br.com.gwpay.pagamento.service.GetNetService;
import br.com.gwpay.pagamento.service.IPagamentoWS;

@WebService
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public class PagamentoWS {
	
	//Credito autenticado. Nova função.
	
	@WebMethod(operationName="credito")
	public String realizarCredito(){
		IPagamentoWS service = new GetNetService();
		Parametros params = new Parametros();
		return service.realizarCredito(params);
	}
	
	@WebMethod(operationName="creditoAutorizacao")
	public String realizarCreditoAutorizacao(@XmlElement(required=true) @WebParam(name="parametros") ParametrosAutorizacao params) throws Exception{
		IPagamentoWS service = new GetNetService();
		
		if(params.getCodCliente() == null || params.getCodCliente().equals("")){
				throw new Exception("Favor PReenhcer todos os campos");
		}
		
		
		params.setCodCliente("D087729102");
		params.setCodRastreio("111111117");
		params.setBandeira("Master");
		params.setNumCartao("5453010000083303");
		params.setCodSegurancaCartao("321");
		params.setNomePortador("ANTONIO NUNES SILVA");
		params.setAnoVencimento("2017");
		params.setMesVencimento("04");
		params.setValor(100.00);
		
		return "Oi";
		//return service.realizarCreditoAutorizacao(params);
	}
	
	@WebMethod(operationName="creditoConfirmacao")
	public String realizarCreditoConfirmacao(){
		IPagamentoWS service = new GetNetService();
		
		Parametros params = new Parametros();

		
		return service.realizarCreditoConfirmacao(params);
	}
	
	@WebMethod(operationName="debito")
	public String realizarDebito(){
		IPagamentoWS service = new GetNetService();
		return service.realizarDebito();
	}
	
	@WebMethod(operationName="creditoAutenticacao")
	public String realizarCreditoAutenticacao(){
		IPagamentoWS service = new GetNetService();
		return service.realizarDebito();
	}
	
	@WebMethod(operationName="consultaTransacao")
	public String realizarConsultaTransacao(@WebParam(name="codigoCliente") String codigoCliente,@WebParam(name="codigoRastreio") String codigoRastreio){
		IPagamentoWS service = new GetNetService();
		Parametros params = new Parametros();

		return service.realizarConsultaTransacao(params);
	}
	
	@WebMethod(operationName="cancelamento")
	public String realizarCancelamento(){
		IPagamentoWS service = new GetNetService();
		return service.realizarEstorno();
	}
	
}
