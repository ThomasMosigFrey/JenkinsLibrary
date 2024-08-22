import com.jenkins.*;

def call(Closure body) {
    LinkedHashMap config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    if(!config.nexusHost) {
        config.nexusHost = "10.20.60.59"
    }

    if(!config.maven) {
        config.maven = "maven3"
    }

    if(env.BRANCH_NAME == "dev") {
        config.jbossHost = "10.10.60.59"
    } else if (env.BRANCH_NAME == "master") {
        config.jbossHost = "10.10.60.58"
    } else {
        config.jbossHost = "10.10.60.59"
    }

    pipeline {
        agent {
            label "${config.label}"
        }
        environment {
            NEXUS_HOST = "${config.nexusHost}"
        }
        options {
          buildDiscarder logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '5', daysToKeepStr: '', numToKeepStr: '5')
          disableConcurrentBuilds abortPrevious: true
          retry(conditions: [agent()], count: 3)
          timeout(time: 1, unit: 'HOURS')
        }

        stages {
            stage ('compile/test') {
                steps {
                    script {
                        new MavenBuild(this, 'maven3', "ae44f8b3-3bf7-4624-8e87-74659f3f817f").compile()
                    }
                }
            }
            stage('deploy to nexus') {
                steps {
                    script {
                        new MavenBuild(this, 'maven3', "ae44f8b3-3bf7-4624-8e87-74659f3f817f").install(config.nexusHost)
                    }
                }
            }
            stage('deploy to jboss') {
                steps {
                    script {
                        new MavenBuild(this, 'maven3', "ae44f8b3-3bf7-4624-8e87-74659f3f817f").deploy(config.jbossHost, '1cbbdb5b-fc28-4cd0-8e7b-698a55743423')
                    }
                }
            }
        }
        post {
            always {
                emailext attachLog: true, body: '', subject: 'Build ${env.JOB_NAME} endet with status ${env.JOB_STATUS}', to: 'thomas@mosig-frey.de'
            }
        }
    }
}
