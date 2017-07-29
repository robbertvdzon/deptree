    function initialPaintDepGroupGroup() {
        initialPaintDependencies("depgroupgroup",nodes.depGroupGroups);
    }

    function repaintDepGroupGroup() {
        repaintDependencies("depgroupgroup",nodes.depGroupGroups);
    }

    function positionDepGroupGroups(){
        // reset all groups
        for (i = 0; i < nodes.depGroupGroups.length; i++) {
            var dep = nodes.depGroupGroups[i];
            dep.x1 = -1;
            dep.y1 = -1;
            dep.x2 = -1;
            dep.y2 = -1;
            dep.opacity = 1;
            dep.highlight = false;
            dep.dimmed = false;
        }

        var groupCache = createGroupArray();
        switch (state) {
            case ALL_GROUPS_DIAGRAM:
                for (i = 0; i < nodes.depGroupGroups.length; i++) {
                    var dep = nodes.depGroupGroups[i];
                    var groupFrom = groupCache[dep.from];
                    var groupTo = groupCache[dep.to];
                    setDepGroupGroupCoordinates(groupFrom, groupTo, dep);
                }
                break;
            case SELECTED_GROUPS_DIAGRAM:
                for (i = 0; i < nodes.depGroupGroups.length; i++) {
                    var dep = nodes.depGroupGroups[i];
                    var groupFrom = groupCache[dep.from];
                    var groupTo = groupCache[dep.to];
                    if (
                        groupFrom.isVisible
                        && groupTo.isVisible
                        && !groupFrom.expanded
                        && !groupTo.expanded
                        && (groupFrom.name == selectedGroup || groupTo.name == selectedGroup)
                    ) {
                        setDepGroupGroupCoordinates(groupFrom, groupTo, dep);
                    }
                }
                break;
            case GROUP_MODULES_DIAGRAM:
            case SELECTED_MODULE_DIAGRAM:
                break;
        }
    }

    function setDepGroupGroupCoordinates(groupFrom, groupTo, dep){
        var groupFromCenter = centerOfGroup(groupFrom);
        var groupToCenter = centerOfGroup(groupTo);
        var groupFromEdge = getEdgeOfGroup(groupFrom, groupToCenter);
        var groupToEdge = getEdgeOfGroup(groupTo, groupFromCenter);

        dep.x1 = groupFromEdge.x;
        dep.y1 = groupFromEdge.y;
        dep.x2 = groupToEdge.x;
        dep.y2 = groupToEdge.y;
    }
