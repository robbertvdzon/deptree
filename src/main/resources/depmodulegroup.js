    function initialPaintDepModuleGroup() {
        initialPaintDependencies("depmodulegroup",nodes.depModuleGroups);
    }

    function repaintDepModuleGroup() {
        repaintDependencies("depmodulegroup",nodes.depModuleGroups);
    }
    function positionDepModuleGroup(){
        var moduleCache = createModuleArray();
        var groupCache = createGroupArray();
        for (i = 0; i < nodes.depModuleGroups.length; i++) {
            var dep = nodes.depModuleGroups[i];
            dep.x1 = -1;
            dep.y1 = -1;
            dep.x2 = -1;
            dep.y2 = -1;
        }
        switch (state) {
            case SELECTED_GROUPS_DIAGRAM:
                for (i = 0; i < nodes.depModuleGroups.length; i++) {
                    var dep = nodes.depModuleGroups[i];
                    var moduleFrom = moduleCache[dep.from];
                    var groupTo = groupCache[dep.to];
                    if (
                        moduleFrom.isVisible
                        && groupTo.isVisible
                        && !groupTo.expanded
                        && ((moduleFrom.group == selectedGroup) || (groupTo.name == selectedGroup))

                        && (moduleFrom.group != groupTo.name)
                    ) {
                        setDepModuleGroupCoordinates(moduleFrom, groupTo, dep);
                    }
                }
                break;
            default:
                break;
        }
    }

    function setDepModuleGroupCoordinates(moduleFrom, groupTo, dep){
        var groupToCenter = centerOfGroup(groupTo);
        var moduleFromEdge = getEdgeOfModule(moduleFrom, groupToCenter);
        dep.x1 = moduleFromEdge.x;
        dep.y1 = moduleFromEdge.y;
        dep.x2 = groupToCenter.x;
        dep.y2 = groupToCenter.y;
    }


