import com.jenkins.*

def call(Closure body) {
    echo body
    echo "mvn clean package"
}