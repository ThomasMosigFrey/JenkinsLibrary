package com.jenkins;

import com.jenkins.lib.CallCounter;
import hudson.*;

public class MavenBuild {
    static final def mavenVersion = 'Maven 3.3.9';
    static final def sonarEnv = 'jenkins';

    static def deployToNexus(def steps, def host, def port, def creds) {
        steps.withCredentials([usernamePassword(credentialsId: creds, passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
            steps.sh "mvn deploy -Dmaven.install.skip=true -Dmaven.resources.skip=true -Dmaven.compile.skip=true -Dmaven.testResources.skip=true -Dmaven.testCompile.skip=true -Dmaven.test.skip=true -Ddeploy.jboss.host=" + host + " -Ddeploy.jboss.port=" + port + " -Ddeploy.jboss.user=${USERNAME} -Ddeploy.jboss.password=${PASSWORD}"
        }
    }

    static def callMaven(def steps, def params) {
        //steps.withSonarQubeEnv(sonarEnv) {
            steps.withMaven(maven: mavenVersion) {
                try {
                    steps.sh 'mvn ' + params
                } catch(e) {
                    // react on exc
                    // ..

                    // throw it back to jenkins
                    throw e
                    //throw new Exception("my error msg");
                } finally {
                    // do something in any case
                }
            }
        //}
    }
}