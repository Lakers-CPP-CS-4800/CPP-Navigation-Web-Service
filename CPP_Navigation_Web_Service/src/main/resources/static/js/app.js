$( document ).ready(init());

var nav_bar_open;
var classes;

//make sure nothing triggers before the page is ready
function init(){
	//load button bar and variables
	nav_bar_open = false;
	$("#toggleNavBar").on("click",function(){
		toggleBar();
	});
	
	//load in the map
	loadMap();
	adjustMapSize();
	readSavedClasses();
	if(window.innerWidth >= 800){
		toggleBar();
	}
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

function loadMap(){
	$.ajax({
        type: "POST",
        contentType: "application/json",
		url: "/js/getMap",
		success: function (data){
			$("#map").html(data);
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
