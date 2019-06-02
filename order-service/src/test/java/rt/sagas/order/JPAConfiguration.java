package rt.sagas.order;

import org.h2.jdbcx.JdbcDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "rt.sagas.order.repositories")
@EnableTransactionManagement
@ActiveProfiles("test")
public class JPAConfiguration {

    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String userName;
    @Value("${spring.datasource.password}")
    private String password;

    @Bean(name={"dataSource", "atomikosJdbcConnectionFactory"}, initMethod = "init", destroyMethod = "close")
    public AtomikosDataSourceBean atomikosJdbcConnectionFactory() throws Exception {

        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL(url);
        dataSource.setUser(userName);
        dataSource.setPassword(password);

        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
        xaDataSource.setXaDataSource(dataSource);
        xaDataSource.setUniqueResourceName("datasource");
        xaDataSource.setPoolSize( 10 );
        xaDataSource.setMaxPoolSize( 50 );
        xaDataSource.setTestQuery("SELECT 1");
        xaDataSource.afterPropertiesSet();

        return xaDataSource;
    }

    @Bean
    public HibernateJpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setDatabase(Database.H2);
        hibernateJpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.H2Dialect");
        hibernateJpaVendorAdapter.setGenerateDdl(true);
        return hibernateJpaVendorAdapter;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() throws Exception {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(atomikosJdbcConnectionFactory());
        factoryBean.setJpaVendorAdapter(jpaVendorAdapter());
        factoryBean.setPackagesToScan("rt.sagas.order.entities");
        return factoryBean;
    }
}
