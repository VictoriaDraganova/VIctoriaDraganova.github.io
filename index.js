    var margin = {top: 180, right: 500, bottom: 20, left: 400},
    width = 900,
    height = 400;

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


    var margin2 = {top: 300, right: 500, bottom: 400, left: 300}

    //  append the svg object to the body of the page
    var svg2 = d3version4.select('body')
        .append("svg")
        .attr("width", width + margin2.left + margin2.right)
        .attr("height", height + margin2.top + margin2.bottom)
        .append("g")
        .attr("transform",
            "translate(" + margin2.left + "," + margin2.top + ")");



   // update(1)
    function update(i) {
        var csvFile = "query4.csv"
        if(i==2){
            csvFile = "query3.csv"
        }
        if(i==3){
            csvFile = "query4.csv"
        }
        d3version4.csv(csvFile, function(data) {

            // List of subgroups = header of the csv files = soil condition here
            var subgroups = data.columns.slice(1)

            // List of groups = species here = value of the first column called group -> I show them on the X axis
            var groups = d3version4.map(data, function(d){return(d.group)}).keys()

            // Add X axis
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



            // Add Y axis
            var y = d3version4.scaleLinear()
                .domain([0, 100])
                .range([ height, 0 ]);
            svg2.append("g")
                .call(d3version4.axisLeft(y).ticks(20, "s").tickFormat(d => d + "%"));


            // color palette = one color per subgroup
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



            // What happens when user hover a bar
            var mouseover = function(d) {
                // what subgroup are we hovering?
                var subgroupName = d3version4.select(this.parentNode).datum().key; // This was the tricky part
                var subgroupValue = d.data[subgroupName];
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

// legend

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

    }



