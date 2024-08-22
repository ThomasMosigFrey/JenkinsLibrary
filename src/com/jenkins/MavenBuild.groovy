package com.jenkins;

import com.jenkins.lib.CallCounter;
import hudson.*;

public class MavenBuild {
    def mavenTool  = 'maven3'
    def settings = ""
    def script

    public MavenBuild(def script, def mavenTool, def settings) {
        this.mavenTool = mavenTool
        this.settings = settings
        this.script = script
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
        script {
            withCredentials([usernamePassword(credentialsId: credId, passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
                callMaven("deploy -Dmaven.test.skip=true -Ddeploy.jboss.host="+serverAddress+ " -Ddeploy.jboss.port=10090 -Ddeploy.jboss.user=${env.USERNAME} -Ddeploy.jboss.password=${env.PASSWORD}")
            }
        }
    }

    def callMaven(def params) {
        this.script.withMaven(globalMavenSettingsConfig: this.settings, maven: this.mavenTool) {
            this.scripts.sh 'mvn ' + params
        }
    }
}