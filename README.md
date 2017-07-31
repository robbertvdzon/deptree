
Step 1: build de deps:
mvn com.vdzon.maven.plugin:deptree:build-dep-tree

Step 2: Manual update the group.yml:
Place the modules in the correct groups

Step 3: generate the web code and start the page in a browser:
mvn com.vdzon.maven.plugin:deptree:start
  

   