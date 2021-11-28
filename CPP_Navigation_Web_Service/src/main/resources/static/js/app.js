$( document ).ready(init());

var nav_bar_open;
var classes;
var directionsService;
var directionsRenderer;
var map;
var locations;
var markers = [];
var currentLocation;
var currentDestination;

//make sure nothing triggers before the page is ready
function init(){
	//load button bar and variables
	nav_bar_open = false;
	$("#toggleNavBar").on("click",function(){
		toggleBar();
	});
	
	//initialize the location array
	loadLocations();
	
	adjustMapSize();
	readSavedClasses();
	/*if(window.innerWidth >= 800){
		toggleBar();
	}*/
}

function toggleBar(){
	var bar = document.getElementById("navBar");
	if(nav_bar_open){
		bar.style.marginRight = "-280px";
	}
	else{
		bar.style.marginRight = "0";
	}
	nav_bar_open = !nav_bar_open;
}

function adjustMapSize(){
	$("#map").css("height",window.innerHeight + "px");
}

//classroom functions
function showClassMenu(){
	$("#classMenu").show();
	$("#backdrop").show();
}

function hideClassMenu(){
	$("#classMenu").hide();
	$("#backdrop").hide();
	$("#classSearchBar").val('');
}

//cookie functions
function readSavedClasses(){
	var json = readCookies("classes");
	if(json != ""){
		classes =  JSON.parse(json);
	}
	else classes = new Array();
}

function writeCookies(c_name, c_value){
	const current = new Date();
	current.addMonths(1);
	var expires = "expires="+ current.toUTCString();
	document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
}

function readCookies(c_name){
	var data = "";
	var cookie_string = decodeURIComponent(document.cookie);
	
	var begin = cookie_string.search(c_name + "=");
	if(begin > -1){
		var end = cookie_string.indexOf(";", begin);
		data = cookie_string.substring(begin + c_name.length + 1, end );
	}
	return data;
}

// Initializes google map
function initMap(){
	
	directionsService = new google.maps.DirectionsService();
	directionsRenderer = new google.maps.DirectionsRenderer();
	
	const cpp = { lat: 34.058881, lng: -117.819725 };
	
	// The map, centered at CPP
	map = new google.maps.Map(document.getElementById("map"), {
		zoom: 12,
		center: cpp,
	});
	
	directionsRenderer.setMap(map);
}

// Returns the index of the specified building
function getBldgIndex(string) {
	
	for(let i = 0; i < locations.length; i++) {
		
		// Return the correct index if the building is found
		if(locations[i][0].includes(string)) {
			return i;
		}
	}
	
	// Return -1 if the building is not in the array
	return -1;
}

// Displays the walking directions from currentLocation to currentDestination
function getDirections() {
	
	var start = null;
	var end = null;
	
	// Get the current destination from the search field
	currentDestination = document.getElementById("instantSearchBar").value;
	currentDestIndex = getBldgIndex(currentDestination);
	
	// If the manual location text field is not disabled, then GPS location isn't being used
	if(document.getElementById("currentLocation").getAttribute("disabled") == null) {
		
		// Get the current location from the manual location text field
		currentLocation = document.getElementById("currentLocation").value;
		currentBldgIndex = getBldgIndex(currentLocation);
		
		// Set start and end locations if the current location and current destination are valid
		if((currentBldgIndex >= 0) && (currentDestIndex >= 0) && (currentBldgIndex != currentDestIndex)) {
			
			start = { lat: locations[currentBldgIndex][1], lng: locations[currentBldgIndex][2] };
			end = { lat: locations[currentDestIndex][1], lng: locations[currentDestIndex][2] };
		}
		
	// GPS is being used
	} else {
		
		if(currentDestIndex >= 0) {
			
			// Ensure the index is valid
			start = { lat: currentLocation.coords.latitude, lng: currentLocation.coords.longitude };
			end = { lat: locations[currentDestIndex][1], lng: locations[currentDestIndex][2] };
		}
	}
	
	// Display the walking route if start and end locations are provided
	if((start != null) && (end != null)) {
		var request = {
				origin: start,
				destination: end,
				travelMode: "WALKING"
		};
		
		// API call
		directionsService.route(request, function(result, status) {
			if (status == "OK") {
				directionsRenderer.setDirections(result);
			}
		});
	}
}

