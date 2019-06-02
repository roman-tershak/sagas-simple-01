package rt.sagas.order;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

@Configuration
public class JTAConfiguration {

    @Bean
    @DependsOn({ "atomikosUserTransactionManager", "atomikosUserTransaction", "atomikosJdbcConnectionFactory", "atomikosJmsConnectionFactory" })
    public PlatformTransactionManager transactionManager() throws SystemException {
        JtaTransactionManager manager = new JtaTransactionManager();
        manager.setTransactionManager( atomikosUserTransactionManager() );
        manager.setUserTransaction( atomikosUserTransaction() );
        manager.setTransactionManagerName("txManager");
        manager.setDefaultTimeout(100);
        manager.setAllowCustomIsolationLevels(true);
        manager.afterPropertiesSet();
        return manager;
    }

    @Bean(initMethod = "init", destroyMethod = "close")
    public UserTransactionManager atomikosUserTransactionManager() throws SystemException {
        UserTransactionManager manager = new UserTransactionManager();
        manager.setStartupTransactionService(true);
        manager.setForceShutdown(false);
//        manager.setTransactionTimeout( 10000 );
        return manager;
    }

    @Bean
    public UserTransaction atomikosUserTransaction() {
        return new UserTransactionImp();
    }
}
