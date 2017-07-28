    function initialPaintDepModuleModule() {
        initialPaintDependencies("depmodulemodule",nodes.depModuleModules);
    }

    function repaintDepModuleModule() {
        repaintDependencies("depmodulemodule",nodes.depModuleModules);
    }
    function positionDepModuleModule(){
        for (i = 0; i < nodes.depModuleModules.length; i++) {
            var dep = nodes.depModuleModules[i];
            dep.x1 = -1;
            dep.y1 = -1;
            dep.x2 = -1;
            dep.y2 = -1;
        }
        var moduleCache = createModuleArray();
        switch (state) {
            case ALL_GROUPS_DIAGRAM:
                break;
            case SELECTED_GROUPS_DIAGRAM:
                for (i = 0; i < nodes.depModuleModules.length; i++) {
                    var dep = nodes.depModuleModules[i];
                    var moduleFrom = moduleCache[dep.from];
                    var moduleTo = moduleCache[dep.to];
                    if (
                        moduleFrom.isVisible
                        && moduleTo.isVisible
                        && ((moduleTo.group == selectedGroup) || (moduleFrom.group == selectedGroup))
                        && (moduleTo.group != moduleFrom.group)
                    ) {
                        setDepModuleModuleCoordinates(moduleFrom, moduleTo, dep);
                    }
                }
                break;
            case GROUP_MODULES_DIAGRAM:
                for (i = 0; i < nodes.depModuleModules.length; i++) {
                    var dep = nodes.depModuleModules[i];
                    var moduleFrom = moduleCache[dep.from];
                    var moduleTo = moduleCache[dep.to];
                    if (
                        moduleFrom.isVisible
                        && moduleTo.isVisible
                    ) {
                        setDepModuleModuleCoordinates(moduleFrom, moduleTo, dep);
                    }
                }
                break;
            case SELECTED_MODULE_DIAGRAM:
                for (i = 0; i < nodes.depModuleModules.length; i++) {
                    var dep = nodes.depModuleModules[i];
                    var moduleFrom = moduleCache[dep.from];
                    var moduleTo = moduleCache[dep.to];
                    if (
                        moduleFrom.isVisible
                        && moduleTo.isVisible
                        && (moduleFrom.name == selectedModule || moduleTo.name == selectedModule)
                    ) {
                        setDepModuleModuleCoordinates(moduleFrom, moduleTo, dep);
                    }
                }
                break;
        }
    }

    function setDepModuleModuleCoordinates(moduleFrom, moduleTo, dep){
        var moduleFromCenter = centerOfModule(moduleFrom);
        var moduleToCenter = centerOfModule(moduleTo);
        var moduleFromEdge = getEdgeOfModule(moduleFrom, moduleToCenter);
        var moduleToEdge = getEdgeOfModule(moduleToCenter, moduleFromCenter);
        dep.x1 = moduleFromEdge.x;
        dep.y1 = moduleFromEdge.y;
        dep.x2 = moduleToEdge.x;
        dep.y2 = moduleToEdge.y;
    }
