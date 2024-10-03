import com.jenkins.*

def call(String username, String password, Closure body) {
    echo username
    echo password
    body()
}
