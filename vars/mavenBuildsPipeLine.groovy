import com.jenkins.lib.*

def call(body) {
    LinkedHashMap config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    pipeline {

        triggers {
            cron(config.cron)
        }

        agent { label config.label }

        stages {


            stage('Compile/Test/Install') {
                steps {
                    script {
                        com.jenkins.lib.MavenBuild.callMaven("clean install")
                    }
                }
            }

            stage('Code Analysis') {
                steps {
                    script {
                        com.jenkins.lib.MavenBuild.callMaven("sonar:sonar")
                    }
                }
            }

            stage('Deploy') {
                steps {
                    script {
                        com.jenkins.lib.MavenBuild.callMaven(this, 'deploy -Dmaven.test.skip=true')
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