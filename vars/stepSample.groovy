import com.jenkins.*

def call(String name, Closure body) {
    echo body.toString()
    print body.getMaximumNumberOfParameters()
    echo "mvn clean package"
    echo name
    body()
}
