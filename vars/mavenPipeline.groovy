import com.jenkins.*;

def call(body) {
    LinkedHashMap config = [:]
    
    def mvnBuild = new MavenBuild()    
    
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    
        pipeline {
          agent { label 'Demo' }
          stages {
              stage('Compile/Test') {
                  steps {
                      script {
                          echo config.timeout
                          echo config.mavenVersion
                          mvnBuild.cleanInstall(this)
                      }
                  }
              }
              stage('Report') {
                  steps {
                      junit allowEmptyResults: true, testResults: '**/*.xml'
                  }
              }
              stage('Source Code Check') {
                  steps {
                      withMaven(maven: 'Maven 3.8.6') {
                          withSonarQubeEnv(credentialsId: 'e33b1bc1-7530-4989-b282-d348ac73bc27', installationName: 'sonar') { // You can override the credential to be used
                              sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.7.0.1746:sonar'
                          }
                      }
                  }
              }
              stage('Deploy') {
                  steps {
                      withMaven(maven: 'Maven 3.8.6') {
                          echo "deploy"
                      }
                  }
              }
         }
         post {
             always {
                 sh 'printenv'
                 emailext body: '', recipientProviders: [developers()], subject: 'Build executed ', to: 'thomas@mosig-frey.de'
             }
             success {
                  cleanWs()
             }
         }
      }
}
