var margin = {top: 180, right: 500, bottom: 20, left: 400},
    width = 900
    height = 400

var y = d3version3.scale.ordinal()
    .rangeRoundBands([0, height], .3);

var x = d3version3.scale.linear()
    .rangeRound([0, width]);

var xAxis = d3version3.svg.axis()
    .scale(x)
    .orient("top");

var yAxis = d3version3.svg.axis()
    .scale(y)
    .tickSize(0)
    .orient("left");

var color = d3version3.scale.ordinal()
    .range(["#B0E0E6","#4682B4"]);

var svg = d3version3.select('body').append("svg")
    .attr("width", width + margin.left + margin.right)
    .attr("height", height + margin.top + margin.bottom)
    .append("g")
    .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

d3version3.csv("query1.csv", function(error, data) {

    var workforceSize = d3version3.keys(data[0]).filter(function(key) { return key !== "rows"; });
    var industries = data.map(function(d) { return d.rows; });
    var neutralIndex = Math.floor(workforceSize.length/2);

    color.domain(workforceSize);

    data.forEach(function(row) {
        row.totalSent =d3version3.sum(workforceSize.slice(0,2).map(function(name) { return +row[name]; }));
        row.totalResponded =d3version3.sum(workforceSize.slice(2,4).map(function(name) { return +row[name]; }));
        row.total = d3version3.sum(workforceSize.map(function(name) { return +row[name]; }));
        workforceSize.forEach(function(name) { row['relative'+name] = (row.total !==0 ? +row[name]/row.total : 0); });

        var x0 = -1 * d3version3.sum(workforceSize.map(function(name, i) { return i < neutralIndex ? +row['relative'+name] : 0; }));
        if (workforceSize.length & 1) x0 += -1 * row['relative' + workforceSize[neutralIndex] ]/2;

        row.boxes = workforceSize.map(function(name) {
            return {name: name, x0: x0, x1: x0 += row['relative'+name], totalSent: row.totalSent, totalResponded: row.totalResponded, absolute: row[name]};
        });

    });

    var min = d3version3.min(data, function(d) { return d.boxes["0"].x0; });
    var max = d3version3.max(data, function(d) { return d.boxes[d.boxes.length-1].x1; });

    x.domain([min, max]).nice();
    y.domain(industries);

    // svg.append("g")
    //     .attr("class", "x axis")
    //     .call(xAxis);

// y axis industries
    svg.append("g")
        .attr("class", "y axis")
        .call(yAxis);



// rows in the chart
    var div = d3version3.select("body").append("div")
        .attr("class", "tooltip")
        .style("opacity", 0);

    var rows = svg.selectAll(".row")
        .data(data)
        .enter().append("g")
        .attr("class", "bar")
        .attr("transform", function(d) { return "translate(0," + y(d.rows) + ")"; })
        .on("mouseover", function(d) {
            svg.selectAll('.y').selectAll('text').filter(function(text) { return text===d.rows; })
                .transition().duration(100).style('font','15px sans-serif');
        })
        .on("mouseout", function(d) {
            svg.selectAll('.y').selectAll('text').filter(function(text) { return text===d.rows; })
                .transition().duration(100).style('font','10px sans-serif');
        });


// horizontal bars inside the chart
    var bars = rows.selectAll("rect")
        .data(function(d) { return d.boxes; })
        .enter().append("g")
        .on("mouseover", function(d) {
           var message= "Total Surveys Sent: "+d.totalSent;
           if(d.name.slice(-1)=="d"){
               message= "Total Survey Responses: "+d.totalResponded;
           }
            div.transition()
                .style("opacity", 0.9);
            div.html(message)
                .style("left", (d3version3.event.pageX)+"px")
                .style("top", (d3version3.event.pageY-28) + "px")
                .attr("fill", "steelblue");
        })
        .on("mousemove", function(d) {
            div
                .style("left", (d3version3.event.pageX+10)+"px")
                .style("top", (d3version3.event.pageY-10) + "px")
                .attr("fill", "steelblue");
        })
        .on("mouseout", function(d) {
            div.transition()
                .style("opacity", 0);
        });

    bars.append("rect")
        .attr("height", y.rangeBand())
        .attr("x", function(d) { return x(d.x0); })
        .attr("width", function(d) { return x(d.x1) - x(d.x0); })
        .style("fill", function(d) { return color(d.name); });

    bars.append("text")
        .attr("x", function(d) { return x(d.x0); })
        .attr("y", y.rangeBand()/2)
        .attr("dy", "0.5em")
        .attr("dx", "0.5em")
        .style("text-anchor", "begin")
        .text(function(d) { return d.absolute !== 0 && (d.x1-d.x0)>0.04 ? d.absolute : "" });



// black line in the middle
    svg.append("g")
        .attr("class", "y axis")
        .append("line")
        .attr("x1", x(0))
        .attr("x2", x(0))
        .attr("y2", height);


    svg.append("text")
        .attr("x", 475)
        .attr("y", -15)
        .attr("font-family", "sans-serif")
        .style("font-size", "15px")
        .text("← Surveys Sent ‎ ‎ ‎ ‎ ‎ ‎ Survey Responses →");


    svg.append("text")
        .attr("x", -200)
        .attr("y", -100)
        .attr("font-family", "sans-serif")
        .style("font-size", "20px")
        .text("Number of Samples Sent vs Number of Responses to the BICS survey broken down by industry, UK, 6 April to 19 April 2020");


// legend

    var legend = svg.selectAll(".legend")
        .data(workforceSize.slice(0, 2))
        .enter().append("g")
        .attr("class", "legend")
        .attr("transform", function(d, i) { return "translate(860," + width/25 * i + ")"; });


    legend.append("rect")
        .attr("y", -55)
        .attr("width", 19)
        .attr("height", 18)
        .style("fill", color);

    legend.append("text")
        .attr("x", 30)
        .attr("y", -55+9)
        .attr("dy", ".35em")
        .style("text-anchor", "begin")
        .style("font-size", "15px")
        .text(function(d) { return d; });

});

// d3.csv("query2.csv", function(error, data) {
//
//
//
// });