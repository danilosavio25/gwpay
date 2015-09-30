package br.com.gwpay.pagamento.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ParametrosAutorizacao extends Parametros {
	
	@XmlElement(required=true)
	private String bandeira;
	@XmlElement(required=true)
	private String numCartao;
	@XmlElement(required=true)
	private String nomePortador;
	@XmlElement(required=true)
	private String mesVencimento;
	@XmlElement(required=true)
	private String anoVencimento;
	@XmlElement(required=true)
	private String codSegurancaCartao;
	
	
	@XmlElement(required=false)
	private String tipoParcelamento; // V, PL, PE
	private int numParcelas;

	public ParametrosAutorizacao(){
		super();
		
	}
	
	public String getNumCartao() {
		return numCartao;
	}


	public void setNumCartao(String numCartao) {
		this.numCartao = numCartao;
	}




	public String getAnoVencimento() {
		return anoVencimento;
	}




	public void setAnoVencimento(String anoVencimento) {
		this.anoVencimento = anoVencimento;
	}




	public String getMesVencimento() {
		return mesVencimento;
	}




	public void setMesVencimento(String mesVencimento) {
		this.mesVencimento = mesVencimento;
	}




	public String getCodSegurancaCartao() {
		return codSegurancaCartao;
	}




	public void setCodSegurancaCartao(String codSegurancaCartao) {
		this.codSegurancaCartao = codSegurancaCartao;
	}




	public String getNomePortador() {
		return nomePortador;
	}




	public void setNomePortador(String nomePortador) {
		this.nomePortador = nomePortador;
	}




	public String getBandeira() {
		return bandeira;
	}




	public void setBandeira(String bandeira) {
		this.bandeira = bandeira;
	}




	public int getNumParcelas() {
		return numParcelas;
	}




	public void setNumParcelas(int numParcelas) {
		this.numParcelas = numParcelas;
	}




	public String getTipoParcelamento() {
		return tipoParcelamento;
	}




	public void setTipoParcelamento(String tipoParcelamento) {
		this.tipoParcelamento = tipoParcelamento;
	}
	
}
