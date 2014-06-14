var viewer =null;
var netscape4 = false;

//Sniff the browser version
if ((navigator.appName == "Netscape") && (navigator.appVersion.indexOf("4.") == 0))
{
	netscape4 = true;
}

String.prototype.startsWith = function(str){
    return (this.indexOf(str) === 0);
}

function onBodyLoad()
{	
	if (!(document.applets && document.RELChart && document.RELChart.isActive()))
	{
		window.setTimeout(onBodyLoad, 200);
		return;
	}
	viewer = document.RELChart;
	
  	PollEvent();
  	
  	if (!netscape4)
  	{
  		viewer.width = viewer.offsetWidth; //Fixes width to stop flickering.
  		viewer.height = viewer.offsetHeight; //Fixes height to stop flickering.
  	}
  	
  	doInitialize();
  	//viewFitScreen();
  	//disable unused applet events to improve performance
  	viewer.disableEvent(2); //selection changed
  	
  
}

function PollEvent() 
{
	var strEvent = String(viewer.getEvent());	
	var iEventID = Number(strEvent.substring(0, strEvent.search(/:/)));
	var strParam = String(strEvent.substring(strEvent.search(/:/)+1, strEvent.length));
		
	if(iEventID > 0) 
	{
	    //alert(iEventID);	    
		var strEventDescription="";
		switch(iEventID) 
		{
			case EVENT_MENU_COMMAND:
			   //alert(strParam);
				OnMenuCommand(strParam);
				break;
			case EVENT_LAYOUTDONE:
				OnLayoutDone();
				viewer.scrollCenter();
				break;
			case EVENT_FILEDONE:
				OnFileDone(strParam);
				break;
			case EVENT_CONTEXTMENU:
				OnContextMenu(strParam);
				break;
			case EVENT_CLICK:
				//OnOneClick(strParam);
				break;
			case EVENT_DOUBLECLICK:
				//viewer.scrollCenter();
				OnDoubleClick(strParam);
				break;
			case EVENT_MOUSEIN:
				window.status = "right click show menu!";
				break;
			case EVENT_MOUSEOUT:
				window.status = "";
				break;			
		
		}
		window.setTimeout(PollEvent,0); //event found so try again immediately
		return;
	}
	window.setTimeout(PollEvent,200); //no event found so try again in 1/5 sec
}

