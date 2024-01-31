package com.jenkins;

import com.jenkins.lib.CallCounter;
import hudson.*;

public class MavenDeployments {
    private script

    public MavenDeployments(def script) {
        this.script = script
    }

    def deployToJBoss(def host, def port, def creds) {
        script.withCredentials([script.usernamePassword(credentialsId: creds, passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
            script.sh "mvn deploy -Dmaven.install.skip=true -Dmaven.resources.skip=true -Dmaven.compile.skip=true -Dmaven.testResources.skip=true -Dmaven.testCompile.skip=true -Dmaven.test.skip=true -Ddeploy.jboss.host=" + host + " -Ddeploy.jboss.port=" + port + " -Ddeploy.jboss.user=$USERNAME -Ddeploy.jboss.password=$PASSWORD"
        }
    }
}