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

                        archiveArtifacts artifacts: 'config.json', allowEmptyArchive: true, fingerprintArtifacts: true
                        copyArtifactPermission('*')
                        copyArtifacts filter: 'config.json', fingerprintArtifacts: true, optional: true, projectName: '/Thomas Frey/JeeExamples/dev', selector: lastWithArtifacts(), target: 'tmp'
                    }
                }
            }

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
                        new MavenBuild(this, 'maven3').callMaven('deploy -Dmaven.test.skip=true')
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