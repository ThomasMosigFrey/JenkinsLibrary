import com.jenkins.*

def call(Closure body) {
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
                        new MavenBuild(this, 'maven3').callMaven("clean install")
                    }
                }
            }

            stage('Deploy') {
                steps {
                    script {
                        new MavenBuild(this, 'maven3').callMaven('install -Dmaven.test.skip=true')
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