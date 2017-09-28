# SSHConfig

Maintenance helper tool for maintaining SSH client configs.

The tool will alter your SSH client config. It will backup the last file, but not all.

## Purpose

The purpose of SSHConfig is to simplify the maintenance of SSH client configurations
with many hosts. There are Unix shell completion mechanisms that help to
expand host names while typing if you have a well-maintained SSH client configuration.
This is where the tool comes into place.

## Usage

Use it in your command line. There are multiple use-cases implemented:
* Discover (-d): Discover new hosts given in the command line using DNS lookups.
* Update (-u): Update the known hosts in the database using DNS and tested host reachability. 

## Files

The files involved are:
* `~/.ssh/config`: The SSH config being read to and written to.
* `~/.sshconfig.json`: A JSON file that holds the data for all hosts. Is used to track which hosts are reachable or not, because only reachable hosts are takeninto the SSH client config above.

## License

Copyright 2017 1&1 Internet SE

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
