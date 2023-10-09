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

**By Status**

_**Only supported with Vulnerability Scanner**_

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

	--ignore-status
 	   --ignore-unfixed
     
	- Ex:
		trivy image --ignore-status affected,fixed ruby:2.4.0
  		trivy image --ignore-unfixed ruby:2.4.0

**By Severity**

_**Supported for all scanners: secret, vuln, config, license**_

Use --severity option.

	trivy image --severity HIGH,CRITICAL ruby:2.4.0
	trivy conf --severity HIGH,CRITICAL examples/misconf/mixed

**By Finding IDs** (allow the CVE risk or critical or high)

Trivy supports the ```.trivyignore``` and ```.trivyignore.yaml``` ignore files.

_**.trivyignore**_

```
# Accept the risk
CVE-2018-14618

# Accept the risk until 2023-01-01
CVE-2019-14697 exp:2023-01-01

# No impact in our settings
CVE-2019-1543

# Ignore misconfigurations
AVD-DS-0002

# Ignore secrets
generic-unwanted-rule
aws-account-id
```

_**.trivyignore.yaml**_

```
vulnerabilities:
  - id: CVE-2022-40897
    paths:
      - "usr/local/lib/python3.9/site-packages/setuptools-58.1.0.dist-info/METADATA"
    statement: Accept the risk
  - id: CVE-2023-2650
  - id: CVE-2023-3446
  - id: CVE-2023-3817
  - id: CVE-2023-29491
    expired_at: 2023-09-01

misconfigurations:
  - id: AVD-DS-0001
  - id: AVD-DS-0002
    paths:
      - "docs/Dockerfile"
    statement: The image needs root privileges

secrets:
  - id: aws-access-key-id
  - id: aws-secret-access-key
    paths:
      - "foo/bar/aws.secret"

licenses:
  - id: GPL-3.0 # License name is used as ID
    paths:
      - "usr/share/gcc/python/libstdcxx/v6/__init__.py"
```
