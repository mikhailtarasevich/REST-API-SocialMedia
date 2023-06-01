package com.mikhail.tarasevich.socialmedia.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
@EnableAutoConfiguration
@EntityScan("com.mikhail.tarasevich.socialmedia.entity")
@ComponentScan("com.mikhail.tarasevich.socialmedia")
@EnableJpaRepositories("com.mikhail.tarasevich.socialmedia.repository")
public class SpringTestConfig {

    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("classpath:sql/schema.sql")
                .addScripts("classpath:sql/data.sql")
                .build();
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }

    @Bean
    public EntityManagerFactory entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();

        Properties props = new Properties();
        props.setProperty("hibernate.format_sql", String.valueOf(true));
        props.setProperty("hibernate.connection.autocommit", String.valueOf(true));

        factoryBean.setDataSource(dataSource);
        factoryBean.setPackagesToScan("com.mikhail.tarasevich.socialmedia");
        factoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        factoryBean.setJpaProperties(props);
        factoryBean.afterPropertiesSet();

        return factoryBean.getNativeEntityManagerFactory();
    }

}
