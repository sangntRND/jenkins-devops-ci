			_____ ____  _____     ____   __
			|_   _|  _ \|_ _\ \   / /\ \ / /
			| | | |_) || | \ \ / /  \ V / 
			| | |  _ < | |  \ V /    | |  
			|_| |_| \_\___|  \_/     |_|  


By default, vulnerability and secret scanning are enabled

| Left-Aligned  | Center Aligned  | Right Aligned |
| :------------ |:---------------:| -----:|
| col 3 is      | some wordy text | $1600 |
| col 2 is      | centered        |   $12 |
| zebra stripes | are neat        |    $1 |


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