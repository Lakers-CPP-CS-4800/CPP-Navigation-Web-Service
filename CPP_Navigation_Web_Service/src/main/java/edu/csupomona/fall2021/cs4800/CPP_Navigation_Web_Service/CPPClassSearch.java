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
import java.util.regex.*;

// http://localhost:8080/sections/cs/4800

@Service
class CPPClassSearch {
    
	public static List<SectionDataDto> getSections(Map<String, String> searchParameters) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
    	
    	ArrayList<SectionDataDto> sectionList = new ArrayList<>();
    	WebClient webClient = new WebClient(BrowserVersion.BEST_SUPPORTED);
//        webClient.options.isCssEnabled = false;
//        webClient.options.isJavaScriptEnabled = false;
        HtmlPage searchPage = webClient.getPage("https://schedule.cpp.edu");

        for(Map.Entry<String, String> entry : searchParameters.entrySet()) {
        	
        	searchPage.getElementById(entry.getKey()).setAttribute("value", entry.getValue());
        	System.out.println(entry);
        	
        }// end for
        
        HtmlSelect term = ((HtmlSelect) searchPage.getElementsById("ctl00_ContentPlaceHolder1_TermDDL").get(0));
        HtmlOption opt = term.getOptionByValue(searchParameters.get("ctl00_ContentPlaceHolder1_TermDDL"));
        term.setSelectedAttribute(opt, true);

//        println(searchPage.getElementById("ctl00_ContentPlaceHolder1_TermDDL"));
//
//        println(searchParameters);

        DomElement resetForm = searchPage.getElementById("ctl00_ContentPlaceHolder1_Button4");
        DomElement submit = searchPage.getElementById("ctl00_ContentPlaceHolder1_SearchButton");
        HtmlPage resultsPage = null;
		try {
			resultsPage = submit.click();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("submission error occurred");
		}
        //println(resultsPage.body.visibleText);
        DomNodeList<HtmlElement> resultElements = ((DomElement)resultsPage.getByXPath("/html/body/main/div/section/form/div[3]/ol").get(0))//.getFirstElementChild()
            .getElementsByTagName("li");
        
        for(HtmlElement entry : resultElements) {
        	
        	sectionList.add(extractSectionData(/*(DomElement)*/entry));
        	//println(sectionList.last());
        	
        }// end for
        
        webClient.close();
        return sectionList;
    }
	
	public static SectionDataDto extractSectionData(DomElement sectionElement) {
        
    	DomNodeList<HtmlElement> sectionTableData = sectionElement.getElementsByTagName("td");
        var course = sectionElement.getFirstElementChild();
        
        boolean prevWasSpace = false;
        String timeText = sectionTableData.get(4).asNormalizedText();
        String parsedText = "";
        for(int i = 0; i < timeText.length(); i++) {
        	
        	if(!prevWasSpace) {
        		
        		if(timeText.charAt(i) == ' ') {
            		
            		prevWasSpace = true;
            		
            	}// end if
        		
        		parsedText += timeText.charAt(i);
        		
        	} else if(timeText.charAt(i) != ' ') {
        		
        		prevWasSpace = false;
        		parsedText += timeText.charAt(i);
        		
        	}// end if
        	
        }// end for
        
        return new SectionDataDto (
        		course.asNormalizedText().split(" ")[0], // subject
                course.asNormalizedText().split(" ")[1], // catalog #
                sectionTableData.get(8).getTextContent().split(",")[0].trim(), // instructor last
                sectionTableData.get(8).getTextContent().split(",")[1].trim(), // instructor first
                //sectionTableData.get(4).asNormalizedText().replace(Regex("\\s+"), " ").replace("–", "-"),
                parsedText.replace("–", "-"), // time
                sectionTableData.get(5).getTextContent().trim() // location

            /*
            sectionNumber = course.nextSibling.asNormalizedText().split(' ')[1],
            classNumber = sectionTableData[0].textContent.trim(),
            capacity = sectionTableData[1].textContent.trim().toIntOrNull(),
            title = sectionTableData[2].textContent.trim(),
            units = sectionTableData[3].textContent.trim().toIntOrNull(),
            time = sectionTableData[4].asNormalizedText().replace(Regex("\\s+"), " ").replace("–", "-"),
            location = sectionTableData[5].textContent.trim(),
            date = sectionTableData[6].textContent.trim(),
            session = sectionTableData[7].textContent.trim(),
            mode = sectionTableData[9].textContent.split(",")[1].trim(),
            component = sectionTableData[9].textContent.split(",")[0].trim()
            */

        );
    }
	
	public static HashMap<String, String> buildSearchParams(
	    ClassSubject subject,
	    String catalogNumber,
	    //ArrayList<ClassDays> possibleDays,
	    //CourseTime startTime,
	    //CourseTime endTime,
	    String instructor,
	    CourseTime ... times
	) {
		HashMap<String, String> params = new HashMap<>();
	    params.put("ctl00_ContentPlaceHolder1_TermDDL", "2217");
	    params.put("ctl00_ContentPlaceHolder1_ClassSubject", subject != null ? subject.name() : "cs");
	    params.put("ctl00_ContentPlaceHolder1_CatalogNumber", catalogNumber != null ? catalogNumber : "");
	    //params["ctl00_ContentPlaceHolder1_Description"] = title ?: "";
	    //params["ctl00_ContentPlaceHolder1_CourseComponentDDL"] = courseComponent?.value ?: "Any Component"
	    //params["ctl00_ContentPlaceHolder1_CourseAttributeDDL"] = courseAttribute?.value ?: "Any Attribute"
	    //params["ctl00_ContentPlaceHolder1_CourseCareerDDL"] = courseCareer?.value ?: "Any Career"
	    //params["ctl00_ContentPlaceHolder1_InstModesDDL"] = instructionMode?.value ?: "Any Mode"
	    //params["ctl00_ContentPlaceHolder1_SessionDDL"] = courseSession?.value ?: "Any Session"
	    params.put("ctl00_ContentPlaceHolder1_StartTime", times.length > 1 ? times[0].toString() : "ANY");// = startTime?.value ?: "ANY";
	    params.put("ctl00_ContentPlaceHolder1_EndTime", times.length > 1 ? times[1].toString() : "ANY");
	    params.put("ctl00_ContentPlaceHolder1_Instructor", instructor != null ? instructor : "");

	    return params;
	}
}

@JsonAutoDetect(fieldVisibility = Visibility.ANY)
class SectionDataDto {
    String subject;
    String catalogNumber;
    String instructorLast;
    String instructorFirst;
    String time;
    String location;

    /*
    String classNumber;
    String sectionNumber;
    int capacity;
    String title;
    int units;
    String time;
    String location;
    String date;
    String session;
    String mode;
    String component;
    */
    
    SectionDataDto(String subject, String catalogNumber, String instructorLast, String instructorFirst, String time, String location) {
    	
    	this.subject = subject;
    	this.catalogNumber = catalogNumber;
    	this.instructorLast = instructorLast;
    	this.instructorFirst = instructorFirst;
    	this.time = time;
    	this.location = location;
    	
    }
}