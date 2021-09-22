package edu.csupomona.fall2021.cs4800.CPP_Navigation_Web_Service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebController {

	@GetMapping("/PaytonPerchez")
	public String paytonHomePage() {
		return "Welcome to Payton's Homepage! https://github.com/PaytonPerchez";
	}

	@GetMapping("/jordantbui")
	public String jordanHomePage() {
		return "Welcome to Jordan's Page (https://github.com/jordantbui)";
	}

	@GetMapping("/cfortinof")
	public String cfortinoHomePage() {
		return "Hi, my name is C. Here's my github: https://github.com/Flores5505";
	}
}