function setColor(color) {
	var property = document.getElementById("instantSearchGPSButton");
	property.style.backgroundColor = color;
}

function setCurrentLocation(position) {
	currentLocation = position;
}

function getLocation() {
	if (navigator.geolocation) {
		navigator.geolocation.getCurrentPosition(setCurrentLocation);
		setColor("#00FF00");
		$("#currentLocation").attr("disabled", "disabled");
	} else {
		x.innerHTML = "Geolocation is not supported by this browser.";
	}
}

function findBldg(bldgIndex) {
	
	// Ensure the index is valid
	if(bldgIndex >= 0) {
		markers.push(
			new google.maps.Marker({
				position: new google.maps.LatLng(locations[bldgIndex][1], locations[bldgIndex][2]),
				title: locations[bldgIndex][0],
				map: map
			})
		);
	}
}

function clearMarkers() {
	for(let i = 0; i < markers.length; i++) {
		markers[i].setMap(null);
	}
	markers = [];
}

/* * * * * * * * * * * *
 * =-=-=-=-=-=-=-=-=-= *
 * Click Even Handlers *
 * =-=-=-=-=-=-=-=-=-= *
 * * * * * * * * * * * */

function checkboxClick() {
	
	if(document.getElementById("checkbox").checked == true) {
		
		// Prompt for manual location or GPS
		document.getElementById("manualCurrentLocation").style.display = "";
		
	} else {
		
		// Hide manual location controls
		document.getElementById("manualCurrentLocation").style.display = "none";
		
	}
}

function searchBtnClick() {
	
	clearMarkers();
	
	if(document.getElementById("checkbox").checked == true) {
		
		getDirections();
		
	} else {
		
		directionsRenderer.set("directions", null);
		findBldg(getBldgIndex(document.getElementById("instantSearchBar").value));
		
	}
}

function gpsClick() {
	
	// If the manual location text field is not disabled, then GPS location isn't being used
	if(document.getElementById("currentLocation").getAttribute("disabled") == null) {
		
		// Set currentLocation as the user's gps location
		// Disable the manual location text field if successful
		// Set the background color of the button to green if successful
		getLocation();
		
	} else {
		currentLocation = null;							// reset current location
		setColor("#D7D7D7");							// reset background color of button
		$('#currentLocation').removeAttr("disabled");	// enable manual location text field
	}
}

function getCourseInfo() {
	$.ajax({
        type: "GET",
        contentType: "application/json",
		url: "/sections/" + document.getElementById("subject").value + "/" + document.getElementById("num").value,
		success: function (data){
			
			// Clear courses already being displayed
			$("#list").empty();
			
			for(const [key, value] of Object.entries(data)) {
				
				// Create an unordered list representing the course
				var course = document.createElement("ul");
				
				for(const [key1, value1] of Object.entries(value)) {
					
					// Add the course time, instructor last name, and location to the "course" unordered list
					if(key1.includes("time") ||
					   key1.includes("instructorLast") ||
					   (key1.includes("location") && (value1.toUpperCase() != "")))
					{
						var courseAttribute = document.createElement("li");
						courseAttribute.appendChild(document.createTextNode(`${value1}`));
						course.appendChild(courseAttribute);
					}
				}
				
				var listItem = document.createElement("li");
				listItem.appendChild(document.createTextNode("-=-=-=-=-=-=-=-=-=-=-=-=-"));	// separates courses with an empty line
				
				// Add the "course" unordered list as a list item of the list of courses
				listItem.appendChild(course);
				document.getElementById("list").appendChild(listItem);
				
			}
		}
	});
}

