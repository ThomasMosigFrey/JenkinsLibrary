import com.jenkins.*

def call(Closure body, String name) {
    echo name
    echo "mvn clean package"
}