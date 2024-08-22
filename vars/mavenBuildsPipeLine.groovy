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

        options {
            copyArtifactPermission('*')
        }

        stages {

            stage('Load and save config') {

                steps {
                    script {
                        def json = libraryResource "config.json"
                        writeFile file: 'config.json', text: json

                        def shellScript = libraryResource("exampleShell.sh")
                        writeFile file: 'exampleShell.sh', text: shellScript
                        sh "chmod +x exampleShell.sh"

                        def binaryFile = libraryResource("specificBinary.bin")
                        writeFile file: 'specificBinary.bin', text: binaryFile
                        sh "chmod +x specificBinary.bin"

                        archiveArtifacts artifacts: 'config.json', allowEmptyArchive: true

                        copyArtifacts filter: 'config.json', fingerprintArtifacts: true, optional: true, projectName: env.JOB_NAME, selector: lastWithArtifacts(), target: 'tmp'
                    }
                }
            }

            stage('Compile/Test/Install') {
                steps {
                    script {
                        lock(label: 'maven', resourceSelectStrategy: 'sequential') {
                            new MavenBuild(this, 'maven3', "ae44f8b3-3bf7-4624-8e87-74659f3f817f").install()
                        }
                    }
                }
            }

            stage('Deploy') {
                input {
                    message 'Soll ich wirklich jboss deployen?'
                    ok 'Ja'
                    submitter 'jenkins'
                }
                steps {
                    script {
                        lock(label: 'maven', resourceSelectStrategy: 'sequential') {
                            new MavenBuild(this, 'maven3', "ae44f8b3-3bf7-4624-8e87-74659f3f817f" ).deploy()
                        }
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