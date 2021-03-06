    function initialPaintDepArtifactArtifact() {
        initialPaintDependencies("departifactartifact",nodes.depArtifactArtifacts);
    }

    function repaintDepArtifactArtifact() {
        repaintDependencies("departifactartifact",nodes.depArtifactArtifacts);
    }
    function positionDepArtifactArtifact(){
        for (i = 0; i < nodes.depArtifactArtifacts.length; i++) {
            var dep = nodes.depArtifactArtifacts[i];
            dep.x1 = -1;
            dep.y1 = -1;
            dep.x2 = -1;
            dep.y2 = -1;
            dep.opacity = 1;
            dep.highlight = false;
            dep.dimmed = false;
        }
        var artifactCache = createArtifactArray();
        switch (state) {
            case ALL_MODULES_DIAGRAM:
                break;
            case SELECTED_MODULES_DIAGRAM:
                for (i = 0; i < nodes.depArtifactArtifacts.length; i++) {
                    var dep = nodes.depArtifactArtifacts[i];
                    var artifactFrom = artifactCache[dep.from];
                    var artifactTo = artifactCache[dep.to];
                    if (
                        artifactFrom.isVisible
                        && artifactTo.isVisible
                        && ((artifactTo.module == selectedModule) || (artifactFrom.module == selectedModule))
                        && (artifactTo.module != artifactFrom.module)
                    ) {
                        setDepArtifactArtifactCoordinates(artifactFrom, artifactTo, dep);
                    }
                }
                break;
            case MODULE_ARTIFACTS_DIAGRAM:
                for (i = 0; i < nodes.depArtifactArtifacts.length; i++) {
                    var dep = nodes.depArtifactArtifacts[i];
                    var artifactFrom = artifactCache[dep.from];
                    var artifactTo = artifactCache[dep.to];
                    if (
                        artifactFrom.isVisible
                        && artifactTo.isVisible
                    ) {
                        setDepArtifactArtifactCoordinates(artifactFrom, artifactTo, dep);
                    }
                }
                break;
            case SELECTED_ARTIFACT_DIAGRAM:
                for (i = 0; i < nodes.depArtifactArtifacts.length; i++) {
                    var dep = nodes.depArtifactArtifacts[i];
                    var artifactFrom = artifactCache[dep.from];
                    var artifactTo = artifactCache[dep.to];
                    if (
                        artifactFrom.isVisible
                        && artifactTo.isVisible
                         && (artifactFrom.name == selectedArtifact || artifactTo.name == selectedArtifact)
                    ) {
                        setDepArtifactArtifactCoordinates(artifactFrom, artifactTo, dep);
                        if (artifactFrom.module != artifactTo.module){
                            dep.opacity = 0.3;
                        }
                    }
                }
                break;
        }
    }

    function setDepArtifactArtifactCoordinates(artifactFrom, artifactTo, dep){
        var artifactFromCenter = centerOfArtifact(artifactFrom);
        var artifactToCenter = centerOfArtifact(artifactTo);
        var artifactFromEdge = getEdgeOfArtifact(artifactFrom, artifactToCenter);
        var artifactToEdge = getEdgeOfArtifact(artifactToCenter, artifactFromCenter);
        dep.x1 = artifactFromEdge.x;
        dep.y1 = artifactFromEdge.y;
        dep.x2 = artifactToEdge.x;
        dep.y2 = artifactToEdge.y;
    }
