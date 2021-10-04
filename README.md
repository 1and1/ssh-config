SSH-Config
===================
[![Java CI with Maven](https://github.com/1and1/ssh-config/actions/workflows/maven.yml/badge.svg)](https://github.com/1and1/ssh-config/actions/workflows/maven.yml)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

Tool for helping automatically maintain SSH client configs.
It can be used to maintain a database of your logins to remote hosts.

SSH-Config has the ability to probe for (new) hosts using the
DNS service and add the hosts to your SSH config.
Out of that it can connect to SSH servers and find out which servers are reachable and which aren't.

## Purpose

The purpose of SSH-Config is to simplify the maintenance of SSH client configurations
containing many hosts. There are Unix shell completion mechanisms that help to
expand host names while typing if you have a well-maintained SSH client configuration.
This is where the tool comes into place.

## Usage

Use it in your command line. There are multiple use-cases implemented:
* **Discover (-d):** Discover new hosts given in the command line using DNS lookups.
* **Update (-u):** Update the known hosts in the database using DNS and tested host reachability. 
* **Export (-e):** Export the database to a file / stdout. Supports filtering by user or group name.
* **Import (-i):** Import the database from a file / stdin. 


Full command line parameters:
```
 -database (-D) FILE : The database to use. (default: /home/$USER/.sshconfig.json
                       )
 -discover (-d)      : Discover hosts given in the command line using DNS.
                       (default: false)
 -export (-e)        : Export the database. Writes to stdout or file argument.
                       User and group parameters can be used for filtering.
                       (default: false)
 -group (-G) GROUP   : The group name to use for the entry for discovery or
                       export filtering.
 -help (-h)          : Show this command line help. (default: true)
 -import (-i)        : Import a database. Reads either from argument or stdin.
                       (default: false)
 -sshcfg (-s) FILE   : The ssh config to update. (default:
                       /home/$USER/.ssh/config)
 -update (-u)        : Update all database hosts IP addresses. (default: false)
 -user (-U) USER     : The user name to use for the entry for discovery or
                       export filtering.
```

### Use case 1: Add new hosts to your `$HOME/.ssh/config`

To add new hosts to your ssh config you typically issue a command like this:

```
$ ssh-config -d vm-alpha vm-beta vm-gamma
$
```

After this there are entries in the database `$HOME/.sshconfig.json`

```
...
   {
      "name" : "vm-alpha",
      "createdAt" : 1504778353193,
      "enabled" : true,
      "fqdn" : "vm-alpha.foo.domain",
      "sshServerVersion" : "SSH-2.0-OpenSSH_6.7p1 Debian-5+deb8u3",
      "id" : "a7369847-e833-49bd-af95-e0d1292b3ed6",
      "updatedAt" : 1520958429041,
      "ips" : [
         "10.123.123.123"
      ]
   },
...
```

and in your $HOME/.ssh/config:

```
...
# <<< BEGIN{a7369847-e833-49bd-af95-e0d1292b3ed6}
Host vm-alpha
	Hostname vm-alpha.foo.domain
	Hostname 10.123.123.123
# >>> END{a7369847-e833-49bd-af95-e0d1292b3ed6}
...
```

### Use case 2: Update hosts to your ~/.ssh/config

To update the existing hosts to your ssh config you typically issue a command like this:

```
$ ssh-config -u
$
```

After this there are updated entries in the database $HOME/.sshconfig.json

```
...
   {
      "name" : "vm-alpha",
      "createdAt" : 1504778353193,
      "enabled" : false,
      "fqdn" : "vm-alpha.foo.domain",
      "sshServerVersion" : "SSH-2.0-OpenSSH_6.7p1 Debian-5+deb8u3",
      "id" : "a7369847-e833-49bd-af95-e0d1292b3ed6",
      "updatedAt" : 1520959605730,
      "ips" : [
         "10.123.123.123"
      ]
   },
...
```

And the host is removed because of no longer reachability in your $HOME/.ssh/config.

## Files

The files involved are:
* `~/.ssh/config`: The SSH config being read to and written to. The SSH-Config specific parts are marked with special magic markers so the config does not get messed up completely.
* `~/.sshconfig.json`: A JSON file that holds the data for all hosts. Is used to track which hosts are reachable or not, because only reachable hosts are takeninto the SSH client config above.

## Installation

There's a Debian and a CentOS package provided for installation.
Please see the [Releases](https://github.com/1and1/ssh-config/releases) section on Github.
Both install a shell wrapper in `/usr/bin/ssh-config`.

## Building

The system requirements for building are
* Oracle JDK 8
* Apache Maven for building

Building can be triggered with the command
```
mvn clean package
```

## License

Copyright 2018 1&1 Internet SE
Copyright 2019 1&1 Ionos SE

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
