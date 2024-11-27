
// create a network
var container = document.getElementById('network');

var nodes = new vis.DataSet(nodeData);
var edges = new vis.DataSet(edgeData);

// provide the data in the vis format
var data = {
	nodes: nodes,
	edges: edges
};
var options = {
	nodes: {
      shape: "dot",
    }
};
var network = new vis.Network(container, data, options);

//white background for image
network.on("beforeDrawing",  function(ctx) {
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
	
network.on("afterDrawing", function (ctx) {
<!-- network.fit(); -->
var dataURL = ctx.canvas.toDataURL();
document.getElementById('canvasImg').href = dataURL;
});