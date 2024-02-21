import com.jenkins.*;

def call(Closure body) {
    LinkedHashMap config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    pipeline {

        agent { label 'mac'}

        stages {

            stage ('parallel build/test/push') {
                parallel {
                    stage('MAC Build (Java Threading)') {
                        agent { label 'mac' }
                        tools {
                            maven 'Maven 3.3.9'
                        }
                        steps {
                            dir('threading') {
                                // checkout
                                git branch: 'master', credentialsId: 'd6ae3020-ebc8-4fa7-9071-5f96e10ce3f8', url: 'https://thomasmosigfrey@git.code.sf.net/p/threadingexample/code'

                                // build
                                withMaven( maven: 'Maven 3.3.9') {
                                    sh 'mvn clean install'
                                }

                                // archive artifacts
                                stash allowEmpty: true, includes: 'target/Thr*.jar', name: 'jarFilesThreading'
                            }
                        }
                    }
                    stage('UNIX Build (Kotlin) ') {
                        agent { label 'unix' }
                        tools {
                            maven 'Maven 3.3.9'
                        }
                        steps {
                            dir('kotlin') {
                                // checkout
                                git branch: 'master', credentialsId: 'd6ae3020-ebc8-4fa7-9071-5f96e10ce3f8', url: 'https://thomasmosigfrey@git.code.sf.net/p/the-example-app-kotlin/code'

                                // build
                                withMaven(maven: 'Maven 3.3.9') {
                                    sh 'mvn clean install'
                                }

                                // archive artifacts
                                stash allowEmpty: true, includes: 'target/dagg*.jar', name: 'jarFilesKotlin'
                            }
                        }
                    }
                }
            }
            stage ('SIT Test') {
                agent { label 'test' }
                tools {
                    maven 'Maven 3.3.9'
                }
                steps {
                    // retrieve artifacts
                    unstash 'jarFilesKotlin'
                    unstash 'jarFilesThreading'

                    // use them testwise
                    // save test results
                }
            }
        }
    }
}