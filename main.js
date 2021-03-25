var width = 600
var height = 500
var margin = 50

// The radius of the pieplot is half the width or half the height (smallest one). I subtract a bit of margin.
var radius = Math.min(width, height) / 2 - margin


// append the svg object to the div called 'my_dataviz'
var svg = d3.select("#my_dataviz")
    .append("svg")
    .attr("width", width)
    .attr("height", height)
    .append("g")
    .attr("transform", "translate(" + width / 2 + "," + height / 2 + ")");


// create 2 data_set
var data1 = {a: 15, b: 1785, c:130, d:722, e:1819, f: 975, g:528, k:794, v:112, j:1303, h:1287, s:303, w:145, q:415, z:108, r:1}
var data2 = {a: 6, b: 16, c:20, d:14, e:19, f:12}
var data3 = {a: 600, b: 160, c:200, d:140, e:190, f:120}

var colorCodes = ["#5E4FA2", "#3288BD", "#66C2A5", "#E6F598",
    "#FDAE61", "#F46D43", "#D53E4F", "#9E0142",
    "#18c61a", "#24b7f1", "#fa82ce", "#736c31",
    "#1263e2", "#18c199", "#ed990a", "#d31911"];
// set the color scale
var color = d3.scaleOrdinal()
    .domain(["a", "b", "c", "d", "e", "f", "g", "k", "v", "j", "h", "s", "w", "q", "z", "r"])
    .range(colorCodes);



// A function that create / update the plot for a given variable:
function update(data) {

    // Compute the position of each group on the pie:
    var pie = d3.pie()
        .value(function(d) {return d.value; })
        .sort(function(a, b) { console.log(a) ; return d3.ascending(a.key, b.key);} ) // This make sure that group order remains the same in the pie chart
    var data_ready = pie(d3.entries(data))

    // map to data
    var u = svg.selectAll("path")
        .data(data_ready)


    var arc = d3.arc()
        .innerRadius(100)
        .outerRadius(radius)
    // Build the pie chart: Basically, each part of the pie is a path that we build using the arc function.
    u
        .enter()
        .append('path')
        .merge(u)
        .transition()
        .duration(1000)
        .attr('d', arc)
        .attr('fill', function(d){ return(color(d.data.key)) })
        .attr("stroke", "white")
        .style("stroke-width", "2px")
        .style("opacity", 1)

    u
        .on("mouseover", function(d) {
            d3.select("#tooltip").style('opacity', 1)
                .select("#value").text(d.value + " : " + d.data.key);
        })
        .on("mousemove", function(d) {
            d3.select("#tooltip").style("top", (d3.event.pageY - 10) + "px")
                .style("left", (d3.event.pageX + 10) + "px");
        })
        .on("mouseout", function() {
            d3.select("#tooltip").style('opacity', 0);
        })

// legend
//     u
//         .enter()
//         .append("rect")
//         .attr("x", 250)
//         .attr("y", function(d,i){ return -190 + i*(25)}) // 100 is where the first dot appears. 25 is the distance between dots
//         .attr("width", 20)
//         .attr("height", 20)
//         .style("fill", function(d){ return color(d.data.key)})
//
//     u
//         .enter()
//         .append("text")
//         .attr("x", 250 + 20*1.2)
//         .attr("y", function(d,i){ return -190 + i*(20+5) + (20/2)}) // 100 is where the first dot appears. 25 is the distance between dots
//         .style("fill", function(d){ return color(d.data.key)})
//         .text(function(d){ return d.data.key})
//         .attr("text-anchor", "left")
//         .style("alignment-baseline", "middle")


    // remove the group that is not present anymore
    u
        .exit()
        .remove()

}

// Initialize the plot with the first dataset
update(data1)
update(data1)
