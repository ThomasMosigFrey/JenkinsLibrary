import com.jenkins.*

def call(Closure body, def name) {
    echo body.toString()
    print body.getMaximumNumberOfParameters
    echo "mvn clean package"
}
