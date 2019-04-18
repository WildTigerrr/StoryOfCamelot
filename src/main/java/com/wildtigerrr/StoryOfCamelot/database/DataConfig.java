package com.wildtigerrr.StoryOfCamelot.database;

import org.apache.commons.dbcp2.BasicDataSource;
import org.hibernate.ejb.HibernatePersistence;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.net.URISyntaxException;
import java.util.Properties;


@Configuration
@EnableTransactionManagement
@ComponentScan("com.devcolibri.dataexam")
@PropertySource("classpath:application.properties")
@EnableJpaRepositories("com.wildtigerrr.StoryOfCamelot.repository")
public class DataConfig {

    private static final String PROP_HIBERNATE_DIALECT = "db.hibernate.dialect";
    private static final String PROP_HIBERNATE_SHOW_SQL = "db.hibernate.show_sql";
    private static final String PROP_ENTITYMANAGER_PACKAGES_TO_SCAN = "db.entitymanager.packages.to.scan";
    private static final String PROP_HIBERNATE_HBM2DDL_AUTO = "db.hibernate.hbm2ddl.auto";

    @Resource
    private Environment env;

    @Bean
    public BasicDataSource dataSource() throws URISyntaxException {
        String dbUrl = System.getenv("JDBC_DATABASE_URL");
        String username = System.getenv("JDBC_DATABASE_USERNAME");
        String password = System.getenv("JDBC_DATABASE_PASSWORD");

        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl(dbUrl);
        basicDataSource.setUsername(username);
        basicDataSource.setPassword(password);

        return basicDataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() throws URISyntaxException {
        System.out.println("EMF Start");
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        System.out.println("EMF Bean: " + entityManagerFactoryBean);
        System.out.println("EMF DataSource: " + dataSource());
        entityManagerFactoryBean.setDataSource(dataSource());
        System.out.println("EMF Persistence: " + HibernatePersistence.class);
        entityManagerFactoryBean.setPersistenceProviderClass(HibernatePersistence.class);
        System.out.println("EMF Packages: " + env.getRequiredProperty(PROP_ENTITYMANAGER_PACKAGES_TO_SCAN));
        entityManagerFactoryBean.setPackagesToScan(env.getRequiredProperty(PROP_ENTITYMANAGER_PACKAGES_TO_SCAN));
        System.out.println("EMF Properties: " + getHibernateProperties());

        entityManagerFactoryBean.setJpaProperties(getHibernateProperties());

        return entityManagerFactoryBean;
    }

    @Bean
    public JpaTransactionManager transactionManager() throws URISyntaxException {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());

        return transactionManager;
    }

    private Properties getHibernateProperties() {
        Properties properties = new Properties();
        properties.put(PROP_HIBERNATE_DIALECT, env.getRequiredProperty(PROP_HIBERNATE_DIALECT));
        properties.put(PROP_HIBERNATE_SHOW_SQL, env.getRequiredProperty(PROP_HIBERNATE_SHOW_SQL));
        properties.put(PROP_HIBERNATE_HBM2DDL_AUTO, env.getRequiredProperty(PROP_HIBERNATE_HBM2DDL_AUTO));

        return properties;
    }

}