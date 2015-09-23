package br.com.gwpay.pagamento.ws;

import javax.xml.ws.Endpoint;

public class PublicaPagamentoWS {

	public static void main(String[] args) {
		PagamentoWS implementacaoWS = new PagamentoWS();
		String URL = "http://localhost:8080/PagamentoWS";

		System.out.println("PagamentoWS rodando: " + URL);

		//associando URL com a implementacao
		Endpoint.publish(URL, implementacaoWS);
	}
}
