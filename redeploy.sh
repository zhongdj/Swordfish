#!/bin/bash

mvn clean install

cd Business/Archive

mvn glassfish:redeploy

cd ../../
