var rootName = 'lectureStudio Server';

var rootColor = '#F472B6';
var relayColor = '#A78BFA';
var clientColor = '#60A5FA';
var edgeColor = 'rgba(151, 194, 252, 1)';


var nodeData =[
    
{id: 'lectureStudio Server', label: 'lectureStudio Server', color: rootColor },
{id: 'Client12', label: 'Client12', color: clientColor },
{id: 'Client4', label: 'Client4', color: relayColor },
{id: 'Client14', label: 'Client14', color: clientColor },
{id: 'Client1', label: 'Client1', color: relayColor },
{id: 'Client13', label: 'Client13', color: clientColor },
{id: 'Client9', label: 'Client9', color: clientColor },
{id: 'Client7', label: 'Client7', color: clientColor },
{id: 'Client5', label: 'Client5', color: clientColor },
{id: 'Client15', label: 'Client15', color: relayColor },
{id: 'Client10', label: 'Client10', color: clientColor },
{id: 'Client11', label: 'Client11', color: clientColor },
{id: 'Client8', label: 'Client8', color: clientColor },
{id: 'Client2', label: 'Client2', color: clientColor },
{id: 'Client3', label: 'Client3', color: clientColor },
{id: 'Client6', label: 'Client6', color: clientColor },
];

// create an array with edges
var edgeData = [
	
{id: 'lectureStudio ServerClient10', from: 'lectureStudio Server', to: 'Client10' , value: '20514'  , color: {color: edgeColor, opacity: 1.0}  },
{id: 'lectureStudio ServerClient7', from: 'lectureStudio Server', to: 'Client7' , value: '29163'  , color: {color: edgeColor, opacity: 1.0}  },
{id: 'Client1Client14', from: 'Client1', to: 'Client14' , value: '37909'  , color: {color: edgeColor, opacity: 1.0}  },
{id: 'Client1Client3', from: 'Client1', to: 'Client3' , value: '36838'  , color: {color: edgeColor, opacity: 1.0}  },
{id: 'lectureStudio ServerClient4', from: 'lectureStudio Server', to: 'Client4' , value: '43090'  , color: {color: edgeColor, opacity: 1.0}  },
{id: 'lectureStudio ServerClient13', from: 'lectureStudio Server', to: 'Client13' , value: '25740'  , color: {color: edgeColor, opacity: 1.0}  },
{id: 'Client15Client6', from: 'Client15', to: 'Client6' , value: '28350'  , color: {color: edgeColor, opacity: 1.0}  },
{id: 'lectureStudio ServerClient11', from: 'lectureStudio Server', to: 'Client11' , value: '28568'  , color: {color: edgeColor, opacity: 1.0}  },
{id: 'Client4Client9', from: 'Client4', to: 'Client9' , value: '23870'  , color: {color: edgeColor, opacity: 1.0}  },
{id: 'lectureStudio ServerClient1', from: 'lectureStudio Server', to: 'Client1' , value: '10106'  , color: {color: edgeColor, opacity: 1.0}  },
{id: 'Client15Client8', from: 'Client15', to: 'Client8' , value: '33340'  , color: {color: edgeColor, opacity: 1.0}  },
{id: 'Client1Client12', from: 'Client1', to: 'Client12' , value: '28538'  , color: {color: edgeColor, opacity: 1.0}  },
{id: 'lectureStudio ServerClient15', from: 'lectureStudio Server', to: 'Client15' , value: '27305'  , color: {color: edgeColor, opacity: 1.0}  },
{id: 'Client1Client2', from: 'Client1', to: 'Client2' , value: '28514'  , color: {color: edgeColor, opacity: 1.0}  },
{id: 'Client15Client5', from: 'Client15', to: 'Client5' , value: '29437'  , color: {color: edgeColor, opacity: 1.0}  },
];

var removedNodes = [
	
{id: 'Client4', label: 'Client4', color: relayColor },
];

var updatedEdges = [
	
{id: 'Client15Client9', from: 'Client15', to: 'Client9' , value: '27218'  , color: {color: edgeColor, opacity: 1.0}  },
];

var additionalNodes = [
	
{id: 'New Client1', label: 'New Client1', color: clientColor },
{id: 'New Client5', label: 'New Client5', color: clientColor },
{id: 'New Client4', label: 'New Client4', color: clientColor },
{id: 'New Client2', label: 'New Client2', color: clientColor },
{id: 'New Client6', label: 'New Client6', color: clientColor },
{id: 'New Client3', label: 'New Client3', color: clientColor },
];

var additionalEdges = [
	
{id: 'lectureStudio ServerNew Client2', from: 'lectureStudio Server', to: 'New Client2' , value: '33647'  , color: {color: edgeColor, opacity: 1.0}  },
{id: 'Client10New Client1', from: 'Client10', to: 'New Client1' , value: '37653'  , color: {color: edgeColor, opacity: 1.0}  },
{id: 'lectureStudio ServerNew Client3', from: 'lectureStudio Server', to: 'New Client3' , value: '49943'  , color: {color: edgeColor, opacity: 1.0}  },
{id: 'Client1New Client4', from: 'Client1', to: 'New Client4' , value: '31294'  , color: {color: edgeColor, opacity: 1.0}  },
{id: 'Client10New Client6', from: 'Client10', to: 'New Client6' , value: '43298'  , color: {color: edgeColor, opacity: 1.0}  },
{id: 'Client15New Client5', from: 'Client15', to: 'New Client5' , value: '40043'  , color: {color: edgeColor, opacity: 1.0}  },
];

