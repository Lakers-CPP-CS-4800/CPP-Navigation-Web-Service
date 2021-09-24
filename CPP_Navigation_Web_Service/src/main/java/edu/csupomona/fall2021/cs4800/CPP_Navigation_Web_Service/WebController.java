package edu.csupomona.fall2021.cs4800.CPP_Navigation_Web_Service;

import org.apache.commons.math3.util.FastMath;
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
	
	@GetMapping("/ryanchiu")
	public String ryanHomePage() {
		return "Placeholder, you can check out my page for now: https://github.com/ryanchiu2017";
	}
	
	@GetMapping("/congzeng")
	public String czengHomePage() {
		return "Checkout my github: https://github.com/ZohnnyInc ";
	}
	
	@GetMapping("/PaytonPerchez/pi")
	public String piApproximation() {
		return "Approximation of pi using FastMath trigonometric functions: "
				+ (100000.0 / FastMath.tan(FastMath.PI * (0.5 - (1.0 / 100000))));
	}
	
	@GetMapping("/jordantbui/guava")
	public boolean compareNull() {
		return Objects.equal(null, "a"); // returns false
	}
}