function loadLocations() {
	locations = [
		["Building 1: Building One", 34.059631520066574, -117.82426000336348],
		["Building 2: Huntley College of Agriculture", 34.05760344926454, -117.8267557370109],
		["Building 3: Science Laboratory", 34.0581124768776, -117.82575743857959],
		["Building 4: Biotechnology Building", 34.057516026052625, -117.82552701665345],
		["Building 4A: BioTrek Learning Center", 34.057213795556514, -117.8260645585157],
		["Building 5: College of Letters, Arts and Social Sciences", 34.05780236764564, -117.82445190600853],
		["Building 6: College of Education and Integrative Studies", 34.058613597726854, -117.82282003876132],
		["Building 7: College of Environmental Design", 34.05712624562348, -117.82739885280135],
		["Building 8: College of Science", 34.0585658246808, -117.82481672192716],
		["Building 9: College of Engineering", 34.05917825650974, -117.82230168148551],
		["Building 13: Art Department and Engineering Annex", 34.058764725011, -117.82082335393085],
		["Building 13B: Army ROTC", 34.058215981298865, -117.8207849532018],
		["Building 13C: Army ROTC", 34.05836708495806, -117.8206985591866],
		["Building 13D: Pre-College TRIO Programs", 34.05851023600962, -117.82042977550269],
		["Building 15: Library", 34.05800125076911, -117.82150490566264],
		["Building 17: Engineering Laboratories", 34.05998150068934, -117.82146653437995],
		["Building 20: Residence Hall, Encinitas", 34.06236734848226, -117.82054497794185],
		["Building 21: Residence Hall, Montecito", 34.062279862977576, -117.81925859573167],
		["Building 22: Residence Hall, Alamitos", 34.06230370556362, -117.81808741148302],
		["Building 23: Residence Hall, Aliso", 34.06297968797636, -117.81776099535918],
		["Building 24: Music", 34.05689579182925, -117.82277199228754],
		["Building 24A: Temporary Classroom A", 34.056283431157375, -117.82246480077345],
		["Building 24B: Temporary Classroom B", 34.05617209327843, -117.82237840542172],
		["Building 24C: Temporary Classroom C", 34.055949412155655, -117.82252238730575],
		["Building 24D: Temporary Classroom D", 34.05590169711679, -117.82241679550302],
		["Building 24E: Temporary Classroom E", 34.056172089090325, -117.82261838476923],
		["Building 25: Drama Department/Theatre", 34.05641863505303, -117.82207123688254],
		["Building 26: University Plaza", 34.056983296615684, -117.82033378128855],
		["Building 26A: Student Orientation Center", 34.05668904248401, -117.82055456366817],
		["Building 28: Fruit/Crops Unit", 34.06002885472723, -117.81081096710467],
		["Building 29: W.K. Kellogg Arabian Horse Center", 34.058764604716195, -117.814871684067],
		["Building 30: Agricultural Unit", 34.05506640249697, -117.82846417575188],
		["Building 31: Poultry Unit/Poultry Houses", 34.05461309800921, -117.82837774134732],
		["Building 32: Beef Unit/Feed Shed", 34.05542433271566, -117.8276002920111],
		["Building 33: Feedmill", 34.053937160157034, -117.8275521700886],
		["Building 34: Meat Laboratory", 34.0538416914779, -117.82812809657125],
		["Building 35: Bronco Student Center", 34.05647431178761, -117.82145688869723],
		["Building 35A: W. Keith and Janet Kellogg University Art Gallery", 34.05699919372779, -117.82179286995775],
		["Building 37: Swine Unit/Shelters", 34.052132056225766, -117.82290623783607],
		["Building 38: Sheep/Wool Unit", 34.05216387868576, -117.82229192071715],
		["Building 41: Darlene May Gymnasium", 34.0539930368061, -117.82126487755953],
		["Building 42: Bronco Recreation Intramural Complex (BRIC)", 34.054613358695526, -117.82075613744178],
		["Building 43: Kellogg Arena", 34.054215713080936, -117.81914351151016],
		["Building 44: Swimming Pool", 34.0535715413108, -117.82042977046899],
		["Building 45: Apparel Merchandising and Management", 34.06113431569635, -117.81107963537343],
		["Building 46: Health Services", 34.05775447251989, -117.828147654641],
		["Building 47: Agricultural Engineering Tractor Shop", 34.05982980059373, -117.8081519035563],
		["Building 48: Custodial Offices", 34.058994779995516, -117.80840161050499],
		["Building 49: Training Center", 34.05986966869773, -117.80925585098674],
		["Building 52: Vista Market", 34.05391348750753, -117.81787645859485],
		["Building 54: Residence Suites", 34.05559949914317, -117.81888431597754],
		["Building 55: Foundation Administration Offices", 34.056267543071286, -117.81993061561171],
		["Building 56: Storage Building", 34.061945484021926, -117.81083955100902],
		["Building 57: Residence Hall, Palmitas", 34.06055409115224, -117.82238811030776],
		["Building 58: Residence Hall, Cedritos", 34.06157206439982, -117.82124576039234],
		["Building 59: La Cienega Center (University Housing Services)", 34.061055124920756, -117.82192733550204],
		["Building 60: Residence Suites", 34.054987126057, -117.8183947754932],
		["Building 61: Residence Suites", 34.054692863029416, -117.81788603491925],
		["Building 62: Residence Suites", 34.05408843902405, -117.81740610554405],
		["Building 63: Residence Suites", 34.05396913292524, -117.81686856990979],
		["Building 64: Rose Float Laboratory", 34.06009224708904, -117.80819026427564],
		["Building 66: Bronco Bookstore", 34.05600510100255, -117.8204489712592],
		["Building 67: Animal Health Science", 34.059639378310116, -117.81412286139602],
		["Building 68: Hay Barn", 34.05417573308418, -117.82774416933448],
		["Building 70: Los Olivos Commons", 34.06242301257574, -117.82153376591027],
		["Building 71: Recreation/Maintenance", 34.06305918947279, -117.81670499799958],
		["Building 75: Procurement/Receiving", 34.05947191082911, -117.80801756136975],
		["Building 76: Kellogg West Education/Dining", 34.05664124617022, -117.82477822344536],
		["Building 76A: Kellogg West/Addition", 34.0567128066936, -117.82517179583061],
		["Building 77: Kellogg West Main Lodge", 34.0562435567727, -117.82597810038851],
		["Building 78: Kellogg West Addition", 34.05678435811499, -117.82574775450179],
		["Building 79: Collins College of Hospitality Management", 34.05493141732202, -117.82401021131412],
		["Building 79A: Collins College of Hospitality Management", 34.055297233611725, -117.82442298735693],
		["Building 79B: Collins College of Hospitality Management", 34.055082492378084, -117.82486453334548],
		["Building 80: Collins College of Hospitality Management", 34.05477231156314, -117.82541166067863],
		["Building 81: Facilities Management", 34.059384511357074, -117.80886233340624],
		["Building 82: Facilities Management Warehouse", 34.059702604825944, -117.8086606992657],
		["Building 82A: Carpenter Shop", 34.05994119544736, -117.8087278635832],
		["Building 83: Auto Shop", 34.059281063360956, -117.80821917917253],
		["Building 85: I-Poly High School", 34.05089939419179, -117.81946031148513],
		["Building 86: English Language Institute", 34.05330909685131, -117.8196810599257],
		["Building 86A: English Language Institute", 34.05320571156534, -117.81997862471096],
		["Building 86B: English Language Institute", 34.05347610729126, -117.82005541449627],
		["Building 86C: English Language Institute", 34.05329319287387, -117.82016100244442],
		["Building 89: Interim Design Center", 34.06058565540655, -117.81227005489816],
		["Building 89A: Interim Design Center", 34.06052997046267, -117.81203966818266],
		["Building 89B: Interim Design Center", 34.06008463340781, -117.81234689956128],
		["Building 90: Medic-1", 34.06130161157231, -117.81647467933851],
		["Building 91: IT Operations", 34.061341423861485, -117.81859622434612],
		["Building 92: Laboratory Facility", 34.05780229761299, -117.82618938990066],
		["Building 94: University Office Building", 34.05920209733115, -117.82320403664103],
		["Building 95: Cultural Centers", 34.05796942278004, -117.82268562865363],
		["Building 97: Campus Center", 34.05777854285688, -117.82327118408499],
		["Building 98: Classroom/Laboratory/Administration (C/L/A)", 34.05983040198532, -117.82001699455441],
		["Building 98C: CLA Classrooms", 34.05990992775197, -117.81951781519163],
		["Building 98P: CLA Paseo", 34.06010079555158, -117.81957541104558],
		["Building 98T: CLA Tower (Closed January 2019)", 34.05936118605922, -117.81995939915082],
		["Building 99: Storage Building", 34.06185801411659, -117.81098355808726],
		["Building 100: Storage Building", 34.06203295824604, -117.81075314265955],
		["Building 109: Police and Parking Services", 34.06082441870419, -117.8157643232601],
		["Building 111: Manor House", 34.06052226964143, -117.8229352905363],
		["Building 112: Kellogg House Pomona", 34.06276492235382, -117.82440414698341],
		["Building 113: Guest House", 34.0624467974394, -117.82476892735487],
		["Building 116: Child Care Center", 34.056028955517945, -117.81935466731201],
		["Building 121: Student Services Building", 34.05837503167824, -117.81906665724289],
		["Building 150: MASA Building", 34.057341171873425, -117.82088094170724],
		["Building 162: College of Business Administration", 34.06134143346169, -117.81951780124709],
		["Building 163: College of Business Administration", 34.06124600241195, -117.8201897845771],
		["Building 164: College of Business Administration", 34.06169931152014, -117.81981539155846],
		["Building 193: Chilled Water Center Plant", 34.05703862242463, -117.82955868172282],
		["Building 200: University Village", 34.04844985308753, -117.81592816278088],
		["Building 207: John T. Lyle Center for Regenerative Studies", 34.04982574399903, -117.82278138819515],
		["Building 208: John T. Lyle Center for Regenerative Studies", 34.04980984167038, -117.82260861557519],
		["Building 209: Center for Regenerative Studies", 34.04949963675741, -117.82438431468344],
		["Building 209C: John T. Lyle Center for Regenerative Studies", 34.049634842138325, -117.82414436028623],
		["Building 209L: John T. Lyle Center for Regenerative Studies", 34.04959507015678, -117.82439391766147],
		["Building 209R: John T. Lyle Center for Regenerative Studies", 34.0492769473199, -117.82470105104308],
		["Building 209S: John T. Lyle Center for Regenerative Studies", 34.04953143448973, -117.82477785149966],
		["Building 209W: John T. Lyle Center for Regenerative Studies", 34.04968255011323, -117.82442271715831],
		["Building 210: John T. Lyle Center for Regenerative Studies", 34.049603083288964, -117.8215719796031],
		["Building 211: AGRIscapes/Farm Store", 34.04817157917745, -117.81928757107424],
		["Building 211A: Agriscapes", 34.048282915816685, -117.81900921955018],
		["Building 215: Edison SCE2", 34.05189314388656, -117.8109846584171],
		["Building 216: Innovation Village SCE", 34.050994661589485, -117.81387393249037],
		["Building 218: American Red Cross Headquarters", 34.053531446233094, -117.81130124041697],
		["Building 219: Edison SCE1", 34.052497704436384, -117.81318270722008],
		["Building 220A: Center for Training, Technology and Incubation", 34.049921091380455, -117.81512181500823],
		["Building 220B: Center for Training, Technology and Incubation", 34.05036645233417, -117.81520817422602],
		["Building 220C: Center for Training, Technology and Incubation", 34.05012784802998, -117.81474746118333],
		["Building 65: Pesticide Building", 34.05994139715458, -117.81111816416839],
		["Building 72: Centerpointe Dining", 34.05682423063423, -117.81878830241759],
		["Building 73: Sicomoro Hall", 34.056275481646495, -117.81842354343202],
		["Building 74: Secoya Hall", 34.05633113310398, -117.81748282136374],
		["Building 81A: Environmental Health and Safety", 34.058796001674054, -117.80884321418458]
	];
}