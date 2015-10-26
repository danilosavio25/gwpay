package br.com.gwpay.pagamento.exception;

import javax.xml.ws.WebFault;

@WebFault(name="GWPayFault", messageName="GWPayFault")
public class GWPayException extends Exception {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -388286821059586714L;
	private InfoFault infoFault;
	
	public GWPayException(String msg){
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
