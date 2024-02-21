import com.jenkins.*

def call(String key = 'default', int value = 0) {
    echo "key: ${key}, value: ${value}."

    // inject your pipeline here
    pipeline {
        agent { label 'linux' }
        stages {
            stage('Parameters Demo') {
                steps {
                    script {
                        echo "Hello World"
                    }
                }
            }
        }
    }
}