package com.jl.mastermind;

import javax.sql.DataSource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.java.Log;


@SpringBootApplication
@Log
public class MastermindApplication {

	private final DataSource dataSource;

	public MastermindApplication(final DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public static void main(String[] args) {
		SpringApplication.run(MastermindApplication.class, args);
	}

}
