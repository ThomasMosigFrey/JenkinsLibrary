import com.jenkins.*

String name

public stepSample(String name) {
    this.name =name;
}

def call(Closure body) {
    echo body.toString()
    print body.getMaximumNumberOfParameters()
    echo "mvn clean package"
    echo this.name
    body()
}
