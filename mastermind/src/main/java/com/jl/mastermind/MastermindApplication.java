package com.jl.mastermind;

import javax.sql.DataSource;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

import lombok.extern.java.Log;


@SpringBootApplication
@Log
public class MastermindApplication {
// public class MastermindApplication implements CommandLineRunner {

	private final DataSource dataSource;

	public MastermindApplication(final DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public static void main(String[] args) {
		SpringApplication.run(MastermindApplication.class, args);
	}

	// @Override
	// public void run(final String...args) {
	// 	log.info("Datasource: " + dataSource.toString());
	// 	final JdbcTemplate restTemplate = new JdbcTemplate(dataSource);
	// 	restTemplate.execute("SELECT 1");
	// }


}
