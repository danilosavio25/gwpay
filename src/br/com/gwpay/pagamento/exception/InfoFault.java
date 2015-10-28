package br.com.gwpay.pagamento.exception;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class InfoFault {

	private String codigoErro;
	private String mensagemErro;
    private String descricaoErro;
    private String acao;
   


    

    public InfoFault(String codigoErro, String mensagemErro, String descricaoErro, String acao) {
    	this.codigoErro = codigoErro;	
    	this.mensagemErro = mensagemErro;
    	this.descricaoErro = descricaoErro;
    	this.acao = acao;

    }

    //JAX-B precisa
    InfoFault() {
    }
    
    
    public String getCodigoErro() {
		return codigoErro;
	}

	public void setCodigoErro(String codigoErro) {
		this.codigoErro = codigoErro;
	}

	public String getMensagemErro() {
		return mensagemErro;
	}

	public void setMensagemErro(String mensagemErro) {
		this.mensagemErro = mensagemErro;
	}

	public String getDescricaoErro() {
		return descricaoErro;
	}

	public void setDescricaoErro(String descricaoErro) {
		this.descricaoErro = descricaoErro;
	}

	public String getAcao() {
		return acao;
	}

	public void setAcao(String acao) {
		this.acao = acao;
	}

}