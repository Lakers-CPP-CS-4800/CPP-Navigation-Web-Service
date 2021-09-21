package edu.csupomona.fall2021.cs4800.CPP_Navigation_Web_Service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebController {

	@GetMapping("/PaytonPerchez")
	public String paytonHomePage() {
		return "Welcome to Payton's Homepage!"
				+ "\nGitHub Profile: https://github.com/PaytonPerchez";
	}
}
