import com.jenkins.*

def call(Closure body) {
    echo body.toString()
    echo "mvn clean package"
}