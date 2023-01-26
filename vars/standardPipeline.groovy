import com.jenkins.*;

def call(body) {
    LinkedHashMap config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    pipeline {
        agent { node { label 'demo' } }

        triggers { pollSCM('H * * * *') }

        stages {
            stage('Compile/Test') {
                steps {
                    withMaven(maven: 'Maven 3.3.9') {
                        echo "mvn clean install"
                    }
                    archiveArtifacts allowEmptyArchive: true, artifacts: 'target/*.jar', followSymlinks: false, onlyIfSuccessful: true
                }
            }
        }
        post {
            success {
                // One or more steps need to be included within each condition's block.
                echo "Yeah!!"
            }
            unsuccessful {
                // One or more steps need to be included within each condition's block.
                echo "email ..."
            }
        }
    }
}