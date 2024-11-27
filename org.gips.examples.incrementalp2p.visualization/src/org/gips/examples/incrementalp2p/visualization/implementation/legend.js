
// create a network
var legendContainer = document.getElementById('legend');

var legendNodes = [];
var legendEdges = [];

// legend
var x = 0;
var y = 0;
var step = 70;

//node legend
legendNodes.push({
  id: 1,
  x: x ,
  y: y,
  color: rootColor,
  label: "lectureStudio Server",
  value: 10,
  fixed: true,
  physics: false,
});
legendNodes.push({
  id: 2,
  x: x,
  y: y + step,
  color: relayColor,
  label: "Relay Client",
  value: 10,
  fixed: true,
  physics: false,
});
legendNodes.push({
  id: 3,
  x: x,
  y: y + 2 * step,
  color: clientColor,
  label: "Client",
  value: 10,
  fixed: true,
  physics: false,
});

//edge legend

legendNodes.push({
    id: 4,
    x: x - 55,
    y: y + 3 * step,
    label: "",
    value: 10,
    fixed: true,
    physics: false,
  });

  legendNodes.push({
    id: 5,
    x: x + 55,
    y: y + 3 * step,
    label: "",
    value: 10,
    fixed: true,
    physics: false,
  });

  legendNodes.push({
    id: 6,
    x: x - 55,
    y: y + 4 * step,
    label: "",
    value: 10,
    fixed: true,
    physics: false,
  });

  legendNodes.push({
    id: 7,
    x: x + 55,
    y: y + 4 * step,
    label: "",
    value: 10,
    fixed: true,
    physics: false,
  });

legendEdges.push({
    id: 1, 
    from: 4, 
    to: 5 ,
    color: edgeColor,
    label: "High\nConnection\nBandwidth", 
    font: { align: "bottom" },
    value: 12  },
)

legendEdges.push({
    id: 2, 
    from: 6, 
    to: 7 ,
    color: edgeColor,
    label: "Low\nConnection\nBandwidth", 
    font: { align: "bottom" },
    value: 8  , 
    },
)

// provide the data in the vis format
var legendData = {
	nodes: new vis.DataSet(legendNodes),
    edges: new vis.DataSet(legendEdges),
};

const legendOptions = {
    height: "95%",
    width: "95%",
    nodes: {
      shape: "dot",
    },edges:{       
        scaling:{
          min: 1,
          max: 15,
          label: {
            enabled: true,
            min: 12,
            max: 12,
            maxVisible: 30,
            drawThreshold: 1
          }
      }
    }
  };

var legend = new vis.Network(legendContainer, legendData, legendOptions);

//white background for image
legend.on("beforeDrawing",  function(ctx) {
	// save current translate/zoom
	ctx.save();
	// reset transform to identity
	ctx.setTransform(1, 0, 0, 1, 0, 0);
	// fill background with solid white
	ctx.fillStyle = '#ffffff';
	ctx.fillRect(0, 0, ctx.canvas.width, ctx.canvas.height)
	// restore old transform
	ctx.restore();
})
