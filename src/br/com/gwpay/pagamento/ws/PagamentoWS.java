package br.com.gwpay.pagamento.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import br.com.gwpay.pagamento.service.GetNetService;
import br.com.gwpay.pagamento.service.IPagamentoWS;

@WebService
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public class PagamentoWS {
	
	@WebMethod(operationName="credito")
	public String realizarCredito(){
		IPagamentoWS service = new GetNetService();
		return service.realizarCredito();
	}
	
	@WebMethod(operationName="creditoAutorizacao")
	public String realizarCreditoAutorizacao(){
		IPagamentoWS service = new GetNetService();
		return service.realizarCreditoAutorizacao();
	}
	
	@WebMethod(operationName="creditoConfirmacao")
	public String realizarCreditoConfirmacao(){
		IPagamentoWS service = new GetNetService();
		return service.realizarCreditoConfirmacao();
	}
	
	@WebMethod(operationName="debito")
	public String realizarDebito(){
		IPagamentoWS service = new GetNetService();
		return service.realizarDebito();
	}
	
	@WebMethod(operationName="consultaTransacao")
	public String realizarConsultaTransacao(@WebParam(name="codigoCliente") String codigoCliente,@WebParam(name="codigoRastreio") String codigoRastreio){
		IPagamentoWS service = new GetNetService();
		return service.realizarConsultaTransacao(codigoCliente, codigoRastreio);
	}
	
	@WebMethod(operationName="estorno")
	public String realizarEstorno(){
		IPagamentoWS service = new GetNetService();
		return service.realizarEstorno();
	}
	
}
