    function initialPaintDepArtifactModule() {
        initialPaintDependencies("departifactmodule",nodes.depArtifactModules);
    }

    function repaintDepArtifactModule() {
        repaintDependencies("departifactmodule",nodes.depArtifactModules);
    }
    function positionDepArtifactModule(){
        var artifactCache = createArtifactArray();
        var moduleCache = createModuleArray();
        for (i = 0; i < nodes.depArtifactModules.length; i++) {
            var dep = nodes.depArtifactModules[i];
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
                for (i = 0; i < nodes.depArtifactModules.length; i++) {
                    var dep = nodes.depArtifactModules[i];
                    var artifactFrom = artifactCache[dep.from];
                    var moduleTo = moduleCache[dep.to];
                    if (
                        artifactFrom.isVisible
                        && moduleTo.isVisible
                        && !moduleTo.expanded
                        && ((artifactFrom.module == selectedModule) || (moduleTo.name == selectedModule))

                        && (artifactFrom.module != moduleTo.name)
                    ) {
                        setDepArtifactModuleCoordinates(artifactFrom, moduleTo, dep);
                    }
                }
                break;
            default:
                break;
        }
    }

    function setDepArtifactModuleCoordinates(artifactFrom, moduleTo, dep){
        var artifactFromCenter = centerOfArtifact(artifactFrom);
        var moduleToCenter = centerOfModule(moduleTo);
        var artifactFromEdge = getEdgeOfArtifact(artifactFrom, moduleToCenter);
        var moduleToEdge = getEdgeOfModule(moduleTo, artifactFromCenter);

        dep.x1 = artifactFromEdge.x;
        dep.y1 = artifactFromEdge.y;
        dep.x2 = moduleToEdge.x;
        dep.y2 = moduleToEdge.y;
    }


