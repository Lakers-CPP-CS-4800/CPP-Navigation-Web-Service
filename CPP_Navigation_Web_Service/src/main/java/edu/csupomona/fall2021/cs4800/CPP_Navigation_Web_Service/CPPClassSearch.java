import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;



@Service
class CPPClassSearch {
    
	public List<SectionDataDto> getSections(Map<String, String> searchParameters) {
    	
    	ArrayList<SectionDataDto> sectionList = new ArrayList<>();
    	WebClient webClient = new WebClient(BrowserVersion.BEST_SUPPORTED);
        webClient.options.isCssEnabled = false;
        webClient.options.isJavaScriptEnabled = false;
        var searchPage = webClient.getPage<HtmlPage>("https://schedule.cpp.edu");

        searchParameters.forEach { searchPage.getElementById(it.key).setAttribute("value", it.value) }

        HtmlSelect term = searchPage.getElementsById("ctl00_ContentPlaceHolder1_TermDDL")[0];
        HtmlOption opt = term.getOptionByValue(searchParameters["ctl00_ContentPlaceHolder1_TermDDL"]);
        term.setSelectedAttribute<HtmlPage>(opt, true);

//        println(searchPage.getElementById("ctl00_ContentPlaceHolder1_TermDDL"));
//
//        println(searchParameters);

        var resetForm = searchPage.getElementById("ctl00_ContentPlaceHolder1_Button4");
        var submit = searchPage.getElementById("ctl00_ContentPlaceHolder1_SearchButton");
        var resultsPage = submit.click<HtmlPage>();
        //println(resultsPage.body.visibleText);
        resultsPage.getByXPath<HtmlOrderedList>("/html/body/main/div/section/form/div[3]/ol").first()
            .getElementsByTagName("li").forEach {
                sectionList.add(extractSectionData(it))
                //println(sectionList.last());
            }

        webClient.close();
        return sectionList;
    }
}

class SectionDataDto{
    String subject;
    String catalogNumber;
    String sectionNumber;
    String classNumber;
    int capacity;
    String title;
    int units;
    int time;
    String location;
    String date;
    String session;
    String instructorLast;
    String instructorFirst;
    String mode;
    String component;
}

fun extractSectionData(sectionElement: DomElement): SectionDataDto {
    val sectionTableData = sectionElement.getElementsByTagName("td")
    val course = sectionElement.childElements.first()

    return SectionDataDto(
        subject = course.asNormalizedText().split(' ')[0],
        catalogNumber = course.asNormalizedText().split(' ')[1],
        sectionNumber = course.nextSibling.asNormalizedText().split(' ')[1],
        classNumber = sectionTableData[0].textContent.trim(),
        capacity = sectionTableData[1].textContent.trim().toIntOrNull(),
        title = sectionTableData[2].textContent.trim(),
        units = sectionTableData[3].textContent.trim().toIntOrNull(),
        time = sectionTableData[4].asNormalizedText().replace(Regex("\\s+"), " ").replace("–", "-"),
        location = sectionTableData[5].textContent.trim(),
        date = sectionTableData[6].textContent.trim(),
        session = sectionTableData[7].textContent.trim(),
        instructorLast = sectionTableData[8].textContent.split(",")[0].trim(),
        instructorFirst = sectionTableData[8].textContent.split(",")[1].trim(),
        mode = sectionTableData[9].textContent.split(",")[1].trim(),
        component = sectionTableData[9].textContent.split(",")[0].trim()
    )
}