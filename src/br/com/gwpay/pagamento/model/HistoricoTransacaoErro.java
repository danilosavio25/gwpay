package br.com.gwpay.pagamento.model;

import java.util.Date;

public class HistoricoTransacaoErro {
	
	private String codCliente; //terminalId
	private String codRastreio; //trackId
	private double valor;
	private String moeda;
	private String tipoPagamento;
	private int numParcelas;
	private String numCartao;
	private int mesVencimentoCartao;
	private int anoVencimentoCartao;
	private String nomePortador;
	private String campo1;
	private String campo2;
	private String campo3;
	private String campo4;
	private String campo5;
	private String codNSU;
	private Date dataTransacao;
	private String codTransacaoOriginal;
	private String descricaoResposta;
	private String codResposta;
	private String codErroGateway;
	private String descricaoErroGateway;
	private double valorPrimeiraParcela;
	private double valorOutrasParcelas;
	private double valorTotal;
	private double taxaJuros;
	private double jurosInstFinanceira;
	private String codErroWS;
	private String descricaoErroWS;
	private double valorCancelado;
	private int tipoTransacaoId;
	private int clienteId;
	private int bandeiraId;
	private int tipoCancelamentoId;
	private String codSegurancaCartao;
	

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
	public double getValor() {
		return valor;
	}
	public void setValor(double valor) {
		this.valor = valor;
	}
	public String getMoeda() {
		return moeda;
	}
	public void setMoeda(String moeda) {
		this.moeda = moeda;
	}
	public String getTipoPagamento() {
		return tipoPagamento;
	}
	public void setTipoPagamento(String tipoPagamento) {
		this.tipoPagamento = tipoPagamento;
	}
	public int getNumParcelas() {
		return numParcelas;
	}
	public void setNumParcelas(int numParcelas) {
		this.numParcelas = numParcelas;
	}
	public String getNumCartao() {
		return numCartao;
	}
	public void setNumCartao(String numCartao) {
		this.numCartao = numCartao;
	}
	public int getMesVencimentoCartao() {
		return mesVencimentoCartao;
	}
	public void setMesVencimentoCartao(int mesVencimentoCartao) {
		this.mesVencimentoCartao = mesVencimentoCartao;
	}
	public int getAnoVencimentoCartao() {
		return anoVencimentoCartao;
	}
	public void setAnoVencimentoCartao(int anoVencimentoCartao) {
		this.anoVencimentoCartao = anoVencimentoCartao;
	}
	public String getNomePortador() {
		return nomePortador;
	}
	public void setNomePortador(String nomePortador) {
		this.nomePortador = nomePortador;
	}
	public String getCampo1() {
		return campo1;
	}
	public void setCampo1(String campo1) {
		this.campo1 = campo1;
	}
	public String getCampo2() {
		return campo2;
	}
	public void setCampo2(String campo2) {
		this.campo2 = campo2;
	}
	public String getCampo3() {
		return campo3;
	}
	public void setCampo3(String campo3) {
		this.campo3 = campo3;
	}
	public String getCampo4() {
		return campo4;
	}
	public void setCampo4(String campo4) {
		this.campo4 = campo4;
	}
	public String getCampo5() {
		return campo5;
	}
	public void setCampo5(String campo5) {
		this.campo5 = campo5;
	}
	public String getCodNSU() {
		return codNSU;
	}
	public void setCodNSU(String codNSU) {
		this.codNSU = codNSU;
	}
	public Date getDataTransacao() {
		return dataTransacao;
	}
	public void setDataTransacao(Date dataTransacao) {
		this.dataTransacao = dataTransacao;
	}
	public String getCodTransacaoOriginal() {
		return codTransacaoOriginal;
	}
	public void setCodTransacaoOriginal(String codTransacaoOriginal) {
		this.codTransacaoOriginal = codTransacaoOriginal;
	}
	public String getDescricaoResposta() {
		return descricaoResposta;
	}
	public void setDescricaoResposta(String descricaoResposta) {
		this.descricaoResposta = descricaoResposta;
	}
	public String getCodResposta() {
		return codResposta;
	}
	public void setCodResposta(String codResposta) {
		this.codResposta = codResposta;
	}
	public String getCodErroGateway() {
		return codErroGateway;
	}
	public void setCodErroGateway(String codErroGateway) {
		this.codErroGateway = codErroGateway;
	}
	public String getDescricaoErroGateway() {
		return descricaoErroGateway;
	}
	public void setDescricaoErroGateway(String descricaoErroGateway) {
		this.descricaoErroGateway = descricaoErroGateway;
	}
	public double getValorPrimeiraParcela() {
		return valorPrimeiraParcela;
	}
	public void setValorPrimeiraParcela(double valorPrimeiraParcela) {
		this.valorPrimeiraParcela = valorPrimeiraParcela;
	}
	public double getValorOutrasParcelas() {
		return valorOutrasParcelas;
	}
	public void setValorOutrasParcelas(double valorOutrasParcelas) {
		this.valorOutrasParcelas = valorOutrasParcelas;
	}
	public double getValorTotal() {
		return valorTotal;
	}
	public void setValorTotal(double valorTotal) {
		this.valorTotal = valorTotal;
	}
	public double getTaxaJuros() {
		return taxaJuros;
	}
	public void setTaxaJuros(double taxaJuros) {
		this.taxaJuros = taxaJuros;
	}
	public double getJurosInstFinanceira() {
		return jurosInstFinanceira;
	}
	public void setJurosInstFinanceira(double jurosInstFinanceira) {
		this.jurosInstFinanceira = jurosInstFinanceira;
	}
	public String getCodErroWS() {
		return codErroWS;
	}
	public void setCodErroWS(String codErroWS) {
		this.codErroWS = codErroWS;
	}
	public String getDescricaoErroWS() {
		return descricaoErroWS;
	}
	public void setDescricaoErroWS(String descricaoErroWS) {
		this.descricaoErroWS = descricaoErroWS;
	}
	public double getValorCancelado() {
		return valorCancelado;
	}
	public void setValorCancelado(double valorCancelado) {
		this.valorCancelado = valorCancelado;
	}
	public int getTipoTransacaoId() {
		return tipoTransacaoId;
	}
	public void setTipoTransacaoId(int tipoTransacaoId) {
		this.tipoTransacaoId = tipoTransacaoId;
	}
	public int getClienteId() {
		return clienteId;
	}
	public void setClienteId(int clienteId) {
		this.clienteId = clienteId;
	}
	public int getBandeiraId() {
		return bandeiraId;
	}
	public void setBandeiraId(int bandeiraId) {
		this.bandeiraId = bandeiraId;
	}
	public int getTipoCancelamentoId() {
		return tipoCancelamentoId;
	}
	public void setTipoCancelamentoId(int tipoCancelamentoId) {
		this.tipoCancelamentoId = tipoCancelamentoId;
	}
	public String getCodSegurancaCartao() {
		return codSegurancaCartao;
	}
	public void setCodSegurancaCartao(String codSegurancaCartao) {
		this.codSegurancaCartao = codSegurancaCartao;
	}
	
	
	
	
}
