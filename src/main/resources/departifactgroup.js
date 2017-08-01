    function initialPaintDepArtifactGroup() {
        initialPaintDependencies("departifactgroup",nodes.depArtifactGroups);
    }

    function repaintDepArtifactGroup() {
        repaintDependencies("departifactgroup",nodes.depArtifactGroups);
    }
    function positionDepArtifactGroup(){
        var artifactCache = createArtifactArray();
        var groupCache = createGroupArray();
        for (i = 0; i < nodes.depArtifactGroups.length; i++) {
            var dep = nodes.depArtifactGroups[i];
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
                for (i = 0; i < nodes.depArtifactGroups.length; i++) {
                    var dep = nodes.depArtifactGroups[i];
                    var artifactFrom = artifactCache[dep.from];
                    var groupTo = groupCache[dep.to];
                    if (
                        artifactFrom.isVisible
                        && groupTo.isVisible
                        && !groupTo.expanded
                        && ((artifactFrom.group == selectedGroup) || (groupTo.name == selectedGroup))

                        && (artifactFrom.group != groupTo.name)
                    ) {
                        setDepArtifactGroupCoordinates(artifactFrom, groupTo, dep);
                    }
                }
                break;
            default:
                break;
        }
    }

    function setDepArtifactGroupCoordinates(artifactFrom, groupTo, dep){
        var artifactFromCenter = centerOfArtifact(artifactFrom);
        var groupToCenter = centerOfGroup(groupTo);
        var artifactFromEdge = getEdgeOfArtifact(artifactFrom, groupToCenter);
        var groupToEdge = getEdgeOfGroup(groupTo, artifactFromCenter);

        dep.x1 = artifactFromEdge.x;
        dep.y1 = artifactFromEdge.y;
        dep.x2 = groupToEdge.x;
        dep.y2 = groupToEdge.y;
    }


