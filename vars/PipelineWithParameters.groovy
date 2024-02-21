import com.jenkins.*

def call(String key = 'default', int value = 0) {
    echo "key: ${key}, value: ${value}."

    // inject your pipeline here
    pipeline {
        agent { label 'Parameters Demo' }
        stages {
            stage('Compile/Test') {
                steps {
                    script {
                        echo "Hello World"
                    }
                }
            }
        }
    }
}