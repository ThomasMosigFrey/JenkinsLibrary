import com.jenkins.*

def call(String agent = 'linux', String key = 'default', int value = 0) {
    // inject your pipeline here
    pipeline {
        agent { label "${agent}" }
        stages {
            stage('Parameters Demo') {
                steps {
                    script {
                        echo "key: ${key}, value: ${value}."
                    }
                }
            }
        }
    }
}