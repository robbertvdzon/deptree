
Step 1: build de deps:
mvn com.vdzon.maven.plugin:deptree:build-dep-tree

Step 2: Manual update the module.yml:
Place the artifacts in the correct module

Step 3: generate the web code and start the page in a browser:
mvn com.vdzon.maven.plugin:deptree:start
  

   