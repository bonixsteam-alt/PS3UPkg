# PS3UPkg
PS3UPkg is a project made out of boredom and every programmer's sheer need to automate something in 6 hours instead of doing it manually in 5 minutes.

The main target of this app is to save you some time when you want to update a game on RPCS3.


## Usage
The syntax is simple:  
`ps3upkg GAME_ID [DOWNLOAD_DIR]`  

*_GAME_ID is mandatory, DOWNLOAD_DIR is optional_
# Building
## Windows
### Dependencies:
* Launch4j
* Maven
* JDK 8

### Steps
1. Run `build.bat` from `/build-files/win/`
2. Import ps3upkg.xml from `/build-files/win/` to Launch4j.
3. Start building

## Linux/Mac
### Dependencies
* Maven
* JDK 8

### Steps
1. Run build.sh from `/build-files/linux-mac/` in the repo
2. Wait for it to build (it will build to repo root). 

# GUI?
**_Soon:tm:_**

## Other planned features:
* Local database with search function
* GUI &copy;

# Licensing
None. Forks and custom versions are more than welcome.
