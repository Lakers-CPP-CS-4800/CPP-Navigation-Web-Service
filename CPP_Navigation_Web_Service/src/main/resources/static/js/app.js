$( document ).ready(init());

var button_bar_open;

//make sure nothing triggers before the page is ready
function init(){
	//load button bar and variables
	button_bar_open = false;
	$("#toggleButtonBar").on("click",function(){
		toggleBar();
	});
	
	//load in the map
}

function toggleBar(){
	var bar = document.getElementById("buttonBar");
	if(button_bar_open){
		bar.style.marginRight = "-10vw";
	}
	else{
		bar.style.marginRight = "0";
	}
	button_bar_open = !button_bar_open;
}