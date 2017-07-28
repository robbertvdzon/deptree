    function initialPaintDependencies(name, collection) {
        svg
            .selectAll("."+name)
            .data(collection)
            .enter()
            .append("line")
            .attr("class", name)
            .attr("x1", function(d) { return d.x1 })
            .attr("y1", function(d) { return d.y1 })
            .attr("x2", function(d) { return d.x2 })
            .attr("y2", function(d) { return d.y2 })
            .attr("marker-end", "url(#triangle)")
            .attr("stroke-width", 1)
            .attr("stroke", "black");
    }

    function repaintDependencies(name, collection) {
        svg
            .selectAll("."+name)
            .data(collection)
            .transition()
            .duration(1000)
            .attr("x1", function(d) { return d.x1 })
            .attr("y1", function(d) { return d.y1 })
            .attr("x2", function(d) { return d.x2 })
            .attr("y2", function(d) { return d.y2 });
    }
