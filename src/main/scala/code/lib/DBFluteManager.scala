package code.lib

import java.util.{ArrayList, List}
import javax.sql.DataSource
import javax.transaction.{TransactionManager, UserTransaction}

import com.atomikos.icatch.jta.{UserTransactionManager, UserTransactionImp}
import com.atomikos.jdbc.nonxa.AtomikosNonXADataSourceBean
import com.example.dbflute.scala.EmbeddedH2UrlFactoryBean
import com.example.dbflute.scala.dbflute.allcommon.{DBFlutist, DBFluteModule}
import com.google.inject.{Guice, AbstractModule, Module}
import org.joda.time.{LocalDateTime, LocalDate}
import org.seasar.dbflute.util.DfTypeUtil

/**
 * Created by andreas on 15/06/12.
 */
object DBFluteManager {

  // ===================================================================================
  //                                                                            Settings
  //                                                                            ========
  def setUp() {
    val _xcurrentActiveInjector = Guice.createInjector(prepareModuleList());
    DBFlutist.play(_xcurrentActiveInjector);
  }

  protected def prepareModuleList(): List[Module] = {
    val dataSource = createDataSource();
    val moduleList = new ArrayList[Module]();
    moduleList.add(new DBFluteModule(dataSource));
    val transactionModule = createTransactionModule(dataSource);
    if (transactionModule != null) {
      moduleList.add(transactionModule);
    }
    return moduleList;
  }

  // ===================================================================================
  //                                                                          DataSource
  //                                                                          ==========
  def createDataSource(): DataSource = {
    val bean = new AtomikosNonXADataSourceBean();
    bean.setUniqueResourceName("NONXADBMS");
    bean.setDriverClassName("org.h2.jdbcx.JdbcDataSource");
    val factoryBean = new EmbeddedH2UrlFactoryBean();
    factoryBean.urlSuffix = "/exampledb/exampledb";
    factoryBean.referenceClassName = classOf[EmbeddedH2UrlFactoryBean].getName();
    val url = try {
      factoryBean.getObject().toString();
    } catch {
      case e: Exception => {
        val msg = "The factoryBean was invalid: " + factoryBean;
        throw new IllegalStateException(msg, e);
      }
    }
    bean.setUrl(url.toString());
    bean.setUser("sa");
    bean.setPassword("");
    bean.setPoolSize(20);
    bean.setBorrowConnectionTimeout(60);
    return bean;
  }

  protected def createTransactionModule(dataSource: DataSource): TransactionModule = {
    return new TransactionModule(dataSource);
  }

  protected class TransactionModule(dataSource: DataSource) extends AbstractModule {

    @Override
    protected def configure(): Unit = {
      try {
        val userTransactionImp = new UserTransactionImp();
        userTransactionImp.setTransactionTimeout(300);
        val userTransactionManager = new UserTransactionManager();
        userTransactionManager.setForceShutdown(true);
        userTransactionManager.init();
        bind(classOf[UserTransaction]).toInstance(userTransactionImp);
        bind(classOf[TransactionManager]).toInstance(userTransactionManager);
        bind(classOf[DataSource]).toInstance(dataSource);
      } catch {
        case e: Exception => {
          throw new IllegalStateException(e);
        }
      }
    }
  }

  // ===================================================================================
  //                                                                       Assist Helper
  //                                                                       =============
  protected def toLocalDate(exp: String): LocalDate = {
    return LocalDate.fromDateFields(DfTypeUtil.toDate(exp));
  }

  protected def toLocalDateTime(exp: String): LocalDateTime = {
    return LocalDateTime.fromDateFields(DfTypeUtil.toTimestamp(exp));
  }

  protected def currentLocalDate(): LocalDate = {
    return LocalDate.now();
  }

  protected def currentLocalDateTime(): LocalDateTime = {
    return LocalDateTime.now();
  }

}
