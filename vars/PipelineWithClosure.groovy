import com.jenkins.*

def call(Closure body) {
    // ingest configs from from job's Closure
    LinkedHashMap config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    echo "key: ${config.key}, value: ${config.value}."

    // inject your pipeline here
    pipeline {
        agent { label 'linux' }
        stages {
            stage('Closure Demo') {
                steps {
                    script {
                        echo "Hello World"
                    }
                }
            }
        }
    }
}