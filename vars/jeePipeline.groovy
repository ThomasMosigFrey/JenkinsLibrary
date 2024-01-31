import com.jenkins.*

def call(body) {
    def mvnBuild = new MavenBuild()
    LinkedHashMap config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    pipeline {
        agent {
            label config.agentLabels
        }
        options {
            buildDiscarder logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '3', daysToKeepStr: '', numToKeepStr: '3')
            timeout(time: config.timeout, unit: 'MINUTES')
        }
        stages {
            stage ('compile/test') {
                steps {
                    withMaven(globalMavenSettingsConfig: 'ae44f8b3-3bf7-4624-8e87-74659f3f817f', maven: 'maven3', mavenSettingsConfig: '', traceability: true) {
                        sh "mvn clean package"
                    }
                }
            }
            stage('deploy to nexus') {
                steps {
                    withMaven(globalMavenSettingsConfig: 'ae44f8b3-3bf7-4624-8e87-74659f3f817f', maven: 'maven3', mavenSettingsConfig: '', traceability: true) {
                        sh "mvn install -DskipTests"
                    }
                }
            }
            stage('deploy to jboss to uat test system') {
                when { branch 'master'}
                steps {
                    withMaven(globalMavenSettingsConfig: 'ae44f8b3-3bf7-4624-8e87-74659f3f817f', maven: 'maven3', mavenSettingsConfig: '', traceability: true) {
                        script {
                            mvnBuild.deployToJBoss(steps, config.uatHost, config.uatPort, config.uatCreds)
                        }
                    }
                }
            }
            stage('deploy to jboss to dev test system') {
                when { branch 'dev'}
                steps {
                    withMaven(globalMavenSettingsConfig: 'ae44f8b3-3bf7-4624-8e87-74659f3f817f', maven: 'maven3', mavenSettingsConfig: '', traceability: true) {
                        script {
                            mvnBuild.deployToJBoss(steps, config.devHost, config.devPort, config.devCreds)
                        }
                    }
                }
            }
        }
    }
}