package br.com.gwpay.pagamento.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Parametros {
	
	@XmlElement(required=true)
	private String codCliente; //terminalId
	@XmlElement(required=true)
	private String codNSU;
	@XmlElement(required=true)
	private double valor;
	@XmlElement(required=false)
	private String codRastreio; //trackId


	
	public Parametros(){
		
	}



	public String getCodCliente() {
		return codCliente;
	}



	public void setCodCliente(String codCliente) {
		this.codCliente = codCliente;
	}



	public String getCodRastreio() {
		return codRastreio;
	}



	public void setCodRastreio(String codRastreio) {
		this.codRastreio = codRastreio;
	}



	public String getCodNSU() {
		return codNSU;
	}



	public void setCodNSU(String codNSU) {
		this.codNSU = codNSU;
	}



	public double getValor() {
		return valor;
	}



	public void setValor(double valor) {
		this.valor = valor;
	}
	
	
	
}
