import com.jenkins.*;
import com.jenkins.lib.*;


def call(body) {
    LinkedHashMap config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    lock("eval1") {

        pipeline {

            triggers {
                cron(config.cron)
            }

            agent { label "unix"}

            environment {
                MYKEY = "value"
            }

            stages {

                stage('Print build Variables') {
                    steps {
                        echo currentBuild.buildVariables.MYKEY
                    }
                }

                stage('Print previous build Variables') {
                    when {
                        not {
                            equals expected: null, actual: currentBuild.previousBuild
                        }
                    }
                    steps {
                        echo currentBuild.previousBuild.buildVariables.MYKEY
                    }
                }

                stage('Compile/Test/Install') {
                    steps {
                        script {
                            MavenBuild.callMaven("clean install")
                        }
                    }
                }

                stage('Code Analysis') {
                    steps {
                        script {
                            MavenBuild.callMaven("sonar:sonar")
                        }
                    }
                }

                stage('Deploy') {
                    steps {
                        script {
                            MavenBuild.callMaven(this, 'deploy -Dmaven.test.skip=true')
                        }
                    }
                }

                stage('Archive artifacts') {
                    steps {
                        archiveArtifacts artifacts: '**/*.jar', allowEmptyArchive: true
                        archiveArtifacts artifacts: 'target/surefire-reports/*.xml', allowEmptyArchive: true
                    }
                }
            }
        }
    }
}