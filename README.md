# BotoSeis

BotoSeis is an open-source seismic data processing software developed to run in a simple and flexible interactive environment the Seismic Unix programs.

BotoSeis is an interactive user-friendly interface to create and manage projects, lines and flowcharts for seismic data processing. It is also possible to add new SU programs or to edit any already included programs in an interactive mode, without knowledge of  any programming language. BotoSeis project, created by German Garabito, was started at UFPA-Belém and is currently updated at UFRN-Natal. The main contributors to this project are Williams Lima and Gabriel Almeida.

The BotoSeis project also aims to develop interactive tools for data visualization, velocity analysis, f-k filtering, muting and others. Currently, two programs have already been developed, the botoView for data visualization and botoVelan for velocity analysis.
Create and manage projects, lines, and flows:

- Create and save a project
- Load projects
- Create and load several lines for a project
- Create and load several workflows for a line
- Rename a project, line and workflow
- Delete projects, lines and flows

Handling of processing flows:
- Copy a flow from one line to another
- Move up and move down a process
- Insert a process in any place
- Comment a process
- Deleting a process

Information about running flows:
- Log file
- Stop job execution
- Status of a job
- Start and end time

Interactive adding new programs and editing
- Addition of a new SU program
- Editing parameters of an added program
- Addition of any proprietary program

## Installation

### Prerequisites
- An Unix based system: Linux, macOS.
- [Java SE Runtime Environment 6 or later](https://www.oracle.com/java/technologies/javase-downloads.html)
- [Seismic Unix](https://github.com/JohnWStockwellJr/SeisUnix/wiki#installation-notes)

### Installing prerequisites

#### Java

Run this command on the terminal to install Java using APT
```
sudo apt install -y default-jdk
```

#### Seismic Unix

Run this command
```
bash -c "$(wget -qO- https://git.io/JJcRJ)"
```
What this does is download and run our [Seismic Unix installation script](https://gist.github.com/botoseis/7230737e34fb5306039ad13dd833bf3f)

### Option 1 - Install binaries only (recommended for most users)

Run this command
```
bash -c "$(wget -qO- https://git.io/JJlgk)"
```
What this does is download and run our [BotoSeis installation script](https://gist.github.com/botoseis/fe86c3c13f65e3d43b11e4fa9560ce30), which clones the [distribution repository](https://github.com/botoseis/botoseis-bin).

### Option 2 - Download and compile source code

Additional prequisites:
- [NetBeans](https://netbeans.apache.org/download/index.html)

Clone this repository as `BotoseisProject`
```
git clone https://github.com/botoseis/BotoSeis.git 'BotoseisProject'
```

Set up persistent enviroment variables. This is mandatory since the program uses the `BOTOSEIS_ROOT` enviroment variable.
```
echo "export BOTOSEIS_ROOT='path/to/BotoseisProject/Botoseis/scripts'" >>~/.bashrc
echo 'export PATH="${PATH}:${BOTOSEIS_ROOT}/bin"' >>~/.bashrc
```

Update your shell session
```
source ~/.bashrc
```

Build: open the Botoseis project with NetBeans > Clean and Build

You can also run the software inside NetBeans or by using the following command, which was set in your `PATH`
```
runboto.sh
```

## License

This softare is under the [GNU General Public License v3.0](LICENSE).
