var rootName = 'lectureStudio Server';

var rootColor = '#F472B6';
var relayColor = '#A78BFA';
var clientColor = '#60A5FA';
var edgeColor = 'rgba(151, 194, 252, 1)';


var nodeData = [

    { id: 'lectureStudio Server', label: 'lectureStudio Server', color: rootColor },
    { id: 'Client6', label: 'Client6', color: clientColor },
    { id: 'Client8', label: 'Client8', color: clientColor },
    { id: 'Client3', label: 'Client3', color: clientColor },
    { id: 'Client1', label: 'Client1', color: relayColor },
    { id: 'Client10', label: 'Client10', color: relayColor },
    { id: 'Client13', label: 'Client13', color: clientColor },
    { id: 'Client4', label: 'Client4', color: clientColor },
    { id: 'Client11', label: 'Client11', color: relayColor },
    { id: 'Client7', label: 'Client7', color: clientColor },
    { id: 'Client15', label: 'Client15', color: clientColor },
    { id: 'Client14', label: 'Client14', color: clientColor },
    { id: 'Client5', label: 'Client5', color: clientColor },
    { id: 'Client2', label: 'Client2', color: clientColor },
    { id: 'Client9', label: 'Client9', color: clientColor },
    { id: 'Client12', label: 'Client12', color: clientColor },
];

// create an array with edges
var edgeData = [

    { id: 'lectureStudio ServerClient2', from: 'lectureStudio Server', to: 'Client2', value: '23550', color: { color: edgeColor, opacity: 1.0 } },
    { id: 'Client11Client15', from: 'Client11', to: 'Client15', value: '25636', color: { color: edgeColor, opacity: 1.0 } },
    { id: 'lectureStudio ServerClient5', from: 'lectureStudio Server', to: 'Client5', value: '30374', color: { color: edgeColor, opacity: 1.0 } },
    { id: 'lectureStudio ServerClient10', from: 'lectureStudio Server', to: 'Client10', value: '35641', color: { color: edgeColor, opacity: 1.0 } },
    { id: 'Client10Client13', from: 'Client10', to: 'Client13', value: '32737', color: { color: edgeColor, opacity: 1.0 } },
    { id: 'Client1Client14', from: 'Client1', to: 'Client14', value: '31782', color: { color: edgeColor, opacity: 1.0 } },
    { id: 'lectureStudio ServerClient11', from: 'lectureStudio Server', to: 'Client11', value: '25707', color: { color: edgeColor, opacity: 1.0 } },
    { id: 'lectureStudio ServerClient12', from: 'lectureStudio Server', to: 'Client12', value: '34880', color: { color: edgeColor, opacity: 1.0 } },
    { id: 'Client11Client3', from: 'Client11', to: 'Client3', value: '22744', color: { color: edgeColor, opacity: 1.0 } },
    { id: 'Client1Client6', from: 'Client1', to: 'Client6', value: '42219', color: { color: edgeColor, opacity: 1.0 } },
    { id: 'Client1Client9', from: 'Client1', to: 'Client9', value: '33598', color: { color: edgeColor, opacity: 1.0 } },
    { id: 'lectureStudio ServerClient7', from: 'lectureStudio Server', to: 'Client7', value: '42203', color: { color: edgeColor, opacity: 1.0 } },
    { id: 'lectureStudio ServerClient1', from: 'lectureStudio Server', to: 'Client1', value: '27845', color: { color: edgeColor, opacity: 1.0 } },
    { id: 'Client11Client8', from: 'Client11', to: 'Client8', value: '33687', color: { color: edgeColor, opacity: 1.0 } },
    { id: 'Client10Client4', from: 'Client10', to: 'Client4', value: '35813', color: { color: edgeColor, opacity: 1.0 } },
];

var removedNodes = [

    { id: 'Client1', label: 'Client1', color: relayColor },
];

var updatedEdges = [

    { id: 'lectureStudio ServerClient9', from: 'lectureStudio Server', to: 'Client9', value: '43310', color: { color: edgeColor, opacity: 1.0 } },
    { id: 'Client10Client14', from: 'Client10', to: 'Client14', value: '24666', color: { color: edgeColor, opacity: 1.0 } },
    { id: 'Client12Client6', from: 'Client12', to: 'Client6', value: '32751', color: { color: edgeColor, opacity: 1.0 } },
];

var additionalNodes = [

    { id: 'New Client5', label: 'New Client5', color: clientColor },
    { id: 'New Client4', label: 'New Client4', color: clientColor },
    { id: 'New Client2', label: 'New Client2', color: clientColor },
    { id: 'New Client1', label: 'New Client1', color: clientColor },
    { id: 'New Client3', label: 'New Client3', color: clientColor },
    { id: 'New Client6', label: 'New Client6', color: clientColor },
];

var additionalEdges = [

    { id: 'Client12New Client1', from: 'Client12', to: 'New Client1', value: '32858', color: { color: edgeColor, opacity: 1.0 } },
    { id: 'Client12New Client2', from: 'Client12', to: 'New Client2', value: '27498', color: { color: edgeColor, opacity: 1.0 } },
    { id: 'lectureStudio ServerNew Client4', from: 'lectureStudio Server', to: 'New Client4', value: '33752', color: { color: edgeColor, opacity: 1.0 } },
    { id: 'Client11New Client3', from: 'Client11', to: 'New Client3', value: '34781', color: { color: edgeColor, opacity: 1.0 } },
    { id: 'Client9New Client5', from: 'Client9', to: 'New Client5', value: '32255', color: { color: edgeColor, opacity: 1.0 } },
    { id: 'Client9New Client6', from: 'Client9', to: 'New Client6', value: '41836', color: { color: edgeColor, opacity: 1.0 } },
];

