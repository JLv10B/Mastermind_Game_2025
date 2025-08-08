package com.jl.mastermind;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.java.Log;


@SpringBootApplication
@Log
public class MastermindApplication {


	public MastermindApplication() {

	}

	public static void main(String[] args) {
		SpringApplication.run(MastermindApplication.class, args);
	}

}
