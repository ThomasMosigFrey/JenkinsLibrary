package com.jenkins;

import com.jenkins.lib.CallCounter;
import hudson.*;

public class MavenBuild {
    def mavenVersion  = 'maven3'
    def scripts

    public MavenBuild(def scripts, String mavenVersion) {
        this.mavenVersion = mavenVersion
        this.scripts = scripts
    }

    def callMaven(def params) {
        scripts.withMaven(maven: mavenVersion) {
            try {
                scripts.sh 'mvn ' + params
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
    }
}