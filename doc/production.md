# Installation on Amazon Web Services EC2 machine

1. Install [MongoDB] (http://docs.mongodb.org/manual/tutorial/install-mongodb-on-ubuntu/)
    Follow instructions, but at the moment of writing this document there was no release for Vivid version of
    Ubuntu so I had to execute this:
      
    `echo "deb http://repo.mongodb.org/apt/debian wheezy/mongodb-org/3.0 main" | sudo tee /etc/apt/sources.list.d/mongodb-org-3.0.list`
    
2. Go to `~/workspace/hrstka.sk` and get the latest version from GitHub.
3. At the moment I had to install my special fork of ReactiveMongo (for Play Framework 2.4.0)
     - [Install SBT](http://www.scala-sbt.org/0.13/tutorial/Installing-sbt-on-Linux.html)
     - [Clone my fork](https://github.com/RadoBuransky/Play-ReactiveMongo)
     - Switch to `play240` branch.
     - `sbt publishLocal`
4. [Redirect port 80 to 9000] (http://serverfault.com/a/112798)