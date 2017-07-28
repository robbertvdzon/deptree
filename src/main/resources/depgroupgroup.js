    function initialPaintDepGroupGroup() {
        initialPaintDependencies("depgroupgroup",nodes.depGroupGroups);
    }

    function repaintDepGroupGroup() {
        repaintDependencies("depgroupgroup",nodes.depGroupGroups);
    }

    function positionDepGroupGroups(){
        var groupCache = createGroupArray();
        switch (state) {
            case ALL_GROUPS_DIAGRAM:
                for (i = 0; i < nodes.depGroupGroups.length; i++) {
                    var dep = nodes.depGroupGroups[i];
                    var groupFrom = groupCache[dep.from];
                    var groupTo = groupCache[dep.to];
                    dep.x1 = groupFrom.x + groupFrom.width / 2;
                    dep.y1 = groupFrom.y + groupFrom.height / 2;
                    dep.x2 = groupTo.x + groupTo.width / 2;
                    dep.y2 = groupTo.y + groupTo.height / 2;
                    placeXYOnEdges(dep);
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
                        dep.x1 = groupFrom.x+groupFrom.width/2;
                        dep.x2 = groupTo.x+groupTo.width/2;
                        if (groupFrom.y>groupTo.y){
                            dep.y1 = groupFrom.y;
                            dep.y2 = groupTo.y+groupTo.height;
                        }
                        else{
                            dep.y1 = groupFrom.y+groupFrom.height;
                            dep.y2 = groupTo.y;
                        }

//                        placeXYOnEdges(dep);
                    }
                    else{
                        dep.x1 = -1;
                        dep.y1 = -1;
                        dep.x2 = -1;
                        dep.y2 = -1;
                    }
                }
                break;
            case GROUP_MODULES_DIAGRAM:
            case SELECTED_MODULE_DIAGRAM:
                for (i = 0; i < nodes.depGroupGroups.length; i++) {
                    var dep = nodes.depGroupGroups[i];
                    dep.x1 = -1;
                    dep.y1 = -1;
                    dep.x2 = -1;
                    dep.y2 = -1;
                }
                break;
        }
    }
