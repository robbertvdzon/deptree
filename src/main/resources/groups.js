    function initialPaintGroups() {
        svg
            .selectAll(".group")
            .data(nodes.groups)
            .enter()
            .append("rect")
            .attr("class", "group")
            .attr("x", function(d) { return d.x })
            .attr("y", function(d) { return d.y })
            .attr("width", function(d) { return d.width })
            .attr("height", function(d) { return d.height })
            .attr('stroke-width', 1)
            .attr('stroke', "#000000")
            .attr('opacity', function(d) { return d.opacity })
            .style("fill", "#EEEEEE");
        svg
            .selectAll(".groupname")
            .data(nodes.groups)
            .enter()
            .append("text")
            .text(function(d) { return d.name })
            .attr("class", "groupname")
            .attr("x", function(d) { return d.x + GROUPS_SMALL_WIDTH + 10})
            .attr("y", function(d) { return d.y +10})
            .attr('opacity', function(d) { return d.opacity })
            .attr("transform", function(d) {
                return "rotate("+d.angle+","+d.x+" ,"+ d.y+")"
            });

    }

    function repaintGroups() {
        svg
            .selectAll(".group")
            .data(nodes.groups)
            .transition()
            .duration(1000)
            .attr('stroke', "#000000" )
            .attr("x", function(d) { return d.x })
            .attr("y", function(d) { return d.y })
            .attr("width", function(d) { return d.width })
            .attr("height", function(d) { return d.height })
            .attr('opacity', function(d) { return d.opacity });
        svg
            .selectAll(".groupname")
            .data(nodes.groups)
            .transition()
            .duration(1000)
            .attr("x", function(d) { return d.x + GROUPS_SMALL_WIDTH + 10})
            .attr("y", function(d) { return d.y +10})
            .attr('opacity', function(d) { return d.opacity })
            .attr("transform", function(d) {
                return "rotate("+d.angle+","+d.x+" ,"+ d.y+")"
            });
    }

    function expandWithModules(group) {
        var modulesCount = 0;
        for (j = 0; j < nodes.modules.length; j++) {
            var module = nodes.modules[j];
            if (module.group == group.name) {
                modulesCount++;
                if (group.angle==0){
                    module.angle = -45;
                }
                else {
                    module.angle = group.angle;
                }
                module.opacity = 1;
                module.isVisible = true;
                module.x = group.x + 20 + CIRCLE_R + (modulesCount - 1) * (CIRCLE_R * 2 + 20);
                module.y = group.y +10;
            }
        }
        group.width = GROUPS_SMALL_WIDTH + 20 + CIRCLE_R + (modulesCount - 1) * (CIRCLE_R * 2 + 20) + CIRCLE_R;
        group.height = GROUPS_SMALL_HEIGHT + 40;
        group.y = group.y - 15;
    }



    function positionGroups() {
        switch (state) {
            case ALL_GROUPS_DIAGRAM:
                for (i = 0; i < nodes.groups.length; i++) {
                    var angle = (360 / totalGroups) * i;
                    var x = offset + RADIUS_GROUPS * Math.cos(angle * Math.PI / 180);
                    var y = offset + RADIUS_GROUPS * Math.sin(angle * Math.PI / 180);
                    nodes.groups[i].x = x;
                    nodes.groups[i].y = y;
                    nodes.groups[i].width = GROUPS_SMALL_WIDTH;
                    nodes.groups[i].height = GROUPS_SMALL_HEIGHT;
                    nodes.groups[i].angle = angle;
                    nodes.groups[i].opacity = 1;
                    nodes.groups[i].isVisible = true;
                }
                break;
            case SELECTED_GROUPS_DIAGRAM:
                var groupDepsTo = []
                var groupDepsFrom = []
                for (i = 0; i < nodes.depGroupGroups.length; i++) {
                    var depGroupGroup = nodes.depGroupGroups[i];
                    if (depGroupGroup.from == selectedGroup){
                        if (groupDepsTo.indexOf(depGroupGroup.to)==-1) {
                            groupDepsTo.push(depGroupGroup.to);
                        }
                    }
                }
                for (i = 0; i < nodes.depGroupGroups.length; i++) {
                    var depGroupGroup = nodes.depGroupGroups[i];
                    if (depGroupGroup.to == selectedGroup){
                        if ((groupDepsFrom.indexOf(depGroupGroup.to)==-1) && groupDepsTo.indexOf(depGroupGroup.to)==-1) {
                            groupDepsFrom.push(depGroupGroup.from);
                        }
                    }
                }
                var depFromIndex = 0;
                var depToIndex = 0;
                var nextX = 50;
                var nextToX = 50;

                for (i = 0; i < nodes.groups.length; i++) {
                    var group = nodes.groups[i];
                    var groupName = nodes.groups[i].name;
                    if (groupDepsFrom.indexOf(groupName)!=-1){
                        group.x = nextX;
                        group.y = 100;
                        group.angle = -45;
                        group.width = GROUPS_SMALL_WIDTH;
                        group.height = GROUPS_SMALL_HEIGHT;
                        group.opacity = 1;
                        group.isVisible = true;
                        if (group.expanded){
                            expandWithModules(group);
                        }
                        depFromIndex++;
                        nextX = group.x+group.width+25;
                    }
                    else if (groupDepsTo.indexOf(groupName)!=-1){
                        nodes.groups[i].x = nextToX;
                        nodes.groups[i].y = 500;
                        nodes.groups[i].angle = 45;
                        nodes.groups[i].width = GROUPS_SMALL_WIDTH;
                        nodes.groups[i].height = GROUPS_SMALL_HEIGHT;
                        nodes.groups[i].opacity = 1;
                        nodes.groups[i].isVisible = true;
                        if (group.expanded){
                            expandWithModules(group);
                        }
                        depToIndex++;
                        nextToX = group.x+group.width+25;
                    }
                    else if (groupName == selectedGroup){
                        nodes.groups[i].x = 200;
                        nodes.groups[i].y = 300;
                        nodes.groups[i].angle = 0;
                        nodes.groups[i].width = GROUPS_SMALL_WIDTH;
                        nodes.groups[i].height = GROUPS_SMALL_HEIGHT;
                        nodes.groups[i].opacity = 1;
                        nodes.groups[i].isVisible = true;
                        if (group.expanded){
                            expandWithModules(group);
                        }
                    }
                    else{
                        nodes.groups[i].width = 0;
                        nodes.groups[i].height = 0;
                        nodes.groups[i].x = 0;
                        nodes.groups[i].y = -20;
                        nodes.groups[i].opacity = 0;
                        nodes.groups[i].isVisible = false;
                    }
                }
                break;
            case GROUP_MODULES_DIAGRAM:
            case SELECTED_MODULE_DIAGRAM:
                for (i = 0; i < nodes.groups.length; i++) {
                    var groupName = nodes.groups[i].name;
                    if (groupName == selectedGroup){
                        nodes.groups[i].x = 2;
                        nodes.groups[i].y = 2;
                        nodes.groups[i].width = width-4;
                        nodes.groups[i].height = height-4;
                        nodes.groups[i].opacity = 1;
                    }
                    else{
                        nodes.groups[i].width = 0;
                        nodes.groups[i].height = 0;
                        nodes.groups[i].x = 0;
                        nodes.groups[i].y = -20;
                        nodes.groups[i].opacity = 0;
                    }
                }
                break;
            default:
                for (i = 0; i < nodes.groups.length; i++) {
                    nodes.groups[i].x = CIRCLE_R*-1;
                    nodes.groups[i].y = CIRCLE_R*-1;
                    nodes.groups[i].opacity = 0;
                }
                break;
        }
    }
