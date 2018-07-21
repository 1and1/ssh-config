SSH-Config
===================
![Travis CI Status](https://travis-ci.org/1and1/ssh-config.svg?branch=master)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/e2354f91e2ab4fb48f6e460a0ab1ad99)](https://www.codacy.com/app/sfuhrm/ssh-config?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=1and1/ssh-config&amp;utm_campaign=Badge_Grade)

Tool for helping maintain SSH client configs.

SSH-Config has the ability to probe for (new) hosts using the
DNS service and add the hosts to your SSH config.
Out of that it can connect to SSH servers and find out which servers are reachable and which aren't.

It will backup the last file, but not all.

## Purpose

The purpose of SSH-Config is to simplify the maintenance of SSH client configurations
with many hosts. There are Unix shell completion mechanisms that help to
expand host names while typing if you have a well-maintained SSH client configuration.
This is where the tool comes into place.

## Usage

Use it in your command line. There are multiple use-cases implemented:
* **Discover (-d):** Discover new hosts given in the command line using DNS lookups.
* **Update (-u):** Update the known hosts in the database using DNS and tested host reachability. 

Full command line parameters:
```
 -database (-D) FILE : The database to use. (Vorgabe: /home/$LOGINUSER/.sshconfig.json
                       )
 -discover (-d)      : Discover hosts given in the command line using DNS.
                       (Default: false)
 -help (-h)          : Show this command line help. (Default: true)
 -sshcfg (-s) FILE   : The ssh config to update. (Default:
                       /home/$LOGINUSER/.ssh/config)
 -update (-u)        : Update all hosts IP addresses. (Vorgabe: false)
 -user (-U) USER     : The user name to use for the entry for discovery.
                       (Default: fury)
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

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
