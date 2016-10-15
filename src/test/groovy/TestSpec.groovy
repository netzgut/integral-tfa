
import net.netzgut.integral.tfa.TwoFactorAuthServiceImplementation
import spock.lang.Specification

class TestSpec extends Specification {

    def setupSpec() {
    }

    def "buildURI"() {

        given:
        def label = "My Label"
        def algorithm = "HmacSHA1"
        def timeStep = 30
        def issuer = "TestIssuer"
        def codeAgeOffset = 1
        def authService = new TwoFactorAuthServiceImplementation(algorithm, timeStep, issuer, codeAgeOffset)
        def secret = authService.generateSecret()

        when:
        def uri = authService.buildURI(secret, label)

        then:
        uri.scheme == "otpauth"
        uri.authority == "totp"
        uri.path == "/${label}"

        def query = uri.query.split("&")
        query.length == 3
        query.find { q -> q.startsWith("secret=")}
        query.find { q -> q == "issuer=${issuer}"}
        query.find { q -> q == "period=${timeStep}"}
    }
}