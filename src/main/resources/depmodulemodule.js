    function initialPaintDepModuleModule() {
        initialPaintDependencies("depmodulemodule",nodes.depModuleModules);
    }

    function repaintDepModuleModule() {
        repaintDependencies("depmodulemodule",nodes.depModuleModules);
    }

    function positionDepModuleModules(){
        // reset all modules
        for (i = 0; i < nodes.depModuleModules.length; i++) {
            var dep = nodes.depModuleModules[i];
            dep.x1 = -1;
            dep.y1 = -1;
            dep.x2 = -1;
            dep.y2 = -1;
            dep.opacity = 1;
            dep.highlight = false;
            dep.dimmed = false;
        }

        var moduleCache = createModuleArray();
        switch (state) {
            case ALL_MODULES_DIAGRAM:
                for (i = 0; i < nodes.depModuleModules.length; i++) {
                    var dep = nodes.depModuleModules[i];
                    var moduleFrom = moduleCache[dep.from];
                    var moduleTo = moduleCache[dep.to];
                    setDepModuleModuleCoordinates(moduleFrom, moduleTo, dep);
                }
                break;
            case SELECTED_MODULES_DIAGRAM:
                for (i = 0; i < nodes.depModuleModules.length; i++) {
                    var dep = nodes.depModuleModules[i];
                    var moduleFrom = moduleCache[dep.from];
                    var moduleTo = moduleCache[dep.to];
                    if (
                        moduleFrom.isVisible
                        && moduleTo.isVisible
                        && !moduleFrom.expanded
                        && !moduleTo.expanded
                        && (moduleFrom.name == selectedModule || moduleTo.name == selectedModule)
                    ) {
                        setDepModuleModuleCoordinates(moduleFrom, moduleTo, dep);
                    }
                }
                break;
            case MODULE_ARTIFACTS_DIAGRAM:
            case SELECTED_ARTIFACT_DIAGRAM:
                break;
        }
    }

    function setDepModuleModuleCoordinates(moduleFrom, moduleTo, dep){
        var moduleFromCenter = centerOfModule(moduleFrom);
        var moduleToCenter = centerOfModule(moduleTo);
        var moduleFromEdge = getEdgeOfModule(moduleFrom, moduleToCenter);
        var moduleToEdge = getEdgeOfModule(moduleTo, moduleFromCenter);

        dep.x1 = moduleFromEdge.x;
        dep.y1 = moduleFromEdge.y;
        dep.x2 = moduleToEdge.x;
        dep.y2 = moduleToEdge.y;
    }
