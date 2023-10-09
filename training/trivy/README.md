			_____ ____  _____     ____   __
			|_   _|  _ \|_ _\ \   / /\ \ / /
			| | | |_) || | \ \ / /  \ V / 
			| | |  _ < | |  \ V /    | |  
			|_| |_| \_\___|  \_/     |_|  


By default, vulnerability and secret scanning are enabled

| Left-Aligned  | Center Aligned  | Right Aligned |
| :------------ |:---------------:| -----:|
| File System     | trivy fs | $1600 |
| Code Repository     | trivy repo        |   $12 |
| Container Image | trivy image        |    $1 |


# File System
- Secrets
	trivy fs . --scanners secret

- Vulnerabilitys
	trivy fs . --scanners vuln

- Misconfigurations
	trivy fs . --scanners config

- Licenses
	trivy fs . --scanners license

- Multiple scanners into one line
	trivy fs . --scanners secret,vuln,config,license

# Code Repository



# Filtering
	trivy fs . --scanners vuln --severity HIGH,CRITICAL
