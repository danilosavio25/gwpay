package br.com.gwpay.pagamento.test;

import static org.junit.Assert.*;

import org.junit.Test;

import br.com.gwpay.pagamento.dao.BandeiraDao;
import br.com.gwpay.pagamento.dao.ClienteDao;
import br.com.gwpay.pagamento.dao.ConnectionFactory;
import br.com.gwpay.pagamento.dao.ErroAdquirenteDao;
import br.com.gwpay.pagamento.dao.HistoricoTransacaoDao;
import br.com.gwpay.pagamento.dao.HistoricoTransacaoErroDao;
import br.com.gwpay.pagamento.dao.OperacaoDao;
import br.com.gwpay.pagamento.dao.SessaoDao;
import br.com.gwpay.pagamento.dao.TipoCancelamentoDao;
import br.com.gwpay.pagamento.dao.TipoTransacaoDao;
import br.com.gwpay.pagamento.dao.UsuarioDao;
import br.com.gwpay.pagamento.model.Sessao;

public class DaoTest {
	
	@Test
	public void testaConexaoDasClassesDao(){
		ConnectionFactory connectionFactory = new ConnectionFactory();
		assertNotNull(connectionFactory.getConnection());
		
		BandeiraDao bDao = new BandeiraDao();
		assertNotNull(bDao.conn);
		
		ClienteDao cDao = new ClienteDao();
		assertNotNull(cDao.conn);
		
		ErroAdquirenteDao eaDao = new ErroAdquirenteDao();
		assertNotNull(eaDao.conn);
		
		HistoricoTransacaoDao hDao = new HistoricoTransacaoDao();
		assertNotNull(hDao.conn);
		
		HistoricoTransacaoErroDao heDao = new HistoricoTransacaoErroDao();
		assertNotNull(heDao.conn);
		
		OperacaoDao oDao = new OperacaoDao();
		assertNotNull(oDao.conn);
		
		SessaoDao sDao = new SessaoDao();
		assertNotNull(sDao.conn);
		
		TipoCancelamentoDao tcDao = new TipoCancelamentoDao();
		assertNotNull(tcDao.conn);
		
		TipoTransacaoDao ttDao = new TipoTransacaoDao();
		assertNotNull(ttDao.conn);
		
		UsuarioDao uDao = new UsuarioDao();
		assertNotNull(uDao.conn);
	}
	
	

	
	
	
}
