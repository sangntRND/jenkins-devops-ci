      ___     ___   __   __   ___      ___    ___   
     |   \   | __|  \ \ / /  / _ \    | _ \  / __|  
     | |) |  | _|    \ V /  | (_) |   |  _/  \__ \  
     |___/   |___|   _\_/_   \___/   _|_|_   |___/  

# DevOps Project
This is the devops project and will contain all automation related to CI Architecture. 


Folder Structure
 - resources - Jenkins Library Resources
 - vars - Jenkins Libarary Scripts
 - training - Include a guide to implementing ci flow with Groovy script.

Loading resources:
External libraries may load adjunct files from a resources/ directory using the libraryResource step. The argument is a relative pathname, akin to Java resource loading:

def request = libraryResource 'com/mycorp/pipeline/somelib/request.json'


Only entire pipelines can be defined in shared libraries as of this time. This can only be done in vars/*.groovy, and only in a call method. Only one Declarative Pipeline can be executed in a single build, and if you attempt to execute a second one, your build will fail as a result.

https://www.jenkins.io/doc/book/pipeline/shared-libraries/