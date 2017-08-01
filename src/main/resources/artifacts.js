    function initialPaintArtifacts() {
        svg
            .selectAll(".artifact")
            .data(nodes.artifacts)
            .enter()
            .append("circle")
            .attr("class", "artifact")
            .attr("cx", function(d) { return d.x })
            .attr("cy", function(d) { return d.y })
            .attr("r", CIRCLE_R)
            .attr('stroke-width', 1)
            .attr('stroke', "#000000")
            .style("fill", "#EEEEEE")
            .attr('opacity', function(d) { return d.opacity });

        svg
            .selectAll(".artifactname")
            .data(nodes.artifacts)
            .enter()
            .append("text")
            .text(function(d) { return getArtifactName(d.name) })
            .attr("class", "artifactname")
            .attr("x", function(d) { return d.x+20})
            .attr("y", function(d) { return d.y})
            .attr('opacity', function(d) { return d.opacity })
            .attr("transform", function(d) {
                return "rotate("+d.angle+","+d.x+" ,"+ d.y+")"
            })
        ;

    }

    function getArtifactName(name) {
        var names = name.split(":");
        return names[1];
    }

    function repaintArtifacts() {
        svg
            .selectAll(".artifact")
            .data(nodes.artifacts)
            .transition()
            .duration(1000)
            .attr("cx", function(d) { return d.x })
            .attr("cy", function(d) { return d.y })
            .attr('opacity', function(d) { return d.opacity });
        svg
            .selectAll(".artifactname")
            .data(nodes.artifacts)
            .transition()
            .duration(1000)
            .attr("x", function(d) { return d.x+20})
            .attr("y", function(d) { return d.y})
            .attr("transform", function(d) {
                return "rotate("+d.angle+","+d.x+" ,"+ d.y+")"
            })
            .attr('opacity', function(d) { return d.opacity });

    }

    function resetArtifacts() {
        for (i = 0; i < nodes.artifacts.length; i++) {
            nodes.artifacts[i].x = CIRCLE_R*-1;
            nodes.artifacts[i].y = CIRCLE_R*-1;
            nodes.artifacts[i].isVisible = false;
            nodes.artifacts[i].angle= 0;
            nodes.artifacts[i].opacity = 0;
        }
    }

    function positionArtifacts() {
        var groupArtifacts = [];
        var artifactCache = createArtifactArray();
        var selectedArtifactObject = artifactCache[selectedArtifact];
        for (i = 0; i < nodes.artifacts.length; i++) {
            var artifact = nodes.artifacts[i];
            if (artifact.group == selectedGroup) {
                groupArtifacts.push(artifact);
            }
        }

        var totalArtifacts = groupArtifacts.length;


        // set only the selected artifacts
        switch (state) {
            case ALL_GROUPS_DIAGRAM:
                break;
            case SELECTED_GROUPS_DIAGRAM:
                break;
            case GROUP_ARTIFACTS_DIAGRAM:
                for (i = 0; i < groupArtifacts.length; i++) {
                    var angle = (360 / totalArtifacts) * i;
                    var x = offset + RADIUS_GROUP_ARTIFACT * Math.cos(angle * Math.PI / 180);
                    var y = offset + RADIUS_GROUP_ARTIFACT * Math.sin(angle * Math.PI / 180);
                    groupArtifacts[i].x = x;
                    groupArtifacts[i].y = y;
                    groupArtifacts[i].angle = angle;
                    groupArtifacts[i].opacity = 1;
                    groupArtifacts[i].isVisible = true;
                }
                break;
            case SELECTED_ARTIFACT_DIAGRAM:
                var artifactsDepsTo = [];
                var artifactsDepsFrom = [];
                for (i = 0; i < nodes.depArtifactArtifacts.length; i++) {
                    var depArtifactArtifact = nodes.depArtifactArtifacts[i];
                    if (depArtifactArtifact.from == selectedArtifact){
                        if (artifactsDepsTo.indexOf(depArtifactArtifact.to)==-1) {
                            artifactsDepsTo.push(depArtifactArtifact.to);
                        }
                    }
                }
                for (i = 0; i < nodes.depArtifactArtifacts.length; i++) {
                    var depArtifactArtifact = nodes.depArtifactArtifacts[i];
                    if (depArtifactArtifact.to == selectedArtifact){
                        if ((artifactsDepsFrom.indexOf(depArtifactArtifact.to)==-1) && artifactsDepsFrom.indexOf(depArtifactArtifact.to)==-1) {
                            artifactsDepsFrom.push(depArtifactArtifact.from);
                        }
                    }
                }
                var processData = {};
                processData.depFromIndex = 0;
                processData.depToIndex = 0;
                processData.artifactsToList = groupArtifacts;
                processData.artifactsDepsFrom = artifactsDepsFrom;
                processData.artifactsDepsTo = artifactsDepsTo;
                processData.selectedArtifactObject = selectedArtifactObject;


                var processedArtifacts = {};
                for (i = 0; i < groupArtifacts.length; i++) {
                    var artifact = groupArtifacts[i];
                    processedArtifacts[artifact.name] = artifact;
                    processArtifact(artifact, processData);
                }
                processData.artifactsToList = nodes.artifacts;
                for (i = 0; i < nodes.artifacts.length; i++) {
                    var artifact = nodes.artifacts[i];
                    if (!processedArtifacts[artifact.name]) {
                        processArtifact(processData.artifactsToList[i], processData);
                    }
                }
                break;

            default:
                break;
        }
    }

    function processArtifact(artifact, processData){
        var artifactName = artifact.name;
        if (processData.artifactsDepsFrom.indexOf(artifactName)!=-1){
            artifact.x = 50+processData.depFromIndex*50;
            artifact.y = DEPS_FROM_Y;
            artifact.angle = -45;
            artifact.opacity = 0.3;
            if (artifact.group == processData.selectedArtifactObject.group){
                artifact.opacity = 1;
            }
            artifact.isVisible = true;
            processData.depFromIndex++;
        }
        else if (processData.artifactsDepsTo.indexOf(artifactName)!=-1){
            artifact.x = 50+processData.depToIndex*50;
            artifact.y = DEPS_TO_Y;
            artifact.angle = 45;
            artifact.opacity = 0.3;
            if (artifact.group == processData.selectedArtifactObject.group){
                artifact.opacity = 1;
            }
            artifact.isVisible = true;
            processData.depToIndex++;
        }
        else if (artifactName == selectedArtifact){
            artifact.x = 200;
            artifact.y = SELECTED_DEP_Y;
            artifact.angle = 0;
            artifact.opacity = 1;
            artifact.isVisible = true;
        }
        else{
            artifact.width = 0;
            artifact.height = 0;
            artifact.x = 0;
            artifact.y = -20;
            artifact.opacity = 0;
        }

    }

