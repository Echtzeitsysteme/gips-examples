var rootName = 'lectureStudio Server';

var rootColor = '#F472B6';
var relayColor = '#A78BFA';
var clientColor = '#60A5FA';
var edgeColor = 'rgba(151, 194, 252, 1)';


var nodeData = [

    { id: 'lectureStudio Server', label: 'lectureStudio Server', color: rootColor },
    { id: 'Client13', label: 'Client13', color: clientColor },
    { id: 'Client2', label: 'Client2', color: clientColor },
    { id: 'Client12', label: 'Client12', color: clientColor },
    { id: 'Client6', label: 'Client6', color: clientColor },
    { id: 'Client3', label: 'Client3', color: clientColor },
    { id: 'Client5', label: 'Client5', color: clientColor },
    { id: 'Client1', label: 'Client1', color: clientColor },
    { id: 'Client4', label: 'Client4', color: clientColor },
    { id: 'Client11', label: 'Client11', color: clientColor },
    { id: 'Client8', label: 'Client8', color: clientColor },
    { id: 'Client15', label: 'Client15', color: clientColor },
    { id: 'Client14', label: 'Client14', color: clientColor },
    { id: 'Client7', label: 'Client7', color: clientColor },
    { id: 'Client10', label: 'Client10', color: clientColor },
    { id: 'Client9', label: 'Client9', color: clientColor },
];

// create an array with edges
var edgeData = [

    { id: 'lectureStudio ServerClient1', from: 'lectureStudio Server', to: 'Client1', value: '37573', color: { color: edgeColor, opacity: 1.0 } },
    { id: 'lectureStudio ServerClient8', from: 'lectureStudio Server', to: 'Client8', value: '33541', color: { color: edgeColor, opacity: 1.0 } },
    { id: 'lectureStudio ServerClient12', from: 'lectureStudio Server', to: 'Client12', value: '34884', color: { color: edgeColor, opacity: 1.0 } },
    { id: 'lectureStudio ServerClient14', from: 'lectureStudio Server', to: 'Client14', value: '33404', color: { color: edgeColor, opacity: 1.0 } },
    { id: 'lectureStudio ServerClient10', from: 'lectureStudio Server', to: 'Client10', value: '37896', color: { color: edgeColor, opacity: 1.0 } },
    { id: 'lectureStudio ServerClient9', from: 'lectureStudio Server', to: 'Client9', value: '37124', color: { color: edgeColor, opacity: 1.0 } },
    { id: 'lectureStudio ServerClient2', from: 'lectureStudio Server', to: 'Client2', value: '30782', color: { color: edgeColor, opacity: 1.0 } },
    { id: 'lectureStudio ServerClient3', from: 'lectureStudio Server', to: 'Client3', value: '32426', color: { color: edgeColor, opacity: 1.0 } },
    { id: 'lectureStudio ServerClient15', from: 'lectureStudio Server', to: 'Client15', value: '25305', color: { color: edgeColor, opacity: 1.0 } },
    { id: 'lectureStudio ServerClient13', from: 'lectureStudio Server', to: 'Client13', value: '35671', color: { color: edgeColor, opacity: 1.0 } },
    { id: 'lectureStudio ServerClient4', from: 'lectureStudio Server', to: 'Client4', value: '26906', color: { color: edgeColor, opacity: 1.0 } },
    { id: 'lectureStudio ServerClient7', from: 'lectureStudio Server', to: 'Client7', value: '31138', color: { color: edgeColor, opacity: 1.0 } },
    { id: 'lectureStudio ServerClient11', from: 'lectureStudio Server', to: 'Client11', value: '27565', color: { color: edgeColor, opacity: 1.0 } },
    { id: 'lectureStudio ServerClient6', from: 'lectureStudio Server', to: 'Client6', value: '31537', color: { color: edgeColor, opacity: 1.0 } },
    { id: 'lectureStudio ServerClient5', from: 'lectureStudio Server', to: 'Client5', value: '37412', color: { color: edgeColor, opacity: 1.0 } },
];

var removedNodes = [

];

var updatedEdges = [

];

var additionalNodes = [

    { id: 'New Client6', label: 'New Client6', color: clientColor },
    { id: 'New Client2', label: 'New Client2', color: clientColor },
    { id: 'New Client4', label: 'New Client4', color: clientColor },
    { id: 'New Client1', label: 'New Client1', color: clientColor },
    { id: 'New Client3', label: 'New Client3', color: clientColor },
    { id: 'New Client5', label: 'New Client5', color: clientColor },
];

var additionalEdges = [

    { id: 'lectureStudio ServerNew Client4', from: 'lectureStudio Server', to: 'New Client4', value: '27041', color: { color: edgeColor, opacity: 1.0 } },
    { id: 'lectureStudio ServerNew Client6', from: 'lectureStudio Server', to: 'New Client6', value: '18739', color: { color: edgeColor, opacity: 1.0 } },
    { id: 'lectureStudio ServerNew Client2', from: 'lectureStudio Server', to: 'New Client2', value: '33607', color: { color: edgeColor, opacity: 1.0 } },
    { id: 'lectureStudio ServerNew Client1', from: 'lectureStudio Server', to: 'New Client1', value: '41656', color: { color: edgeColor, opacity: 1.0 } },
    { id: 'lectureStudio ServerNew Client3', from: 'lectureStudio Server', to: 'New Client3', value: '20517', color: { color: edgeColor, opacity: 1.0 } },
    { id: 'lectureStudio ServerNew Client5', from: 'lectureStudio Server', to: 'New Client5', value: '30938', color: { color: edgeColor, opacity: 1.0 } },
];

