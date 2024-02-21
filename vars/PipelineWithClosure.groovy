import com.jenkins.*

def call(Closure body) {
    // ingest configs from jobs
    LinkedHashMap config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config

    body()

    echo "key: ${config.key}, value: ${config.value}."

    // inject your pipeline here
}