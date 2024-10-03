import com.jenkins.*

def call(Closure body) {
    echo body.toString()
    print body.getMaximumNumberOfParameters()
    body()
    echo "mvn clean package"
}
