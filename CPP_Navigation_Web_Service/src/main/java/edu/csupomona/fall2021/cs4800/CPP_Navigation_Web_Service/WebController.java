package edu.csupomona.fall2021.cs4800.CPP_Navigation_Web_Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.math3.util.FastMath;
//import org.apache.commons.text.translate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

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

	@GetMapping("/cfortinof/dependency")
	public String dependencies()
	{
		String path = "CPP-Navigation-Web-Service\\CPP_Navigation_Web_Service\\pom.xml";
		return "Our dependencies are managed by maven in " + FilenameUtils.getName(path) + " .";
	}
	
	@PostMapping("/js/getMap")
	public String getMap() {
		String map = readFile("/static/Temp.txt");
		return map;
	}
	
	@PostMapping("/js/getLocations")
	public String getLocations() {
		String locations = readFile("/static/LocationsJSON.txt");
		return locations;
	}
	
	public String readFile(String fileName) {
		Resource resource  = new ClassPathResource(fileName);
		String text;
		
		try {
			InputStream inputStream = resource.getInputStream();
			text = new String(inputStream.readAllBytes());
			inputStream.close();
		}
		catch(Exception e){
			text = e.toString();
		}
		
		return text;
	}
	
	// This method was taken from the following repository:
	// https://github.com/voidstarr/CPPScheduleAPI/blob/master/src/main/kotlin/com/broncomoredirect/api/BroncoMoreDirectAPIApplication.kt
	@GetMapping("/sections/{subject}/{catalogNumber}")
	public ResponseEntity<List<SectionDataDto>> searchSections(
		@PathVariable("subject") String subject,
		@PathVariable("catalogNumber") String catalogNumber
	) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		
		return ResponseEntity.ok(
			CPPClassSearch.getSections(
				CPPClassSearch.buildSearchParams(
					ClassSubject.valueOf(subject.toUpperCase()),
					catalogNumber,
					null
				)
			)
		);
	}
	
	/*@GetMapping("/jordantbui/guava")
	public boolean compareNull() {
		return Objects.equal(null, "a"); // returns false
	}
	
	@GetMapping("/congzeng/translate")
	public void whenTranslate_thenCorrect() {
		UnicodeEscaper ue = UnicodeEscaper.above(0);
		String result = ue.translate("ABCD");
    		assertEquals("\\u0041\\u0042\\u0043\\u0044", result);
	}*/
}
