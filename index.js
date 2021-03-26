    var width = 900,
    height = 400;

// First Chart -----------------------------------------------------------------------

    var margin = {top: 180, right: 500, bottom: 20, left: 400}

    var y = d3version3.scale.ordinal()
        .rangeRoundBands([0, height], .3);

    var x = d3version3.scale.linear()
        .rangeRound([0, width]);

    var yAxis = d3version3.svg.axis()
        .scale(y)
        .tickSize(0)
        .orient("left");

    var color = d3version3.scale.ordinal()
        .range(["#B0E0E6", "#4682B4"]);

    var svg = d3version3.select('body').append("svg")
        .attr("width", width + margin.left + margin.right)
        .attr("height", height + margin.top + margin.bottom)
        .append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

    d3version3.csv("query1.csv", function (error, data) {

        var workforceSize = d3version3.keys(data[0]).filter(function (key) {
            return key !== "rows";
        });
        var industries = data.map(function (d) {
            return d.rows;
        });
        var neutralIndex = Math.floor(workforceSize.length / 2);

        color.domain(workforceSize);

        data.forEach(function (row) {
            row.totalSent = d3version3.sum(workforceSize.slice(0, 2).map(function (name) {
                return +row[name];
            }));
            row.totalResponded = d3version3.sum(workforceSize.slice(2, 4).map(function (name) {
                return +row[name];
            }));
            row.total = d3version3.sum(workforceSize.map(function (name) {
                return +row[name];
            }));
            workforceSize.forEach(function (name) {
                row['relative' + name] = (row.total !== 0 ? +row[name] / row.total : 0);
            });

            var x0 = -1 * d3version3.sum(workforceSize.map(function (name, i) {
                return i < neutralIndex ? +row['relative' + name] : 0;
            }));
            if (workforceSize.length & 1) x0 += -1 * row['relative' + workforceSize[neutralIndex]] / 2;

            row.boxes = workforceSize.map(function (name) {
                return {
                    name: name,
                    x0: x0,
                    x1: x0 += row['relative' + name],
                    totalSent: row.totalSent,
                    totalResponded: row.totalResponded,
                    absolute: row[name]
                };
            });

        });

        var min = d3version3.min(data, function (d) {
            return d.boxes["0"].x0;
        });
        var max = d3version3.max(data, function (d) {
            return d.boxes[d.boxes.length - 1].x1;
        });

        x.domain([min, max]).nice();
        y.domain(industries);


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
            .attr("transform", function (d) {
                return "translate(0," + y(d.rows) + ")";
            })
            .on("mouseover", function (d) {
                svg.selectAll('.y').selectAll('text').filter(function (text) {
                    return text === d.rows;
                })
                    .transition().duration(100).style('font', '15px sans-serif');
            })
            .on("mouseout", function (d) {
                svg.selectAll('.y').selectAll('text').filter(function (text) {
                    return text === d.rows;
                })
                    .transition().duration(100).style('font', '10px sans-serif');
            });


// horizontal bars inside the chart
        var bars = rows.selectAll("rect")
            .data(function (d) {
                return d.boxes;
            })
            .enter().append("g")
            .on("mouseover", function (d) {
                var message = "Total Surveys Sent: " + d.totalSent;
                if (d.name.slice(-1) == "d") {
                    message = "Total Survey Responses: " + d.totalResponded;
                }
                div.transition()
                    .style("opacity", 0.9);
                div.html(message)
                    .style("left", (d3version3.event.pageX) + "px")
                    .style("top", (d3version3.event.pageY - 28) + "px")
                    .attr("fill", "steelblue");
            })
            .on("mousemove", function (d) {
                div
                    .style("left", (d3version3.event.pageX + 10) + "px")
                    .style("top", (d3version3.event.pageY - 10) + "px")
                    .attr("fill", "steelblue");
            })
            .on("mouseout", function (d) {
                div.transition()
                    .style("opacity", 0);
            });

        bars.append("rect")
            .attr("height", y.rangeBand())
            .attr("x", function (d) {
                return x(d.x0);
            })
            .attr("width", function (d) {
                return x(d.x1) - x(d.x0);
            })
            .style("fill", function (d) {
                return color(d.name);
            });

        bars.append("text")
            .attr("x", function (d) {
                return x(d.x0);
            })
            .attr("y", y.rangeBand() / 2)
            .attr("dy", "0.5em")
            .attr("dx", "0.5em")
            .style("text-anchor", "begin")
            .text(function (d) {
                return d.absolute !== 0 && (d.x1 - d.x0) > 0.04 ? d.absolute : ""
            });


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
            .attr("x", -300)
            .attr("y", -100)
            .attr("font-family", "sans-serif")
            .style("font-size", "20px")
            .text("Number of Samples Sent vs Number of Responses to the BICS survey broken down by Industry and Workforce Size, UK, 6 April to 19 April 2020");


// legend

        var legend = svg.selectAll(".legend")
            .data(workforceSize.slice(0, 2))
            .enter().append("g")
            .attr("class", "legend")
            .attr("transform", function (d, i) {
                return "translate(860," + width / 25 * i + ")";
            });


        legend.append("rect")
            .attr("y", -55)
            .attr("width", 19)
            .attr("height", 18)
            .style("fill", color);

        legend.append("text")
            .attr("x", 30)
            .attr("y", -55 + 9)
            .attr("dy", ".35em")
            .style("text-anchor", "begin")
            .style("font-size", "15px")
            .text(function (d) {
                return d;
            });

    });

