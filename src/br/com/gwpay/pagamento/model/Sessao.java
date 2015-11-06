package br.com.gwpay.pagamento.model;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

public class Sessao {
	private String token;
	private String login;
	private Timestamp dataExpiracao;
	private Timestamp dataAcesso;
	private int clienteId;
	
	public Sessao() {
	}
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public Timestamp getDataExpiracao() {
		return dataExpiracao;
	}
	public void setDataExpiracao(Timestamp dataExpiracao) {
		this.dataExpiracao = dataExpiracao;
	}
	public Timestamp getDataAcesso() {
		return dataAcesso;
	}
	public void setDataAcesso(Timestamp dataAcesso) {
		this.dataAcesso = dataAcesso;
	}
	public int getClienteId() {
		return clienteId;
	}
	public void setClienteId(int clienteId) {
		this.clienteId = clienteId;
	}
	
	

	
}
