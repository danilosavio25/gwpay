package br.com.gwpay.pagamento.test;

public class GetNetServiceTest {
	
	public static void main(String[] args) {
		
		String numCartao = "4012001038166662";
		String parteAterada = numCartao.substring(6, 12);
		String numMascarado = numCartao;
		numMascarado = numMascarado.replace(parteAterada, "******");
		System.out.println(numMascarado);
	}
	
	private String mascararCartao(String numCartao){
		
		if(numCartao == null || numCartao.equals("") || numCartao.length() != 16){
			return "";
		}
		
		String numMascarado = numCartao;
		String parteAterada = numCartao.substring(6, 12);
		numMascarado = numMascarado.replace(parteAterada, "******");
		return numMascarado;
	}
}
