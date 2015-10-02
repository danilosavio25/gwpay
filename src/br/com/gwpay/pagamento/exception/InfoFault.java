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
}