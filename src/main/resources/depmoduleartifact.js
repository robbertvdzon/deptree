    function initialPaintDepModuleArtifact() {
        initialPaintDependencies("depmoduleartifact",nodes.depModuleArtifacts);
    }

    function repaintDepModuleArtifact() {
        repaintDependencies("depmoduleartifact",nodes.depModuleArtifacts);
    }

    function positionDepModuleArtifact(){
        var artifactCache = createArtifactArray();
        var moduleCache = createModuleArray();
        for (i = 0; i < nodes.depModuleArtifacts.length; i++) {
            var dep = nodes.depModuleArtifacts[i];
            dep.x1 = -1;
            dep.y1 = -1;
            dep.x2 = -1;
            dep.y2 = -1;
            dep.opacity = 1;
            dep.highlight = false;
            dep.dimmed = false;
        }
        switch (state) {
            case SELECTED_MODULES_DIAGRAM:
                for (i = 0; i < nodes.depModuleArtifacts.length; i++) {
                    var dep = nodes.depModuleArtifacts[i];
                    var moduleFrom = moduleCache[dep.from];
                    var artifactTo = artifactCache[dep.to];
                    if (
                        moduleFrom.isVisible
                        && artifactTo.isVisible
                        && !moduleFrom.expanded
                        && ((artifactTo.module == selectedModule) || (moduleFrom.name == selectedModule))
                        && (artifactTo.module != moduleFrom.name)
                    ) {
                        setDepModuleArtifactsCoordinates(moduleFrom, artifactTo, dep);
                    }
                }
                break;
            default:
                break;
        }
    }

    function setDepModuleArtifactsCoordinates(moduleFrom, artifactTo, dep){
        var moduleFromCenter = centerOfModule(moduleFrom);
        var artifactToCenter = centerOfArtifact(artifactTo);
        var artifactToEdge = getEdgeOfArtifact(artifactTo, moduleFromCenter);
        var moduleFromEdge = getEdgeOfModule(moduleFrom, artifactToCenter);

        dep.x1 = moduleFromEdge.x;
        dep.y1 = moduleFromEdge.y;
        dep.x2 = artifactToEdge.x;
        dep.y2 = artifactToEdge.y;
    }
