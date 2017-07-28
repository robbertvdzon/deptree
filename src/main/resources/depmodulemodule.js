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
                        var moduleFromCenter = centerOfModule(moduleFrom);
                        var moduleToCenter = centerOfModule(moduleTo);
                        dep.x1 = moduleFromCenter.x;
                        dep.y1 = moduleFromCenter.y;
                        dep.x2 = moduleToCenter.x;
                        dep.y2 = moduleToCenter.y;
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
                        var moduleFromCenter = centerOfModule(moduleFrom);
                        var moduleToCenter = centerOfModule(moduleTo);
                        dep.x1 = moduleFromCenter.x;
                        dep.y1 = moduleFromCenter.y;
                        dep.x2 = moduleToCenter.x;
                        dep.y2 = moduleToCenter.y;
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
                        var moduleFromCenter = centerOfModule(moduleFrom);
                        var moduleToCenter = centerOfModule(moduleTo);
                        dep.x1 = moduleFromCenter.x;
                        dep.y1 = moduleFromCenter.y;
                        dep.x2 = moduleToCenter.x;
                        dep.y2 = moduleToCenter.y;
                    }
                }
                break;
        }
    }