// Second Chart -----------------------------------------------------------------------


    var margin2 = {top: 300, right: 500, bottom: 400, left: 300}

    //  append the svg object to the body of the page
    var svg2 = d3version4.select('body')
        .append("svg")
        .attr("width", width + margin2.left + margin2.right)
        .attr("height", height + margin2.top + margin2.bottom)
        .append("g")
        .attr("transform",
            "translate(" + margin2.left + "," + margin2.top + ")");


    // What happens when user hover a bar
    var mouseover = function(d) {
        // what subgroup are we hovering?
        var subgroupName = d3version4.select(this.parentNode).datum().key; // This was the tricky part
        // Reduce opacity of all rect to 0.2
        d3version4.selectAll(".myRect").style("opacity", 0.2)
        // Highlight all rects of this subgroup with opacity 0.8. It is possible to select them since they have a specific class = their name.
        d3version4.selectAll("."+subgroupName)
            .style("opacity", 1)
    }

    // When user do not hover anymore
    var mouseleave = function(d) {
        // Back to normal opacity: 0.8
        d3version4.selectAll(".myRect")
            .style("opacity",0.8)
    }

        d3version4.csv( "query2.csv", function(data) {

            // List of subgroups = header of the csv files = soil condition here
            var subgroups = data.columns.slice(1)

            // List of groups = species here = value of the first column called group -> I show them on the X axis
            var groups = d3version4.map(data, function(d){return(d.group)}).keys()

            var x = d3version4.scaleBand()
                .domain(groups)
                .range([0, width])
                .padding([0.2])
            svg2.append("g")
                .attr("transform", "translate(0," + height  + ")")
                .call(d3version4.axisBottom(x).tickSizeOuter(0))
                .selectAll("text")
                .attr("transform", "translate(-10,0)rotate(-45)")
                .style("font-size", "13px")
                .style("text-anchor", "end");

            var y = d3version4.scaleLinear()
                .domain([0, 100])
                .range([ height, 0 ]);
            svg2.append("g")
                .call(d3version4.axisLeft(y).ticks(20, "s").tickFormat(d => d + "%"));

            var color = d3version4.scaleOrdinal()
                .domain(subgroups)
                .range(['#90EE90','#DC143C','#FFA07A'])

            // Normalize the data -> sum of each group must be 100!
            dataNormalized = []
            data.forEach(function(d){
                // Compute the total
                tot = 0
                for (i in subgroups){ name=subgroups[i] ; tot += +d[name] }
                // Now normalize
                for (i in subgroups){ name=subgroups[i] ; d[name] = d[name] / tot * 100}
            })

            //stack the data? --> stack per subgroup
            var stackedData = d3version4.stack()
                .keys(subgroups)
                (data)


            // Show the bars
            svg2.append("g")
                .selectAll("g")
                // Enter in the stack data = loop key per key = group per group
                .data(stackedData)
                .enter().append("g")
                .attr("fill", function(d) { return color(d.key); })
                .attr("class", function(d){ return "myRect " + d.key }) // Add a class to each subgroup: their name
                .selectAll("rect")
                // enter a second time = loop subgroup per subgroup to add all rectangles
                .data(function(d) { return d; })
                .enter().append("rect")
                .attr("x", function(d) { return x(d.data.group); })
                .attr("y", function(d) { return y(d[1]); })
                .attr("height", function(d) { return y(d[0]) - y(d[1]); })
                .attr("width",x.bandwidth())
                .attr("stroke", "white")
                .on("mouseover", mouseover)
                .on("mouseleave", mouseleave)

            svg2.append("text")
                .attr("x", 0)
                .attr("y", -100)
                .attr("font-family", "sans-serif")
                .style("font-size", "20px")
                .text("Trading Status of all responding businesses, broken down by Industry, UK, 6 April to 19 April 2020");

            keys = [stackedData[2].key,stackedData[1].key,stackedData[0].key]

            var legend2 = svg2.selectAll(".legend")
                .data(keys)
                .enter().append("g")
                .attr("class", "legend")
                .attr("transform", function(d, i) { return "translate(" + 250 * i + ",-55)"; });


            legend2.append("rect")
                .attr("x", 100)
                .attr("y", 0)
                .attr("width", 19)
                .attr("height", 18)
                .style("fill", color);

            legend2.append("text")
                .attr("x", 125)
                .attr("y", 9)
                .attr("dy", ".35em")
                .style("text-anchor", "begin")
                .style("font-size", "15px")
                .text(function (d) {
                    return d;});


        })


