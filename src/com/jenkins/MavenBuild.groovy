package com.jenkins.lib;

import com.jenkins.lib.CallCounter;
import hudson.*;

public class MavenBuild {
    static final def mavenVersion = 'Maven 3.3.9';
    static final def sonarEnv = 'jenkins';

    static def callMaven(def steps, def params) {
        steps.withSonarQubeEnv(sonarEnv) {
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
        }
    }
}