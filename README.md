train-my-ear
============

Ear trainer software for beginner guitarists

See the [official site](http://chords.btanase.ro) for screenshots, installer download and documentation.

## Build instructions

1) Clone the repository:

    git clone git://github.com/bftanase/train-my-ear.git

2) Cd to the cloned directory and use maven to build a jar executable

    mvn package -DpresetData=empty_db

The `empty_db` variable is the directory where the default configuration and media files are stored, 
relative to project root. 

On the first run this directory will be copied to your users directory.

This repository doesn't contain any media samples and the DB is empty. Check the website for documentation on 
how to import sound samples and create lessons & exercises.

3) Execute 

    cd target
    java -jar train-my-ear-<version>.jar
