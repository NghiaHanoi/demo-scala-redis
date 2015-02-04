import org.scalatest._
/**
 * Base acceptance test spec class
 */
abstract class BaseAcceptanceSpec extends FeatureSpec with Matchers with
  OptionValues with Inside with Inspectors with BeforeAndAfter with GivenWhenThen{

}