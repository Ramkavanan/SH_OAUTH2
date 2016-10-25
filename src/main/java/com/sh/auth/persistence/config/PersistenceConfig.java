package com.sh.auth.persistence.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.netflix.config.DynamicPropertyFactory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * All persistence related beans and configuration defined here
 * 
 * @author RamaKavanan
 */

@Configuration
@EnableTransactionManagement
public class PersistenceConfig {
	
	private static final String DATASOURCE_CLASS_NAME = "oauth.dataSourceClassName";
	
    private static final String DATASOURCE_DB_NAME = "oauth.databaseName";
    
    private static final String DATASOURCE_USERNAME = "oauth.user";
    
    private static final String DATASOURCE_PASSWORD = "oauth.password";
    
    private static final String DATASOURCE_PORT = "oauth.portNumber";
    
    private static final String DATASOURCE_SERVER_NAME = "oauth.serverName";
    
    private static final String MAX_POOL_SIZE = "oauth.maximumPoolSize";

    private static final String DIALECT = "hibernate.dialect";
    
    private static final String SHOW_SQL = "hibernate.show_sql";
    
    private static final String FORMAT_SQL = "hibernate.format_sql";
    
    private static final String HBM2DDL_AUTO = "hbm2ddl.auto";
    
    private String getClassName() {
        return DynamicPropertyFactory.getInstance()
                .getStringProperty(DATASOURCE_CLASS_NAME, "com.mysql.jdbc.jdbc2.optional.MysqlDataSource").getValue();
    }

    private int getMaxPoolSize() {
        return DynamicPropertyFactory.getInstance().getIntProperty(MAX_POOL_SIZE, 6).getValue();
    }

    private String getServername() {
        return DynamicPropertyFactory.getInstance()
                .getStringProperty(DATASOURCE_SERVER_NAME, "localhost").getValue();
    }

    private int getPortNumber() {
        return DynamicPropertyFactory.getInstance().getIntProperty(DATASOURCE_PORT, 3306).getValue();
    }

    private String getDatabaseName() {
        return DynamicPropertyFactory.getInstance()
                .getStringProperty(DATASOURCE_DB_NAME, "fashnoid_test").getValue();
    }

    private String getHbm2DdlAuto() {
        return DynamicPropertyFactory.getInstance().getStringProperty(HBM2DDL_AUTO, "validate").getValue();
    }

    private String getUsername() {
        return DynamicPropertyFactory.getInstance().getStringProperty(DATASOURCE_USERNAME, "root").getValue();
    }

    private String getPassword() {
        return DynamicPropertyFactory.getInstance().getStringProperty(DATASOURCE_PASSWORD, "root").getValue();
    }

    private String getDialect() {
        return DynamicPropertyFactory.getInstance()
                .getStringProperty(DIALECT, "org.hibernate.dialect.MySQLDialect").getValue();
    }

    private Boolean getShowSql() {
        return DynamicPropertyFactory.getInstance().getBooleanProperty(SHOW_SQL, true).getValue();
    }

    private Boolean getFormatSql() {
        return DynamicPropertyFactory.getInstance().getBooleanProperty(FORMAT_SQL, true).getValue();
    }

    
    @Bean(name="oauthSessionFactory")
    public LocalSessionFactoryBean sessionFactoryBean(@Qualifier("oauthDataSource") DataSource dataSource) {
        LocalSessionFactoryBean mwynAuthSessionFactory = new LocalSessionFactoryBean();
        mwynAuthSessionFactory.setDataSource(dataSource);
        mwynAuthSessionFactory.setPackagesToScan("com.fashnoid.auth");
        mwynAuthSessionFactory.setHibernateProperties(hibernateProperties());
        return mwynAuthSessionFactory;
    }

    @Bean(name = "oauthDataSource")
    public DataSource dataSource() {
        Properties props = new Properties();
        props.setProperty("dataSourceClassName", getClassName());
        props.setProperty("dataSource.url", "jdbc:mysql://localhost:3306/"+getDatabaseName());
        props.setProperty("dataSource.user", getUsername());
        props.setProperty("dataSource.password", getPassword());
        props.setProperty("dataSource.databaseName", getDatabaseName());
        props.setProperty("dataSource.serverName", getServername());
        props.setProperty("maximumPoolSize", String.valueOf(getMaxPoolSize()));
        props.setProperty("dataSource.portNumber", String.valueOf(getPortNumber()));
        HikariConfig config = new HikariConfig(props);

        return new HikariDataSource(config);
    }

    private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.put(DIALECT, getDialect());
        properties.put(SHOW_SQL, getShowSql());
        properties.put(FORMAT_SQL, getFormatSql());
        properties.put(HBM2DDL_AUTO, getHbm2DdlAuto());
        return properties;
    }

    @Bean
    public HibernateTransactionManager transactionManager(@Qualifier("oauthSessionFactory") SessionFactory mwynAuthSessionFactory) {
        return new HibernateTransactionManager(mwynAuthSessionFactory);
    }

    
    @Bean
    PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslationPostProcessorBean() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

}
