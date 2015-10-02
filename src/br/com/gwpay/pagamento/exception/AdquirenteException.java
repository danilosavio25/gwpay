package br.com.gwpay.pagamento.exception;

import javax.xml.ws.WebFault;

@WebFault(name="AdquirenteFault", messageName="AdquirenteFault")
public class AdquirenteException extends Exception {
	
	private InfoFault infoFault;
	
	public AdquirenteException(String msg){
		super(msg);
		infoFault = new InfoFault("", "", "", "");
	}
	
	
	public void setInfoFault(String codigoErro, String mensagemErro, String descricaoErro, String acao){
		infoFault = new InfoFault(codigoErro, mensagemErro, descricaoErro, acao);
	}
	
	public InfoFault getFaultInfo() {
	    return infoFault;
	}
}