// Third Chart -----------------------------------------------------------------------

    var margin3 = {top: 150, right: 500, bottom: 200, left: 300}

    //  append the svg object to the body of the page
    var svg3 = d3version4.select('body')
        .append("svg")
        .attr("width", width + margin3.left + margin3.right)
        .attr("height", height + margin3.top + margin3.bottom)
        .append("g")
        .attr("transform",
            "translate(" + margin3.left + "," + margin3.top + ")");


    d3version4.csv( "query3.csv", function(data) {

        // List of subgroups = header of the csv files = soil condition here
        var subgroups = data.columns.slice(1)

        // List of groups = species here = value of the first column called group -> I show them on the X axis
        var groups = d3version4.map(data, function(d){return(d.group)}).keys()

        var x = d3version4.scaleBand()
            .domain(groups)
            .range([0, width])
            .padding([0.2])
        svg3.append("g")
            .attr("transform", "translate(0," + height  + ")")
            .call(d3version4.axisBottom(x).tickSizeOuter(0))
            .selectAll("text")
            .style("font-size", "13px");

        var y = d3version4.scaleLinear()
            .domain([0, 100])
            .range([ height, 0 ]);
        svg3.append("g")
            .call(d3version4.axisLeft(y).ticks(20, "s").tickFormat(d => d + "%"));

        var color = d3version4.scaleOrdinal()
            .domain(subgroups)
            .range(['#90EE90','#DC143C','#FFA07A'])

        // Normalize the data -> sum of each group must be 100!
        dataNormalized = []
        data.forEach(function(d){
            // Compute the total
            tot = 0
            for (i in subgroups){ name=subgroups[i] ; tot += +d[name] }
            // Now normalize
            for (i in subgroups){ name=subgroups[i] ; d[name] = d[name] / tot * 100}
        })

        //stack the data? --> stack per subgroup
        var stackedData = d3version4.stack()
            .keys(subgroups)
            (data)


        // Show the bars
        svg3.append("g")
            .selectAll("g")
            // Enter in the stack data = loop key per key = group per group
            .data(stackedData)
            .enter().append("g")
            .attr("fill", function(d) { return color(d.key); })
            .attr("class", function(d){ return "myRect " + d.key }) // Add a class to each subgroup: their name
            .selectAll("rect")
            // enter a second time = loop subgroup per subgroup to add all rectangles
            .data(function(d) { return d; })
            .enter().append("rect")
            .attr("x", function(d) { return x(d.data.group); })
            .attr("y", function(d) { return y(d[1]); })
            .attr("height", function(d) { return y(d[0]) - y(d[1]); })
            .attr("width",x.bandwidth())
            .attr("stroke", "white")
            .on("mouseover", mouseover)
            .on("mouseleave", mouseleave)

        svg3.append("text")
            .attr("x", 0)
            .attr("y", -100)
            .attr("font-family", "sans-serif")
            .style("font-size", "20px")
            .text("Trading Status of all responding businesses, broken down by Workforce Size, UK, 6 April to 19 April 2020");

        keys = [stackedData[2].key,stackedData[1].key,stackedData[0].key]

        var legend3 = svg3.selectAll(".legend")
            .data(keys)
            .enter().append("g")
            .attr("class", "legend")
            .attr("transform", function(d, i) { return "translate(" + 250 * i + ",-55)"; });


        legend3.append("rect")
            .attr("x", 100)
            .attr("y", 0)
            .attr("width", 19)
            .attr("height", 18)
            .style("fill", color);

        legend3.append("text")
            .attr("x", 125)
            .attr("y", 9)
            .attr("dy", ".35em")
            .style("text-anchor", "begin")
            .style("font-size", "15px")
            .text(function (d) {
                return d;});


    })
