package com.knowledgeVista.Migration;


import java.util.EnumSet;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.tool.schema.TargetType;
import org.hibernate.tool.schema.spi.ExecutionOptions;
import org.hibernate.tool.schema.spi.SchemaCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Service;

import com.zaxxer.hikari.HikariDataSource;

//@Service
//public class DynamicDataSourceService {

//    public DataSource switchToDatabase(String databaseName, String username, String password) {
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName("org.postgresql.Driver");
//        dataSource.setUrl("jdbc:postgresql://localhost:9000/" + databaseName);
//        dataSource.setUsername(username);
//        dataSource.setPassword(password);
//        return dataSource;
//    }
//}


import jakarta.persistence.EntityManagerFactory;

@Service
public class DynamicDataSourceService {

//    @Autowired
//    private ApplicationContext applicationContext;
//
//    @Autowired
//    private DataSourceConfig dataSourceConfig;
//
//    public void switchDataSource(String dbName) {
//        DataSource newDataSource = dataSourceConfig.createDynamicDataSource(dbName);
//
//        // Update the Spring context with the new DataSource
//        ((AbstractRoutingDataSource) applicationContext.getBean(DataSource.class))
//                .setTargetDataSources(Map.of("default", newDataSource));
//        ((AbstractRoutingDataSource) applicationContext.getBean(DataSource.class)).afterPropertiesSet();
//    }
//
//    public void resetToDefaultDataSource() {
//        DataSource defaultDataSource = dataSourceConfig.createDefaultDataSource();
//
//        // Reset to the original DataSource
//        ((AbstractRoutingDataSource) applicationContext.getBean(DataSource.class))
//                .setTargetDataSources(Map.of("default", defaultDataSource));
//        ((AbstractRoutingDataSource) applicationContext.getBean(DataSource.class)).afterPropertiesSet();
//    }
	
//	 @Autowired
//	    private DataSource dataSource;
//
//	    public void switchDataSource(String dbName) {
//	        if (dataSource instanceof HikariDataSource) {
//	            HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
//	            String newJdbcUrl = "jdbc:postgresql://localhost:9000/" + dbName;
//	            hikariDataSource.setJdbcUrl(newJdbcUrl); // Dynamically update the JDBC URL
//	        } else {
//	            throw new UnsupportedOperationException("Dynamic switching is supported only for HikariDataSource");
//	        }
//	    }
//	    
//	    public void resetToDefaultDataSource(String defaultDbName) {
//	        if (dataSource instanceof HikariDataSource) {
//	            HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
//	            String defaultJdbcUrl = "jdbc:postgresql://localhost:9000/" + defaultDbName;
//	            hikariDataSource.setJdbcUrl(defaultJdbcUrl); // Reset to the default JDBC URL
//	        } else {
//	            throw new UnsupportedOperationException("Dynamic switching is supported only for HikariDataSource");
//	        }
//	    }
//	 @Autowired
//	    private ApplicationContext applicationContext;
//
//	    private HikariDataSource currentDataSource;
//
//	    public void switchDataSource(String dbName) {
//	        // Create a new HikariDataSource with the new database name
//	        HikariDataSource newDataSource = new HikariDataSource();
//	        newDataSource.setJdbcUrl("jdbc:postgresql://localhost:9000/" + dbName);
//	        newDataSource.setUsername("postgres");
//	        newDataSource.setPassword("admin");
//	        newDataSource.setDriverClassName("org.postgresql.Driver");
//	        newDataSource.setMaximumPoolSize(10);
//
//	        // Replace the current DataSource
//	        if (currentDataSource != null) {
//	            currentDataSource.close(); // Close the old DataSource
//	        }
//
//	        currentDataSource = newDataSource;
//
//	        // Access the BeanFactory and register the new DataSource
//	        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
//	        configurableApplicationContext.getBeanFactory().registerSingleton("dataSource", newDataSource);
//	    }
//
//	    public void resetToDefaultDataSource() {
//	        switchDataSource("learnhub"); // Replace with the default database name
//	    }
	 @Autowired
	    private ConfigurableApplicationContext applicationContext;

	    private HikariDataSource currentDataSource;
	    
	    public void switchDataSource(String dbName) {
	        // Create a new HikariDataSource
	        HikariDataSource newDataSource = new HikariDataSource();
	        newDataSource.setJdbcUrl("jdbc:postgresql://localhost:9000/" + dbName);
	        newDataSource.setUsername("postgres");
	        newDataSource.setPassword("admin");
	        newDataSource.setDriverClassName("org.postgresql.Driver");
	        newDataSource.setMaximumPoolSize(10);

	        // Access BeanFactory
	        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getBeanFactory();

	        // Remove the old DataSource
	        if (beanFactory.containsBean("dataSource")) {
	            beanFactory.destroySingleton("dataSource");
	        }
	        // Register the new DataSource
	        beanFactory.registerSingleton("dataSource", newDataSource);

	        // Update the current DataSource reference
	        if (currentDataSource != null) {
	            currentDataSource.close();
	        }
	        currentDataSource = newDataSource;
	        refreshEntityManagerFactory();
	    }
	    private void refreshEntityManagerFactory() {
	        // Retrieve the current EntityManagerFactory bean from the application context
	        EntityManagerFactory emf = applicationContext.getBean(EntityManagerFactory.class);

	        // Close the existing EntityManagerFactory
	        if (emf != null) {
	            emf.close();
	        }

	        // Rebuild the EntityManagerFactory with the new DataSource
	        LocalContainerEntityManagerFactoryBean factoryBean = (LocalContainerEntityManagerFactoryBean)
	                applicationContext.getBean("&entityManagerFactory");

	        factoryBean.setDataSource(currentDataSource);
	        factoryBean.afterPropertiesSet();  // Initialize the new factory

	        // Ensure that the EntityManagerFactory is properly refreshed
	        applicationContext.getBean(EntityManagerFactory.class);  // Refresh the EntityManagerFactory in the context
	    }
	    
	    
//	    @Autowired
//	    private EntityManagerFactory entityManagerFactory;
//
//	    private void recreateSchema() {
//	    	 LocalContainerEntityManagerFactoryBean factoryBean =
//	                 (LocalContainerEntityManagerFactoryBean) applicationContext.getBean("&entityManagerFactory");
//
//	         // Get the Hibernate SessionFactory
//	         EntityManagerFactory entityManagerFactory = factoryBean.getNativeEntityManagerFactory();
//	         SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
//	         // Build Metadata from the SessionFactory
//	         var serviceRegistry = new StandardServiceRegistryBuilder()
//	                 .applySettings(sessionFactory.getProperties())
//	                 .build();
//
//	         var metadata = new MetadataSources(serviceRegistry).buildMetadata();
//
//	         // Use Hibernate's SchemaCreator to recreate the schema
//	         SchemaCreator schemaCreator = new StandardSchemaCreatorImpl();
//	         ExecutionOptions executionOptions = ExecutionOptions.using(sessionFactory.getServiceRegistry());
//
//	         schemaCreator.doCreation(metadata, executionOptions, EnumSet.of(TargetType.DATABASE));
//
//	         serviceRegistry.close(); // Ensure the service registry is cleaned up
//	     }

	    public void resetToDefaultDataSource() {
	        switchDataSource("learnhub"); // Replace with the default database name
	    }

}
