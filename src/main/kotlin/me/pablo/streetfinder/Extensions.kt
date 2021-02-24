import edu.stanford.nlp.ling.CoreAnnotations
import edu.stanford.nlp.ling.CoreLabel
import org.slf4j.LoggerFactory

fun CoreLabel.label(): String = this.get(CoreAnnotations.AnswerAnnotation::class.java)
fun CoreLabel.probability(): Double = this.get(CoreAnnotations.AnswerProbAnnotation::class.java)

fun <T> logger(clazz: Class<T>) = LoggerFactory.getLogger(clazz)