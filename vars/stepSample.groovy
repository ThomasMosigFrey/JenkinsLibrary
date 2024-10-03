import com.jenkins.*

def call(Closure body) {
    echo body.toString()
    print body.getMaximumNumberOfParameters()
    echo "mvn clean package"
}
