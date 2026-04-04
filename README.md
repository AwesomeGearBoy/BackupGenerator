# Minecraft Java Server Backup Generator
## What It Is
I created this program for recreational purposes. Its intended use is to be put into the folder of your Minecraft server and ran in order to create backups automatically without having to manually copy the world and rename it and such.

As I was managing my own Minecraft Server I ran into this issue. There was no way for Minecraft to automattically back up your world and none of the plugins I found were exactly what I was looking for. So, I created my own sort of "plugin" that I will now use (and share with you if you wish to use it) that allows you to create backups of your world simply by running the program.

## How To Run
### Important Caveats
I programmed this console application in Java 17 because I figured that it was the most compatable with those who are already running a Minecraft Java Edition server. It is worth mentioning that I have no idea if this program works in earlier versions of the OpenJDK, and although it should be compatible with later versions of the OpenJDK as Java gets more updates this program may become incompatible without an update. Now, with that said, I am fairly certain that this program should be fine to run with any Java Edition Server. As for Bedrock Edition servers, I have never ran one myself so I do not know if the folder naming conventions are the same, and this program relies heavily on your folders being named in a particular way.

### Requirements
 - OpenJDK 17 (untested in earlier and later versions; you should already have Java installed if you've ran your server before)

### Running the tool
Ok, with all of the boring stuff out of the way, we can now run the tool I have created. Here is a step-by-step guide on how to run the program:

1. In your filesystem, move the ```Backupgenerator.java``` file packaged in this folder to the folder you use for your server. It should be in the root of that folder, A.K.A. the same folder that your ```server.jar``` file is in. The program relies on being in this folder, so make sure it is in the right place.

2. Now that the ```BackupGenerator.java``` file is in the right place, it is worth mentioning that this application came without being compiled. You will have to compile this program manually. In order to do so, you must navigate to your server's folder in your file system (you should already know how to do this if you have ran your server before). Use the commands ```ls``` and ```cd``` in order to change your directory to inside of your server's folder. Now that you are here, run the command ```javac BackupGenerator.java``` in order to compile the software manually. You do not need to do this step again if you have already completed it once before. Though, if you update Java I recommend doing this again. You are now ready for the next step.

3. Now that the program is compiled, all you need to do is run it. However, it runs a little differently than the ```server.jar``` file that your server runs from. In your file system, you will now see ```.class``` files inside of your server's folder. These are the compiled files that you will be running. In fact, you don't even need the ```BackupGenerator.java``` file anymore unless you plan on recompiling the code in the future. All you have to do now (assuming you are still in the server folder directory) is run the command ```java BackupGenerator``` and the program will run.

## What It Does
This program was built for a very plug-and-play experience. Here is exactly what it does step-by-step:

1. Upon running, the program will start by doing a small series of checks. It will first check if you have a world. It does this by looking for a ```world``` folder; that is, a folder named specifically "world." If it finds one, it will continue with the checks. If it cannot find one, then the program will exit with an error. It will tell you that you must create a world first. 

2. Once it does find a ```world``` folder, it will then go ahead and check to see if you have a ```backups``` folder already. Don't worry if you do not have one yet, if the program does not find a ```backups``` folder it will create one itself. All of your backups from now on will go into the ```backups``` folder.

3. Once checks are complete, it will then simply calculate the size of your world and then copy it into the ```backups``` folder. This may take a minute depending on the size of your world. The backup copy in ```backups``` will always be named according to the time and date that you made the backup. It is always in this format: ```backup_YYYY-MM-DD_hh-mm-ss```*. This ensures that the most recent backup is always at the bottom of your list of backups (if your folders are sorted alphabetically).

*```Y``` is the year, ```M``` is the month, ```D``` is the day, ```h``` is the hour, ```m``` is the minute, and ```s``` is the second

*If you somehow manage to run this twice in the same second, one of the times you run it will overwrite the other and basically replace the existing backup. But.. who would actually take the time to run this twice in one second?