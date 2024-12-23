package io.github.robert_f_ruff.rules_engine;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;

import javax.sql.DataSource;

/**
 * Spring Boot configuration class; provides beans needed to interact with
 * the database.
 * @author Robert F. Ruff
 * @version 1.0
 */
@Configuration
@EnableJpaRepositories
@EnableTransactionManagement
@PropertySource("classpath:application.properties")
public class ApplicationConfig {

  /**
   * Returns an EntityManager factory bean that will provide connectivity to the database.
   * @param dataSource The DataSource reference to use in generating EntityManager instances.
   * @return An EntityManger factory bean.
   * @since 1.0
   */
  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
    LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
    factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
    factory.setPackagesToScan("io.github.robert_f_ruff.rules_engine.loader");
    factory.setDataSource(dataSource);
    return factory;
  }

  /**
   * Returns a Session factory bean that will generate Session instances, which provide connectivity
   * to the database.
   * @param entityManagerFactory The EntityManager factory bean that contains the Session factory.
   * @return A session factory bean.
   * @since 1.0
   */
  @Bean
  @Primary
  public SessionFactory getSessionFactory(EntityManagerFactory entityManagerFactory) {
    if (entityManagerFactory.unwrap(SessionFactory.class) == null) {
      throw new NullPointerException("The session factory is not a hibernate factory.");
    }
    return entityManagerFactory.unwrap(SessionFactory.class);
  }

  /**
   * New instance of ApplicationConfig.
   * @since 1.0
   */
  public ApplicationConfig() { }
}
