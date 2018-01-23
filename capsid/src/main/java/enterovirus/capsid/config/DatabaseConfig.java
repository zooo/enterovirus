package enterovirus.capsid.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class DatabaseConfig {

	/*
	 * No need to setup profile for "DataSource", since everything
	 * is currently using the same database setup.
	 */
	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.postgresql.Driver");
		dataSource.setUrl("jdbc:postgresql://localhost:5432/enterovirus");
//		dataSource.setUsername("enterovirus_capsid");
		dataSource.setUsername("enterovirus");
		dataSource.setPassword("zooo");
		return dataSource;
	}
}