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
        scripts.withMaven(globalMavenSettingsConfig: 'ae44f8b3-3bf7-4624-8e87-74659f3f817f', maven: mavenVersion) {
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