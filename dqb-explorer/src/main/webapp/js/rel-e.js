//触发菜单的实体或连接id
var contextItemId = null;
var timeLine = false;

var PEOPLE = "REN"; //此项与数据库配置相结合,表明实体类型，用在加载数据情况


function OnFileDone(strParam) {	
	viewer.showWaiting(false);
	
	var AddedEndIDs = new String(viewer.getAddedEndIDs()).split(/,/);
	var AddedLinkIDs = new String(viewer.getAddedLinkIDs()).split(/,/);
	
	var n1 = AddedEndIDs.length;
	if ((AddedEndIDs == "") || (n1 < 1)) {
		n1 = 0;
	}
	var n2 = AddedLinkIDs.length;
	
	if ((AddedLinkIDs == "") || (n2 < 1)) {
		n2 = 0;
	}
	if (n1 + n2 > 0) {
		if (timeLine == false) {
			viewer.reorganize();
		}
	}
	  window.status = 'number of new added entities：'+n1+' ;number of new added links：'+n2;
}

function OnLayoutDone() {
	
	viewer.showWaiting(false);	
	viewer.repaint();
	//viewer.setZoom(1);
	
	// alert("now can check cooprate!");
	// cooprateCheck();
}

function OnContextMenu(strParam) {
	viewer.clearMenuItems();
	contextItemId = strParam;
	var type= viewer.getItemType(strParam);
	var sUrl = "./loadMenu.do?t=" + type
	var url = encodeURI(sUrl);
	$.getJSON(url, function(data) {
		//if(!data) return ;
		viewer.clearMenuItems();
		for (var i = 0; i<data.length; i++) {
			var item = data[i];
			if(item && item.id == "s"){
				viewer.addSeparator(item.parentId);
			}
			else if(item && (item.parentId == null || item.parentId == "")) {
				viewer.addMenuItem(item.id,item.text);				
			}
			else if( item){
				viewer.addSubMenuItem(item.parentId,item.id,item.text);
			}
		}

		viewer.showMenu();
	});
}

function OnDoubleClick(strParam){
	var id = strParam;
	expandEntity("", id);	
}

function OnMenuCommand(strParam) {
	var command = String(strParam.substring(0, strParam.search(/,/)));
	var commandParam = String(strParam.substring(strParam.search(/,/)+1, strParam.length));
	
	if( command  &&  command.startsWith("e")){
		var selectedEndIds = viewer.getSelectedElementsEx(true,false);
		expandEntity(commandParam,selectedEndIds);		
	}
	
	if(strParam == "u10" && contextItemId)
	{
		 var itemType = new String(viewer.getItemType(contextItemId));
		 showProperties(contextItemId,itemType);
	}
	if(strParam == "l10" )
	{
		loadRkxx();
	}
}
var winPropties = false;
function showProperties(itemId, endType){
		
	  var sUrl = "./property.do?i="+itemId+"&t=" + endType;
	  var encodeUrl = encodeURI(sUrl);

	  if(winPropties && winPropties.close)
			winPropties.close();	  
	  winPropties = window.open(encodeUrl, "properties list", "top=200,left=200,width=600,height=400,scrollbars=yes,resizable=yes");
}

function expandEntity(path, endIds)
{
	var endIdArray = new String(endIds).split(",");
	for(var i=0; i<endIdArray.length; i++){
		var id = endIdArray[i];
		var type= viewer.getItemType(id);
		var sUrl = "./expand.do?p=" + path + "&t=" + type + "&i=" + id;
		var url = encodeURI(sUrl);
		//$("#log").html(url);

		viewer.showWaiting(true);
		viewer.mergeRel(url);
	}
}

function loadRkxx()
{
	var sUrl = "./dqb/loadRkxx.html";
	var iWidth=550;   //窗口宽度
	var iHeight=520;  //窗口高度

	var returnValue = openModalDialog(sUrl,iWidth,iHeight);
	
	var returnArray = new String(returnValue).split(",");
	var s = "t=" + PEOPLE;
	var clear = false;
	for(var i=0; i<returnArray.length; i++)
	{
		if( "1" == returnArray[0]) clear = true;
		
		if( i > 0){
				s = s + "&c=RYBH="+ "'" + returnArray[i] + "'";
			}
		
	}
	
	var url = "./load.do?"+s;
	if(clear) 
	{
		viewer.setFile("./init.do");	
	}
	
	viewer.showWaiting(true);
	//$("#log").html(encodeURI(url));
	viewer.mergeRel(encodeURI(url));
	
}

function openModalDialog(sUrl,iWidth,iHeight){
	  var iTop=(window.screen.height-iHeight)/2;
	  var iLeft=(window.screen.width-iWidth)/2;
	  var returnValue = window.showModalDialog(sUrl,window,"dialogHeight: "+iHeight+"px; dialogWidth: "+iWidth+"px; dialogTop: "+iTop+"; dialogLeft: "+iLeft+"; resizable: yes; status: no;scroll:auto");
	  return returnValue;
}


function onBodyResize() 
{	
	//alert("resize");
	
    if (!(document.applets && document.RELChart && document.RELChart.isActive()))
	{
		return;
	}else{
	    viewFitScreen();
	} 
}

//重新设置applet大小
function viewFitScreen()
{
	var iWidth=160;
//	if(document.getElementById("leftoff").style.display=="none") {
//	  iWidth=8;
//	}    
	//alert(iWidth);
    var iHeight=26;
    var cWidth=0;
    	cWidth=document.body.clientWidth-iWidth-8-8;
    var cHeight=0;	
		cHeight = document.body.clientHeight-iHeight-8-10;
		//alert(cWidth);
		//divContent2 viewer
//		document.all.divContent2.style.top=iHeight+4;
//		document.all.divContent2.style.left=iWidth+8+8;
//		document.all.divContent2.style.width=cWidth;
//		document.all.divContent2.style.height=cHeight;
	
	//tblContent 
		viewer.width = '100%'; //Frees height and width to allow resizing
	    viewer.height = '100%';
	    viewer.width = viewer.offsetWidth; //Re-Fixes to stop flickering.
	    viewer.height = viewer.offsetHeight;    
}
