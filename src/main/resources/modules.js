    function initialPaintModules() {
        svg
            .selectAll(".module")
            .data(nodes.modules)
            .enter()
            .append("rect")
            .attr("class", "module")
            .attr("rx", 6)
            .attr("ry", 6)
            .attr("x", function(d) { return d.x })
            .attr("y", function(d) { return d.y })
            .attr("width", function(d) { return d.width })
            .attr("height", function(d) { return d.height })
            .attr('stroke-width', 1)
            .attr('stroke', "#000000")
            .attr('opacity', function(d) { return d.opacity })
            .style("fill", "#EEEEEE");
        svg
            .selectAll(".modulename")
            .data(nodes.modules)
            .enter()
            .append("text")
            .text(function(d) { return d.name })
            .attr("class", "modulename")
            .attr("x", function(d) { return d.x + MODULES_SMALL_WIDTH + 10})
            .attr("y", function(d) { return d.y +10})
            .attr('opacity', function(d) { return d.opacity })
            .attr("transform", function(d) {
                return "rotate("+d.angle+","+d.x+" ,"+ d.y+")"
            });

    }

    function repaintModules() {
        svg
            .selectAll(".module")
            .data(nodes.modules)
            .transition()
            .duration(1000)
            .attr('stroke', "#000000" )
            .attr("x", function(d) { return d.x })
            .attr("y", function(d) { return d.y })
            .attr("width", function(d) { return d.width })
            .attr("height", function(d) { return d.height })
            .attr('opacity', function(d) { return d.opacity });
        svg
            .selectAll(".modulename")
            .data(nodes.modules)
            .transition()
            .duration(1000)
            .attr("x", function(d) { return d.x + MODULES_SMALL_WIDTH + 10})
            .attr("y", function(d) { return d.y +10})
            .attr('opacity', function(d) { return d.opacity })
            .attr("transform", function(d) {
                return "rotate("+d.angle+","+d.x+" ,"+ d.y+")"
            });
    }

    function expandWithArtifacts(module) {
        var artifactsCount = 0;
        for (j = 0; j < nodes.artifacts.length; j++) {
            var artifact = nodes.artifacts[j];
            if (artifact.module == module.name) {
                artifactsCount++;
                if (module.angle==0){
                    artifact.angle = -45;
                }
                else {
                    artifact.angle = module.angle;
                }
                artifact.opacity = 1;
                artifact.isVisible = true;
                artifact.x = module.x + 20 + CIRCLE_R + (artifactsCount - 1) * (CIRCLE_R * 2 + 20);
                artifact.y = module.y +10;
            }
        }
        module.width = MODULES_SMALL_WIDTH + 20 + CIRCLE_R + (artifactsCount - 1) * (CIRCLE_R * 2 + 20) + CIRCLE_R;
        module.height = MODULES_SMALL_HEIGHT + 40;
        module.y = module.y - 15;
    }



    function positionModules() {
        switch (state) {
            case ALL_MODULES_DIAGRAM:
                for (i = 0; i < nodes.modules.length; i++) {
                    var angle = (360 / totalModules) * i;
                    var x = offset + RADIUS_MODULES * Math.cos(angle * Math.PI / 180);
                    var y = offset + RADIUS_MODULES * Math.sin(angle * Math.PI / 180);
                    nodes.modules[i].x = x;
                    nodes.modules[i].y = y;
                    nodes.modules[i].width = MODULES_SMALL_WIDTH;
                    nodes.modules[i].height = MODULES_SMALL_HEIGHT;
                    nodes.modules[i].angle = angle;
                    nodes.modules[i].opacity = 1;
                    nodes.modules[i].isVisible = true;
                }
                break;
            case SELECTED_MODULES_DIAGRAM:
                var moduleDepsTo = []
                var moduleDepsFrom = []
                for (i = 0; i < nodes.depModuleModules.length; i++) {
                    var depModuleModule = nodes.depModuleModules[i];
                    if (depModuleModule.from == selectedModule){
                        if (moduleDepsTo.indexOf(depModuleModule.to)==-1) {
                            moduleDepsTo.push(depModuleModule.to);
                        }
                    }
                }
                for (i = 0; i < nodes.depModuleModules.length; i++) {
                    var depModuleModule = nodes.depModuleModules[i];
                    if (depModuleModule.to == selectedModule){
                        if ((moduleDepsFrom.indexOf(depModuleModule.to)==-1) && moduleDepsTo.indexOf(depModuleModule.to)==-1) {
                            moduleDepsFrom.push(depModuleModule.from);
                        }
                    }
                }
                var depFromIndex = 0;
                var depToIndex = 0;
                var nextX = 50;
                var nextToX = 50;

                for (i = 0; i < nodes.modules.length; i++) {
                    var module = nodes.modules[i];
                    var moduleName = nodes.modules[i].name;
                    if (moduleDepsFrom.indexOf(moduleName)!=-1){
                        module.x = nextX;
                        module.y = DEPS_FROM_Y;
                        module.angle = -45;
                        module.width = MODULES_SMALL_WIDTH;
                        module.height = MODULES_SMALL_HEIGHT;
                        module.opacity = 1;
                        module.isVisible = true;
                        if (module.expanded){
                            expandWithArtifacts(module);
                        }
                        depFromIndex++;
                        nextX = module.x+module.width+25;
                    }
                    else if (moduleDepsTo.indexOf(moduleName)!=-1){
                        nodes.modules[i].x = nextToX;
                        nodes.modules[i].y = DEPS_TO_Y;
                        nodes.modules[i].angle = 45;
                        nodes.modules[i].width = MODULES_SMALL_WIDTH;
                        nodes.modules[i].height = MODULES_SMALL_HEIGHT;
                        nodes.modules[i].opacity = 1;
                        nodes.modules[i].isVisible = true;
                        if (module.expanded){
                            expandWithArtifacts(module);
                        }
                        depToIndex++;
                        nextToX = module.x+module.width+25;
                    }
                    else if (moduleName == selectedModule){
                        nodes.modules[i].x = 200;
                        nodes.modules[i].y = SELECTED_DEP_Y;
                        nodes.modules[i].angle = 0;
                        nodes.modules[i].width = MODULES_SMALL_WIDTH;
                        nodes.modules[i].height = MODULES_SMALL_HEIGHT;
                        nodes.modules[i].opacity = 1;
                        nodes.modules[i].isVisible = true;
                        if (module.expanded){
                            expandWithArtifacts(module);
                            nodes.modules[i].angle = -45;
                        }
                    }
                    else{
                        nodes.modules[i].width = 0;
                        nodes.modules[i].height = 0;
                        nodes.modules[i].x = 0;
                        nodes.modules[i].y = -20;
                        nodes.modules[i].opacity = 0;
                        nodes.modules[i].isVisible = false;
                    }
                }
                break;
            case MODULE_ARTIFACTS_DIAGRAM:
            case SELECTED_ARTIFACT_DIAGRAM:
            default:
                for (i = 0; i < nodes.modules.length; i++) {
                    nodes.modules[i].x = CIRCLE_R*-1;
                    nodes.modules[i].y = CIRCLE_R*-1;
                    nodes.modules[i].opacity = 0;
                }
                break;
        }
    }
