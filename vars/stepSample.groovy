import com.jenkins.*

def  name

public stepSample(def name) {
    this.name = name
}

def call(Closure body) {
    echo body.toString()
    print this.name
    echo "mvn clean package"
}