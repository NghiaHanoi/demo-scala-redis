import org.scalatest._
/**
 * Base Unit test class
 */
abstract class BaseUnitSpec extends FlatSpec with Matchers with
  OptionValues with Inside with Inspectors with BeforeAndAfter with GivenWhenThen {

}