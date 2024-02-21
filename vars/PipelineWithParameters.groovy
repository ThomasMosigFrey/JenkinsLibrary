import com.jenkins.*

def call(String host = 'linux', String key = 'default', int value = 0) {
    // inject your pipeline here
    pipeline {
        agent { label "${host}" }
        stages {
            stage('Parameters Demo') {
                steps {
                    echo "key: ${key}, value: ${value}."
                }
            }
        }
    }
}