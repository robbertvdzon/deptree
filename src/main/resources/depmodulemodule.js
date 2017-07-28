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
                        dep.x1 = moduleFrom.x;
                        dep.y1 = moduleFrom.y;
                        dep.x2 = moduleTo.x;
                        dep.y2 = moduleTo.y;
                        placeXYOnEdges(dep);
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
                        dep.x1 = moduleFrom.x;
                        dep.y1 = moduleFrom.y;
                        dep.x2 = moduleTo.x;
                        dep.y2 = moduleTo.y;
                        placeXYOnEdges(dep);
                    }
                    placeXYOnEdges(dep);
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
                        dep.x1 = moduleFrom.x;
                        dep.y1 = moduleFrom.y;
                        dep.x2 = moduleTo.x;
                        dep.y2 = moduleTo.y;
                        placeXYOnEdges(dep);
                    }
                }
                break;
        }
    }

