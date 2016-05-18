#!/bin/bash

mvn clean install

java -cp target/*:target/dependency/* com.tascape.qa.th.webui.tools.WebSUiViewer
