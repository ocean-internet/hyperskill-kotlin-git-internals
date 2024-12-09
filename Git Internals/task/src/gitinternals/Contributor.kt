package gitinternals

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

private const val DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss z"

abstract class Contributor(
    open val name: String,
    open val email: String,
    open val timestamp: String,
) {
  companion object {
    const val AUTHOR = "author"
    const val COMMITTER = "committer"

    fun fromString(string: String): Contributor {
      val timestamp = extractTimestamp(string)
      val email = extractEmail(string)

      return when {
        string.startsWith(AUTHOR) -> Author(extractName(string, AUTHOR), email, timestamp)
        string.startsWith(COMMITTER) -> Committer(extractName(string, COMMITTER), email, timestamp)

        else -> throw IllegalArgumentException("Unknown contributor type")
      }
    }

    private fun extractName(string: String, prefix: String) =
        "$prefix (.*) <".toRegex().find(string)?.groups?.get(1)?.value.orEmpty()

    private fun extractEmail(string: String): String =
        "<(.*@.*)>".toRegex().find(string)?.groups?.get(1)?.value.orEmpty()

    private fun extractTimestamp(string: String): String {
      val regex = "\\d{10} [+-]\\d{4}".toRegex()
      val (timestamp, timezone) = regex.find(string)?.value.orEmpty().split(" ", limit = 2)
      val zonedDateTime =
          ZonedDateTime.ofInstant(Instant.ofEpochSecond(timestamp.toLong()), ZoneId.of(timezone))

      return DateTimeFormatter.ofPattern(DATE_TIME_FORMAT).format(zonedDateTime)
    }
  }
}
