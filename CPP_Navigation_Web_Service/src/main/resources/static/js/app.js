$( document ).ready(init());

var nav_bar_open;

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
	
}

function toggleBar(){
	var bar = document.getElementById("navBar");
	if(nav_bar_open){
		bar.style.marginRight = "-10vw";
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
	alert($("#map").css("height"));
}