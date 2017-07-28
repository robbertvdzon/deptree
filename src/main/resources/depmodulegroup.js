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
                        dep.x1 = moduleFrom.x;
                        dep.y1 = moduleFrom.y;
                        dep.x2 = groupTo.x;
                        dep.y2 = groupTo.y;
                        placeXYOnEdges(dep);
                    }
                }
                break;
            default:
                break;
        }
    }