// Fourth Chart -----------------------------------------------------------------------

    var margin4 = {top: 150, right: 500, bottom: 300, left: 300}

    //  append the svg object to the body of the page
    var svg4 = d3version4.select('body')
        .append("svg")
        .attr("width", width + margin4.left + margin4.right)
        .attr("height", height + margin4.top + margin4.bottom)
        .append("g")
        .attr("transform",
            "translate(" + margin4.left + "," + margin4.top + ")");


    d3version4.csv( "query4.csv", function(data) {

        // List of subgroups = header of the csv files = soil condition here
        var subgroups = data.columns.slice(1)

        // List of groups = species here = value of the first column called group -> I show them on the X axis
        var groups = d3version4.map(data, function(d){return(d.group)}).keys()

        var x = d3version4.scaleBand()
            .domain(groups)
            .range([0, width])
            .padding([0.2])
        svg4.append("g")
            .attr("transform", "translate(0," + height  + ")")
            .call(d3version4.axisBottom(x).tickSizeOuter(0))
            .selectAll("text")
            .style("font-size", "13px");

        var y = d3version4.scaleLinear()
            .domain([0, 100])
            .range([ height, 0 ]);
        svg4.append("g")
            .call(d3version4.axisLeft(y).ticks(20, "s").tickFormat(d => d + "%"));

        var color = d3version4.scaleOrdinal()
            .domain(subgroups)
            .range(['#90EE90','#DC143C','#FFA07A'])

        // Normalize the data -> sum of each group must be 100!
        dataNormalized = []
        data.forEach(function(d){
            // Compute the total
            tot = 0
            for (i in subgroups){ name=subgroups[i] ; tot += +d[name] }
            // Now normalize
            for (i in subgroups){ name=subgroups[i] ; d[name] = d[name] / tot * 100}
        })

        //stack the data? --> stack per subgroup
        var stackedData = d3version4.stack()
            .keys(subgroups)
            (data)


        // Show the bars
        svg4.append("g")
            .selectAll("g")
            // Enter in the stack data = loop key per key = group per group
            .data(stackedData)
            .enter().append("g")
            .attr("fill", function(d) { return color(d.key); })
            .attr("class", function(d){ return "myRect " + d.key }) // Add a class to each subgroup: their name
            .selectAll("rect")
            // enter a second time = loop subgroup per subgroup to add all rectangles
            .data(function(d) { return d; })
            .enter().append("rect")
            .attr("x", function(d) { return x(d.data.group); })
            .attr("y", function(d) { return y(d[1]); })
            .attr("height", function(d) { return y(d[0]) - y(d[1]); })
            .attr("width",x.bandwidth())
            .attr("stroke", "white")
            .on("mouseover", mouseover)
            .on("mouseleave", mouseleave)

        svg4.append("text")
            .attr("x", 0)
            .attr("y", -100)
            .attr("font-family", "sans-serif")
            .style("font-size", "20px")
            .text("Trading Status of all responding businesses, broken down by Country, UK, 6 April to 19 April 2020");

        keys = [stackedData[2].key,stackedData[1].key,stackedData[0].key]

        var legend4 = svg4.selectAll(".legend")
            .data(keys)
            .enter().append("g")
            .attr("class", "legend")
            .attr("transform", function(d, i) { return "translate(" + 250 * i + ",-55)"; });


        legend4.append("rect")
            .attr("x", 100)
            .attr("y", 0)
            .attr("width", 19)
            .attr("height", 18)
            .style("fill", color);

        legend4.append("text")
            .attr("x", 125)
            .attr("y", 9)
            .attr("dy", ".35em")
            .style("text-anchor", "begin")
            .style("font-size", "15px")
            .text(function (d) {
                return d;});


    })


