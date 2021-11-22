package edu.csupomona.fall2021.cs4800.CPP_Navigation_Web_Service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class was HEAVILY inspired by the following repository:
 * https://github.com/voidstarr/CPPScheduleAPI/blob/master/src/main/kotlin/com/broncomoredirect/api/CPPScheduleService.kt
 * 
 * Minor adjustments made to run in Java.
 */
@Service
class CPPClassSearch {
    
	/**
	 * Provides all of the course sections returned from schedule.cpp.edu based on the given search parameters.
	 * @param searchParameters The given search parameters.
	 * @return Course sections returned from schedule.cpp.edu.
	 * @throws FailingHttpStatusCodeException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static List<SectionDataDto> getSections(Map<String, String> searchParameters)
			throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		
		// Course sections
		ArrayList<SectionDataDto> sectionList = new ArrayList<>();
		
		WebClient webClient = new WebClient(BrowserVersion.BEST_SUPPORTED);
		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setJavaScriptEnabled(false);
		
		HtmlPage searchPage = webClient.getPage("https://schedule.cpp.edu");
		
		// Fill in the search page with the search parameters
		for(Map.Entry<String, String> searchParameter : searchParameters.entrySet()) {
			
			searchPage.getElementById(searchParameter.getKey()).setAttribute("value", searchParameter.getValue());
			
		}// end for
		
		// Get the current term
		HtmlSelect term = ((HtmlSelect) searchPage.getElementsById("ctl00_ContentPlaceHolder1_TermDDL").get(0));
		
		// Set the selected term as the current term
		HtmlOption opt = term.getOptionByValue(searchParameters.get("ctl00_ContentPlaceHolder1_TermDDL"));
		term.setSelectedAttribute(opt, true);
		
		// The search button
		DomElement search = searchPage.getElementById("ctl00_ContentPlaceHolder1_SearchButton");
		
		// Get search results from schedule.cpp.edu
		HtmlPage resultsPage = search.click();
		
		// Get course sections as elements
		DomNodeList<HtmlElement> resultElements = ((DomElement)resultsPage.getByXPath("/html/body/main/div/section/form/div[3]/ol").get(0))
			.getElementsByTagName("li");
		
		// Extract course data from elements
		for(HtmlElement course : resultElements) {
			
			sectionList.add(extractSectionData(course));
			
		}// end for
		
		webClient.close();
		return sectionList;
		
	}// end getSections
	
	/**
	 * Extracts section data from the specified dom element.
	 * @param sectionElement The specified dom element.
	 * @return Section data of the specified element.
	 */
	public static SectionDataDto extractSectionData(DomElement sectionElement) {
		
		DomNodeList<HtmlElement> sectionTableData = sectionElement.getElementsByTagName("td");
		DomElement course = sectionElement.getFirstElementChild();
		
		// Initialize variables for parsing the course time
		boolean prevWasSpace = false;
		String timeText = sectionTableData.get(4).asNormalizedText();
		String parsedText = "";
		
		// Remove duplicate ' ' characters from timeText
		for(int i = 0; i < timeText.length(); i++) {
			
			// Don't count extra spaces
			if(!prevWasSpace) {
				
				// Acknowledge that a space is being added
				if(timeText.charAt(i) == ' ') {
					
					prevWasSpace = true;
					
				}// end if
				
				parsedText += timeText.charAt(i);
				
			// Add all characters that are not duplicate spaces
			} else if(timeText.charAt(i) != ' ') {
				
				prevWasSpace = false;
				parsedText += timeText.charAt(i);
				
			}// end if
			
		}// end for
		
		return new SectionDataDto (
				course.asNormalizedText().split(" ")[0], 						// subject
				course.asNormalizedText().split(" ")[1], 						// catalog #
				sectionTableData.get(8).getTextContent().split(",")[0].trim(),	// instructor last
				sectionTableData.get(8).getTextContent().split(",")[1].trim(),	// instructor first
				parsedText.replace("–", "-"),									// time
				sectionTableData.get(5).getTextContent().trim()					// location
		);
		
	}// end extractSectionData
	
	/**
	 * Associates course section parameters with their respective html elements in schedule.cpp.edu.
	 * @param subject The course subject.
	 * @param catalogNumber The course catalog number.
	 * @param instructor The course instructor.
	 * @param times The course time.
	 * @return A hash map where each key represents an html element in schedule.cpp.edu and each value is
	 * the user input.
	 */
	public static HashMap<String, String> buildSearchParams(
		ClassSubject subject,
		String catalogNumber,
		String instructor,
		CourseTime ... times
	) {
		HashMap<String, String> params = new HashMap<>();
		params.put("ctl00_ContentPlaceHolder1_TermDDL", "2217");	// set the term to Fall 2021
		params.put("ctl00_ContentPlaceHolder1_ClassSubject", subject != null ? subject.name() : "cs");
		params.put("ctl00_ContentPlaceHolder1_CatalogNumber", catalogNumber != null ? catalogNumber : "");
		params.put("ctl00_ContentPlaceHolder1_StartTime", times.length > 1 ? times[0].toString() : "ANY");
		params.put("ctl00_ContentPlaceHolder1_EndTime", times.length > 1 ? times[1].toString() : "ANY");
		params.put("ctl00_ContentPlaceHolder1_Instructor", instructor != null ? instructor : "");
		
		return params;
		
	}// end buildSearchParams
	
}// end CPPClassSearch

@JsonAutoDetect(fieldVisibility = Visibility.ANY)
class SectionDataDto {
	
	String subject;
	String catalogNumber;
	String instructorLast;
	String instructorFirst;
	String time;
	String location;
	
	/**
	 * Instantiates a new SectionDataDto object with the specified parameters.
	 * @param subject The course subject.
	 * @param catalogNumber The course catalog number.
	 * @param instructorLast The course instructor's last name.
	 * @param instructorFirst The course instructor's first name.
	 * @param time The course time.
	 * @param location The course location.
	 */
	SectionDataDto(String subject, String catalogNumber, String instructorLast, String instructorFirst, String time, String location) {
		
		this.subject = subject;
		this.catalogNumber = catalogNumber;
		this.instructorLast = instructorLast;
		this.instructorFirst = instructorFirst;
		this.time = time;
		this.location = location;
		
	}// end constructor
	
}// end SectionDataDto