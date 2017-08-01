    function initialPaintDepGroupArtifact() {
        initialPaintDependencies("depgroupartifact",nodes.depGroupArtifacts);
    }

    function repaintDepGroupArtifact() {
        repaintDependencies("depgroupartifact",nodes.depGroupArtifacts);
    }

    function positionDepGroupArtifact(){
        var artifactCache = createArtifactArray();
        var groupCache = createGroupArray();
        for (i = 0; i < nodes.depGroupArtifacts.length; i++) {
            var dep = nodes.depGroupArtifacts[i];
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
                for (i = 0; i < nodes.depGroupArtifacts.length; i++) {
                    var dep = nodes.depGroupArtifacts[i];
                    var groupFrom = groupCache[dep.from];
                    var artifactTo = artifactCache[dep.to];
                    if (
                        groupFrom.isVisible
                        && artifactTo.isVisible
                        && !groupFrom.expanded
                        && ((artifactTo.group == selectedGroup) || (groupFrom.name == selectedGroup))
                        && (artifactTo.group != groupFrom.name)
                    ) {
                        setDepGroupArtifactsCoordinates(groupFrom, artifactTo, dep);
                    }
                }
                break;
            default:
                break;
        }
    }

    function setDepGroupArtifactsCoordinates(groupFrom, artifactTo, dep){
        var groupFromCenter = centerOfGroup(groupFrom);
        var artifactToCenter = centerOfArtifact(artifactTo);
        var artifactToEdge = getEdgeOfArtifact(artifactTo, groupFromCenter);
        var groupFromEdge = getEdgeOfGroup(groupFrom, artifactToCenter);

        dep.x1 = groupFromEdge.x;
        dep.y1 = groupFromEdge.y;
        dep.x2 = artifactToEdge.x;
        dep.y2 = artifactToEdge.y;
    }
