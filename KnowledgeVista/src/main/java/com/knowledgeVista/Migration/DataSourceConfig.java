package com.knowledgeVista.Migration;

import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceConfig {

//    @Value("${spring.datasource.username}")
//    private String username;
//
//    @Value("${spring.datasource.password}")
//    private String password;
//
//    @Value("${spring.datasource.driver-class-name}")
//    private String driverClassName;
//    
//    @Value("${spring.datasource.url}")
//    private String defaultDatabaseUrl; 

//    @Bean
//    public DataSource dataSource() {
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName(driverClassName);
//        dataSource.setUrl("jdbc:postgresql://localhost:9000/backup");  // Use the newly created database
//        dataSource.setUsername(username);
//        dataSource.setPassword(password);
//        return dataSource;
//    }
//    public DataSource createDynamicDataSource(String dbName) {
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName(driverClassName);
//        dataSource.setUrl("jdbc:postgresql://localhost:9000/" + dbName);  // Dynamic DB URL
//        dataSource.setUsername(username);
//        dataSource.setPassword(password);
//        return dataSource;
//    }
//    
//    @Primary // Default DataSource
//    public DataSource createDefaultDataSource() {
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName(driverClassName);
//        dataSource.setUrl(defaultDatabaseUrl); // Use the default database URL
//        dataSource.setUsername(username);
//        dataSource.setPassword(password);
//        return dataSource;
//    }
//	 @Bean
//	    @Primary
//	    public DataSource dataSource() {
//	        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//	        dataSource.setDriverClassName("org.postgresql.Driver");
//	        dataSource.setUrl("jdbc:postgresql://localhost:9000/learnhub"); // Default database
//	        dataSource.setUsername("postgres");
//	        dataSource.setPassword("admin");
//	        return dataSource;
//	    }
	
//	 @Bean
//	    public DataSource dataSource() {
//	        DynamicRoutingDataSource dynamicRoutingDataSource = new DynamicRoutingDataSource();
//
//	        DriverManagerDataSource defaultDataSource = new DriverManagerDataSource();
//	        defaultDataSource.setUrl("jdbc:postgresql://localhost:9000/learnhub");
//	        defaultDataSource.setUsername("postgres");
//	        defaultDataSource.setPassword("admin");
//	        defaultDataSource.setDriverClassName("org.postgresql.Driver");
//
//	        DriverManagerDataSource backupDataSource = new DriverManagerDataSource();
//	        backupDataSource.setUrl("jdbc:postgresql://localhost:9000/backup");
//	        backupDataSource.setUsername("postgres");
//	        backupDataSource.setPassword("admin");
//	        backupDataSource.setDriverClassName("org.postgresql.Driver");
//
//	        Map<Object, Object> dataSources = new HashMap<>();
//	        dataSources.put("learnhub", defaultDataSource);
//	        dataSources.put("backup", backupDataSource);
//
//	        dynamicRoutingDataSource.setTargetDataSources(dataSources);
//	        dynamicRoutingDataSource.setDefaultTargetDataSource(defaultDataSource);
//
//	        return dynamicRoutingDataSource;
//	    }
}
