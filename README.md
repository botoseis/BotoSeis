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

Download and run our [Seismic Unix installation script](https://gist.github.com/botoseis/4ca681cb9ef706c1b8410153c6199cf2). Only tested on Ubuntu 22.04 LTS, but should work on any modern Ubuntu-based distro.
```
bash -c "$(wget -qO- https://gist.githubusercontent.com/botoseis/4ca681cb9ef706c1b8410153c6199cf2/raw/b16ad754839d1ca3657d8e8396db14f5ba791e6b/install_seismic_unix.sh)"
```
This script automates the steps shown on the [official installation guide](https://wiki.seismic-unix.org/sudoc:su_installation).

### Option 1 - Install binaries only (recommended for most users)

Run this command
```
bash -c "$(wget -qO- https://git.io/JJlgk)"
```
What this does is download and run our [BotoSeis installation script](https://gist.github.com/botoseis/fe86c3c13f65e3d43b11e4fa9560ce30), which clones the [distribution repository](https://github.com/botoseis/botoseis-bin).

### Option 2 - Download and compile source code

Additional prequisites:
- [NetBeans](https://netbeans.apache.org/download/index.html)

#### 1. Clone this repository as `BotoseisProject`
```
git clone https://github.com/botoseis/BotoSeis.git BotoseisProject
```

#### 2. Set up persistent enviroment variables. 
This is mandatory since the program requires the `BOTOSEIS_ROOT` enviroment variable to run properly.

You can use these commands to add lines to the end of the `~/.bashrc` file, if you use bash. Remember to substitute `path/to/BotoseisProject` with the location where you cloned this repository.
```
# Example: setting persistent enviroment variables using ~/.bashrc file

echo "export BOTOSEIS_ROOT='path/to/BotoseisProject/Botoseis/scripts'" >>~/.bashrc
echo 'export PATH="${PATH}:${BOTOSEIS_ROOT}/bin"' >>~/.bashrc
```

#### 3. Update your shell session
```
source ~/.bashrc
```

#### 4. Build the project

Open the Botoseis project with NetBeans > Clean and Build

### 5. How to run?

After building it, you can run the project inside NetBeans using the "Run Project" button, or you can run it in the terminal by using the following command, which should already be available from your `PATH` if you completed steps 2 and 3 correctly.
```
runboto.sh
```
You can find a BotoSeis user guide with seismic data processing examples at: https://germangarabito.wordpress.com/

## License

This softare is under the [GNU General Public License v3.0](LICENSE).
