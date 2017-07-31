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
            .attr("x", function(d) { return d.x+20})
            .attr("y", function(d) { return d.y})
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
            .attr("x", function(d) { return d.x+20})
            .attr("y", function(d) { return d.y})
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
        var moduleCache = createModuleArray();
        var selectedModuleObject = moduleCache[selectedModule];
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
                var processData = {};
                processData.depFromIndex = 0;
                processData.depToIndex = 0;
                processData.modulesToList = groupModules;
                processData.modulesDepsFrom = modulesDepsFrom;
                processData.modulesDepsTo = modulesDepsTo;
                processData.selectedModuleObject = selectedModuleObject;


                var processedModules = {};
                for (i = 0; i < groupModules.length; i++) {
                    var module = groupModules[i];
                    processedModules[module.name] = module;
                    processModule(module, processData);
                }
                processData.modulesToList = nodes.modules;
                for (i = 0; i < nodes.modules.length; i++) {
                    var module = nodes.modules[i];
                    if (!processedModules[module.name]) {
                        processModule(processData.modulesToList[i], processData);
                    }
                }
                break;

            default:
                break;
        }
    }

    function processModule(module, processData){
        var moduleName = module.name;
        if (processData.modulesDepsFrom.indexOf(moduleName)!=-1){
            module.x = 50+processData.depFromIndex*50;
            module.y = DEPS_FROM_Y;
            module.angle = -45;
            module.opacity = 0.3;
            if (module.group == processData.selectedModuleObject.group){
                module.opacity = 1;
            }
            module.isVisible = true;
            processData.depFromIndex++;
        }
        else if (processData.modulesDepsTo.indexOf(moduleName)!=-1){
            module.x = 50+processData.depToIndex*50;
            module.y = DEPS_TO_Y;
            module.angle = 45;
            module.opacity = 0.3;
            if (module.group == processData.selectedModuleObject.group){
                module.opacity = 1;
            }
            module.isVisible = true;
            processData.depToIndex++;
        }
        else if (moduleName == selectedModule){
            module.x = 200;
            module.y = SELECTED_DEP_Y;
            module.angle = 0;
            module.opacity = 1;
            module.isVisible = true;
        }
        else{
            module.width = 0;
            module.height = 0;
            module.x = 0;
            module.y = -20;
            module.opacity = 0;
        }

    }

