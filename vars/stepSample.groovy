import com.jenkins.*

def call(Closure body, def name) {
    echo body.toString()
    print name
    echo "mvn clean package"
}
