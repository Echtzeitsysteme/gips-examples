
// create a network
var container = document.getElementById('network');

var circledNodes = nodeData
	.map((node, index, arr) => 
	{
		const angle = 2 * Math.PI * (index / arr.length + 0.75);
		node.x = 400 * Math.cos(angle);
		node.y = 400 * Math.sin(angle);
		if (index % 2 === 0) {
		  node.value = index + 1;
		}
		return node;
    });

var nodes = new vis.DataSet(circledNodes);
var edges = new vis.DataSet(edgeData);

// provide the data in the vis format
var data = {
	nodes: nodes,
	edges: edges
};
var options = {physics: false};	
	
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