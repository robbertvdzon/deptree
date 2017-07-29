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
            dep.opacity = 1;
            dep.highlight = false;
            dep.dimmed = false;
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
                        setDepGroupModulesCoordinates(groupFrom, moduleTo, dep);
                    }
                }
                break;
            default:
                break;
        }
    }

    function setDepGroupModulesCoordinates(groupFrom, moduleTo, dep){
        var groupFromCenter = centerOfGroup(groupFrom);
        var moduleToCenter = centerOfModule(moduleTo);
        var moduleToEdge = getEdgeOfModule(moduleTo, groupFromCenter);
        var groupFromEdge = getEdgeOfGroup(groupFrom, moduleToCenter);

        dep.x1 = groupFromEdge.x;
        dep.y1 = groupFromEdge.y;
        dep.x2 = moduleToEdge.x;
        dep.y2 = moduleToEdge.y;
    }
