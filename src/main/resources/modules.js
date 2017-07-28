    function initialPaintModules() {
        svg
            .selectAll(".module")
            .data(nodes.modules)
            .enter()
            .append("circle")
            .attr("class", "module")
            .attr("cx", function(d) { return d.x })
            .attr("cy", function(d) { return d.y })
            .attr("r", CIRCLE_R)
            .attr('stroke-width', 1)
            .attr('stroke', "#000000")
            .style("fill", "#EEEEEE")
            .attr('opacity', function(d) { return d.opacity });

        svg
            .selectAll(".modulename")
            .data(nodes.modules)
            .enter()
            .append("text")
            .text(function(d) { return getArtifactName(d.name) })
            .attr("class", "modulename")
            .attr("x", function(d) { return d.x + CIRCLE_R + 10})
            .attr("y", function(d) { return d.y +10})
            .attr('opacity', function(d) { return d.opacity })
            .attr("transform", function(d) {
                return "rotate("+d.angle+","+d.x+" ,"+ d.y+")"
            })
        ;

    }

    function getArtifactName(name) {
        var names = name.split(":");
        return names[1];
    }

    function repaintModules() {
        svg
            .selectAll(".module")
            .data(nodes.modules)
            .transition()
            .duration(1000)
            .attr("cx", function(d) { return d.x })
            .attr("cy", function(d) { return d.y })
            .attr('opacity', function(d) { return d.opacity });
        svg
            .selectAll(".modulename")
            .data(nodes.modules)
            .transition()
            .duration(1000)
            .attr("x", function(d) { return d.x + CIRCLE_R + 10})
            .attr("y", function(d) { return d.y +10})
            .attr("transform", function(d) {
                return "rotate("+d.angle+","+d.x+" ,"+ d.y+")"
            })
            .attr('opacity', function(d) { return d.opacity });

    }

    function resetModules() {
        for (i = 0; i < nodes.modules.length; i++) {
            nodes.modules[i].x = CIRCLE_R*-1;
            nodes.modules[i].y = CIRCLE_R*-1;
            nodes.modules[i].isVisible = false;
            nodes.modules[i].angle= 0;
            nodes.modules[i].opacity = 0;
        }
    }

    function positionModules() {
        var groupModules = [];
        for (i = 0; i < nodes.modules.length; i++) {
            var module = nodes.modules[i];
            if (module.group == selectedGroup) {
                groupModules.push(module);
            }
        }

        var totalModules = groupModules.length;


        // set only the selected modules
        switch (state) {
            case ALL_GROUPS_DIAGRAM:
                break;
            case SELECTED_GROUPS_DIAGRAM:
                break;
            case GROUP_MODULES_DIAGRAM:
                for (i = 0; i < groupModules.length; i++) {
                    var angle = (360 / totalModules) * i;
                    var x = offset + RADIUS_GROUP_MODULE * Math.cos(angle * Math.PI / 180);
                    var y = offset + RADIUS_GROUP_MODULE * Math.sin(angle * Math.PI / 180);
                    groupModules[i].x = x;
                    groupModules[i].y = y;
                    groupModules[i].angle = angle;
                    groupModules[i].opacity = 1;
                    groupModules[i].isVisible = true;
                }
                break;
            case SELECTED_MODULE_DIAGRAM:
                var modulesDepsTo = [];
                var modulesDepsFrom = [];
                for (i = 0; i < nodes.depModuleModules.length; i++) {
                    var depModuleModule = nodes.depModuleModules[i];
                    if (depModuleModule.from == selectedModule){
                        if (modulesDepsTo.indexOf(depModuleModule.to)==-1) {
                            modulesDepsTo.push(depModuleModule.to);
                        }
                    }
                }
                for (i = 0; i < nodes.depModuleModules.length; i++) {
                    var depModuleModule = nodes.depModuleModules[i];
                    if (depModuleModule.to == selectedModule){
                        if ((modulesDepsFrom.indexOf(depModuleModule.to)==-1) && modulesDepsFrom.indexOf(depModuleModule.to)==-1) {
                            modulesDepsFrom.push(depModuleModule.from);
                        }
                    }
                }
                var depFromIndex = 0;
                var depToIndex = 0;

                for (i = 0; i < groupModules.length; i++) {
                    var moduleName = groupModules[i].name;
                    if (modulesDepsFrom.indexOf(moduleName)!=-1){
                        groupModules[i].x = 50+depFromIndex*50;
                        groupModules[i].y = 200;
                        groupModules[i].angle = -45;
                        groupModules[i].opacity = 1;
                        groupModules[i].isVisible = true;
                        depFromIndex++;
                    }
                    else if (modulesDepsTo.indexOf(moduleName)!=-1){
                        groupModules[i].x = 50+depToIndex*50;
                        groupModules[i].y = 400;
                        groupModules[i].angle = 45;
                        groupModules[i].opacity = 1;
                        groupModules[i].isVisible = true;
                        depToIndex++;
                    }
                    else if (moduleName == selectedModule){
                        groupModules[i].x = 200;
                        groupModules[i].y = 300;
                        groupModules[i].angle = 0;
                        groupModules[i].opacity = 1;
                        groupModules[i].isVisible = true;
                    }
                    else{
                        groupModules[i].width = 0;
                        groupModules[i].height = 0;
                        groupModules[i].x = 0;
                        groupModules[i].y = -20;
                        groupModules[i].opacity = 0;
                    }
                }
                break;

            default:
                break;
        }
    }

