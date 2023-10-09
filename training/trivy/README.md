			_____ ____  _____     ____   __
			|_   _|  _ \|_ _\ \   / /\ \ / /
			| | | |_) || | \ \ / /  \ V / 
			| | |  _ < | |  \ V /    | |  
			|_| |_| \_\___|  \_/     |_|  


By default, vulnerability and secret scanning are enabled

# General usage

| Target  | Command  | Scanner |
| :------------ |:---------------| :-----|
| File System     | trivy fs | trivy fs . --scanners secret,vuln,config,license |
| Code Repository     | trivy repo        |   trivy repo --scanners secret,vuln,config,license https://github.com/LocTaRND/jenkins-devops-ci |
| Container Image | trivy image        |    trivy image --scanners secret,vuln,config,license ubuntu:22.04 |

# Filtering

**Only supported with Vulnerability Scanner**

Trivy supports the following vulnerability statuses:
- unknown
- not_affected
- affected
- fixed
- under_investigation
- will_not_fix
- fix_deferred
- end_of_life

To ignore vulnerabilities with specific statuses, use the

	```--ignore-status
 	   --ignore-unfixed```
     
	- Ex:
		```trivy image --ignore-status affected,fixed ruby:2.4.0```
