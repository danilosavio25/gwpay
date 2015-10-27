package br.com.gwpay.pagamento.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ResultadoWS {
	
	private String codigoResposta;
    private String mensagemResposta;
    private String descricaoResposta;
	private String codigoNSU;
    private String codigoRastreio;
    private String dataTransacao;
	
    public ResultadoWS() {
	}
	
	public String getCodigoResposta() {
		return codigoResposta;
	}
	public void setCodigoResposta(String codigoResposta) {
		this.codigoResposta = codigoResposta;
	}
	public String getMensagemResposta() {
		return mensagemResposta;
	}
	public void setMensagemResposta(String mensagemResposta) {
		this.mensagemResposta = mensagemResposta;
	}
	public String getDescricaoResposta() {
		return descricaoResposta;
	}
	public void setDescricaoResposta(String descricaoResposta) {
		this.descricaoResposta = descricaoResposta;
	}
	public String getCodigoNSU() {
		return codigoNSU;
	}
	public void setCodigoNSU(String codigoNSU) {
		this.codigoNSU = codigoNSU;
	}
	public String getCodigoRastreio() {
		return codigoRastreio;
	}
	public void setCodigoRastreio(String codigoRastreio) {
		this.codigoRastreio = codigoRastreio;
	}
	public String getDataTransacao() {
		return dataTransacao;
	}
	public void setDataTransacao(String dataTransacao) {
		this.dataTransacao = dataTransacao;
	}
	
}
