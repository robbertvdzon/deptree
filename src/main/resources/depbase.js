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
            .attr('opacity', function(d) { if (d.highlight) return 1; else if (d.dimmed) return 0; else return d.opacity })
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
            .attr("y2", function(d) { return d.y2 })
            .attr('opacity', function(d) { if (d.highlight) return 1; else if (d.dimmed) return 0; else return d.opacity });
    }

    function centerOfModule(module){
        var point = {};
        point.x = module.x+module.width/2;
        point.y = module.y+module.height/2;
        return point;
    }

    function centerOfArtifact(artifact){
        var point = {};
        point.x = artifact.x;
        point.y = artifact.y;
        return point;
    }
