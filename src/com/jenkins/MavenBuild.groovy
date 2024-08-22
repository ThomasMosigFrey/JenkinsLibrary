package com.jenkins;

import com.jenkins.lib.CallCounter;
import hudson.*;

public class MavenBuild {
    def mavenTool  = 'maven3'
    def settings = ""
    def scripts

    public MavenBuild(def scripts, def mavenTool, def settings) {
        this.mavenTool = mavenTool
        this.settings = settings
        this.scripts = scripts
    }

    def compile() {
        callMaven("clean compile")
    }

    def packageArtifact() {
        callMaven("package -Dmaven.test.skip=true")
    }

    def install(def repositoryAddress) {
        callMaven( "install -Dmaven.test.skip=true")
    }

    def deploy(def serverAddress, def credId) {
        this.scripts.withCredentials([this.scripts.usernamePassword(credentialsId: credId, passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
            callMaven("deploy -Dmaven.test.skip=true -Ddeploy.jboss.host="+serverAddress+ " -Ddeploy.jboss.port=10090 -Ddeploy.jboss.user=${USERNAME} -Ddeploy.jboss.password=${PASSWORD}")
        }
    }

    def callMaven(def params) {
        this.scripts.withMaven(globalMavenSettingsConfig: this.settings, maven: this.mavenTool) {
            this.scripts.sh 'mvn ' + params
        }
    }
}