//Fifth chart----------------------------------------------------------------------------------
    var diameter = 900
    var margin6 = {top: 0, right: 500, bottom: 600, left: 1600}

    var svg5 = d3version4.select('body')
        .append("svg")
        .attr("width", width + margin6.left + margin6.right)
        .attr("height", height + margin6.top + margin6.bottom)
    g = svg5.append("g").attr("transform", "translate(" + diameter / 1.2 + "," + diameter / 2. + ")");


    var color5 = d3version4.scaleLinear()
        .domain([-1, 5])
        .range(["hsl(152,80%,80%)", "hsl(228,30%,40%)"])
        .interpolate(d3version4.interpolateHcl);

    var pack = d3version4.pack()
        .size([diameter, diameter ])
        .padding(2);

    d3version4.json("query5.json", function(error, root) {
        if (error) throw error;

        root = d3version4.hierarchy(root)
            .sum(function(d) { return d.size; })
            .sort(function(a, b) { return b.value - a.value; })

        var focus = root,
            nodes = pack(root).descendants(),
            view;


        var circle = g.selectAll("circle")
            .data(nodes)
            .enter().append("circle")
            .attr("class", function(d) { return d.parent ? d.children ? "node" : "node node--leaf" : "node node--root"; })
            .style("fill", function(d) {return d.children ? color5(d.depth) : null; })
            .on("click", function(d) { if (focus !== d) zoom(d), d3version4.event.stopPropagation(); })


        var text = g.selectAll("text.label")
            .data(nodes)
            .enter().append("text")
            .attr("class", "label")
            .style("fill-opacity", function(d) { return d.parent === root ? 1 : 0; })
            .style("display", function(d) { return d.parent === root ? "inline" : "none"; })
            .text(function(d) {
                if (d.data.size) {
                    return d.data.name + " - " + (d.data.size * 100).toFixed(1) + "%";
                } else {
                    return d.data.name;
                }
            });


        var node = g.selectAll("circle,text")


        svg5
            .style("background", color5(0))
            .on("click", function() { zoom(root); })


        zoomTo([root.x, root.y, root.r * 2]);

        function zoom(d) {
            var focus0 = focus; focus = d;

            var transition = d3version4.transition()
                .duration(d3version4.event.altKey ? 7500 : 750)
                .tween("zoom", function(d) {
                    var i = d3version4.interpolateZoom(view, [focus.x, focus.y, focus.r * 2 ]);
                    return function(t) { zoomTo(i(t)); };
                });

            transition.selectAll("text.label")
                .filter(function(d) { return d.parent === focus || this.style.display === "inline"; })
                .style("fill-opacity", function(d) { return d.parent === focus ? 1 : 0; })
                .on("start", function(d) { if (d.parent === focus) this.style.display = "inline"; })
                .on("end", function(d) { if (d.parent !== focus) this.style.display = "none"; });
        }

        function zoomTo(v) {
            var k = diameter / v[2]; view = v;
            node.attr("transform", function(d) { return "translate(" + (d.x - v[0]) * k + "," + (d.y - v[1]) * k + ")"; });
            circle.attr("r", function(d) { return d.r * k; });
        }
    });


    svg4.append("text")
        .attr("x", -195)
        .attr("y", 690)
        .attr("font-family", "sans-serif")
        .style("font-size", "20px")
        .text("'Applied For', 'Received' and 'Intended To Apply For' Initiatives of all responding businesses, broken down by industry, UK, 6 April to 19 April 2020");
