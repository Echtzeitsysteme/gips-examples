var currentTime = 0;

function finishLoading(newName)
{	
	document.getElementById("simulation").innerHTML = newName
			console.log("finished");
}

function finishSimulation()
{	
	document.getElementById("simulation").remove();
}

function loading()
{
	// document.getElementById("init").innerHTML = '<i class="fa fa-spinner fa-spin"></i>'
	// document.getElementById("send").innerHTML = '<i class="fa fa-spinner fa-spin"></i>'
	document.getElementById("simulation").innerHTML = '<i class="fa fa-spinner fa-spin"></i>'
			console.log("loading");

}

function setCurrentTime(time)
{
	currentTime = time;
	displayCurrentTime();
}

function displayCurrentTime()
{
	document.getElementById("time").innerHTML = currentTime
}


function updateEdges()
{
	var finished = updatedEdges.filter(x=> x.time < currentTime);
	finished.forEach(x=> network.updateEdge(x.id, {dashes:false, color: 'rgb(60, 179, 113)'}));
}

function initSimulation()
{
	// setCurrentTime(0);	
	// updatedEdges.forEach(x=> network.updateEdge(x.id, {dashes:false, color: 'rgba(151, 194, 252, 1)'}));
}


var t = -1;
var step1 = 'Remove clients';
var step2 = 'Redistribute';
var step3 = 'Add additional clients';
var step4 = 'Distribute additional clients';
var step5 = 'Finished';


var state = step1;
function runSimulation()
{
	loading();
	
	if(state == step1) 
	{
		runStep1();
		state = step2;
	}
	else if(state == step2)
	{
		runStep2();
		state = step3;
	}
	else if(state == step3)
	{
		runStep3();
		state = step4;
	}
	else if(state == step4)
	{
		runStep4();
		state = step5;
	}
}

function runStep1()
{
	if(removedNodes.length == 0) 
	{		
		finishLoading(step2);

		return;
	}	
	
	removeClients();
}

function runStep2()
{
	if(updatedEdges.length == 0) 
	{		
		finishLoading(step3);

		return;
	}
	
	updateEdges();
}

function runStep3()
{
	if(additionalNodes.length == 0) 
	{		
		finishLoading(step4);

		return;
	}
	
	addAdditionalNodes();
}

function runStep4()
{
	if(additionalEdges.length == 0) 
	{		
		finishSimulation();

		return;
	}
	
	addAdditionalEdges();
}



function removeClients()
{
	if(removedNodes.length > 0)
	{
		removeNode();
		setTimeout(runStep1, 750)
	}
}

function updateEdges()
{
	if(updatedEdges.length > 0)
	{
		updateEdge();
		setTimeout(runStep2, 750)
	}
}

function addAdditionalNodes()
{
	if(additionalNodes.length > 0)
	{
		addAdditionalNode();
		setTimeout(runStep3, 750)
	}
}

function addAdditionalEdges()
{
	if(additionalEdges.length > 0)
	{
		addAdditionalEdge();
		setTimeout(runStep4, 750)
	}
}

function removeNode()
{
	var toRemove = removedNodes[0];
	removedNodes = removedNodes.splice(1);
	data.nodes.remove({id: toRemove.id})
	data.edges
		.get()
		.filter(x=> x.from == toRemove.id || x.to == toRemove.id)
		.forEach(x=>data.edges.remove({id: x.id}))
}
function updateEdge()
{
	var toAdd = updatedEdges[0];
	updatedEdges = updatedEdges.splice(1);
	connectEdge(toAdd);
}

function connectEdge(edge)
{
	if(edge.from !== rootName)
	{
		data.nodes.update({id: edge.from, color: relayColor});
	}
	data.edges.add(edge);
}


function addAdditionalNode()
{
	var toAdd = additionalNodes[0];
	additionalNodes = additionalNodes.splice(1);
	data.nodes.add(toAdd);
}

function addAdditionalEdge()
{
	var toAdd = additionalEdges[0];
	additionalEdges = additionalEdges.splice(1);
	connectEdge(toAdd);
}


function run()
{
	if(t>=currentTime) 
	{		
		finishLoading();

		return;
	}
	t = currentTime;
	send();
	setTimeout(run, 1500)
}

function bestFit() {

  network.moveTo({scale:1}); 
  network.stopSimulation();
   
  var bigBB = { top: Infinity, left: Infinity, right: -Infinity, bottom: -Infinity }
  nodes.getIds().forEach( function(i) {
	var bb = network.getBoundingBox(i);
	if (bb.top < bigBB.top) bigBB.top = bb.top;
	if (bb.left < bigBB.left) bigBB.left = bb.left;
	if (bb.right > bigBB.right) bigBB.right = bb.right;
	if (bb.bottom > bigBB.bottom) bigBB.bottom = bb.bottom;  
  })
  
  var canvasWidth = network.canvas.body.container.clientWidth;
  var canvasHeight = network.canvas.body.container.clientHeight; 

  var scaleX = canvasWidth/(bigBB.right - bigBB.left);
  var scaleY = canvasHeight/(bigBB.bottom - bigBB.top);
  var scale = scaleX;
  if (scale * (bigBB.bottom - bigBB.top) > canvasHeight ) scale = scaleY;

  if (scale>1) scale = 0.9*scale;
 
  network.moveTo({
	scale: scale,
	position: {
		x: (bigBB.right + bigBB.left)/2,
	  y: (bigBB.bottom + bigBB.top)/2
	}
  })  
}