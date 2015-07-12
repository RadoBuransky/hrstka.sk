# Installation on Amazon Web Services EC2 machine

- Install [MongoDB] (http://docs.mongodb.org/manual/tutorial/install-mongodb-on-ubuntu/)
    Follow instructions, but at the moment of writing this document there was no release for Vivid version of
    Ubuntu so I had to execute this:
      
    `echo "deb http://repo.mongodb.org/apt/debian wheezy/mongodb-org/3.0 main" | sudo tee /etc/apt/sources.list.d/mongodb-org-3.0.list`    
- Go to `~/workspace/hrstka.sk` and get the latest version from GitHub.
- At the moment I had to install my special fork of ReactiveMongo (for Play Framework 2.4.0)
     - [Install SBT](http://www.scala-sbt.org/0.13/tutorial/Installing-sbt-on-Linux.html)
     - [Clone my fork](https://github.com/RadoBuransky/Play-ReactiveMongo)
     - Switch to `play240` branch.
     - `sbt publishLocal`
- Do this only in case you don't have NGINX: [Redirect port 80 to 9000] (http://serverfault.com/a/112798)
- Upload `he.png` to S3.
  - Create bucket `hrstka.sk`
  - Create folder `assets`
  - Upload `he.png` to it and make it public
  - Set storage class to reduced redundancy for both image and the folder
- `mongo hrstka` and run secret stuff to initialize the admin user in Mongo
- `vim ~/.bashrc` and add `HRSTKA_SECRET=...`, reconnect ssh to reload the value 
- `cd ~/workspace/hrstka.sk`
- `./activator stopProd` if the application is already running
- `./activator stage`
- `nohup ./target/universal/stage/bin/website &` (hit enter)

## Install NGINX with PageSpeed

- [https://developers.google.com/speed/pagespeed/module/build_ngx_pagespeed_from_source]()
- `sudo vim /usr/local/nginx/conf/nginx.conf`
- enter configuration from `scripts/nginx.conf`
- `sudo mkdir /var/ngx_pagespeed_cache`
- `sudo chown ubuntu:ubuntu /var/ngx_pagespeed_cache`
- `sudo service nginx restart`

## Set environment variables

- Copypaste contents of `scripts/hrstka-env` to `~/.bashrc` and set the values properly.

## MongoDB backup

- If you have set environment variables, then the only thing you have to do is to copy `scripts/cron.daily/hrstka-backup.sh`
to `/etc/cron.daily`.

## MongoDB restore