import com.jenkins.*

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
                        MavenBuild.callMaven(this, "clean install")
                    }
                }
            }

            stage('Code Analysis') {
                steps {
                    script {
                        MavenBuild.callMaven(this, "sonar:sonar")
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