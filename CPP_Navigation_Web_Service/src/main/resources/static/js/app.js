$( document ).ready(init());

var nav_bar_open;
var classes;
var directionsService;
var directionsRenderer;

//make sure nothing triggers before the page is ready
function init(){
	//load button bar and variables
	nav_bar_open = false;
	$("#toggleNavBar").on("click",function(){
		toggleBar();
	});
	
	//load in the map
	//loadMap();
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

//function loadMap(){
//$.ajax({
//    type: "POST",
//    contentType: "application/json",
//	url: "/js/getMap",
//	success: function (data){
//		$("#map").html(data);
//	}
//});
//}

// Initializes google map
function initMap(){
	
	directionsService = new google.maps.DirectionsService();
    directionsRenderer = new google.maps.DirectionsRenderer();
    const cpp = { lat: 34.058881, lng: -117.819725 };
    // The map, centered at CPP
    var map = new google.maps.Map(document.getElementById("map"), {
      zoom: 12,
      center: cpp,
    });
    directionsRenderer.setMap(map);
}

// Calculates route between start and end
function calcRoute() {
	/*
	var dest1 = document.getElementById('start').value	// Get start building (Ex: "Building 8")
	var dest2 = document.getElementById('end').value;	// Get end building (Ex: "Building 1")
	
	let startIndex = getIndex(dest1);	// Get the index of the start building in the location array
	let endIndex = getIndex(dest2);		// Get the index of the end building in the location array
	
	// Ensure the indices are valid
	if((startIndex >= 0) && (endIndex >= 0) && (startIndex != endIndex)) {
	
		var start = { lat: locations[startIndex][1], lng: locations[startIndex][2] } // lat and lng of start building
		var end = { lat: locations[endIndex][1], lng: locations[endIndex][2] }		 // lat and lng of end building
		
		var request = {
			origin: start,
			destination: end,
			travelMode: 'WALKING'
		};
		
		// API call
		directionsService.route(request, function(result, status) {
			if (status == 'OK') {
				directionsRenderer.setDirections(result);
			}
		});
	}
	*/
}

// Returns the index of the specified building
function getIndex(string) {
	  /*
	  for(let i = 0; i < locations.length; i++) {
		  if(locations[i][0].includes(string)) {
			  return i;
		  }
	  }
	  return -1;
	  */
}

// Creates markers on the map for every building at CPP
function createMarkers() {
	/*
	const markers = [];
    
    for (let i = 0; i < locations.length; i++) {
    	markers.push(
    		new google.maps.Marker({
    	       	position: new google.maps.LatLng(locations[i][1], locations[i][2]),
    	     	title: locations[i][0],
    	     	map: map
    	    })
    	);
    }
    
    // Hiding a marker
    markers[0].setMap(null);
    */
}

// Temporary function for backend testing
function getInfo() {
	//console.log(document.getElementById("subject").value + ", " + document.getElementById("num").value);
	$.ajax({
        type: "GET",
        contentType: "application/json",
		url: "/sections/" + document.getElementById("subject").value + "/" + document.getElementById("num").value,
		success: function (data){
			$("#jsonTest").text(data);
			console.log(data);
		}
	});
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
