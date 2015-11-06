package br.com.gwpay.pagamento.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;
import javax.xml.bind.annotation.XmlElement;

import br.com.gwpay.pagamento.dao.UsuarioDao;
import br.com.gwpay.pagamento.exception.AdquirenteException;
import br.com.gwpay.pagamento.exception.GWPayException;
import br.com.gwpay.pagamento.model.Parametros;
import br.com.gwpay.pagamento.model.ParametrosAutorizacao;
import br.com.gwpay.pagamento.model.ResultadoWS;
import br.com.gwpay.pagamento.model.Usuario;
import br.com.gwpay.pagamento.service.GetNetService;
import br.com.gwpay.pagamento.service.IPagamentoWS;

@WebService
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public class PagamentoWS {
	
	
	
	@WebMethod(operationName="creditoCompleto")
	@WebResult(name="resultadoWS")
	public ResultadoWS realizarCredito(@WebParam(name="autenticacaoUsuario", header=true) Usuario usuario, @XmlElement(required=true) @WebParam(name="parametros") ParametrosAutorizacao params) throws AdquirenteException, GWPayException{
		//### Autenticação ###
		UsuarioDao uDao = new UsuarioDao();
		int usuarioId = uDao.autenticar(usuario.getLogin(), usuario.getSenha());
		if(usuarioId == 0 ){
			GWPayException exception = new GWPayException("Login inválido.");
			exception.setInfoFault("GW04", "Login Inválido" , "Usuário e/ou senha inválidos." , "Favor verificar seu usuário e senha.");
			throw exception;
		}
		IPagamentoWS service = new GetNetService();
		return service.realizarCreditoCompleto(params);
	}
	
	@WebMethod(operationName="creditoAutorizacao")
	@WebResult(name="resultadoWS")
	public ResultadoWS realizarCreditoAutorizacao(@WebParam(name="autenticacaoUsuario", header=true) Usuario usuario, @XmlElement(required=true) @WebParam(name="parametros") ParametrosAutorizacao params) throws AdquirenteException, GWPayException{
		//### Autenticação ###
		UsuarioDao uDao = new UsuarioDao();
		int usuarioId = uDao.autenticar(usuario.getLogin(), usuario.getSenha());
		if(usuarioId == 0 ){
			GWPayException exception = new GWPayException("Login inválido.");
			exception.setInfoFault("GW04", "Login Inválido" , "Usuário e/ou senha inválidos." , "Favor verificar seu usuário e senha.");
			throw exception;
		}
		IPagamentoWS service = new GetNetService();
		return service.realizarCreditoAutorizacao(params);
	}
	
	
	
	@WebMethod(operationName="creditoConfirmacao")
	@WebResult(name="resultadoWS")
	public ResultadoWS realizarCreditoConfirmacao(@WebParam(name="autenticacaoUsuario", header=true) Usuario usuario, @XmlElement(required=true) @WebParam(name="parametros") Parametros params) throws AdquirenteException, GWPayException{
		//### Autenticação ###
		UsuarioDao uDao = new UsuarioDao();
		int usuarioId = uDao.autenticar(usuario.getLogin(), usuario.getSenha());
		if(usuarioId == 0 ){
			GWPayException exception = new GWPayException("Login inválido.");
			exception.setInfoFault("GW04", "Login Inválido" , "Usuário e/ou senha inválidos." , "Favor verificar seu usuário e senha.");
			throw exception;
		}
		IPagamentoWS service = new GetNetService();
		return service.realizarCreditoConfirmacao(params);
	}
	
	@WebMethod(operationName="debito")
	@WebResult(name="resultadoWS")
	public String realizarDebito(@WebParam(name="autenticacaoUsuario", header=true) Usuario usuario) throws AdquirenteException, GWPayException{
		//### Autenticação ###
		UsuarioDao uDao = new UsuarioDao();
		int usuarioId = uDao.autenticar(usuario.getLogin(), usuario.getSenha());
		if(usuarioId == 0 ){
			GWPayException exception = new GWPayException("Login inválido.");
			exception.setInfoFault("GW04", "Login Inválido" , "Usuário e/ou senha inválidos." , "Favor verificar seu usuário e senha.");
			throw exception;
		}
		IPagamentoWS service = new GetNetService();
		return service.realizarDebito();
	}
	
	@WebMethod(operationName="creditoAutenticacao")
	@WebResult(name="resultadoWS")
	public String realizarCreditoAutenticacao(@WebParam(name="autenticacaoUsuario", header=true) Usuario usuario) throws AdquirenteException, GWPayException{
		//### Autenticação ###
		UsuarioDao uDao = new UsuarioDao();
		int usuarioId = uDao.autenticar(usuario.getLogin(), usuario.getSenha());
		if(usuarioId == 0 ){
			GWPayException exception = new GWPayException("Login inválido.");
			exception.setInfoFault("GW04", "Login Inválido" , "Usuário e/ou senha inválidos." , "Favor verificar seu usuário e senha.");
			throw exception;
		}
		IPagamentoWS service = new GetNetService();
		return service.realizarDebito();
	}
	
	@WebMethod(operationName="consultaTransacao")
	@WebResult(name="resultadoWS")
	public ResultadoWS realizarConsultaTransacao(@WebParam(name="token", header=true) String token, @XmlElement(required=true) @WebParam(name="parametros") Parametros params) throws AdquirenteException, GWPayException{
		IPagamentoWS service = new GetNetService();
		return service.realizarConsultaTransacao(token, params);
	}
	
	@WebMethod(operationName="cancelamento")
	@WebResult(name="resultadoWS")
	public ResultadoWS realizarCancelamento(@WebParam(name="autenticacaoUsuario", header=true) Usuario usuario, @XmlElement(required=true) @WebParam(name="parametros") Parametros params) throws AdquirenteException, GWPayException{
		//### Autenticação ###
		UsuarioDao uDao = new UsuarioDao();
		int usuarioId = uDao.autenticar(usuario.getLogin(), usuario.getSenha());
		if(usuarioId == 0 ){
			GWPayException exception = new GWPayException("Login inválido.");
			exception.setInfoFault("GW04", "Login Inválido" , "Usuário e/ou senha inválidos." , "Favor verificar seu usuário e senha.");
			throw exception;
		}
		IPagamentoWS service = new GetNetService();
		return service.realizarCancelamento(params);
	}
	
}
