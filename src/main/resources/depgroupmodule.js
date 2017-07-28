    function initialPaintDepGroupModule() {
        initialPaintDependencies("depgroupmodule",nodes.depGroupModules);
    }

    function repaintDepGroupModule() {
        repaintDependencies("depgroupmodule",nodes.depGroupModules);
    }

    function positionDepGroupModule(){
        var moduleCache = createModuleArray();
        var groupCache = createGroupArray();
        for (i = 0; i < nodes.depGroupModules.length; i++) {
            var dep = nodes.depGroupModules[i];
            dep.x1 = -1;
            dep.y1 = -1;
            dep.x2 = -1;
            dep.y2 = -1;
        }
        switch (state) {
            case SELECTED_GROUPS_DIAGRAM:
                for (i = 0; i < nodes.depGroupModules.length; i++) {
                    var dep = nodes.depGroupModules[i];
                    var groupFrom = groupCache[dep.from];
                    var moduleTo = moduleCache[dep.to];
                    if (
                        groupFrom.isVisible
                        && moduleTo.isVisible
                        && !groupFrom.expanded
                        && ((moduleTo.group == selectedGroup) || (groupFrom.name == selectedGroup))
                        && (moduleTo.group != groupFrom.name)
                    ) {
                        dep.x1 = groupFrom.x;
                        dep.y1 = groupFrom.y;
                        dep.x2 = moduleTo.x;
                        dep.y2 = moduleTo.y;
                        placeXYOnEdges(dep);
                    }
                }
                break;
            default:
                break;
        }
    }
