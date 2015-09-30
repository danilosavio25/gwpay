package br.com.gwpay.pagamento.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ParametrosAutenticacao extends ParametrosAutorizacao{
	
	
	private String tipoTransacaoVisa;
	private double numCelular;
	private String tipoContaVisa; //terminalId


	
	public ParametrosAutenticacao(){
		
	}



	public String getTipoTransacaoVisa() {
		return tipoTransacaoVisa;
	}



	public void setTipoTransacaoVisa(String tipoTransacaoVisa) {
		this.tipoTransacaoVisa = tipoTransacaoVisa;
	}



	public double getNumCelular() {
		return numCelular;
	}



	public void setNumCelular(double numCelular) {
		this.numCelular = numCelular;
	}



	public String getTipoContaVisa() {
		return tipoContaVisa;
	}



	public void setTipoContaVisa(String tipoContaVisa) {
		this.tipoContaVisa = tipoContaVisa;
	}

	
}
