# SSHConfig

Tool for helping maintain SSH client configs.

SSHConfig has the ability to probe for (new) hosts using the
DNS service and add the hosts to your SSH config.
Out of that it can connect to SSH servers and find out which servers are reachable and which aren't.

It will backup the last file, but not all.

## Purpose

The purpose of SSHConfig is to simplify the maintenance of SSH client configurations
with many hosts. There are Unix shell completion mechanisms that help to
expand host names while typing if you have a well-maintained SSH client configuration.
This is where the tool comes into place.

## Usage

Use it in your command line. There are multiple use-cases implemented:
* **Discover (-d):** Discover new hosts given in the command line using DNS lookups.
* **Update (-u):** Update the known hosts in the database using DNS and tested host reachability. 

Full command line parameters:
```
 -database (-D) FILE : The database to use. (Default: /home/$LOGINUSER/.sshconfig.j
                       son)
 -discover (-d)      : Discover hosts given in the command line using DNS.
                       (Default: false)
 -help (-h)          : Show this command line help. (Default: true)
 -set-user (-Z)      : Set the user name. (Default: false)
 -sshcfg (-s) FILE   : The ssh config to update. (Default:
                       /home/$LOGINUSER/.ssh/config)
 -update (-u)        : Update all hosts IP addresses. (Default: false)
 -user (-U) USER     : The user name to use. (Default: $LOGINUSER)
```

## Files

The files involved are:
* `~/.ssh/config`: The SSH config being read to and written to.
* `~/.sshconfig.json`: A JSON file that holds the data for all hosts. Is used to track which hosts are reachable or not, because only reachable hosts are takeninto the SSH client config above.

## Installation

There's a Debian package provided for installation.
It installs to `/opt/sshconfig`, the executable is 
`/opt/sshconfig/sshconfig`.

## Building

The system requirements for building are
* Oracle JDK 8
* Apache Maven for building
* Apache Ant for packaging

Building can be triggered with the command
```
mvn clean package
```

Packaging can be triggered with the command
```
ant
```

Packaging depends heavily on the packages installed on your system, please
see [here](https://docs.oracle.com/javase/8/docs/technotes/guides/deploy/self-contained-packaging.html)
for the requirements.

## License

Copyright 2017 1&1 Internet SE

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
