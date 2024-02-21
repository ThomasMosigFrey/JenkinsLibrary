import com.jenkins.*;

def call(Closure body) {
    LinkedHashMap config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    pipeline {
        agent {
            label 'linux'
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
                        withCredentials([usernamePassword(credentialsId: 'bde764f8-d98e-411f-9a24-900dccac6d04', passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
                            sh "mvn deploy -DskipTests -Ddeploy.jboss.host=10.10.60.63 -Ddeploy.jboss.port=10090 -Ddeploy.jboss.user=${USERNAME} -Ddeploy.jboss.password=${PASSWORD}"
                        }
                    }
                }
            }
            stage('deploy to jboss to dev test system') {
                when { branch 'dev'}
                steps {
                    withMaven(globalMavenSettingsConfig: 'ae44f8b3-3bf7-4624-8e87-74659f3f817f', maven: 'maven3', mavenSettingsConfig: '', traceability: true) {
                        withCredentials([usernamePassword(credentialsId: '24dfea8d-7135-4d8b-908f-c0f74088e116', passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
                            sh "mvn deploy -DskipTests -Ddeploy.jboss.host=10.10.60.63 -Ddeploy.jboss.port=10090 -Ddeploy.jboss.user=${USERNAME} -Ddeploy.jboss.password=${PASSWORD}"
                        }
                    }
                }
            }
        }
    }
}