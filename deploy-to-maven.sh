#!/bin/bash

export GPG_TTY=$(tty) && mvn -Drelease clean deploy
